package gitlet;

import org.checkerframework.checker.units.qual.A;

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

    /* TODO: fill in the rest of this class. */
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

    public void init() {
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system " +
                    "already exists in the current directory.");
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
            throw new GitletException("File does not exist.");
        }

        Commit curr = commitTree.get(head);
        HashMap<String, Blob> currBlob = curr.blobs;

        if (currBlob.containsKey(filename) &&
                currBlob.get(filename).getSha().equals(b.getSha())) {
            index.removedFiles.remove(filename);
            serialize();
            return;
        }
        index.removedFiles.remove(filename);

        index.stagedFiles.put(filename, b);

        serialize();
    }

    public void remove(String filename) {
        if (index.stagedFiles.containsKey(filename)) {
            index.stagedFiles.remove(filename);
        } else if (commitTree.containsKey(filename)) {
            Blob b = new Blob(filename, CWD.getPath());
            index.removedFiles.put(filename, b);
            restrictedDelete(filename);
        } else {
            throw new GitletException("No reason to remove the file");
        }
    }

    public void commit(String msg) {
        Commit parCommit = commitTree.get(head);

        if (index.stagedFiles.isEmpty()) {
            throw new GitletException("No changes added to the commit");
        }

        HashMap<String, Blob> blbs = new HashMap<>(parCommit.parentBlobs);
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
            throw new GitletException("A branch with name already exists.");
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
            throw new GitletException("No commit with that id exists.");
        }
        Commit prev = commitTree.get(shortId);
        if (!prev.blobs.containsKey(filename)) {
            throw new GitletException("File does not exist in that commit.");
        }

        byte[] contents = prev.blobs.get(filename).getContents();
        writeContents(join(CWD.getPath(), filename), contents);
        serialize();
    }

    public void checkout(int n, String branchname) {
        if (!branchTree.containsKey(branchname)) {
            throw new GitletException("No such branch exists.");
        }
        if (branchname.equals(master)) {
            throw new GitletException("No need to checkout the current branch.");
        }

        String h = branchTree.get(branchname).getHead();
        Commit headCommit = commitTree.get(h);

        Commit curr = commitTree.get(head);

        for (Blob b : headCommit.blobs.values()) {
            if (!curr.blobs.containsValue(b)) {
                throw new GitletException("There is an untracked file in the way; delete" +
                        "it, or add and commit it first.");
            }
            byte[] contents = b.getContents();
            writeContents(join(CWD.getPath(), b.getFilename()), contents);
        }

        List<String> allFiles = plainFilenamesIn(CWD);
        if (allFiles != null) {
            for (String s : allFiles) {
                if (!headCommit.blobs.containsKey(s)) {
                    restrictedDelete(s);
                }
            }
        }

        head = headCommit.getShortSha();
        master = branchname;

        serialize();
    }


    public void removeBranch(String branchName) {
        if (!branchTree.containsKey(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (branchName.equals(master)) {
            throw new GitletException("Cannot remove the current branch.");
        }

        branchTree.remove(branchName);

        serialize();
    }


    public void reset(String id) {
        HashSet<String> fileList = (HashSet<String>) commitTree.get(id).blobs.keySet();

        for (String filename : fileList) {
            checkout(id, filename);
        }

        head = id.substring(0, 6);

        serialize();
    }

    public void merge(String branch) {
        Branch curr = branchTree.get(master);
        Branch udda = branchTree.get(branch);
        String split = curr.getSplitCommit();

        if (split.equals(udda.getHead())) {
            throw new GitletException("Given branch is an ancestor of the current branch");
        }

        if (split.equals(curr.getHead())) {
            checkout(0, branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (!index.stagedFiles.isEmpty() || !index.removedFiles.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }

        merge(commitTree.get(curr.getHead()), commitTree.get(udda.getHead()), commitTree.get(split), branch);
    }

    private void merge(Commit curr, Commit udda, Commit split, String branch) {
        HashMap<String, Blob> cBlob = curr.blobs, uBlob = udda.blobs, sBlob = split.blobs;
        boolean conflict = false;

        for (String f : sBlob.keySet()) {
            boolean cMod = Arrays.equals(sBlob.get(f).getContents(), cBlob.get(f).getContents()),
                    uMod = Arrays.equals(sBlob.get(f).getContents(), uBlob.get(f).getContents());

            if (cMod && !uMod) {
                if (uBlob.get(f).getContents() != null) {
                    byte[] contents = uBlob.get(f).getContents();
                    writeContents(join(CWD, f), contents);
                    index.stagedFiles.put(f, uBlob.get(f));
                } else {
                    remove(f);
                }
            } else if (!cMod && !uMod) {
                if (!Arrays.equals(cBlob.get(f).getContents(), uBlob.get(f).getContents())) {
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

        String msg = "Merged" + branch + "into" + master;
        mergeCommit(msg, curr, udda);
        if (conflict) {
            System.out.println("Encountered a merge conflict");
        }
    }

    private void mergeCommit(String msg, Commit curr, Commit udda) {
        HashMap<String, Blob> currBlob = new HashMap<>(commitTree.get(head).blobs);

        currBlob.putAll(index.stagedFiles);
        for (String s : index.removedFiles.keySet()) {
            currBlob.remove(s);
        }

        Commit c = new Commit(msg, currBlob, curr.blobs, udda.blobs, curr.getShortSha(), udda.getShortSha());
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
        byte[] tail = ">>>>>>>".getBytes();

        byte[] currCon = curr.getContents();
        byte[] uddaCon = udda.getContents();

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
            throw new GitletException("Found no commit with that message.");
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
