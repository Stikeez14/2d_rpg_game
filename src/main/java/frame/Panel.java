package frame;

import entities.malePlayer;
import map.mapSettings;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel implements Runnable {

    malePlayer player;
    Thread gameThread; //thread for the game loop

    private static final int FPS = 120;

    public Panel() {
        this.setDoubleBuffered(true); //better rendering performance
        this.setBackground(Color.BLACK);

        player = new malePlayer(400, 300, this);
    }

    public void startThread() {
        this.setFocusable(true);
        this.requestFocusInWindow(); //request focus when game starts
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
            if(timer >= 500000000) {
                System.out.println("FPS: " + drawCount * 2);
                drawCount=0;
                timer=0;
            }
        }
    }

    public void update() {
        player.setPlayer();
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        for(int i=0; i<mapSettings.getTilesHorizontally(); i++){
            for (int j=0; j<mapSettings.getTilesVertically(); j++){
                g2.setColor(Color.WHITE);
                g2.fillRect(i*mapSettings.getTileSize(), j*mapSettings.getTileSize(), mapSettings.getTileSize(), mapSettings.getTileSize());
            }
        }

        player.draw(g2);

        g2.dispose();
    }
}
