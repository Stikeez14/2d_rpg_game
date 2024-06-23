package map;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class generateData {

    public generateData(String mapDataPath){
        int rows = mapSettings.getMaxTilesHorizontally();
        int cols = mapSettings.getMaxTilesVertically();
        List<Integer> elements = getIntegers(rows, cols);

        Collections.shuffle(elements); //shuffle the list to randomize the tiles

        /* WRITING MATRIX IN FILE */
        try (FileWriter writer = new FileWriter(mapDataPath)) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(elements.get(i * cols + j) + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write in mapData!", e);
        }
    }

    private static List<Integer> getIntegers(int rows, int cols) {

        int totalElements = rows * cols;
        double sandTiles = totalElements * 0.80;
        double sandPaddlesTiles = totalElements * 0.05;
        double sandRockTiles = totalElements * 0.05;
        double sandTrees = totalElements - sandTiles - sandPaddlesTiles - sandRockTiles;

        List<Integer> elements = new ArrayList<>(totalElements); // store all the tiles in List

        // 80% tiles - normal sand ( 0 | 1 )
        Random rand = new Random();
        for (int i = 0; i < sandTiles; i++) elements.add(rand.nextInt(2));

        // 5% tiles - sand & rocks ( 2 | 3 | 4 )
        for (int i = 0; i < sandPaddlesTiles; i++) elements.add(rand.nextInt(3) + 2);

        // 10% tiles - trees ( 5 | 6 | 7 | 8 )
        for (int i = 0; i < sandTrees; i++) elements.add(rand.nextInt(5) + 4);

        // 10% tiles - rocks ( 9 | 10 )
        for (int i = 0; i < sandRockTiles; i++) elements.add(rand.nextInt(9) + 2);

        return elements;
    }
}
