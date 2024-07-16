package frame;

import collision.Collision;
import entities.Bandit;
import entities.Entity;
import entities.Player;
import map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Panel extends JPanel implements Runnable {

    public Player player;
    public Bandit bandit1, bandit2,bandit3, bandit4;

    public List<Entity> entities; // list for storing all entities

    Thread gameThread; //thread for the game loop
    public Map map = new Map(this);
    public Collision ck = new Collision(this);

    private static final int FPS = 120;
    private int fpsCount; // attribute used to print the fps on screen

    // attributes required to compute draw time average;
    private long drawTimeAverage = 0;
    private long drawTimes = 0;
    private int frames = 0;

    public Panel() {
        entities = new CopyOnWriteArrayList<>();

        this.setDoubleBuffered(true); //better rendering performance
        this.setBackground(Color.BLACK);

        player = new Player(1500, 1500, this,"malePlayer");
        player.setEquipment(false,false,false, true);

        bandit1 = new Bandit(1600,1600,this, "bandit", "baseballBat");
        bandit2 = new Bandit(1500,1600,this,"bandit", null);
        bandit3 = new Bandit(1500,1500,this,"corruptedBandit", "dagger");
        bandit4 = new Bandit(1600,1700,this,"corruptedBandit", null);

        addEntity(player);
        addEntity(bandit1); addEntity(bandit2);
        addEntity(bandit3); addEntity(bandit4);
    }

    public void startThread() {
        this.setFocusable(true);
        this.requestFocusInWindow(); //request focus when game starts
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0; // tracks progress towards drawing next frame
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0; // used for measuring one sec intervals
        int drawCount = 0; // how many frames are drawn each sec

        /* GAME LOOP */
        while (gameThread != null) {
            currentTime = System.nanoTime(); // current time in ns
            delta += (currentTime - lastTime) / drawInterval; // how much time passed since last loop iteration
            timer += (currentTime - lastTime); // passed time added to timer
            lastTime = currentTime; // update last time to current time for the next iteration

            if (delta >= 1) { // check if enough time passed to draw next frame
                update();
                repaint();
                drawCount++;
                delta--;
            }

            if (timer >= 1000000000) { // get the fps each second
                fpsCount = drawCount;
                drawCount = 0;
                timer = 0;
            }

            try { // short sleep to reduce CPU usage
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {
        for (Entity entity : entities) entity.setEntity();
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        long startTime = System.nanoTime(); // start time -> before drawing

        map.draw(g2); // draw the map and entities

        long endTime = System.nanoTime(); // end time -> after drawing
        long timeToDraw = endTime - startTime; // elapsed time

        drawTimes += timeToDraw; // adding draw times all together
        frames++; // incrementing frames

        if(frames >= 5){ // after 5 frames
            drawTimeAverage = drawTimes/5; // compute average of the 5 draw times
            drawTimes = 0; // reset frames counter & draw times accumulator
            frames = 0;
        }

        g2.dispose();
    }

    /** FPS & DRAW TIME */
    /* get methods */
    public int getFPS(){ return fpsCount; }
    public double getDrawTime(){ return drawTimeAverage / 1000000000.0; }

    /* draw method for information on scree */
    public void drawInfo(Graphics2D g2, String text, int value, int x, int y) {

        g2.setFont(new Font("Arial", Font.BOLD, 32)); // font options
        String drawString = text + value; // create string

        FontRenderContext frc = g2.getFontRenderContext();  // get info about the font to calculate the text's shape and position.
        Shape textShape = g2.getFont().createGlyphVector(frc, drawString).getOutline(x, y);

        g2.setColor(Color.BLACK); // draw black outline of 3 pixels thick
        g2.setStroke(new BasicStroke(3));
        g2.draw(textShape);

        g2.setColor(Color.WHITE); // fill the inside of the text with white
        g2.fill(textShape);
    }

    /* draw method for the time took to draw on screen */
    public void drawTimeToDraw(Graphics2D g2, String text, double value, int x, int y) {
        String formattedValue = String.format("%.6f", value); // gets only the 6 digits after '.'

        g2.setFont(new Font("Arial", Font.BOLD, 32)); // font options
        String drawString = text + formattedValue + "s"; // create string

        FontRenderContext frc = g2.getFontRenderContext(); // get info about the font to calculate the text's shape and position.
        Shape textShape = g2.getFont().createGlyphVector(frc, drawString).getOutline(x, y);

        g2.setColor(Color.BLACK); // draw black outline of 3 pixels thick
        g2.setStroke(new BasicStroke(3));
        g2.draw(textShape);

        g2.setColor(Color.WHITE); // fill the inside of the text with white
        g2.fill(textShape);
    }

    /** ENTITY HANDLING */
    /* add entity to the list */
    public void addEntity(Entity entity){
        if(entity == null) return;
        if(entities.contains(entity)) return;
        entities.add(entity);
    }
    /* remove entity from the list */
    public void removeEntity(Entity entity){ entities.remove(entity); }
    /* get entities list */
    public List<Entity> getEntities(){ return entities; }
}
