package map;

public class Settings {

    /* General Map SETTINGS */
    private static final int DEFAULT_TILE_SIZE = 32; // base size for tiles
    private static int scale = 6; // default scale
    private static int finalTileSize = DEFAULT_TILE_SIZE * scale;

    /* Worlds Map SETTINGS */
    private static final int maxTilesVertically = 500;
    private static final int maxTilesHorizontally = 500;

    /* get & set methods */
    public static int getMaxTilesVertically() { return maxTilesVertically; }

    public static int getMaxTilesHorizontally() { return maxTilesHorizontally; }

    public static int getTileSize() { return finalTileSize; }

    public static int getScale() {
        if(scale < 2 || scale > 10) throw new RuntimeException("Invalid scale value!");
        return scale;
    }

    public static void setSprintScale() { scale = 5; updateFinalTileSize(); }

    public static void setWalkScale() { scale = 6; updateFinalTileSize(); }

    private static void updateFinalTileSize() { finalTileSize = DEFAULT_TILE_SIZE * scale; }
}
