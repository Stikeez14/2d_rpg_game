package map;

import frame.Window;

public class mapSettings {

    /* GENERAL MAP SETTINGS */
    private static final int tileSize = 32;
    private static final int scale = 4;
    private static final int finalTileSize = tileSize * scale;

    private static final int tilesVertically = Window.getScreenHeight() / finalTileSize + 1;
    private static final int tilesHorizontally = Window.getScreenWidth() / finalTileSize;

    /* WORLD MAP SETTINGS */
    private static final int maxTilesVertically = 200;
    private static final int maxTilesHorizontally = 200;
    private final int worldWidth = finalTileSize * maxTilesHorizontally;
    private final int worldHeight = finalTileSize * maxTilesVertically;

    public static int getTilesVertically() {
        return tilesVertically;
    }

    public static int getTilesHorizontally() {
        return tilesHorizontally;
    }

    public static int getMaxTilesVertically() {
        return maxTilesVertically;
    }

    public static int getMaxTilesHorizontally() {
        return maxTilesHorizontally;
    }

    public static int getTileSize() {
        return finalTileSize;
    }
}
