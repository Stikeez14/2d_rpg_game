package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class keyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int value = e.getKeyCode();
        if(value == KeyEvent.VK_W) {
            upPressed = true;
        }
        if(value == KeyEvent.VK_S) {
            downPressed = true;
        }
        if(value == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if(value == KeyEvent.VK_D) {
            rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int value = e.getKeyCode();
        if(value == KeyEvent.VK_W) {
            upPressed = false;
        }
        if(value == KeyEvent.VK_S) {
            downPressed = false;
        }
        if(value == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if(value == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
