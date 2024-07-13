package entities;

import frame.Panel;
import map.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {

    // sprites for diff states & directions of the entity
    protected BufferedImage standing, walkDown1, walkDown2, walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2;

    private int originalX, originalY; // x & y coordinates not adjusted by scaling

    int x;
    int y; // coordinates that are adjusted by scaling

    private final int screenX, screenY; // screen center coordinates

    int width, height; // tile dimensions

    protected Rectangle hitbox; // entity hitbox for checking collision
    protected String direction; // direction the entity is moving towards (based on keyboard keys)

    protected int Speed; // entity movements speed (affected by scaling)

    boolean collisionUp, collisionDown;
    boolean collisionLeft, collisionRight; // flags to indicate collision

    protected int spriteCounter = 0; // counter and flag for sprite animation
    protected int spriteFlag = 1;

    boolean drawHitbox = false; // flag used to draw the entity hitbox on screen

    Panel gamePanel;

    Entity(int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;
        this.originalX=x;
        this.originalY=y;
        this.gamePanel = gamePanel;

        width = Settings.getTileSize(); // get tile dimensions from Settings class
        height = Settings.getTileSize();

        screenX = frame.Window.getScreenWidth() / 2 - width / 2; // initialize screen center coordinates
        screenY = frame.Window.getScreenHeight() / 2 - height / 2;
        // initialize entity hitbox
        hitbox = new Rectangle((int) (width / 3.25), (int) (height / 1.75), 11 * Settings.getScale(), 13 * Settings.getScale());
    }

    /** MOVE ENTITY BASED ON DIRECTION & COLLISION DETECTION */
    protected void moveEntity() {
        // collision detection & movement { -> x & y are used for moving the entity on the map
                                       // { -> originalX & original Y are used for drawing entity position on the map
        if (direction.contains("up") && !collisionUp) {
            y -= Speed;
            originalY -= Speed;
        }
        if (direction.contains("down") && !collisionDown) {
            y += Speed;
            originalY += Speed;
        }
        if (direction.contains("left") && !collisionLeft) {
            x -= Speed;
            originalX -= Speed;
        }
        if (direction.contains("right") && !collisionRight) {
            x += Speed;
            originalX += Speed;
        }
    }

    /* gets the sprite of the entity based on the direction it's moving */
    BufferedImage getCurrentSprite() {
        return switch (direction) {
            case "down", "down&left", "down&right" -> (spriteFlag == 1) ? walkDown1 : walkDown2;
            case "up", "up&left", "up&right" -> (spriteFlag == 1) ? walkUp1 : walkUp2;
            case "left" -> (spriteFlag == 1) ? walkLeft1 : walkLeft2;
            case "right" -> (spriteFlag == 1) ? walkRight1 : walkRight2;
            default -> standing;
        };
    }

    /** HITBOX & POSITION UPDATE */
    /* updates hitbox position based on scale */
    public void updateHitbox() {
        width = Settings.getTileSize();
        height = Settings.getTileSize();
        hitbox = new Rectangle((int) (width / 3.25), (int) (height / 1.75), 11 * Settings.getScale(), 13 * Settings.getScale());
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
    /* entity hitbox affected by scaling */
    public Rectangle getHitbox() { return hitbox; }


    /** DRAW HITBOX */
    /* draws entity hitbox for testing collision */
    void drawEntityHitbox(Graphics2D g2){
        g2.setColor(new Color(255, 0, 0, 100)); // semi-transparent red collision area
        g2.fillRect(getEntityScreenX() + hitbox.x, getEntityScreenY() + hitbox.y, hitbox.width, hitbox.height);
        g2.setColor(Color.RED); // hitbox  drawn with red
        g2.drawRect(getEntityScreenX() + hitbox.x, getEntityScreenY() + hitbox.y, hitbox.width, hitbox.height);
    }

    /* sets hitbox drawing status */
    protected void setDrawHitboxStatus(boolean status){ drawHitbox = status; }

    /** ABSTRACT METHODS */
    protected abstract void setEntity();
    protected abstract void loadEntityVisuals();
    public abstract void draw(Graphics2D g2);
}
