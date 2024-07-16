package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardKeys implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed,
            f3Pressed, f2Pressed, spacePressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        int value = e.getKeyCode(); // take the int value of key

        // activate flags for pressed keys
        if(value == KeyEvent.VK_W) upPressed = true;
        if(value == KeyEvent.VK_S) downPressed = true;
        if(value == KeyEvent.VK_A) leftPressed = true;
        if(value == KeyEvent.VK_D) rightPressed = true;
        if(value == KeyEvent.VK_SHIFT) shiftPressed = true;
        if(value == KeyEvent.VK_F3) f3Pressed = true;
        if(value == KeyEvent.VK_F2) f2Pressed = true;
        if(value == KeyEvent.VK_SPACE) spacePressed = true;

        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int value = e.getKeyCode();

        // deactivate flags when keys are released
        if(value == KeyEvent.VK_W) upPressed = false;
        if(value == KeyEvent.VK_S) downPressed = false;
        if(value == KeyEvent.VK_A) leftPressed = false;
        if(value == KeyEvent.VK_D) rightPressed = false;
        if(value == KeyEvent.VK_SHIFT) shiftPressed = false;
        if(value == KeyEvent.VK_F3) f3Pressed = false;
        if(value == KeyEvent.VK_F2) f2Pressed = false;
        if(value == KeyEvent.VK_SPACE) spacePressed = false;

        e.consume();
    }
}
