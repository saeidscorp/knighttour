package one.scorp.knighttour;

public class Point {
    public int x, y;

    public Point(int nx, int ny) {
        x = nx;
        y = ny;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Point)) return false;
        Point p = (Point) other;
        return p.x == x && p.y == y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
