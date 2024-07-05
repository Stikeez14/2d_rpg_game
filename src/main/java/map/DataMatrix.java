package map;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DataMatrix {

    private boolean dataGenerated; //check if data generation is complete
    private final CountDownLatch latch; // latch for synchronizing data generation completion
    protected static String mapDataPath;
    private static final Random RANDOM = new Random();

    public DataMatrix(String mapDataPath){
        DataMatrix.mapDataPath = mapDataPath;
        this.latch = new CountDownLatch(1); // initialize latch with 1 permit
        startGenerateThread(); // start the thread
    }

    private void startGenerateThread() {
        new Thread(() -> {
            System.out.println("Map generation in progress ... ");

            int rows = Settings.getMaxTilesHorizontally(); // get no. rows & cols from Settings
            int cols = Settings.getMaxTilesVertically();
            int[][] matrix = new int[rows][cols]; // matrix for storing the tile values

            /* tiles percentage */
            double sandTiles = rows * cols * 0.5;
            double sandPaddlesTiles = rows * cols * 0.05;
            double sandRockTiles = rows * cols * 0.225;
            double sandTrees = rows * cols * 0.225;

            // list containing generated tiles
            List<Integer> elements = generateElements(sandTiles, sandPaddlesTiles, sandRockTiles, sandTrees);

            /* fill the matrix with tile values */
            int index = 0;
            for (int i = 1; i < rows - 1; i++) {
                for (int j = 1; j < cols - 1; j++) {
                    matrix[i][j] = elements.get(index++);
                }
            }

            /* MAP BORDERS */
            setLayer(matrix, rows, cols, 0, 6);
            setLayer(matrix, rows, cols, 1, 7);
            setLayer(matrix, rows, cols, 2, 6);
            setLayer(matrix, rows, cols, 3, 7);
            setLayer(matrix, rows, cols, 4, 6);
            setLayer(matrix, rows, cols, 5, 11);

            /* STRUCTURES */
            addStructure(matrix, rows, cols, 12);
            addStructure(matrix, rows, cols, 10);

            writeMatrixToFile(matrix, rows, cols, mapDataPath);

            dataGenerated = true;
            latch.countDown(); // release the latch at completion
            System.out.println("Map generation completed.");
        }).start(); // new thread for generating data
    }

    /* generates a list of elements based on the number of each type of tile */
    private static List<Integer> generateElements(double sandTiles, double sandPaddlesTiles, double sandRockTiles, double sandTrees) {
        List<Integer> elements = new ArrayList<>();
        Random rand = new Random();

        // 50% -> sand tiles
        for (int i = 0; i < sandTiles; i++) {
            elements.add(rand.nextInt(2));
        }

        // 5% -> sand & peddles ( 2 | 3 | 4 )
        for (int i = 0; i < sandPaddlesTiles; i++) {
            elements.add(rand.nextInt(3) + 2);
        }

        // 22.5% -> trees ( 5 | 6 | 7 | 8 )
        for (int i = 0; i < sandTrees; i++) {
            elements.add(rand.nextInt(5) + 4);
        }

        // 22.5% -> rocks ( 9 | 10 )
        for (int i = 0; i < sandRockTiles; i++) {
            elements.add(rand.nextInt(9) + 2);
        }

        Collections.shuffle(elements); //shuffle the list to randomize the tiles

        return elements;
    }

    /* sets a specific layer of the matrix to a given value */
    private static void setLayer(int[][] matrix, int rows, int cols, int layer, int value) {
        for (int i = layer; i < rows - layer; i++) {
            matrix[i][layer] = value;
            matrix[i][cols - layer - 1] = value;
        }
        for (int j = layer; j < cols - layer; j++) {
            matrix[layer][j] = value;
            matrix[rows - layer - 1][j] = value;
        }
    }

    /*  writes the matrix data to a file */
    private static void writeMatrixToFile(int[][] matrix, int rows, int cols, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(matrix[i][j] + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addStructure(int[][] matrix, int rows, int cols, int structureSize) {

        int borderLayers = 7; // layers that need to be avoided when spawning a structure

        // maximum start position of the structure to ensure the structure is not spawning into the borders
        int maxStartRow = rows - borderLayers - structureSize;
        int maxStartCol = cols - borderLayers - structureSize;

        // check if the structure is too large
        if (maxStartRow < borderLayers || maxStartCol < borderLayers) {
            throw new IllegalArgumentException("Structure size is too large!");
        }

        // random start position of the structure within the valid range
        int startRow = RANDOM.nextInt(maxStartRow - borderLayers + 1) + borderLayers;
        int startCol = RANDOM.nextInt(maxStartCol - borderLayers + 1) + borderLayers;

        // fill matrix with structure tiles
        for (int i = startRow; i < startRow + structureSize; i++) {
            for (int j = startCol; j < startCol + structureSize; j++) {
                matrix[i][j] = 10;
            }
        }
    }

    /* wait until the data generation is complete */
    public void awaitDataGeneration() throws InterruptedException { latch.await(); }

    /* get method for generation status */
    public boolean isDataGenerated() { return dataGenerated; }
}
