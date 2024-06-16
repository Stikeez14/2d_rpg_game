package frame;

import javax.swing.*;
import java.awt.*;

public class Window {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    public Window(Panel gamePanel) {

        JFrame mainFrame = new JFrame("Time Bound");

        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainFrame.setContentPane(gamePanel);
        mainFrame.pack(); //adjust the frame size based on the game_panel

        gamePanel.startThread();
        gamePanel.requestFocusInWindow();

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null); //center the frame on the screen
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }

    public static int getScreenWidth () {
        return WIDTH;
    }

    public static int getScreenHeight () {
        return HEIGHT;
    }
}

