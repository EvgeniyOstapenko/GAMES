package br.ol.kv.infra;

import java.awt.Graphics2D;

/**
 * Entity class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Entity {

    private final Scene scene;
    
    public Entity(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
    
    public void update() {
    }
    
    public void draw(Graphics2D g) {
    }
    
}
