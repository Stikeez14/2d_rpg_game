package map;

import frame.Panel;

import java.awt.*;
import java.io.*;

public class Map {

    Panel gamePanel;
    private final Tile[] tiles; // array that contains tile numbers from the map
    private final int[][] mapTileMatrix; // array representing the map

    private static int scale = Settings.getScale();

    public Map(Panel gamePanel) {
        this.gamePanel=gamePanel;
        tiles = new Tile[12];
        mapTileMatrix = new int[Settings.getMaxTilesHorizontally()][Settings.getMaxTilesVertically()];
        getTileImage();  // load images for tiles
        loadMap(DataMatrix.mapDataPath); // load map from mapData file
    }

    /* initialises images used for tiles */
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

    /* draws in the game loop the map tiles & the player */
    public void draw(Graphics2D g2) {
        int tileSize = Settings.getTileSize();
        int scale = Settings.getScale();
        int maxTilesHorizontally = Settings.getMaxTilesHorizontally();
        int maxTilesVertically = Settings.getMaxTilesVertically();

        int playerX = gamePanel.player.getEntityX();
        int playerY = gamePanel.player.getEntityY();
        int playerScreenX = gamePanel.player.getEntityScreenX();
        int playerScreenY = gamePanel.player.getEntityScreenY();
        int playerBottomY = playerY + gamePanel.player.getHitbox().height;

        // tile drawing and collision area update
        for (int worldRow = 0; worldRow < maxTilesVertically; worldRow++) {
            for (int worldCol = 0; worldCol < maxTilesHorizontally; worldCol++) {
                int tileNum = mapTileMatrix[worldCol][worldRow];
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                int screenX = worldX - playerX + playerScreenX;
                int screenY = worldY - playerY + playerScreenY;

                if (isTileInPlayerView(worldX, worldY, tileSize, playerX, playerY, playerScreenX, playerScreenY)) {
                    g2.drawImage(tiles[tileNum].tileImage, screenX, screenY, tileSize, tileSize, null);

                    if (tiles[tileNum].getTileCollision()) { // check if tile has collision area
                        tiles[tileNum].updateCollisionArea(scale); // update collision areas for all tiles

                        /* DRAWING RECTANGLE COLLISION FOR TESTING */
                        // draw tile collision box if the tile has collision
                        g2.setColor(new Color(0, 255, 0, 100)); // semi-transparent green collision area
                        for (Rectangle rect : tiles[tileNum].getCollisionArea()) {
                            g2.fillRect(screenX + rect.x, screenY + rect.y, rect.width, rect.height);
                            g2.setColor(Color.GREEN);
                            g2.drawRect(screenX + rect.x, screenY + rect.y, rect.width, rect.height);
                        }
                    }
                }
            }
        }

        // player & upper part of tiles drawing
        boolean playerDrawn = false;
        for (int worldRow = 0; worldRow < maxTilesVertically; worldRow++) {
            for (int worldCol = 0; worldCol < maxTilesHorizontally; worldCol++) {
                int tileNum = mapTileMatrix[worldCol][worldRow];
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                int screenX = worldX - playerX + playerScreenX;
                int screenY = worldY - playerY + playerScreenY;

                if (isTileInPlayerView(worldX, worldY, tileSize, playerX, playerY, playerScreenX, playerScreenY)) {
                    if (tiles[tileNum].upperTileImage != null) {
                        int tileMiddleY = worldY + tileSize / 2;

                        if (playerBottomY <= tileMiddleY) {
                            g2.drawImage(tiles[tileNum].upperTileImage, screenX, screenY, tileSize, tileSize, null);
                        } else if (!playerDrawn) {
                            gamePanel.player.draw(g2);
                            playerDrawn = true;
                        }
                    }
                }
            }
        }
    }

    /* checks if the tile is in the player view area */
    private boolean isTileInPlayerView(int worldX, int worldY, int tileSize, int playerX, int playerY, int playerScreenX, int playerScreenY) {
        return worldX + 2 * tileSize > playerX - playerScreenX
                && worldX - 2 * tileSize < playerX + playerScreenX
                && worldY + 2 * tileSize > playerY - playerScreenY
                && worldY - 2 * tileSize < playerY + playerScreenY;
    }

    /* reads the random generated map from the mapData file */
    private void loadMap(String mapDataPath) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(mapDataPath));

            int col = 0;
            int row = 0;

            //reads line by line the file
            while (col < Settings.getMaxTilesHorizontally() && row < Settings.getMaxTilesVertically()){
                String line = br.readLine();
                while(col < Settings.getMaxTilesHorizontally()) {
                    String[] numbers = line.split(" "); //split the lines into individual numbers
                    int num = Integer.parseInt(numbers[col]); // & convert string to int
                    mapTileMatrix[col][row]=num; // set the tile value in the map matrix
                    col++;
                }
                if(col == Settings.getMaxTilesHorizontally()){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e){
            throw new RuntimeException("Failed to load map data!", e);
        }
    }

    public Tile[] getTile(){ return tiles; }
    public int[][] getMapTileMatrix(){ return mapTileMatrix;}

    public static void setScale(int value){ scale = value; }
}
