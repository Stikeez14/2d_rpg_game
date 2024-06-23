package map;

import frame.Panel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class generateMap {

    Panel gamePanel;
    Tile[] tiles;
    int[][] mapTileArray;

    public generateMap(Panel gamePanel) {
        this.gamePanel=gamePanel;
        tiles = new Tile[11];
        mapTileArray = new int[mapSettings.getMaxTilesHorizontally()][mapSettings.getMaxTilesVertically()];
        getTileImage();
        loadMap(File.separator + "map" + File.separator + "mapData.txt");
    }

    public void getTileImage() {
        try{
            tiles [0] = new Tile();
            tiles [0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sand1.png")));

            tiles [1] = new Tile();
            tiles [1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sand2.png")));

            tiles [2] = new Tile();
            tiles [2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandPaddle1.png")));

            tiles [3] = new Tile();
            tiles [3].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandPaddle2.png")));

            tiles [4] = new Tile();
            tiles [4].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandPaddle3.png")));

            tiles [5] = new Tile();
            tiles [5].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandTree1.png")));

            tiles [6] = new Tile();
            tiles [6].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandTree2.png")));

            tiles [7] = new Tile();
            tiles [7].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandTree3.png")));

            tiles [8] = new Tile();
            tiles [8].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandTree4.png")));

            tiles [9] = new Tile();
            tiles [9].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandRock1.png")));

            tiles [10] = new Tile();
            tiles [10].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sandRock2.png")));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load map tiles!", e);
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < mapSettings.getMaxTilesHorizontally() && worldRow < mapSettings.getMaxTilesVertically()){

            int tileNum = mapTileArray[worldCol][worldRow];
            int worldX = worldCol * mapSettings.getTileSize();
            int worldY = worldRow * mapSettings.getTileSize();
            int screenX = worldX - gamePanel.player.x + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.y + gamePanel.player.screenY;

            if(worldX + 2 * mapSettings.getTileSize() > gamePanel.player.x - gamePanel.player.screenX
                    && worldX - 2 * mapSettings.getTileSize() < gamePanel.player.x + gamePanel.player.screenX
                    && worldY + 2 * mapSettings.getTileSize() > gamePanel.player.y - gamePanel.player.screenY
                    && worldY - 2 * mapSettings.getTileSize() < gamePanel.player.y + gamePanel.player.screenY)
                g2.drawImage(tiles[tileNum].image, screenX, screenY , mapSettings.getTileSize(), mapSettings.getTileSize(), null);
            worldCol++;

            if (worldCol == mapSettings.getMaxTilesHorizontally()){
                worldCol = 0;
                worldRow ++;
            }
        }
    }

    public void loadMap(String mapDataPath) {
        try {
            InputStream is = getClass().getResourceAsStream(mapDataPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            int col = 0;
            int row = 0;

            while (col < mapSettings.getMaxTilesHorizontally() && row < mapSettings.getMaxTilesVertically()){
                String line = br.readLine();
                while(col < mapSettings.getMaxTilesHorizontally()) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileArray[col][row]=num;
                    col++;
                }
                if(col == mapSettings.getMaxTilesHorizontally()){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e){
            throw new RuntimeException("Failed to load map data!", e);
        }
    }
}
