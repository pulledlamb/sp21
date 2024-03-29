package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    /** branch maintains a commit tree,
     * and keeps track of where split happens.
     */
    String branchName;
    String head;

    public Branch(String branchName, String splitCommit) {
        this.branchName = branchName;
        this.head = splitCommit;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String lastCommit) {
        this.head = lastCommit;
    }
}
