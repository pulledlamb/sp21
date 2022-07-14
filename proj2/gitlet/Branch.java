package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    /** branch maintain a commit tree. */
    String splitCommit;
    String branchName;

    /** keep track of where split happens.  */
    public Branch(String branchName, String splitCommit) {
        this.branchName = branchName;
        this.splitCommit = splitCommit;
    }
}
