package map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tile {

    protected BufferedImage tileImage;
    protected BufferedImage upperTileImage;

    private boolean collision;
    private final List<Rectangle> collisionAreas;

    private int collisionRectX; private int collisionRectY; //co
    private int collisionRectW; private int collisionRectH;

    protected Tile() {
        collision = false;
        collisionAreas = new ArrayList<>();
    }

    /* loads from files the tile image */
    protected void setTileImage(String filePath) throws IOException {
        tileImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(filePath)));
    }

    /* loads from files the image drawn over tile */
    protected void setUpperTileImage(String filePath) throws IOException {
        upperTileImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(filePath)));
    }

    /* sets the initial collision area for tiles based on scale */
    protected void setCollisionArea(int scale, int x, int y, int w, int h) {
        collisionRectX = x; collisionRectY = y; collisionRectW = w; collisionRectH = h;
        int width = Settings.getTileSize();
        int height = Settings.getTileSize();

        int rectangleX, rectangleY;
        if (x == 0 && y == 0) { rectangleX = 0; rectangleY = 0; }
        else if (x == 0) { rectangleX = 0; rectangleY = height/y; }
        else if (y == 0) { rectangleX = width/x; rectangleY = 0; }
        else { rectangleX = width/x; rectangleY = height/y; }

        collisionAreas.add(new Rectangle(rectangleX, rectangleY, w * scale, h * scale));
    }

    /* updates the collision area for tiles based on scale */
    protected void updateCollisionArea(int scale) {
        int width = Settings.getTileSize();
        int height = Settings.getTileSize();
        collisionAreas.clear();

        int rectangleX, rectangleY;
        if (collisionRectX == 0 && collisionRectY == 0) { rectangleX = 0; rectangleY = 0; }
        else if(collisionRectX == 0) { rectangleX = 0; rectangleY = height/collisionRectY; }
        else if (collisionRectY == 0) { rectangleX = width/collisionRectX; rectangleY = 0; }
        else { rectangleX = width/collisionRectX; rectangleY = height/collisionRectY; }
        collisionAreas.add(new Rectangle(rectangleX, rectangleY, collisionRectW * scale, collisionRectH * scale));
    }

    /* returns collision area for a tile */
    public List<Rectangle> getCollisionArea(){ return collisionAreas; }

    /* set & get methods for tile collision */
    public boolean getTileCollision(){ return collision; }
    public void setTileCollision(boolean status) { collision = status; }
}
