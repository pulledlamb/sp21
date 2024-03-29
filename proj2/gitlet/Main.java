package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        if (!firstArg.equals("init")) {
            if (checkInit(repo)) {
                return;
            }
        }
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                repo.init();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                File f = new File(args[1]);
                if (!f.exists()) {
                    System.out.println("File does not exist.");
                    return;
                }
                repo.add(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                if (args[1].equals("") || args[1].isBlank()) {
                    System.out.println("Please enter a commit message.");
                    return;
                }
                repo.commit(args[1]);
                break;
            case "checkout":
                if (args.length == 3) {
                    repo.checkout(args[2]);
                } else if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    repo.checkout(args[1], args[3]);
                } else if (args.length == 2){
                    repo.checkoutBranch(args[1]);
                } else {
                    throw new GitletException("Invalid number of arguments for: checkout");
                }
                break;
            case "log":
                validateNumArgs("log", args, 1);
                repo.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                repo.globalLog();
                break;
            case "status":
                validateNumArgs("status", args, 1);
                checkInit(repo);
                repo.status();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                repo.find(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                repo.rm(args[1]);
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                repo.removeBranch(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                repo.reset(args[1]);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                repo.merge(args[1]);
                break;
            case "add-remote":
                validateNumArgs("add-remote", args, 3);
                repo.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                validateNumArgs("rm-remote", args, 2);
                repo.removeRemote(args[1]);
                break;
            case "fetch":
                validateNumArgs("fetch", args, 3);
                repo.fetch(args[1], args[2]);
                break;
            case "push":
                validateNumArgs("push", args, 3);
                repo.push(args[1], args[2]);
                break;
            case "pull":
                validateNumArgs("pull", args, 3);
                repo.pull(args[1], args[2]);
                break;
            default:
                System.out.println("No command with that name exists");
                break;
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new GitletException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

    public static boolean checkInit(Repository repo) {
        boolean re = false;
        if (!repo.initialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            re = true;
        }

        return re;
    }
}
