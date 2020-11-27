package main;

import br.ol.kv.infra.Display;
import br.ol.kv.infra.Game;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Display display = new Display();
            JFrame frame = new JFrame();
            frame.setTitle("King's Valley");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(display);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            display.requestFocus();
            
            Game game = new Game();
            display.start(game);
        });
    }
    
}
