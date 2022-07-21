package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    /** sha1 hash of the blob. */
    String sha;
    byte[] contents;
    String filename;

    public Blob(String filename, String directory) {
        File f = Utils.join(directory, filename);
        this.contents = Utils.readContents(f);
        this.sha = Utils.sha1(this.contents);
        this.filename = filename;
    }

    public String getSha() {
        return sha;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getFilename() {
        return filename;
    }
}
