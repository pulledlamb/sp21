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

    /** remote */
    HashMap<String, String> remote;

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
        try {
            this.remote = readObject(join(GITLET_DIR, "remote"),
                    HashMap.class);
        } catch (IllegalArgumentException e) {
            this.remote = new HashMap<>();
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

    public void addRemote(String name, String addr) {
        if (remote.containsKey(name)) {
            System.out.println("A remote with that name already exists.");
            return;
        }
        remote.put(name, addr);
        serialize();
    }

    public void removeRemote(String name) {
        if (!remote.containsKey(name)) {
            System.out.println("A remote with that name does not exist.");
            return;
        }

        remote.remove(name);
        serialize();
    }

    public void push(String name, String branch) {
        File f = new File(remote.get(name));
        if (!f.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        HashMap<String, Branch> remoteTree = getRemoteBranch(name);

        String remoteHead = remoteTree.get(branch).getHead();
        if (!commitTree.containsKey(remoteHead)) {
            System.out.println("Please pull down remote changes before pushing.");
            return;
        }

        HashMap<String, Commit> remoteCommit = getRemoteCommit(name);
        if (!remoteTree.containsKey(branch)) {
            Branch b = new Branch(branch, remoteHead);
            remoteTree.put(branch, b);
            remoteTree.get(branch).setSplitCommit(remoteHead);
        }
        LinkedList<String> remotes = new LinkedList<>();
        remotes.add(head);
        while (!remotes.isEmpty()) {
            String s = remotes.pop();
            Commit c = remoteCommit.get(s);
            remoteCommit.put(c.getShortSha(), c);
            if (c.getParentSha() != null) {
                remotes.add(c.getParentSha());
            }
            if (c.getSecondParSha() != null) {
                remotes.add(c.getSecondParSha());
            }
            if (remotes.isEmpty()) {
                Commit last = new Commit(c.getMsg(), c.blobs, commitTree.get(head).blobs,
                        commitTree.get(head).getShortSha());
            }

            serialize();
        }


    }

    public void pull(String name, String branch) {

    }

    public void fetch(String name, String branch) {
        File f = new File(remote.get(name));
        if (!f.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        HashMap<String, Branch> remoteTree = getRemoteBranch(name);
        if (!remoteTree.containsKey(branch)) {
            System.out.println("That remote does not have that branch.");
            return;
        }

        HashMap<String, Commit> remoteCommit = getRemoteCommit(name);
        String bname = join(name, branch).toString();
        if (!branchTree.containsKey(bname)) {
            Branch b = new Branch(bname, head);
            branchTree.put(bname, b);
            branchTree.get(master).setSplitCommit(head);
        }

        String remoteHead = remoteTree.get(branch).getHead();
        LinkedList<String> remotes = new LinkedList<>();
        remotes.add(remoteHead);
        while (!remotes.isEmpty()) {
            String s = remotes.pop();
            Commit c = remoteCommit.get(s);
            if (!commitTree.containsKey(c.getShortSha())) {
                commitTree.put(c.getShortSha(), c);
            }
            if (c.getParentSha() != null) {
                remotes.add(c.getParentSha());
            }
            if (c.getSecondParSha() != null) {
                remotes.add(c.getSecondParSha());
            }
            if (remotes.isEmpty()) {
                Commit last = new Commit(c.getMsg(), c.blobs,
                        commitTree.get(head).blobs, commitTree.get(head).getShortSha());
            }
        }

        serialize();
    }



    @SuppressWarnings("unchecked")
    private HashMap<String, Branch> getRemoteBranch(String name) {
        return readObject(join(remote.get(name), "branch"), HashMap.class);
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Commit> getRemoteCommit(String name) {
        return readObject(join(remote.get(name), "commits"), HashMap.class);
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
        List<String> modified = modified();
        Object[] modifiedKeys = modified.toArray();
        Arrays.sort(modifiedKeys);
        for (Object s : modifiedKeys) {
            System.out.println(s);
        }

        System.out.println("\n=== Untracked Files ===");
        List<String> untracked = untracked();
        Object[] untrackedKeys = untracked.toArray();
        Arrays.sort(untrackedKeys);
        for (Object o : untrackedKeys) {
            System.out.println(o);
        }
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

    public void checkoutBranch(String branchname) {
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

        for (String b : udda.blobs.keySet()) {
            if (!curr.blobs.containsKey(b)) {
                assert allFiles != null;
                if (allFiles.contains(b)) {
                    System.out.println("There is an untracked file in the way; delete "
                            + "it, or add and commit it first.");
                    return;
                }
            }
            byte[] contents = udda.blobs.get(b).getContents();
            writeContents(join(CWD.getPath(), b), contents);
        }
        for (String s : allFiles) {
            if (!udda.blobs.containsKey(s)) {
                restrictedDelete(s);
            }
        }

        serialize();
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
        branchTree.get(master).setHead(head);
        index.clear();

        serialize();
    }

    public void merge(String branch) {
        Branch curr = branchTree.get(master);
        Branch udda = branchTree.get(branch);

        if (!index.stagedFiles.isEmpty() || !index.removedFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }

        if (udda == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        if (curr.equals(udda)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        List<String> allFiles = plainFilenamesIn(CWD);
        for (String b : commitTree.get(udda.getHead()).blobs.keySet()) {
            if (!commitTree.get(head).blobs.containsKey(b)) {
                assert allFiles != null;
                if (allFiles.contains(b)) {
                    System.out.println("There is an untracked file in the way; delete "
                            + "it, or add and commit it first.");
                    return;
                }
            }
        }

        List<String> pastCommits = getAncestor(master);
        String split = getSplit(branch);
        if (pastCommits.contains(udda.getHead())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        List<String> uddaPast = getAncestor(branch);
        if (uddaPast.contains(head)) {
            checkoutBranch(branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        merge(commitTree.get(head), commitTree.get(udda.getHead()),
                commitTree.get(split), branch);

        serialize();
    }

    private void merge(Commit curr, Commit udda, Commit split, String branch) {
        HashMap<String, Blob> cBlob = curr.blobs, uBlob = udda.blobs, sBlob = split.blobs;
        boolean conflict = false;

        for (String f : sBlob.keySet()) {
            /**
             * to keep track of if a file is changed in the current and given branch;
             * and if they are removed;
             */
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
                        Blob b = new Blob(f, CWD.getPath());
                        index.stagedFiles.put(f, b);
                        conflict = true;
                    }
                } else if ((cDel && !uDel) || (!cDel && uDel)) {
                    conflictResolver(cBlob.get(f), uBlob.get(f));
                    Blob b = new Blob(f, CWD.getPath());
                    index.stagedFiles.put(f, b);
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
            currCon = "".getBytes();
        } else {
            currCon = curr.getContents();
        }
        if (udda == null) {
            uddaCon = "".getBytes();
        } else {
            uddaCon = udda.getContents();
        }

        writeContents(join(CWD, curr.getFilename()), header, currCon, div, uddaCon, tail);

        serialize();
    }

    /** get past commits in a given branch */
    private List<String> getAncestor(String branch) {
        List<String> res = new ArrayList<>();
        LinkedList<String> ans = new LinkedList<>();
        String currCommit = branchTree.get(branch).getHead();
        ans.add(currCommit);
        while (!ans.isEmpty()) {
            String s = ans.pop();
            Commit c = commitTree.get(s);
            if (c.getParentSha() != null) {
                res.add(c.getParentSha());
                ans.add(c.getParentSha());
            }
            if (c.getSecondParSha() != null) {
                res.add(c.getSecondParSha());
                ans.add(c.getSecondParSha());
            }
        }

        return res;
    }

    /** find latest common split point of two branches */
    private String getSplit(String branch) {
        List<String> curr = getAncestor(master);
        List<String> udda = getAncestor(branch);
        for (String s : curr) {
            if (udda.contains(s)) {
                return s;
            }
        }
        return null;
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
        writeObject(join(GITLET_DIR, "remote"), remote);
    }

    private List<String> modified() {
        List<String> allFiles = plainFilenamesIn(CWD);
        List<String> deleted = new ArrayList<>();
        List<String> modified = new ArrayList<>();

        for (String s : commitTree.get(head).blobs.keySet()) {
            assert allFiles != null;
            if (allFiles.contains(s)) {
                byte[] b = readContents(join(CWD, s));
                byte[] contents = commitTree.get(head).blobs.get(s).getContents();
                if (!Arrays.equals(contents, b)
                        && !index.stagedFiles.containsKey(s)) {
                    modified.add(s + " (modified)");
                }
            } else {
                if (!index.removedFiles.containsKey(s)) {
                    modified.add(s + " (deleted)");
                }
            }
        }

        for (String s : index.stagedFiles.keySet()) {
            assert allFiles != null;
            if (allFiles.contains(s)) {
                byte[] b = readContents(join(CWD, s));
                byte[] contents = index.stagedFiles.get(s).getContents();
                if (!Arrays.equals(contents, b)) {
                    modified.add(s + " (modified)");
                }
            } else {
                modified.add(s + "(deleted)");
            }
        }

        return modified;
    }

    private List<String> untracked() {
        List<String> allFiles = plainFilenamesIn(CWD);
        List<String> untracked = new ArrayList<>();

        assert allFiles != null;
        for (String s : allFiles) {
            if (!index.stagedFiles.containsKey(s)
                    && !commitTree.get(head).blobs.containsKey(s)) {
                untracked.add(s);
            }
        }

        return untracked;
    }

}
