package br.ol.kv.infra;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * SceneTransition class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SceneTransition {

    public double p;
    public double targetP;
    
    public void update() {
        double dif = targetP - p;
        if (Math.abs(dif) <= 0.025) {
            p = targetP;
        }
        else if (dif > 0) {
            p += 0.025;
        }
        else if (dif < 0) {
            p -= 0.025;
        }
    }
    
    public void draw(Graphics2D g) {
        int w = (int) (Display.SCREEN_WIDTH * p);
        int h = Display.SCREEN_HEIGHT;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
    }

    public void open() {
        targetP = 0;
    }
    
    public void close() {
        targetP = 1;
    }
    
    public boolean isFinished() {
        return p == targetP;
    }
    
}
