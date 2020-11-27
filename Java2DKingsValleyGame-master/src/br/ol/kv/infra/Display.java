package br.ol.kv.infra;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * Display class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Display extends Canvas {
    
    public static final int SCREEN_WIDTH = 256;
    public static final int SCREEN_HEIGHT = 192;
    public static final double SCREEN_SCALE = 3;
    
    private Game game;
    private BufferStrategy bs;
    private boolean running;
    
    private final BufferedImage offscreen;

    public Display() {
        setBackground(Color.BLACK);
        int sx = (int) (SCREEN_WIDTH * SCREEN_SCALE);
        int sy = (int) (SCREEN_HEIGHT * SCREEN_SCALE);
        setPreferredSize(new Dimension(sx, sy));
        offscreen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        addKeyListener(new Keyboard());
    }

    public BufferedImage getOffscreen() {
        return offscreen;
    }
    
    public void start(Game game) {
        this.game = game;
        game.setDisplay(this);
        createBufferStrategy(2);
        bs = getBufferStrategy();
        running = true;
        new Thread(new MainLoop()).start();
    }
    
    private class MainLoop implements Runnable {
        @Override
        public void run() {
            Time.start();
            game.start();
            while (running) {
                Time.update();
                while (Time.needsUpdate()) {
                    update();
                }
                render();
                
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
    
    private void update() {
        game.update();
    }
    
    private void render() {
        draw((Graphics2D) offscreen.getGraphics());
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.scale(SCREEN_SCALE, SCREEN_SCALE);
        g.drawImage(offscreen, 0, 0, null);
        g.dispose();
        bs.show();
    }
    
    private void draw(Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, getWidth(), getHeight());
        game.draw(g);
    }
    
}
