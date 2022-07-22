package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    /** sha1 hash of the blob. */
    String sha;
    byte[] contents;
    String filename;

    public Blob(String filename, String directory) {
        File f = join(directory, filename);
        this.contents = readContents(f);
        this.sha = sha1(this.contents);
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
