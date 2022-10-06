package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Room implements Serializable {
    public static final int MAXROOMSIZE = 10;
    public static final int LARGEROOM = 3;
    int width;
    int height;
    // coordinates of the bottom left corner;
    Position p;
    ArrayList<Door> doors;
    ArrayList<Room> neighbors;
    Random seed;
    TETile[][] world;
    // to keep track of neighbor directions; 0 north, 1 east...
    HashSet<Integer> neighborDir;

    public Room(int w, int h, Position p, long s, TETile[][] world) {
        width = w; height = h;
        this.p = p;
        doors = new ArrayList<>();
        neighbors = new ArrayList<>();
        neighborDir = new HashSet<>();
        seed = new Random(s);
        this.world = world;
    }

    public Room(Position p, long s, TETile[][] world) {
        this(MAXROOMSIZE, MAXROOMSIZE, p, s, world);
    }

    public void generateNeighbors(ArrayList<Room> rooms) {
        for (int i = 0; i < 4; i++) {
            // if there isn't a neighbor to the direction i
            // we generate one neighbor for this room;
            if (!neighborDir.contains(i)) {
                setNeighbors(i, rooms);
            }
        }
    }

    public void setNeighbors(int dir, ArrayList<Room> rooms) {
        int x = p.getX(), y = p.getY();
        int nX = 0, nY = 0, xLim, yLim, dX = 0, dY = 0,
                dW = 0, dDir = 0, nDir = 0;
        // door has a direction for drawing purposes

        Position nDoorPos = new Position();
        int w = RandomUtils.uniform(seed, 1, MAXROOMSIZE - 2),
                h = RandomUtils.uniform(seed, 1, MAXROOMSIZE - 2);
        switch (dir) {
            case 0:
                // neighbor to the north
                nX = RandomUtils.uniform(seed, x, x + width);
                nY = y + height + 2;
                xLim = Math.min(nX + w + 1, x + width + 1);

                // coordinates of the door
                dX = RandomUtils.uniform(seed, nX + 1, xLim);
                dY = y + height + 1;
                dW = RandomUtils.uniform(seed, 1, xLim - dX + 1);
                dDir = 1; nDir = 2;
                nDoorPos = new Position(dX, dY + 1);
                break;
            case 1:
                // neighbor to the east
                nX = x + width + 2;
                nY = RandomUtils.uniform(seed, y, y + height);
                yLim = Math.min(nY + h + 1, y + height + 1);
                dX = x + width + 1;
                dY = RandomUtils.uniform(seed, nY + 1, yLim);
                dW = RandomUtils.uniform(seed, 1, yLim - dY + 1);
                dDir = 0; nDir = 3;
                nDoorPos = new Position(dX + 1, dY);
                break;
            case 2:
                // neighbor to the south
                nX = RandomUtils.uniform(seed, x, x + width);
                nY = y - 2 - h;
                xLim = Math.min(nX + w + 1, x + width + 1);
                dX = RandomUtils.uniform(seed, nX + 1, xLim);
                dY = y;
                dW = RandomUtils.uniform(seed, 1, xLim - dX + 1);
                dDir = 1; nDir = 0;
                nDoorPos = new Position(dX, dY - 1);
                break;
            default:
                // neighbor to the west
                nX = x - 2 - w;
                nY = RandomUtils.uniform(seed, y, y + height);
                yLim = Math.min(nY + h + 1, y + height + 1);

                dX = x;
                dY = RandomUtils.uniform(seed, nY + 1, yLim);
                dW = RandomUtils.uniform(seed, 1, yLim - dY + 1);
                dDir = 0; nDir = 1;
                nDoorPos = new Position(dX - 1, dY);
                break;
        }
        Room neighbor = new Room(w, h, new Position(nX, nY), seed.nextLong(), world);
        if (!neighbor.isOutOfBound() && !neighbor.isOverlap(neighbors)
                && !neighbor.isOverlap(rooms)) {

            neighborDir.add(dir);
            neighbors.add(neighbor);

            Door nDoor = new Door(nDoorPos, world, dW, dDir);
            neighbor.doors.add(nDoor);
            Door door = new Door(new Position(dX, dY), world, dW, dDir);
            doors.add(door);

            neighbor.neighbors.add(this);
            neighbor.neighborDir.add(nDir);
        }
    }

    public boolean isOverlap(ArrayList<Room> rooms) {
        int x = p.getX(), y = p.getY();
        for (Room r : rooms) {
            int oX = r.p.getX(), oY = r.p.getY(),
                    w = r.width, h = r.height;
            // check if left/right, top/bottom overlap
            boolean lr = x > oX + w + 1 || x + width + 1 < oX,
                    tb = y > oY + h + 1 || y + height + 1 < oY;
            if (!(lr || tb)) {
                return true;
            }
        }
        return false;
    }

    public void drawRoom() {
        // draw four walls
        TETile wall = Tileset.WALL;
        int xPos = p.getX(), yPos = p.getY();

        // bottom wall
        drawRow(world, xPos, xPos + width + 1, yPos, 0, wall);
        // up wall
        drawRow(world, xPos, xPos + width + 1,
                yPos + height + 1, 0, wall);
        // left wall
        drawRow(world, yPos, yPos + height + 1, xPos, 1, wall);
        // right wall
        drawRow(world, yPos, yPos + height + 1,
                xPos + width + 1, 1, wall);

        TETile floor = Tileset.FLOOR;
        for (int i = xPos + 1; i < xPos + width + 1; i++) {
            for (int j = yPos + 1; j < yPos + height + 1; j++) {
                world[i][j] = floor;
            }
        }
        for (Door d : doors) {
            d.drawDoor();
        }
    }


    private void drawRow(TETile[][] w, int start, int end,
                         int r, int col, TETile t) {
        // draw column if col = 1
        for (int i = start; i <= end; i++) {
            if (col == 1) {
                w[r][i] = t;
            } else {
                w[i][r] = t;
            }
        }
    }

    public boolean isLargeRoom() {
        return width > LARGEROOM && height > LARGEROOM;
    }

    public boolean isOutOfBound() {
        boolean tb = p.getY() + height + 1 >= Engine.HEIGHT || p.getY() < 3;
        boolean lr = p.getX() + width + 1 >= Engine.WIDTH || p.getX() < 0;
        return tb || lr;
    }

    public Position randomPosition() {
        int x = p.getX(), y = p.getY();
        int xPos = RandomUtils.uniform(seed, x + 1, x + width),
                yPos = RandomUtils.uniform(seed, y + 1, y + height);
        return new Position(xPos, yPos);
    }
}
