package DoodleClassifier;

import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Window extends Canvas{
	JFrame frame;
    public Window(int width, int height, String title, Classifier display){
        frame = new JFrame(title);
        
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
//        frame.setLocationRelativeTo(null);
        frame.add(display);
        frame.setVisible(true);
        display.start();
    }
}
