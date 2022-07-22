package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class Index implements Serializable {
    HashMap<String, Blob> stagedFiles;
    HashMap<String, Blob> removedFiles;

    public Index() {
        stagedFiles = new HashMap<>();
        removedFiles = new HashMap<>();
    }

    public void clear() {
        stagedFiles = new HashMap<>();
        removedFiles = new HashMap<>();
    }

    public boolean staged(String filename) {
        return (stagedFiles.containsKey(filename)
                || removedFiles.containsKey(filename));
    }
}
