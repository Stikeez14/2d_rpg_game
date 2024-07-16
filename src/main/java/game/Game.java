package game;
import frame.Panel;
import frame.Window;
import map.DataMatrix;

import java.io.File;

public class Game {

    public static void main (String[] args){

        System.out.println("time bound ~ 0.15\n");

        String mapPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "map" + File.separator + "mapData.txt";
        DataMatrix genData = new DataMatrix(mapPath);

        try {
            // wait until the map data generation is complete
            genData.awaitDataGeneration();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
            System.err.println("Data generation was interrupted.");
            return;
        }

        // initialize the game after the data generation is complete
        if(genData.isDataGenerated()) initializeGame();
    }

    private static void initializeGame() {
        Panel game = new Panel(); // create a new Panel for the game
        new Window(game); // open a new Window with the created Panel
    }
}

