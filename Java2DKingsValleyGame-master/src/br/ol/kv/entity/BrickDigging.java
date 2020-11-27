package br.ol.kv.entity;

import br.ol.kv.ai.PathMap;
import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Entity;
import br.ol.kv.infra.Scene;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * BrickDigging class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class BrickDigging extends Entity {
    
    private final World world;
    private final Terrain terrain;
    private final AnimationManager animationManager;
    private final SoundManager soundManager;
    
    private final Point diggingBrickPosition1 = new Point(-10, -10); // col, row
    private final Point diggingBrickPosition2 = new Point(-10, -10); // col, row
    private boolean digging;
    private boolean firstBrick;
    
    public BrickDigging(Scene scene) {
        super(scene);
        world = scene.getSceneManager().getGame().getWorld();
        soundManager = scene.getSceneManager().getGame().getSoundManager();
        terrain = world.getTerrain();
        animationManager = new AnimationManager();
        addAnimations();
    }
    
    private void addAnimations() {
        animationManager.addAnimation("digging", false, 0.125,
                "brick_digging_0.png", "brick_digging_1.png", "brick_digging_2.png");
    }

    public boolean isDigging() {
        return digging;
    }
    
    @Override
    public void update() {
        if (digging) {
            animationManager.update();
            
            if (animationManager.getCurrentAnimation().isFinished()) {
                int col = diggingBrickPosition1.x;
                int row = diggingBrickPosition1.y;
                if (firstBrick) {
                    if (terrain.getIdByGrid(col, row + 1) == 1) {
                        animationManager.setAnimation("digging", true);
                        soundManager.play(Sounds.DIG);
                    }
                    terrain.set(col, row, 0);
                    firstBrick = false;
                }
                else {
                    PathMap pathMap = getScene().getSceneManager().getGame().getPathMap();
                    pathMap.dig(col, row);
                    diggingBrickPosition1.setLocation(-10, -10);
                    diggingBrickPosition2.setLocation(-10, -10);
                    digging = false;
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        int x = diggingBrickPosition1.x * Terrain.TILE_SIZE + 4;
        int y = (diggingBrickPosition1.y + 1) * Terrain.TILE_SIZE - 1;
        if (!firstBrick) {
            x = diggingBrickPosition2.x * Terrain.TILE_SIZE + 4;
            y = (diggingBrickPosition2.y + 1) * Terrain.TILE_SIZE - 1;
        }
        animationManager.draw(g, x, y);
    }
    
    public void dig(int col, int row) {
        diggingBrickPosition1.setLocation(col, row);
        diggingBrickPosition2.setLocation(col, row + 1);
        animationManager.setAnimation("digging", true);
        digging = true;
        firstBrick = true;
        soundManager.play(Sounds.DIG);
    }
    
}
