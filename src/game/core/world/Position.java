package game.core.world;

public class Position {
    public final int row;
    public final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position other = (Position) o;
        return this.row == other.row && this.col == other.col;
    }

    public Position up()    { return new Position(row - 1, col); }
    public Position down()  { return new Position(row + 1, col); }
    public Position left()  { return new Position(row, col - 1); }
    public Position right() { return new Position(row, col + 1); }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
