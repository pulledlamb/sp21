package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab13.MemoryGame;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    public static final int SEED = 12345678;
    public static final int FONTLARGE = 36,
            FONTMED = 27, FONTSMALL = 24;
    public static final int SIGHT = 10;
    static boolean isGameOver = false;
    boolean escaped, partial, proj;
    static TETile[][] world, partialWorld;
    long seed;
    WorldGenerator wg; Player p; Guard g;
    InputSource inputSource;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        inputSource = new KeyboardInputSource();
        init();
        startGame();
    }

    public void startGame() {
        StdDraw.clear();
        ter.initialize(WIDTH, HEIGHT);

        while (!isGameOver) {
            if (partial) {
                ter.renderPartialFrame(world, p.getPosition().getX(),
                        p.getPosition().getY());
            } else {
                ter.renderFrame(world);
            }
            if (p.keys > 1) {
                g.hunt();
            }
            if (p.atLockedDoor) {
                displayInfo("Need 3 keys to open.");
            } else {
                displayInfo(isMouseOver());
            }
            if (p.openDoor) {
                drawBlank("Congratulations! You've escaped the dungeon!");
                StdDraw.pause(2500);
                drawInit("");
                break;
            }
            update();

        }
        drawBlank("The guard has caught you.");
    }

    public void init() {
        drawInit("");
        solicitSeed();
        wg = new WorldGenerator(WIDTH, HEIGHT, new Random(seed));
        isGameOver = false; escaped = false; partial = true; proj = false;
        world = wg.getWorld(); partialWorld = wg.getPartialWorld();
        p = new Player(world, WorldGenerator.initPlayer);
        g = new Guard(world, WorldGenerator.initGuard, p);
    }

    public void init(long s) {
        wg = new WorldGenerator(WIDTH, HEIGHT, new Random(seed));
        isGameOver = false; escaped = false; partial = true; proj = false;
        world = wg.getWorld(); partialWorld = wg.getPartialWorld();
        p = new Player(world, WorldGenerator.initPlayer);
        g = new Guard(world, WorldGenerator.initGuard, p);
    }

    public void update() {
        if (inputSource.possibleNextInput()) {
            actions(inputSource.getNextKey());
            g.projPath(proj);
        }

    }

    public String isMouseOver() {
        int x = (int) Math.floor(StdDraw.mouseX()),
                y = (int) Math.floor(StdDraw.mouseY());
        if (!isMouseOutofBound(x, y)) {
            if ((partial && partialWorld[x][y].equals(Tileset.WALL))
                    || (!partial && world[x][y].equals(Tileset.WALL))) {
                return "This is WALL.";
            } else if ((partial && wg.partialWorld[x][y].equals(Tileset.FLOOR))
                    || (!partial && wg.world[x][y].equals(Tileset.FLOOR))) {
                return "This is FLOOR.";
            }
        }
        return "Collect 20 flowers to play a game to win a key.";
    }

    public void displayInfo(String msg) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, 1, "# of keys: " + p.keys + "/3");
        StdDraw.textLeft(11, 1, "# of flowers: " + p.flowers);
        StdDraw.textRight(80, 1, "Toggle on/off t: partial sight; p: projected guard path");
        StdDraw.line(0, 1.8, Engine.WIDTH, 1.8);
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.text(WIDTH / 2, 1, msg);
        StdDraw.show();

    }

    public boolean isMouseOutofBound(int x, int y) {
        return !(x > 0 && x < WIDTH && y > 0 && y < HEIGHT);
    }

    public void drawBlank(String s) {
        StdDraw.clear();
        StdDraw.clear(Color.BLACK);
        Font f1 = new Font("Monaco", Font.BOLD, FONTLARGE);
        StdDraw.setFont(f1);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "THE GAME");
        Font f2 = new Font("Monaco", Font.BOLD, FONTMED);
        StdDraw.setFont(f2);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }


    public void drawInit(String s) {
        StdDraw.clear();
        StdDraw.clear(Color.BLACK);
        Font f1 = new Font("Monaco", Font.BOLD, FONTLARGE);
        StdDraw.setFont(f1);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "THE GAME");

        Font f2 = new Font("Monaco", Font.BOLD, FONTMED);
        StdDraw.setFont(f2);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Quit (Q)");
        Font f3 = new Font("Monaco", Font.BOLD, FONTSMALL);
        StdDraw.setFont(f3);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9, s);
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void solicitSeed() {
        StringBuilder input = new StringBuilder();
        while (true) {
            try {
                if (inputSource.possibleNextInput()) {
                    char c = inputSource.getNextKey();
                    if (c == 'S' || c == 's') {
                        this.seed = Long.parseLong(input.toString());
                        break;
                    } else if (c == 'N' || c == 'n') {
                        input = new StringBuilder();
                        drawInit("Enter seed: ");
                    } else if (c == 'L' || c == 'l') {
                        wg = load();
                        break;
                    } else if (c == 'Q' || c == 'q') {
                        System.exit(0);
                    } else {
                        input.append(c);
                        drawInit("Enter seed: " + input);
                    }
                }
            } catch (Exception e) {
                drawInit("Error! Please enter a valid number!");
                StdDraw.pause(1000);
                drawInit("Enter seed: ");
                input = new StringBuilder();
            }
        }
    }

    public WorldGenerator load() {
        File f = new File("./saved_data");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (WorldGenerator) os.readObject();
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                System.exit(0);
            }
        }

        /* In the case no Editor has been saved yet, we return a new one. */
        return new WorldGenerator(WIDTH, HEIGHT, new Random(SEED));
    }

    public void save(WorldGenerator wGen) {
        File f = new File("./saved_data");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(wGen);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void actions(char c) {

        if (c == 'Q') {
            System.exit(0);
        } else if (c == 'T') {
            partial = !partial;
        } else if (c == ':') {
            if (inputSource.getNextKey() == 'Q') {
                save(wg);
                System.exit(0);
            }
        } else if (c == 'W') {
            p.movePlayer(0, 1);
        } else if (c == 'S') {
            p.movePlayer(0, -1);
        } else if (c == 'A') {
            p.movePlayer(-1, 0);
        } else if (c == 'D') {
            p.movePlayer(1, 0);
        } else if (c == 'P') {
            proj = !proj;
        }
    }
    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        if (!input.matches("^[0-9a-zA-Z:]+$")) {
            System.out.println("Wrong input!");
            return null;
        }
        parseSeed(input);
        init(0);

        input = input.replaceAll("n([0-9])+s", "");
        inputSource = new StringInputDevice(input);

        while (inputSource.possibleNextInput()) {
            char nextKey = inputSource.getNextKey();
            actions(nextKey);
        }
        return world;
    }

    private void parseSeed(String input) {
        Pattern pattern = Pattern.compile("([0-9]+)");
        Matcher m = pattern.matcher(input);
        if (m.find()) {
            seed = Long.parseLong(m.group(1));
        }
    }
}
