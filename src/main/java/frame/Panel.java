package frame;

import inputs.keyHandler;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel implements Runnable {

    keyHandler key = new keyHandler();
    Thread gameThread; //thread for the game loop

    //temporary coordinates and speed for cube representing the player
    int px = 100;
    int py= 100;
    int pSpeed = 3;

    private static final int FPS = 120;

    public Panel() {
        this.setDoubleBuffered(true); //better rendering performance
        this.setBackground(Color.BLACK);
        this.addKeyListener(key);
        this.setFocusable(true); //helps with receiving key events
    }

    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        /* Game Loop */
        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            timer+= (currentTime - lastTime);
            lastTime = currentTime;
            //rendering the next frame
            if (delta >= 1) {
                update();
                repaint();
                delta --;
                drawCount++;
            }
            //printing FPS and resetting counters
            if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount=0;
                timer=0;
            }
        }
    }

    public void update() {
        if(key.upPressed) py -= pSpeed;
        else if(key.downPressed) py += pSpeed;
        else if (key.leftPressed) px -= pSpeed;
        else if (key.rightPressed) px += pSpeed;
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);
        g2.fillRect(px,py, 64, 64);
        g2.dispose();
    }
}
