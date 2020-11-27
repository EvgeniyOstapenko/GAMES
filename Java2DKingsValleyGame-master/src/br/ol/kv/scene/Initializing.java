package br.ol.kv.scene;

import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.Time;

/**
 * Initializing class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Initializing extends Scene {

    private double startTime;
    
    public Initializing(SceneManager sceneManager) {
        super(sceneManager);
    }

    @Override
    public void onEnter() {
        startTime = Time.getCurrent() + 1;
    }
    
    @Override
    public void update() {
        if (Time.getCurrent() > startTime) {
            getSceneManager().setNextScene(SceneManager.OL_PRESENTS);
        }
    }

}
