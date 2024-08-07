package entities;

import frame.Panel;
import map.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class Entity {

    // sprites for diff states & directions of the entity
    protected BufferedImage standing, walkDown1, walkDown2, walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2;

    // sprites for weapons directions
    protected BufferedImage standingBat, walkLeft1Bat, walkLeft2Bat, walkRight1Bat, walkRight2Bat, walkUp1Bat, walkUp2Bat, walkDown1Bat;
    protected BufferedImage standingDag, walkLeft1Dag, walkLeft2Dag, walkRight1Dag, walkRight2Dag, walkUp1Dag, walkUp2Dag, walkDown1Dag;

    private int originalX, originalY; // x & y coordinates not adjusted by scaling

    int x;
    int y; // coordinates that are adjusted by scaling

    private final int screenX, screenY; // screen center coordinates

    int width, height; // tile dimensions

    protected Rectangle collisionBox; // entity collisionBox for checking collision
    protected Rectangle verticalHitBox;
    protected Rectangle horizontalHitBox;

    protected Rectangle attackBox;

    protected String direction; // direction the entity is moving towards (based on keyboard keys)

    protected int Speed; // entity movements speed (affected by scaling)

    boolean collisionUp, collisionDown;
    boolean collisionLeft, collisionRight; // flags to indicate collision

    protected int spriteCounter = 0; // counter and flag for sprite animation
    protected int spriteFlag = 1;

    boolean drawBoxes = false; // flag used to draw the entity collisionBox on screen

    Panel gamePanel; // reference to Panel

    protected boolean isAttacking = false; // flag activated when the entity is attacking

    public double health; // entity health points

    Entity (int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;
        this.originalX=x;
        this.originalY=y;
        this.gamePanel = gamePanel;

        width = Settings.getTileSize(); // get tile dimensions from Settings class
        height = Settings.getTileSize();

        screenX = frame.Window.getScreenWidth() / 2 - width / 2; // initialize screen center coordinates
        screenY = frame.Window.getScreenHeight() / 2 - height / 2;

        // entity boxes
        collisionBox = new Rectangle((int) (width / 3.25), (int) (height / 1.75), 11 * Settings.getScale(), 13 * Settings.getScale());
        verticalHitBox = new Rectangle((int) (width / 3.25), 2 * Settings.getScale(), 11 * Settings.getScale(), 30 * Settings.getScale());
        horizontalHitBox = new Rectangle((int) (width / 4.10), (int) (height / 2.75), 16 * Settings.getScale(), 10 * Settings.getScale());

        loadEntityWeapons();
    }

    /** MOVE ENTITY BASED ON DIRECTION & COLLISION DETECTION */
    protected void moveEntity() {
        // { -> x & y are used for moving the entity on the map
        // { -> originalX & original Y are used for drawing entity position on the map
        int currentSpeed = isAttacking ? 1 : Speed; // while attacking speed is reduced

        // Collision detection & movement
        if (direction.contains("Up") && !collisionUp) {
            y -= currentSpeed;
            originalY -= currentSpeed;
        }
        if (direction.contains("Down") && !collisionDown) {
            y += currentSpeed;
            originalY += currentSpeed;
        }
        if (direction.contains("Left") && !collisionLeft) {
            x -= currentSpeed;
            originalX -= currentSpeed;
        }
        if (direction.contains("Right") && !collisionRight) {
            x += currentSpeed;
            originalX += currentSpeed;
        }
    }

    /* gets the sprite of the entity based on the direction it's moving */
    BufferedImage getCurrentSprite() {

        if(!isAttacking){
            return switch (direction) {
                case "Down", "Down&Left", "Down&Right" -> (spriteFlag == 1) ? walkDown1 : walkDown2;
                case "Up", "Up&Left", "Up&Right" -> (spriteFlag == 1) ? walkUp1 : walkUp2;
                case "Left" -> (spriteFlag == 1) ? walkLeft1 : walkLeft2;
                case "Right" -> (spriteFlag == 1) ? walkRight1 : walkRight2;
                default -> standing;
            };
        }
        else return standing;
    }

    /** HITBOX & POSITION UPDATE */
    /* updates collisionBox position based on scale */
    public void updateHitbox() {
        width = Settings.getTileSize();
        height = Settings.getTileSize();
        collisionBox = new Rectangle((int) (width / 3.25), (int) (height / 1.75), 11 * Settings.getScale(), 13 * Settings.getScale());
        verticalHitBox = new Rectangle((int) (width / 3.25), Settings.getScale(), 11 * Settings.getScale(), 30 * Settings.getScale());
        horizontalHitBox = new Rectangle((int) (width / 4.10), (int) (height / 2.75), 16 * Settings.getScale(), 10 * Settings.getScale());
    }

    /* changes entity position in the right place when scaling changes */
    protected void adjustPosition (int oldScale, int newScale) {
        double scaleFactor = (double) newScale / oldScale;
        x = (int) (x * scaleFactor);
        y = (int) (y * scaleFactor);
    }

    /** COLLISION SET METHODS */
    public void setCollisionUp(boolean status){ collisionUp = status; }
    public void setCollisionDown(boolean status){ collisionDown = status; }
    public void setCollisionLeft(boolean status){ collisionLeft = status; }
    public void setCollisionRight(boolean status){ collisionRight = status; }

    /** GET METHODS */
    /* entity coordinates on the map */
    public int getEntityX(){ return x; }
    public int getEntityY(){ return y; }
    /* original x & y used only for printing the position of the entity on the map */
    public int getOriginalX(){ return originalX; }
    public int getOriginalY(){ return originalY; }
    /* screen x & y return the coordinates for the center of the screen */
    public int getEntityScreenX(){ return screenX; }
    public int getEntityScreenY(){ return screenY; }
    /* tile width & height */
    public int getEntityWidth(){ return width; }
    public int getEntityHeight(){ return height; }
    /* entity Speed affected by scaling */
    public int getSpeed() { return Speed; }
    /* direction of the entity */
    public String getDirection(){ return direction; }
    /* entity collision and hit boxes */
    public Rectangle getCollisionBox() { return collisionBox; }
    public Rectangle getVerticalHitBox() { return verticalHitBox; }
    public Rectangle getHorizontalHitBox() { return horizontalHitBox; }

    /** DRAW ENTITY BOX */
    void drawEntityBox(Graphics2D g2, Rectangle box, Color color){
        g2.setColor(color); // color for the rectangle box
        g2.drawRect(getEntityScreenX() + box.x, getEntityScreenY() + box.y, box.width, box.height);
    }

    /* sets collisionBox drawing status */
    protected void setDrawHitboxStatus(boolean status){ drawBoxes = status; }

    /** LOADS ALL WEAPONS & SPRITES */
    private void loadEntityWeapons() {

        // weapon paths
        String baseballBatPath = "equipment" + File.separator + "weapons" + File.separator + "baseballBat" + File.separator;
        String daggerPath = "equipment" + File.separator + "weapons" + File.separator + "dagger" + File.separator;

        try {
            standingBat = loadImage(baseballBatPath + "standing.png");
            walkLeft2Bat = loadImage(baseballBatPath + "walkLeft2.png");
            walkLeft1Bat = loadImage(baseballBatPath + "walkLeft1.png");
            walkRight2Bat = loadImage(baseballBatPath+ "walkRight2.png");
            walkRight1Bat = loadImage(baseballBatPath + "walkRight1.png");
            walkUp2Bat = loadImage(baseballBatPath + "walkUp2.png");
            walkUp1Bat = loadImage(baseballBatPath + "walkUp1.png");
            walkDown1Bat = loadImage(baseballBatPath + "walkDown1.png");

            standingDag= loadImage(daggerPath + "standing.png");
            walkLeft2Dag = loadImage(daggerPath + "walkLeft2.png");
            walkLeft1Dag = loadImage(daggerPath + "walkLeft1.png");
            walkRight2Dag = loadImage(daggerPath + "walkRight2.png");
            walkRight1Dag = loadImage(daggerPath + "walkRight1.png");
            walkUp2Dag = loadImage(daggerPath + "walkUp2.png");
            walkUp1Dag = loadImage(daggerPath + "walkUp1.png");
            walkDown1Dag = loadImage(daggerPath + "walkDown1.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Bandit Visuals!", e);
        }
    }

    /** HELPER METHOD FOR LOADING IMAGES */
    protected BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + path)));
    }

    /** CHECKS HEALTH STATUS OF THE ENTITY */
    protected void checkEntityHealth(Entity entity){
        if(health <= 0) gamePanel.removeEntity(entity); // if health reaches 0 will remove the entity from the list
    }

    /** ABSTRACT METHODS */
    public abstract void setEntity();
    protected abstract void loadEntityVisuals(String type);
    public abstract void draw(Graphics2D g2);

}
