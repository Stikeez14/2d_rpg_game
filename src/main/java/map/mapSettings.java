package map;

import frame.Window;

public class mapSettings {

    private static final int tileSize = 32;
    private static final int scale = 4;
    private static final int finalTileSize = tileSize * scale;

    private static final int tilesVertically = Window.getScreenHeight() / finalTileSize + 1;
    private static final int tilesHorizontally = Window.getScreenWidth() / finalTileSize;

    public static int getTilesVertically() {
        return tilesVertically;
    }

    public static int getTilesHorizontally() {
        return tilesHorizontally;
    }

    public static int getTileSize() {
        return finalTileSize;
    }
}
