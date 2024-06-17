package game;

import frame.Panel;
import frame.Window;

public class startGame {

    public static void main (String[] args){
        System.out.println("time bound ~ 0.4");
        Panel game = new Panel();
        new Window(game);
    }
}

