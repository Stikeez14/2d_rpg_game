package map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tile {

    public BufferedImage image;
    public boolean collision;
    public List<Rectangle> collisionAreas;

    public Tile() {
        collision = false;
        collisionAreas = new ArrayList<>();
    }
}
