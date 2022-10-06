package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenerator {
    public static final int FLOWER = 4, KEYS = 3;
    int width, height;
    Room initialRoom;
    TETile[][] world, partialWorld;
    ArrayList<Room> rooms, largeRooms;
    Random seed;
    static Position doorPos;
    static Position initPlayer;
    static Position initGuard;


    public WorldGenerator(int w, int h, Random r) {
        this.width = w;
        this.height = h;
        this.seed = r;

        world = new TETile[w][h];
        partialWorld = new TETile[w][h];
        drawBackground(world); drawBackground(partialWorld);

        rooms = new ArrayList<>();
        largeRooms = new ArrayList<>();

        generateWorld();
    }

    private void drawBackground(TETile[][] w) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                w[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void generateWorld() {
        int initX = RandomUtils.uniform(seed, 0, width / 2);
        int initY = RandomUtils.uniform(seed, 3, height / 2);
        Position p = new Position(initX, initY);
        initialRoom = new Room(p, seed.nextLong(), world);
        rooms.add(initialRoom);
        largeRooms.add(initialRoom);

        addRooms();
        addPlayer();

        addLockedDoor(width);
        addGuard();
        addKeys(); addFlowers();
    }

    public void addRooms() {
        ArrayList<Room> roomsToAdd = new ArrayList<>();
        roomsToAdd.add(initialRoom);
        while (!roomsToAdd.isEmpty()) {
            Room curr = roomsToAdd.remove(0);
            curr.generateNeighbors(rooms);
            for (Room other : curr.neighbors) {
                if (!rooms.contains(other)) {
                    if (other.isLargeRoom()) {
                        largeRooms.add(other);
                    }
                    rooms.add(other);
                    roomsToAdd.add(other);
                }
            }
        }
        for (Room r : rooms) {
            r.drawRoom();
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public TETile[][] getPartialWorld() {
        return partialWorld;
    }


    public void addPlayer() {
        int i = RandomUtils.uniform(seed, 0, largeRooms.size() - 1);
        Room r = largeRooms.get(i);
        initPlayer = r.randomPosition();
    }

    public void addGuard() {
        initGuard = new Position(doorPos.getX() - 1, doorPos.getY());
    }

    public void addFlowers() {
        for (Room r : largeRooms) {
            int nFl = RandomUtils.uniform(seed, 1, FLOWER);
            for (int i = 0; i < nFl; i++) {
                Position pos = r.randomPosition();
                int x = pos.getX(), y = pos.getY();
                if (!pos.equals(initPlayer)
                        && !world[x][y].equals(Tileset.KEY)
                        && !pos.equals(initGuard)) {
                    world[x][y] = Tileset.FLOWER;
                }
            }
        }
    }

    public void addKeys() {
        int i = 0;
        while (i < KEYS) {
            int j = RandomUtils.uniform(seed, 0, largeRooms.size() - 1);
            Room r = largeRooms.get(j);
            Position pos = r.randomPosition();
            if (!pos.equals(initPlayer)
                    && !pos.equals(initGuard)) {
                world[pos.getX()][pos.getY()] = Tileset.KEY;
                i += 1;
            }
        }
    }

    // add a locked door to the leftmost of the world
    public void addLockedDoor(int w) {
        int j = -1;
        for (int i = 0; i < height - 1; i++) {
            if (world[w - 1][i].equals(Tileset.WALL)
                    && world[w - 2][i].equals(Tileset.FLOOR)) {
                j = i;
                break;
            }
        }
        if (j != -1) {
            world[w - 1][j] = Tileset.LOCKED_DOOR;
            doorPos = new Position(w - 1, j);
        } else {
            addLockedDoor(w - 1);
        }
    }
}
