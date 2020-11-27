package br.ol.kv.renderer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Sprite class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Sprite {
    
    private BufferedImage image;
    private final Point pivot = new Point();

    public Sprite(String resource) {
        try {        
            image = ImageIO.read(getClass().getResourceAsStream("/res/image/" + resource));
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        int pivotX = image.getWidth() / 2;
        int pivotY = image.getHeight() - 1;
        pivot.setLocation(pivotX, pivotY);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getPivot() {
        return pivot;
    }

    public void draw(Graphics2D g, int tx, int ty) {
        g.drawImage(image, tx - pivot.x, ty - pivot.y, null);
    }

    public void replaceColor(int srcColor, int dstColor) {
        //Set<Integer> colors = new HashSet<Integer>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                //colors.add(image.getRGB(x, y));
                
                if (image.getRGB(x, y) == srcColor) {
                    image.setRGB(x, y, dstColor);
                }
            }
        }
//        System.out.println("---");
//        for (Integer sc : colors) {
//            System.out.println("srcColor=" + sc);
//        }
//        System.out.println("---");
    }
    
}
