package br.ol.kv.physics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * World class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class World {
    
    public static final double GRAVITY = 0.5;
    private final List<Body> bodies = new ArrayList<>();
    private final Terrain terrain;

    public World(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void addBody(Body body) {
        bodies.add(body);
        body.setWorld(this);
    }
    
    public void clear() {
        bodies.clear();
    }
    
    public void update() {
        bodies.forEach((body) -> {
            body.update();
        });
    }

    public void drawDebug(Graphics2D g) {
        bodies.forEach((body) -> {
            body.drawDebug(g);
        });
    }
    
    public Body checkCollision(Body b1, Class ownerType) {
        for (Body b2 : bodies) {
            if (b1 == b2 || !b2.isCollisible()) {
                continue;
            }
            if (ownerType.isInstance(b2.getOwner()) && b1.collides(b2)) {
                return b2;
            }
        }
        return null;
    }
    
}
