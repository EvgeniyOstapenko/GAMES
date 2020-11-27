package br.ol.kv.scene;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Display;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.Time;
import br.ol.kv.physics.World;
import br.ol.kv.renderer.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * OLPresents class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class OLPresents extends Scene {

    private double y;
    private double vy;
    private boolean processingPhysics;
    
    private final SoundManager soundManager;
    private final Sprite image;
    private double waitTime;
    
    private final Color backgroundColor = new Color(32, 32, 247);
    
    public OLPresents(SceneManager sceneManager) {
        super(sceneManager);
        soundManager = sceneManager.getGame().getSoundManager();
        image = new Sprite("ol_presents_screen.png");
    }

    @Override
    public void onEnter() {
        waitTime = -1;
        y = 0;
        vy = 0;
        processingPhysics = true;
    }
    
    @Override
    public void update() {
        if (processingPhysics) {
            y += vy;
            vy += World.GRAVITY;
            if (y > Display.SCREEN_HEIGHT) {
                y = Display.SCREEN_HEIGHT;
                vy *= -0.5;
                soundManager.play(Sounds.BOUNCE);
                if (Math.abs(vy) < 2) {
                    processingPhysics = false;
                }
            }
        }
        else if (waitTime < 0) {
            waitTime = Time.getCurrent() + 3;
        }
        else if (Time.getCurrent() > waitTime) {
            getSceneManager().setNextScene(SceneManager.TITLE);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, Display.SCREEN_WIDTH, Display.SCREEN_HEIGHT);
        image.draw(g, Display.SCREEN_WIDTH / 2, (int) y);
    }
    
}
