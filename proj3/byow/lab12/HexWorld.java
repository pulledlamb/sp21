package byow.lab12;


import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int ROW = 3;

    private static final long SEED = 47256;
    private static final Random RANDOM = new Random(SEED);

    public static void addHexagon(TETile[][] tiles, int x, int y,
                                  int size) {
        TETile t = randomTile();

        for (int j = 0; j < size; j++) {
            int xOff = size - j - 1;
            int length = size + 2 * j, height = 2 * (size - j) - 1;
            drawRow(tiles, x + xOff, y + j, length, height, t);
        }
    }

    private static void drawRow(TETile[][] tiles, int startPosition,
                                int y, int length, int height, TETile t) {
        for (int i = 0; i < length; i++) {
            tiles[startPosition + i][y] = t;
            //draw reflection
            tiles[startPosition + i][y + height] = t;
        }
    }

    public static void fillWithHex(TETile[][] tiles, int size) {
        int length = 3 * size - 2;
        for (int i = 0; i < ROW; i++) {
            fillRow(tiles, i, size);
        }
    }
    private static void fillRow(TETile[][] tiles, int i,
                                int size) {
        int length = 3 * size - 2;
        int startX = i * (2 * size - 1);
        for (int j = 0; j < i + ROW; j++) {
            int startY = (ROW - i - 1) * size + j * (2 * size) + 1;
            addHexagon(tiles, startX, startY, size);
            //draw reflection
            if (i < ROW - 1) {
                int xRef = startX + (2 - i) * length + (2 - i) * size - 1;
                addHexagon(tiles, xRef, startY, size);
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            default: return Tileset.TREE;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                randomTiles[x][y] = Tileset.NOTHING;
            }
        }
        fillWithHex(randomTiles, 4);
        randomTiles[0][4] = Tileset.TREE;

        ter.renderFrame(randomTiles);
    }
}
