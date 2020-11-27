package br.ol.kv.infra;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Keyboard class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Keyboard extends KeyAdapter {

    public static boolean[] keyDown = new boolean[256];
    public static boolean[] keyDownConsumed = new boolean[256];

    public static boolean isKeyDown(int keyCode) {
        return keyDown[keyCode];
    }

    public static boolean isKeyPressedOnce(int keyCode) {
        if (keyDown[keyCode] && !keyDownConsumed[keyCode]) {
            keyDownConsumed[keyCode] = true;
            return true;
        }
        return false;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 255) {
            return;
        }
        keyDown[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 255) {
            return;
        }
        keyDown[e.getKeyCode()] = false;
        keyDownConsumed[e.getKeyCode()] = false;
    }
    
}
