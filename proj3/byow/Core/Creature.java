package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Creature {
    TETile[][] world;
    Position p;

    public Creature(TETile[][] world, Position p) {
        this.world = world; this.p = p;
    }

    public boolean isWall(int x, int y) {
        return world[x][y].equals(Tileset.WALL);
    }

    public boolean isLocked(int x, int y) {
        return world[x][y].equals(Tileset.LOCKED_DOOR);
    }

    public boolean isFloor(int x, int y) {
        return world[x][y].equals(Tileset.FLOOR)
                || world[x][y].equals(Tileset.REDFLOOR);
    }

    public boolean isFlower(int x, int y) {
        return world[x][y].equals(Tileset.FLOWER);
    }

    public boolean isGuard(int x, int y) {
        return world[x][y].equals(Tileset.WATER);
    }

    public boolean isKey(int x, int y) {
        return world[x][y].equals(Tileset.KEY);
    }

    public Position getPosition() {
        return p;
    }
}
