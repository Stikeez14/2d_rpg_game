package map;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class generateData {

    public generateData(String mapDataPath){
        int rows = 100;
        int cols = 100;
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
        double numZerosOnes = totalElements * 0.95;
        double numOthers = totalElements - numZerosOnes;

        List<Integer> elements = new ArrayList<>(totalElements); // store all the tiles in List

        // 95% of tiles will be 0 | 1
        Random rand = new Random();
        for (int i = 0; i < numZerosOnes; i++) elements.add(rand.nextInt(2));

        // 5% of tiles will be 2 | 3 | 4
        for (int i = 0; i < numOthers; i++) elements.add(rand.nextInt(3) + 2);

        return elements;
    }
}
