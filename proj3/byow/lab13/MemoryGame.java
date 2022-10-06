package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    public boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame(6);
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder sb = new StringBuilder();

        while (sb.length() < n) {
            sb.append(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);

        StdDraw.enableDoubleBuffering();

        int midW = width / 2, midH = height / 2;
        if (!gameOver) {
            Font smallF = new Font("Monoca", Font.BOLD, 20);
            StdDraw.setFont(smallF);
            StdDraw.textLeft(1, height - 1, "Round:" + round);
            StdDraw.text(midW, height - 1, playerTurn ? "Type!" : "Watch!");
            StdDraw.textRight(width - 1, height - 1, ENCOURAGEMENT[round % ENCOURAGEMENT.length]);
            StdDraw.line(0, height - 2, width, height - 2);
        }

        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(midW, midH, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        drawFrame("");
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(letters.substring(i, i + 1));
            StdDraw.pause(700);
            drawFrame("");
            StdDraw.pause(700);
        }
        playerTurn = true;
    }

    public String solicitNCharsInput(int n) {
        StringBuilder sb = new StringBuilder();
        drawFrame("");
        while (sb.length() < n) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = StdDraw.nextKeyTyped();
            sb.append(c);
            drawFrame(sb.toString());
        }
        StdDraw.pause(700);
        return sb.toString();
    }

    public void startGame(int maxRound) {
        gameOver = false;
        round = 1;

        while (!gameOver && round <= maxRound) {
            String gameString = generateRandomString(round);
            String playerString = "";
            if (!playerTurn) {
                flashSequence(gameString);
            }
            if (playerTurn) {
                playerString = solicitNCharsInput(round);
                playerTurn = false;
            }
            if (!playerString.equals(gameString)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round:" + round);
                StdDraw.pause(1000);
            } else {
                drawFrame("Well done!");
                StdDraw.pause(1000);
                round += 1;
            }
        }
        if (!gameOver) {
            drawFrame("Congrats. You've won a key.");
        }
    }

}
