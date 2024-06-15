package entities;

import frame.Panel;
import frame.Window;
import javafx.scene.layout.Pane;

public class Player {

    private int x;
    private int y;
    private int xSpeed;

    public Player (int x, int y, Panel gamePanel){
        this.x=x;
        this.y=y;
    }
}
