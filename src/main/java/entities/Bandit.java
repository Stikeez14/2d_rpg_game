package entities;

import frame.Panel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Bandit extends Entity {

    private static final int DIRECTION_CHANGE_INTERVAL = 200;
    private int countCooldown;
    private final Random rand;

    public Bandit(int x, int y, Panel gamePanel) {
        super(x, y, gamePanel);
        this.rand = new Random();
        this.direction = "standing";
        this.Speed = 1;
        loadEntityVisuals();
    }

    @Override
    public void setEntity() {

        gamePanel.ck.checkTileCollision(this);
        gamePanel.ck.checkEntityCollision(this, gamePanel.player);
        moveEntity();
        updateSpriteFlag();
    }

    @Override
    protected void loadEntityVisuals() {
        try {
            String path = "malePlayer" + File.separator;
            standing = loadImage(path + "standingPlayer.png");
            walkDown1 = loadImage(path + "walkDown1.png");
            walkDown2 = loadImage(path + "walkDown2.png");
            walkUp1 = loadImage(path + "walkUp1.png");
            walkUp2 = loadImage(path + "walkUp2.png");
            walkLeft1 = loadImage(path + "walkLeft1.png");
            walkLeft2 = loadImage(path + "walkLeft2.png");
            walkRight1 = loadImage(path + "walkRight1.png");
            walkRight2 = loadImage(path + "walkRight2.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Bandit Visuals!", e);
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + path)));
    }

    @Override
    protected void moveEntity() {
        if (direction.contains("up") && !collisionUp) y -= Speed;
        if (direction.contains("down") && !collisionDown) y += Speed;
        if (direction.contains("left") && !collisionLeft) x -= Speed;
        if (direction.contains("right") && !collisionRight) x += Speed;

        if(countCooldown > 0){
            countCooldown --;
        }
        else if(countCooldown == 0){
            direction = getDirection();
            countCooldown = DIRECTION_CHANGE_INTERVAL;
        }
    }

    private void updateSpriteFlag() {
        spriteCounter++;
        int spriteThreshold = 20;
        if (spriteCounter > spriteThreshold) {
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private String getDirection() {
        String[] directions = {"up", "up&left", "up&right", "down", "down&left", "down&right", "left", "right", "standing"};
        return directions[rand.nextInt(directions.length)];
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite();
        int drawX = x - gamePanel.player.getEntityX() + gamePanel.player.getEntityScreenX();
        int drawY = y - gamePanel.player.getEntityY() + gamePanel.player.getEntityScreenY();
        g2.drawImage(image, drawX, drawY, width, height, null);
        if(gamePanel.player.drawHitbox) drawEntityHitbox(g2, drawX, drawY);
    }

    private void drawEntityHitbox(Graphics2D g2, int drawX, int drawY) {
        g2.setColor(new Color(0, 255, 255, 100));
        g2.fillRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
        g2.setColor(Color.BLUE);
        g2.drawRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
    }
}
