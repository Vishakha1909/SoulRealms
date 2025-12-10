package game.core.world;

public class Tile {
    private TileCategory category;

    public Tile(TileCategory category) {
        this.category = category;
    }

    public TileCategory getCategory() { return category; }

    public void setCategory(TileCategory category) {
        this.category = category;
    }

    public boolean isAccessible() {
        return category != TileCategory.BLOCKED;
    }

    public boolean isMarket() {
        return category == TileCategory.MARKET;
    }

    @Override
    public String toString() {
        String cat = "";
         switch (category) {
            case COMMON: cat = ".";
            case MARKET: cat = "M";
            case BLOCKED: cat = "X";
            case SPECIAL: cat = "*";
        };
        return cat;
    }
}
