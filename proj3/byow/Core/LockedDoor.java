package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class LockedDoor extends Door {
    public LockedDoor(Position p, TETile[][] world, int width, int dir) {
        super(p, world, width, dir);
    }

    @Override
    public void drawDoor() {
        world[p.getX()][p.getY()] = Tileset.LOCKED_DOOR;
    }
}
