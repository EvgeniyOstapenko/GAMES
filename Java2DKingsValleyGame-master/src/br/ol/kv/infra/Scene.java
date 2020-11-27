package br.ol.kv.infra;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Scene class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Scene {

    private final SceneManager sceneManager;
    protected final List<Entity> entities = new ArrayList<>();
    private double pauseTime;
    
    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public void clearAllEntities() {
        entities.clear();
    }
    
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    
    public void onEnter() {
        // override
    }

    public void onExit() {
        // override
    }
    
    public void update() {
        if (!isPaused()) {
            for(Entity entity : entities) {
                entity.update();
                if (sceneManager.getNextScene() != null || isPaused()) {
                    return;
                }
            }
        }
    }
    
    public void draw(Graphics2D g) {
        entities.forEach((entity) -> {
            entity.draw(g);
        });
    }

    public boolean isPaused() {
        return Time.getCurrent() <= pauseTime;
    }
    
    // pause time in seconds
    public void pause(double time) {
        pauseTime = Time.getCurrent() + time;
    }
    
}
