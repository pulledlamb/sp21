package gitlet;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author shidan
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    String head;
    String master;

    Index index;

    HashMap<String, Commit> commitTree;
    HashMap<String, Branch> branchTree;

    /** stating ares. */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** Staging directory. */
    public static final File STAGE_DIR = join(CWD, ".index");

    /* TODO: fill in the rest of this class. */
    /** constructor */
    public Repository() {
        try {
            this.commitTree = Utils.readObject(Utils.join(GITLET_DIR, "commits"),
                    HashMap.class);
        } catch (IllegalArgumentException e) {
            commitTree = new HashMap<>();
        }
        try {
            this.branchTree = Utils.readObject(Utils.join(GITLET_DIR, "branch"),
                    HashMap.class);
        } catch (IllegalArgumentException e) {
            branchTree = new HashMap<>();
        }
        try {
            this.index = Utils.readObject(Utils.join(GITLET_DIR, "index"),
                    Index.class);
        } catch (IllegalArgumentException e) {
            index = new Index();
        }
        try {
            this.head = Utils.readObject(Utils.join(GITLET_DIR, "head"),
                    String.class);
        } catch (IllegalArgumentException e) {
            this.head = "";
        }
        try {
            this.master = Utils.readObject(Utils.join(GITLET_DIR, "master"),
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
        this.master = "master";
        branchTree.put(master, null);

        serialize();
    }

    public void add(String filename) {
        File f = new File(filename);
        Blob b = new Blob(filename, CWD.getPath());
        if (!f.exists()) {
            System.out.println("File does not exist");
            return;
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

    public void commit(String msg) {
        Commit parCommit = commitTree.get(head);

        if (index.stagedFiles.isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }

        HashMap<String, Blob> blbs = new HashMap<>(parCommit.parentBlobs);
        blbs.putAll(index.stagedFiles);
        
        Commit newCommit = new Commit(msg, blbs, parCommit.blobs, parCommit.getShortSha());
        newCommit.setSha();
        head = newCommit.getShortSha();

        commitTree.put(newCommit.getShortSha(), newCommit);
        index.clear();
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

        System.out.println("\n=== Modifications Not Stages For Commit ===");

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
        Utils.writeContents(Utils.join(CWD.getPath(), filename), contents);
        serialize();
    }

    public void log() {
        Commit headCommit = commitTree.get(head);
        while (headCommit != null) {
            headCommit.print();
            headCommit = commitTree.get(headCommit.getParentSha());
        }
    }

    public void serialize() {
        String direc = GITLET_DIR.getPath();
        Utils.writeObject(Utils.join(direc, "index"), index);
        Utils.writeObject(Utils.join(direc, "commits"), commitTree);
        Utils.writeObject(Utils.join(direc, "head"), head);
        Utils.writeObject(Utils.join(direc, "branch"), branchTree);
        Utils.writeObject(Utils.join(GITLET_DIR, "master"), master);
    }
}
