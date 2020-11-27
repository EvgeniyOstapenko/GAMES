package br.ol.kv.infra;

import br.ol.kv.entity.Player;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.Terrain;
import java.awt.Graphics2D;

/**
 * Camera class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Camera extends Entity {
    
    private final Player player;
    private final Terrain terrain;
    private final Body<Camera> body;
    
    public Camera(Scene scene, Player player) {
        super(scene);
        terrain = scene.getSceneManager().getGame().getWorld().getTerrain();
        this.player = player;
        body = new Body(this, 0, 0, Display.SCREEN_WIDTH, Display.SCREEN_HEIGHT, 0, 0, 0);
        updateCameraPosition();
    }

    public Body<Camera> getBody() {
        return body;
    }
  
    private void updateCameraPosition() {
        int dx = player.getBody().getX() - (body.getX() + body.getWidth() / 2);
        int minX = 0;
        int maxX = (terrain.getCols() * Terrain.TILE_SIZE) - body.getWidth();
        int nx = body.getX() + dx;
        nx = nx < minX ? minX : nx;
        nx = nx > maxX ? maxX : nx;
        body.setX(nx);
    }

    @Override
    public void update() {
        updateCameraPosition();
    }

    @Override
    public void draw(Graphics2D g) {
        // body.drawDebug(g);
    }
    
}
