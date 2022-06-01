package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author shidan
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        if (!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdir();
        }
        File d = Utils.join(CAPERS_FOLDER, "dogs");
        if (!d.exists()) {
            d.mkdir();
        }
        File s = Utils.join(CAPERS_FOLDER, "story");
        try {
            s.createNewFile();
        } catch (IOException excp) {
            System.out.println("File story already exists.");
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File s = Utils.join(CAPERS_FOLDER, "story");
        byte[] bts = Utils.readContents(s);
        if (bts.length == 0) {
            Utils.writeContents(s, bts, text);
        } else {
            Utils.writeContents(s, bts, "\n", text);
        }

        System.out.println(Utils.readContentsAsString(s));
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {

        Dog d = new Dog(name, breed, age);
        d.saveDog();
        System.out.println(d);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {

        Dog d = Dog.fromFile(name);
        d.haveBirthday();
        d.saveDog();
    }
}
