package gitlet;

import java.io.File;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author shidan
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * head commit and branch pointers
     */
    String head;
    String master;

    /** Staging area*/
    Index index;

    /**
     * commit tree of shortSHA and commits
     * branch tree of branch name and branches
     */
    HashMap<String, Commit> commitTree;
    HashMap<String, Branch> branchTree;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** Staging directory. */
    public static final File STAGE_DIR = join(CWD, ".index");
    public static final String MASTER = "master";

    /** constructor */
    @SuppressWarnings("unchecked")
    public Repository() {
        try {
            this.commitTree = readObject(join(GITLET_DIR, "commits"),
                    HashMap.class);
        } catch (IllegalArgumentException e) {
            commitTree = new HashMap<>();
        }
        try {
            this.branchTree = readObject(join(GITLET_DIR, "branch"),
                    HashMap.class);
        } catch (IllegalArgumentException e) {
            branchTree = new HashMap<>();
        }
        try {
            this.index = readObject(join(GITLET_DIR, "index"),
                    Index.class);
        } catch (IllegalArgumentException e) {
            index = new Index();
        }
        try {
            this.head = readObject(join(GITLET_DIR, "head"),
                    String.class);
        } catch (IllegalArgumentException e) {
            this.head = "";
        }
        try {
            this.master = readObject(join(GITLET_DIR, "master"),
                    String.class);
        } catch (IllegalArgumentException e) {
            this.master = "";
        }
    }

    public boolean initialized() {
        return GITLET_DIR.exists();
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        Commit initCmt = new Commit();
        initCmt.setSha();

        this.head = initCmt.getShortSha();
        commitTree.put(head, initCmt);
        this.master = MASTER;
        Branch masterBranch = new Branch(master, null);
        branchTree.put(master, masterBranch);

        serialize();
    }

    public void add(String filename) {
        File f = new File(filename);
        Blob b = new Blob(filename, CWD.getPath());
        if (!f.exists()) {
            System.out.println("File does not exist.");
        }

        Commit curr = commitTree.get(head);
        HashMap<String, Blob> currBlob = curr.blobs;

        if (currBlob.containsKey(filename)
                && currBlob.get(filename).getSha().equals(b.getSha())) {
            index.removedFiles.remove(filename);
            serialize();
            return;
        }
        index.removedFiles.remove(filename);

        index.stagedFiles.put(filename, b);

        serialize();
    }

    public void rm(String filename) {
        Commit c = commitTree.get(head);
        if (index.stagedFiles.containsKey(filename)) {
            index.stagedFiles.remove(filename);
        } else if (c.blobs.containsKey(filename)) {
            index.removedFiles.put(filename, c.blobs.get(filename));
            restrictedDelete(filename);
        } else {
            System.out.println("No reason to remove the file");
        }
        serialize();
    }

    public void commit(String msg) {
        Commit parCommit = commitTree.get(head);

        if (index.stagedFiles.isEmpty() && index.removedFiles.isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }

        HashMap<String, Blob> blbs = new HashMap<>(parCommit.blobs);
        blbs.putAll(index.stagedFiles);

        for (String s : index.removedFiles.keySet()) {
            blbs.remove(s);
        }

        Commit newCommit = new Commit(msg, blbs, parCommit.blobs, parCommit.getShortSha());
        newCommit.setSha();
        head = newCommit.getShortSha();
        branchTree.get(master).setHead(head);

        commitTree.put(newCommit.getShortSha(), newCommit);
        index.clear();
        serialize();
    }

    public void branch(String branchName) {
        if (branchTree.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch coolBeans = new Branch(branchName, head);
        branchTree.put(branchName, coolBeans);

        branchTree.get(master).setSplitCommit(head);

        serialize();
    }

    public void status() {
        System.out.println("=== Branches ===");
        Object[] branchKeys = branchTree.keySet().toArray();
        Arrays.sort(branchKeys);

        for (Object s : branchKeys) {
            if (s.toString().equals(master)) {
                System.out.println("*" + master);
            } else {
                System.out.println(s);
            }
        }

        System.out.println("\n=== Staged Files ===");
        Object[] stagedKeys = index.stagedFiles.keySet().toArray();
        Arrays.sort(stagedKeys);
        for (Object s : stagedKeys) {
            System.out.println(s);
        }

        System.out.println("\n=== Removed Files ===");
        Object[] removedKeys = index.removedFiles.keySet().toArray();
        Arrays.sort(removedKeys);
        for (Object s : removedKeys) {
            System.out.println(s);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");

        System.out.println("\n=== Untracked Files ===");
    }

    public void checkout(String filename) {
        checkout(head, filename);
    }

    public void checkout(String id, String filename) {
        String shortId = id.substring(0, 6);
        if (!commitTree.containsKey(shortId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit prev = commitTree.get(shortId);
        if (!prev.blobs.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        byte[] contents = prev.blobs.get(filename).getContents();
        writeContents(join(CWD.getPath(), filename), contents);
        serialize();
    }

    public void checkout(int n, String branchname) {
        if (!branchTree.containsKey(branchname)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchname.equals(master)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        String h = branchTree.get(branchname).getHead();
        Commit udda = commitTree.get(h);

        Commit curr = commitTree.get(head);

        for (String s : curr.blobs.keySet()) {
            if (!udda.blobs.containsKey(s)) {
                restrictedDelete(s);
            }
        }

        checkoutBranchHead(curr, udda);
        head = udda.getShortSha();
        master = branchname;

        serialize();
    }

    private void checkoutBranchHead(Commit curr, Commit udda) {
        List<String> allFiles = plainFilenamesIn(CWD);
        if (allFiles != null) {
            for (String b : udda.blobs.keySet()) {
                if (!curr.blobs.containsKey(b) && allFiles.contains(b)) {
                    System.out.println("There is an untracked file in the way; delete "
                            + "it, or add and commit it first.");
                    return;
                }
                byte[] contents = udda.blobs.get(b).getContents();
                writeContents(join(CWD.getPath(), b), contents);
            }
        }
    }


    public void removeBranch(String branchName) {
        if (!branchTree.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(master)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        branchTree.remove(branchName);

        serialize();
    }


    public void reset(String id) {
        String sid = id.substring(0, 6);
        if (!commitTree.containsKey(sid)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        checkoutBranchHead(commitTree.get(head), commitTree.get(sid));
        head = sid;

        serialize();
    }

    public void merge(String branch) {
        Branch curr = branchTree.get(master);
        Branch udda = branchTree.get(branch);
        String split = curr.getSplitCommit();

        if (split.equals(udda.getHead())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        if (split.equals(curr.getHead())) {
            checkout(0, branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (!index.stagedFiles.isEmpty() || !index.removedFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        merge(commitTree.get(curr.getHead()), commitTree.get(udda.getHead()),
                commitTree.get(split), branch);

        serialize();
    }

    private void merge(Commit curr, Commit udda, Commit split, String branch) {
        HashMap<String, Blob> cBlob = curr.blobs, uBlob = udda.blobs, sBlob = split.blobs;
        boolean conflict = false;

        for (String f : sBlob.keySet()) {
            boolean cMod = false, uMod = false;
            boolean cDel = false, uDel = false;
            if (!cBlob.containsKey(f)) {
                cMod = true;
                cDel = true;
            } else {
                cMod = !Arrays.equals(sBlob.get(f).getContents(), cBlob.get(f).getContents());
            }
            if (!uBlob.containsKey(f)) {
                uMod = true;
                uDel = true;
            } else {
                uMod = !Arrays.equals(sBlob.get(f).getContents(), uBlob.get(f).getContents());
            }

            if (!cMod && uMod) {
                if (uDel) {
                    rm(f);
                } else {
                    byte[] contents = uBlob.get(f).getContents();
                    writeContents(join(CWD, f), contents);
                    index.stagedFiles.put(f, uBlob.get(f));
                }
            } else if (cMod && uMod) {
                if (!cDel && !uDel) {
                    if (!Arrays.equals(cBlob.get(f).getContents(), uBlob.get(f).getContents())) {
                        conflictResolver(cBlob.get(f), uBlob.get(f));
                        conflict = true;
                    }
                } else if ((cDel && !uDel) || (!cDel && uDel)) {
                    conflictResolver(cBlob.get(f), uBlob.get(f));
                    conflict = true;
                }
            }
        }

        for (String f : uBlob.keySet()) {
            if (!sBlob.containsKey(f) && !cBlob.containsKey(f)) {
                writeContents(join(CWD, f), uBlob.get(f).getContents());
                index.stagedFiles.put(f, uBlob.get(f));
            }
        }

        String msg = "Merged " + branch + " into " + master + ".";
        mergeCommit(msg, curr, udda);
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void mergeCommit(String msg, Commit curr, Commit udda) {
        HashMap<String, Blob> currBlob = new HashMap<>(commitTree.get(head).blobs);

        currBlob.putAll(index.stagedFiles);
        for (String s : index.removedFiles.keySet()) {
            currBlob.remove(s);
        }

        Commit c = new Commit(msg, currBlob, curr.blobs, udda.blobs,
                curr.getShortSha(), udda.getShortSha());
        c.setMergSha();
        head = c.getShortSha();
        branchTree.get(master).setHead(head);

        commitTree.put(head, c);
        index.clear();

        serialize();
    }

    private void conflictResolver(Blob curr, Blob udda) {
        byte[] header = "<<<<<<< HEAD\n".getBytes();
        byte[] div = "=======\n".getBytes();
        byte[] tail = ">>>>>>>\n".getBytes();

        byte[] currCon, uddaCon;
        if (curr == null) {
            currCon = "\n".getBytes();
        } else {
            currCon = curr.getContents();
        }
        if (udda == null) {
            uddaCon = "\n".getBytes();
        } else {
            uddaCon = udda.getContents();
        }

        writeContents(join(CWD, curr.getFilename()), header, currCon, div, uddaCon, tail);

        serialize();
    }


    public void log() {
        Commit headCommit = commitTree.get(head);
        while (headCommit != null) {
            headCommit.print();
            headCommit = commitTree.get(headCommit.getParentSha());
        }
    }


    public void globalLog() {
        for (Commit c : commitTree.values()) {
            c.print();
        }
    }

    public void find(String msg) {
        boolean found = false;
        for (Commit c : commitTree.values()) {
            if (c.getMsg().equals(msg)) {
                found = true;
                System.out.println(c.getSha());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void serialize() {
        writeObject(join(GITLET_DIR, "index"), index);
        writeObject(join(GITLET_DIR, "commits"), commitTree);
        writeObject(join(GITLET_DIR, "head"), head);
        writeObject(join(GITLET_DIR, "branch"), branchTree);
        writeObject(join(GITLET_DIR, MASTER), master);
    }
}
