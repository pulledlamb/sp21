package gitlet;

import java.io.Serializable;
import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author shidan
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    String msg;

    /** The SHA1 hash of this Commit. */
    String sha;
    String shortSha;

    /** Parent SHA1 hash of this Commit. */
    String parentSha;

    String timeStamp;

    /** files tracked. */
    HashMap<String, Blob> blobs;

    HashMap<String, Blob> parentBlobs;
    DateFormat outDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
    DateFormat inDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

    /* TODO: fill in the rest of this class. */
    public Commit(String msg, HashMap<String, Blob> blbs, HashMap<String, Blob> parblbs, String parSha) {
        this.msg = msg;
        this.blobs = blbs;
        this.parentBlobs = parblbs;
        this.parentSha = parSha;

        timeStamp = outDate.format(new Date());
    }

    public Commit() {
        msg = "initial commit";
        blobs = new HashMap<>();
        parentBlobs = new HashMap<>();
        try {
            Date dt = inDate.parse("Thu Jan 1 00:00:00 UTC 1970");
            timeStamp = outDate.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.parentSha = null;
    }

    public void setSha() {
        String s = "";
        for (Blob b : blobs.values()) {
            s += b.toString();
        }
        if (parentBlobs != null) {
            for (Blob b : parentBlobs.values()) {
                s += b.toString();
            }
        }
        sha = Utils.sha1(s + msg + timeStamp);
    }

    public String getShortSha() {
        shortSha = sha.substring(0, 6);
        return shortSha;
    }

    public String getSha() {
        return sha;
    }

    public String getMsg() {
        return msg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getParentSha() {
        return parentSha;
    }

    public void print() {
        System.out.println("===");
        System.out.println("commit " + sha);
        System.out.println("Date: " + timeStamp);
        System.out.println(msg + "\n");
    }
}
