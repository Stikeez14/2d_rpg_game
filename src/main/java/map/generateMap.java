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
        tiles = new Tile[2];
        mapTileArray = new int[mapSettings.getTilesHorizontally()][mapSettings.getTilesVertically()];
        getTileImage();
        loadMap(File.separator + "map" + File.separator + "mapData.txt");
    }

    public void getTileImage() {
        try{
            tiles [0] = new Tile();
            tiles [0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sand1.png")));

            tiles [1] = new Tile();
            tiles [1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "tiles" + File.separator + "sand2.png")));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load map tiles!", e);
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < mapSettings.getTilesHorizontally() && row < mapSettings.getTilesVertically()){
            int tileNum = mapTileArray[col][row];
            g2.drawImage(tiles[tileNum].image, x, y , mapSettings.getTileSize(), mapSettings.getTileSize(), null);
            col++;
            x += mapSettings.getTileSize();
            if (col == mapSettings.getTilesHorizontally()){
                col = 0;
                x = 0;
                row ++;
                y += mapSettings.getTileSize();
            }
        }
    }

    public void loadMap(String mapDataPath) {
        try {
            InputStream is = getClass().getResourceAsStream(mapDataPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            int col = 0;
            int row = 0;

            while (col < mapSettings.getTilesHorizontally() && row < mapSettings.getTilesVertically()){
                String line = br.readLine();
                while(col < mapSettings.getTilesHorizontally()) {
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileArray[col][row]=num;
                    col++;
                }
                if(col == mapSettings.getTilesHorizontally()){
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
