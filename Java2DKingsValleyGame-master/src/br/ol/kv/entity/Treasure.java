package br.ol.kv.entity;

import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Keyboard;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.physics.Body;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Treasure class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Treasure extends Item {
    
    private static final int ORIGINAL_COLOR = -2602832;
    
    public Treasure(Scene scene, Player player, int col, int row) {
        super(scene, player);
        createBody(col, row);
        loadAnimation();
        setRandomColor();
    }

    private void createBody(int col, int row) {
        body = new Body<>(this, 8 * col + 4, 8 * (row + 1) - 1, 8, 8, 4, 7, 9);
        world.addBody(body);
    }
    
    private void loadAnimation() {
        animationManager.addAnimation("default", true, 0.5, "treasure_0.png", "treasure_1.png");
    }
    
    private void setRandomColor() {
        int r = 96 + (int) (160 * Math.random());
        int g = 96 + (int) (160 * Math.random());
        int b = 96 + (int) (160 * Math.random());
        animationManager.replaceColor(ORIGINAL_COLOR, new Color(r, g, b).getRGB());
    }

    @Override
    public void update() {
        super.update();
        // updateDebug();
    }
    
    private void updateDebug() {
        if (Keyboard.isKeyDown(KeyEvent.VK_C)) {
            collected = true;
        }
    }

    @Override
    protected boolean collectedByPlayer() {
        soundManager.play(Sounds.TREASURE);
        hudInfo.addScore(ScoreTable.TREASURE_COLLECTED);
        return true;
    }
    
    @Override
    protected void drawCollected(Graphics2D g) {
        // not draw
    }
    
}
