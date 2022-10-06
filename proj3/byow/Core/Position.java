package byow.Core;

public class Position {
    int x, y;

    public Position() {
    }
    public Position(int x, int y) {
        this.x = x; this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void update(int x, int y) {
        this.x = x; this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Position o = (Position) other;
        return o.getX() == x && o.getY() == y;
    }

    @Override
    public int hashCode() {
        return (int) Math.pow(Double.hashCode(x), Double.hashCode(y));
    }
}