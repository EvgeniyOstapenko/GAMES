package br.ol.kv.renderer;

import br.ol.kv.infra.Camera;
import br.ol.kv.physics.Terrain;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * TerrainRenderer class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class TerrainRenderer {
    
    private BufferedImage brickYellow;
    private BufferedImage brickBlue;
    private BufferedImage brickGreen;
    private Map<String, BufferedImage> bricks = new HashMap<>();
            
    private BufferedImage brickUnbreakable;
    private BufferedImage stairLeft;
    private BufferedImage stairRight;
    private BufferedImage stairLeftTop;
    private BufferedImage stairRightTop;
    private BufferedImage[] stairImages;
    
    public TerrainRenderer() {
        loadImages();
    }

    private void loadImages() {
        stairImages = new BufferedImage[10];
        brickYellow = loadImage("brick_yellow.png");
        brickBlue = loadImage("brick_blue.png");
        brickGreen = loadImage("brick_green.png");
        bricks.put("yellow", brickYellow);
        bricks.put("blue", brickBlue);
        bricks.put("green", brickGreen);
        brickUnbreakable = loadImage("brick_unbreakable.png");
        stairLeft = stairImages[3] = loadImage("stair_left.png");
        stairRight = stairImages[6] = loadImage("stair_right.png");
        stairLeftTop = stairImages[4] = loadImage("stair_left_top.png");
        stairRightTop = stairImages[7] = loadImage("stair_right_top.png");
    }
    
    private BufferedImage loadImage(String resource) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/image/" + resource);
            BufferedImage image = ImageIO.read(is);
            return image;
        } catch (IOException ex) {
            Logger.getLogger(TerrainRenderer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return null;
    }
    
    public void draw(Graphics2D g, Terrain terrain, Camera camera) {
        int startCol = camera.getBody().getX() / Terrain.TILE_SIZE;
        int endCol = 1 + (camera.getBody().getX() + camera.getBody().getWidth()) / Terrain.TILE_SIZE;
        for (int row = 0; row < terrain.getRows(); row++) {
            for (int col = startCol; col < endCol; col++) {
                if (terrain.isRigidByGrid(col, row, 0)) {
                    g.drawImage(bricks.get(terrain.getBrickColor()), col * Terrain.TILE_SIZE, row * Terrain.TILE_SIZE, null);
                }
                if (terrain.getIdByGrid(col, row) == 8 || row >= terrain.getRows() - 1) {
                    g.drawImage(brickUnbreakable, col * Terrain.TILE_SIZE, row * Terrain.TILE_SIZE, null);
                }
            }
        }
        // draw stairs
        for (int row = 0; row < terrain.getRows(); row++) {
            for (int col = 0; col < terrain.getCols(); col++) {
                int tileIndex = terrain.getIdByGrid(col, row);
                BufferedImage image = stairImages[tileIndex];
                if (image != null) {
                    g.drawImage(image, col * Terrain.TILE_SIZE - 4, row * Terrain.TILE_SIZE, null);
                }
            }
        }
    }
    
}
