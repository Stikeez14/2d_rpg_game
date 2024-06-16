package entities;

import frame.Panel;
import inputs.keyHandler;
import java.awt.*;

public class Player {

    private final keyHandler key = new keyHandler();

    private int x;
    private int y;

    private final int width;
    private final int height;

    Rectangle hitbox;

    public Player(int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;

        width = 50;
        height = 50;
        hitbox = new Rectangle(x, y, width, height);

        gamePanel.addKeyListener(key);
        gamePanel.setFocusable(true); // helps with receiving key events
        gamePanel.requestFocusInWindow();
    }

    public void setPlayer() {
        int walkSpeed = 2, runSpeed = 4;
        int Speed = key.shiftPressed ? runSpeed : walkSpeed;

        if (key.upPressed && key.leftPressed) {
            y -= Speed;
            x -= Speed;
        } else if (key.upPressed && key.rightPressed) {
            y -= Speed;
            x += Speed;
        } else if (key.downPressed && key.leftPressed) {
            y += Speed;
            x -= Speed;
        } else if (key.downPressed && key.rightPressed) {
            y += Speed;
            x += Speed;
        } else if (key.upPressed) {
            y -= Speed;
        } else if (key.downPressed) {
            y += Speed;
        } else if (key.leftPressed) {
            x -= Speed;
        } else if (key.rightPressed) {
            x += Speed;
        }

        hitbox.x = x;
        hitbox.y = y;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        g2.fillRect(x, y, width, height);
    }
}
