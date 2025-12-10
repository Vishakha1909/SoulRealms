package game.core.world;

public class World {
    private Tile[][] grid;
    private Position partyPosition;

    public World(Tile[][] grid, Position start) {
        this.grid = grid;
        this.partyPosition = start;
    }

    public Tile getTile(Position p) {
        if (p.row < 0 || p.row >= grid.length ||
            p.col < 0 || p.col >= grid[0].length) {
            // Treat out-of-bounds as blocked
            return new Tile(TileCategory.BLOCKED);
        }
        return grid[p.row][p.col];
    }

    public boolean moveTo(Position p) {
        Tile t = getTile(p);
        if (!t.isAccessible()) {
            System.out.println("You cannot move there.");
            return false;
        }
        this.partyPosition = p;
        return true;
    }

    public Position getPartyPosition() {
        return partyPosition;
    }

    public int getRows() {
        return grid.length;
    }

    public int getCols() {
        return grid[0].length;
    }

    public void print() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (partyPosition.row == r && partyPosition.col == c) {
                    System.out.print("P ");
                } else {
                    System.out.print(grid[r][c].toString() + " ");
                }
            }
            System.out.println();
        }
    }

    
}
