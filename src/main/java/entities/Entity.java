package entities;

import collision.Collision;
import frame.Panel;
import map.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {

    protected BufferedImage standing, walkDown1, walkDown2, walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2;

    private int x;
    private int y;

    private final int screenX, screenY;
    private int width, height;

    protected Rectangle hitbox;
    protected String direction;

    protected int Speed;

    private boolean collisionUp;
    private boolean collisionDown;
    private boolean collisionLeft;
    private boolean collisionRight;

    protected int spriteCounter = 0;
    protected int spriteFlag = 1;

    Panel gamePanel;

    Entity(int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;
        this.gamePanel = gamePanel;

        width = Settings.getTileSize();
        height = Settings.getTileSize();

        screenX = frame.Window.getScreenWidth() / 2 - width / 2;
        screenY = frame.Window.getScreenHeight() / 2 - height / 2;

        hitbox = new Rectangle((int) (width / 3.5), (int) (height / 1.75), 13 * Settings.getScale(), 13 * Settings.getScale());
    }

    protected void moveEntity() {

        // collision detection & movement
        if (direction.contains("up") && !collisionUp) {
            y -= Speed;
        }
        if (direction.contains("down") && !collisionDown) {
            y += Speed;
        }
        if (direction.contains("left") && !collisionLeft) {
            x -= Speed;
        }
        if (direction.contains("right") && !collisionRight) {
            x += Speed;
        }
    }

    protected void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite();
        g2.drawImage(image, screenX, screenY, width, height, null);

        /* TESTING FOR PLAYER COLLISION */
        int hitboxScreenX = x - gamePanel.player.getEntityX() + gamePanel.player.getEntityScreenX();
        int hitboxScreenY = y - gamePanel.player.getEntityY() + gamePanel.player.getEntityScreenY();

        //g2.setColor(Color.RED); // drawing the hitbox with red
        //g2.drawRect(hitboxScreenX + hitbox.x, hitboxScreenY + hitbox.y, hitbox.width, hitbox.height);
    }

    private BufferedImage getCurrentSprite() {
        return switch (direction) {
            case "down", "down&left", "down&right" -> (spriteFlag == 1) ? walkDown1 : walkDown2;
            case "up", "up&left", "up&right" -> (spriteFlag == 1) ? walkUp1 : walkUp2;
            case "left" -> (spriteFlag == 1) ? walkLeft1 : walkLeft2;
            case "right" -> (spriteFlag == 1) ? walkRight1 : walkRight2;
            default -> standing;
        };
    }

    /* updates hitbox position based on scale */
    protected void updateHitbox() {
        width = Settings.getTileSize();
        height = Settings.getTileSize();
        hitbox = new Rectangle((int) (width / 3.5), (int) (height / 1.75), 13 * Settings.getScale(), 13 * Settings.getScale());
    }

    /* changes player position in the right place when scaling changes */
    protected void adjustPosition (int oldScale, int newScale) {
        double scaleFactor = (double) newScale / oldScale;
        x = (int) (x * scaleFactor);
        y = (int) (y * scaleFactor);
    }

    /* COLLISION SET METHODS */
    public void setCollisionUp(boolean status){ collisionUp = status; }
    public void setCollisionDown(boolean status){ collisionDown = status; }
    public void setCollisionLeft(boolean status){ collisionLeft = status; }
    public void setCollisionRight(boolean status){ collisionRight = status; }

    public int getEntityX(){ return x; }
    public int getEntityY(){ return y; }
    public int getEntityScreenX(){ return screenX; }
    public int getEntityScreenY(){ return screenY; }
    public int getEntityWidth(){ return width; }
    public int getEntityHeight(){ return height; }
    public int getSpeed() { return Speed; }
    public Rectangle getHitbox() {return hitbox; }

    /* ABSTRACT METHODS */
    protected abstract void setEntity();
    protected abstract void loadEntityVisuals();
}
