package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab13.MemoryGame;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class Player extends Creature {
    public static final int FEE = 20, KEY = 3;
    int flowers, keys, xLim, yLim;
    boolean openDoor, atLockedDoor;
    String msg;

    public Player(TETile[][] realworld, Position p) {
        super(realworld, p);
        xLim = realworld[0].length; yLim = realworld.length;
        msg = "Collect 20 flowers to a play a game to win a key.";
        openDoor = false; atLockedDoor = false;
        drawPlayer();
    }


    public void movePlayer(int dx, int dy) {
        int x = p.getX(), y = p.getY();
        if (isFloor(x + dx, y + dy)
                && !isGuard(x + dx, y + dy)) {
            world[x][y] = Tileset.FLOOR;
            p.update(x + dx, y + dy);
            drawPlayer();
        }
        if (isKey(x + dx, y + dy)) {
            boolean win = false;
            if (flowers >= FEE) {
                win = playGame(xLim, yLim);
                if (win) {
                    keys += 1;
                    world[x][y] = Tileset.FLOOR;
                    p.update(x + dx, y + dy);
                    drawPlayer();
                }
            }
        }
        if (isLocked(x + dx, y + dy)) {
            atLockedDoor = true;
            if (keys == KEY) {
                openDoor = true;
            }
            return;
        }
        if (isFlower(x + dx, y + dy)) {
            world[x][y] = Tileset.FLOOR;
            flowers += 5;
            p.update(x + dx, y + dy);
            drawPlayer();
        }

        atLockedDoor = false;
    }

    // return true if win the game played
    public boolean playGame(int height, int width) {
        flowers -= FEE;
        Random r = new Random();
        double chance = RandomUtils.uniform(r, 0.0, 1.0);
        if (chance > 0.05) {
            MemoryGame mg = new MemoryGame(width, height, r.nextLong());
            mg.startGame((keys + 1) * 3);
            return !mg.gameOver;
        } else {
            return true;
        }
    }


    public void drawPlayer() {
        world[p.getX()][p.getY()] = Tileset.AVATAR;
    }
}
