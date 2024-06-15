package frame;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    public Panel() {
        this.setDoubleBuffered(true); //better rendering performance
        this.setBackground(Color.BLACK);
    }
}
