package game;

import frame.Panel;
import frame.Window;
import map.generateData;

import java.io.File;

public class startGame {

    public static void main (String[] args){
        System.out.println("time bound ~ 0.9");
        Panel game = new Panel();
        new generateData("src" + File.separator + "main" + File.separator + "resources" + File.separator + "map" + File.separator + "mapData.txt");
        new Window(game);
    }
}

