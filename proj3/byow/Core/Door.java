package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Door implements Serializable {
    Position p;
    int width, dir;
    // door has a direction; 0 if it's on left/right walls;
    // 1 if it's on top/bottom walls;\
    TETile[][] world;

    public Door(Position p, TETile[][] world, int width, int dir) {
        this.p = p; this.world = world;
        this.width = width; this.dir = dir;
    }

    public int getxPos() {
        return p.getX();
    }

    public int getyPos() {
        return p.getY();
    }

    public void drawDoor() {
        int x = p.getX(), y = p.getY();
        if (dir == 0) {
            for (int i = y; i < y + width; i++) {
                world[x][i] = Tileset.FLOOR;
            }
        } else {
            for (int i = x; i < x + width; i++) {
                world[i][y] = Tileset.FLOOR;
            }
        }
    }
}
