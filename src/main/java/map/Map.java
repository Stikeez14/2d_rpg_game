package map;

import entities.Entity;
import frame.Panel;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Map {

    Panel gamePanel;
    private final Tile[] tiles; // array that contains tile numbers from the map
    private final int[][] mapTileMatrix; // array representing the map

    private boolean drawCollision = false; // flag to determine if collision debug info should be written on screen

    private static int scale = Settings.getScale(); // gets the scale from Settings

    public Map(Panel gamePanel) {
        this.gamePanel=gamePanel;
        tiles = new Tile[12];
        mapTileMatrix = new int[Settings.getMaxTilesHorizontally()][Settings.getMaxTilesVertically()];
        getTileImage();  // load images for tiles
        loadMap(DataMatrix.mapDataPath); // load map from mapData file
    }

    /** initialises images used for tiles */
    private void getTileImage() {
        try{
            String basePath = File.separator + "tiles" + File.separator;

            /* SAND TILES */
            tiles[0] = new Tile();
            tiles[0].setTileImage(File.separator + "tiles" + File.separator + "sand1.png");

            tiles[1] = new Tile();
            tiles[1].setTileImage(File.separator + "tiles" + File.separator + "sand2.png");

            /* SAND & PEBBLES TILES */
            for (int i = 2; i <= 4; i++) {
                tiles[i] = new Tile();
                tiles[i].setTileImage(basePath + "sandPaddle" + (i - 1) + ".png");
            }

            /* TREE TILES */
            for (int i = 5; i <= 8; i++) {
                tiles[i] = new Tile();
                tiles[i].setTileImage(basePath + "sandTree" + (i - 4) + ".png");
                tiles[i].setUpperTileImage(basePath + "sandTreeUpper" + (i - 4) + ".png");
                tiles[i].setCollisionArea(scale, 3, 2, 10, 12);
                tiles[i].setTileCollision(true);
            }

            /* ROCK TILES */
            for (int i = 9; i <= 10; i++) {
                tiles[i] = new Tile();
                tiles[i].setTileImage(basePath + "sandRock" + (i - 8) + ".png");
                tiles[i].setCollisionArea(scale, 7, 2, 22, 10);
                tiles[i].setTileCollision(true);
            }

            /* BORDER TILES */
            tiles[11] = new Tile();
            tiles[11].setTileImage(basePath + "sand1.png");
            tiles[11].setCollisionArea(scale, 0, 0, 25, 25);
            tiles[11].setTileCollision(true);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load map tiles!", e);
        }
    }

    /** draws in the game loop the map tiles & the player */
    public void draw(Graphics2D g2) {
        int tileSize = Settings.getTileSize();
        int scale = Settings.getScale();
        int maxTilesHorizontally = Settings.getMaxTilesHorizontally();
        int maxTilesVertically = Settings.getMaxTilesVertically();

        // player world coordinates & screen coordinates
        int playerX = gamePanel.player.getEntityX();
        int playerY = gamePanel.player.getEntityY();
        int playerScreenX = gamePanel.player.getEntityScreenX();
        int playerScreenY = gamePanel.player.getEntityScreenY();

        // range of tiles in player view to iterate
        int screenTileCols = gamePanel.getWidth() / tileSize + 2;
        int screenTileRows = gamePanel.getHeight() / tileSize + 2;

        int startCol = Math.max(0, (playerX - playerScreenX) / tileSize - screenTileCols / 2);
        int endCol = Math.min(maxTilesHorizontally, (playerX + playerScreenX) / tileSize + screenTileCols / 2 + 1);
        int startRow = Math.max(0, (playerY - playerScreenY) / tileSize - screenTileRows / 2);
        int endRow = Math.min(maxTilesVertically, (playerY + playerScreenY) / tileSize + screenTileRows / 2 + 1);

        // tile drawing and collision area update
        for (int worldRow = startRow; worldRow < endRow; worldRow++) {
            for (int worldCol = startCol; worldCol < endCol; worldCol++) {
                int tileNum = mapTileMatrix[worldCol][worldRow]; // tile number of current position
                int worldX = worldCol * tileSize; // world x & y position of tile
                int worldY = worldRow * tileSize;
                int screenX = worldX - playerX + playerScreenX; // x & y position on screen for tile
                int screenY = worldY - playerY + playerScreenY;

                // draw tile
                g2.drawImage(tiles[tileNum].tileImage, screenX, screenY, tileSize, tileSize, null);

                if (tiles[tileNum].getTileCollision()) { // check if tile has collision area
                    tiles[tileNum].updateCollisionArea(scale); // update collision areas for all tiles
                    // draw tile collision box if the tile has collision
                    if (drawCollision) drawCollisionAreas(g2, tileNum, screenX, screenY);
                }
            }
        }

        // get all entities and sort them by y coordinate
        List<Entity> entities = gamePanel.getEntities();
        entities.sort(Comparator.comparingInt(Entity::getEntityY));

        int viewWidth = gamePanel.getWidth();
        int viewHeight = gamePanel.getHeight();

        for (Entity entity : entities) {
            int entityX = entity.getEntityX();
            int entityY = entity.getEntityY();
            int entityScreenX = entityX - playerX + playerScreenX;
            int entityScreenY = entityY - playerY + playerScreenY;
            int entityBottomY = entityY + entity.getHitbox().height;

            // entity drawn only in player view range
            if (entityScreenX + entity.getHitbox().width > -tileSize * 2 && entityScreenX < viewWidth + tileSize * 2 &&
                    entityScreenY + entity.getHitbox().height > -tileSize * 2 && entityScreenY < viewHeight + tileSize * 2) {

                entity.draw(g2); // draw entity

                // check tiles around the entity to draw upper parts
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        int tileCol = (entityX / tileSize) + offsetX;
                        int tileRow = (entityY / tileSize) + offsetY;

                        if (tileCol >= 0 && tileCol < maxTilesHorizontally && tileRow >= 0 && tileRow < maxTilesVertically) {
                            int tileNum = mapTileMatrix[tileCol][tileRow];
                            int worldX = tileCol * tileSize;
                            int worldY = tileRow * tileSize;
                            int screenX = worldX - playerX + playerScreenX;
                            int screenY = worldY - playerY + playerScreenY;
                            int tileMiddleY = worldY + tileSize / 2;

                            // draw upper part of the tile if the entity is in the correct position
                            if (tiles[tileNum].upperTileImage != null && entityBottomY <= tileMiddleY) {
                                g2.drawImage(tiles[tileNum].upperTileImage, screenX, screenY, tileSize, tileSize, null);
                                if (drawCollision) drawCollisionAreas(g2, tileNum, screenX, screenY);
                            }
                        }
                    }
                }
            }
        }
    }

    /** reads the random generated map from the mapData file */
    private void loadMap(String mapDataPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(mapDataPath))) {
            int maxCols = Settings.getMaxTilesHorizontally(); // get the max number of rows and cols from Settings
            int maxRows = Settings.getMaxTilesVertically();

            for (int row = 0; row < maxRows; row++) { // iterating through each row
                String line = br.readLine(); // read the next line

                // throw exception if a line is null
                if (line == null) throw new RuntimeException("Not enough lines in the file");

                String[] numbers = line.split(" "); // split line into individual string numbers

                // throw exception if the numbers of cols in a line diff than maxCols
                if (numbers.length != maxCols) throw new RuntimeException("Incorrect number of columns in row " + row);

                for (int col = 0; col < maxCols; col++) { // iterate through each col in a row
                    try {
                        int num = Integer.parseInt(numbers[col]); // convert string to int
                        mapTileMatrix[col][row] = num; // set tile value in the matrix
                    } catch (NumberFormatException e) { // throw exception if conversion fails
                        throw new RuntimeException("Invalid number format at (" + col + ", " + row + ")", e);
                    }
                }
            }
        } catch (IOException e) { // throw exception if errors occur during reading the file
            throw new RuntimeException("Failed to load map data!", e);
        }
    }

    /** GET METHODS */
    public Tile[] getTile(){ return tiles; }
    public int[][] getMapTileMatrix(){ return mapTileMatrix;}

    /** SET METHODS */
    public static void setScale(int value){ scale = value; }


    /** DRAWING TILE COLLISION METHODS */
    /* draws on the map the collision area for the tiles */
    private void drawCollisionAreas(Graphics2D g2, int tileNum, int screenX, int screenY) {
        g2.setColor(new Color(0, 255, 0, 100)); // semi-transparent green collision area
        for (Rectangle rect : tiles[tileNum].getCollisionArea()) {
            g2.fillRect(screenX + rect.x, screenY + rect.y, rect.width, rect.height);
            g2.setColor(Color.GREEN);
            g2.drawRect(screenX + rect.x, screenY + rect.y, rect.width, rect.height);
        }
    }
    /* takes flag input when a key is pressed in Player class */
    public void setDrawCollisionStatus(boolean status){
        drawCollision = status;
    }
}
