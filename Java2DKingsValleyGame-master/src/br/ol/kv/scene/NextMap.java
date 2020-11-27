package br.ol.kv.scene;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.entity.HUD;
import br.ol.kv.infra.Animation;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Game;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.Time;
import br.ol.kv.renderer.BitmapFont;
import br.ol.kv.renderer.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * NextMap class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class NextMap extends Scene {

    private final HUD hud;
    private final HUDInfo hudInfo;
    private final BitmapFont bitmapFont;
    private final SoundManager soundManager;
    
    private final Sprite backgroundImage;
    private final Sprite pyramidImage;
    private final Sprite goalImage;
    
    private final AnimationManager animationManager;
    private Animation pyramidAnimation;
    private Animation arrowAnimation;
    private Animation goalAnimation;
    
    private boolean blinkPreviousPyramid;
    private double previousPyramidTime;

    private boolean blinkNextPyramid;
    private double nextPyramidTime;
    
    public NextMap(SceneManager sceneManager) {
        super(sceneManager);
        hud = new HUD(this);
        hud.setShowFooter(false);
        hudInfo = hud.getHudInfo();
        bitmapFont = sceneManager.getGame().getBitmapFont();
        soundManager = sceneManager.getGame().getSoundManager();
        
        backgroundImage = new Sprite("next_map_screen.png");
        backgroundImage.getPivot().setLocation(0, 0);
        
        pyramidImage = new Sprite("next_map_pyramid_2.png");
        pyramidImage.getPivot().setLocation(0, 0);

        goalImage = new Sprite("next_map_goal_0.png");
        goalImage.getPivot().setLocation(0, 0);
        
        animationManager = new AnimationManager();
        addAllAnimations();
    }
    
    private void addAllAnimations() {
        animationManager.addAnimation("arrow", true, 0.5, 
                "next_map_arrow_0.png", "next_map_arrow_1.png");
        
        animationManager.addAnimation("pyramid", true, 0.5,
                "next_map_pyramid_0.png", "next_map_pyramid_1.png");
        
        animationManager.addAnimation("goal", true, 0.5,
                "next_map_goal_0.png","next_map_goal_1.png");
        
        pyramidAnimation = animationManager.getAnimation("pyramid");
        arrowAnimation = animationManager.getAnimation("arrow");
        goalAnimation = animationManager.getAnimation("goal");
    }
    
    @Override
    public void onEnter() {
        blinkPreviousPyramid = true;
        blinkNextPyramid = false;
        previousPyramidTime = -1;
        nextPyramidTime = -1;
    }
    
    @Override
    public void update() {
        pyramidAnimation.update();
        arrowAnimation.update();
        goalAnimation.update();
        
        if (previousPyramidTime < 0) {
            soundManager.play(Sounds.NEXT_MAP);
            previousPyramidTime = Time.getCurrent() + 3;
        }
        else if (blinkPreviousPyramid && Time.getCurrent() > previousPyramidTime) {
            blinkPreviousPyramid = false;
            blinkNextPyramid = true;
            nextPyramidTime = Time.getCurrent() + 3;
        }
        else if (blinkNextPyramid && Time.getCurrent() > nextPyramidTime) {
            
            // game cleared
            if (hudInfo.getLevel() > Game.LAST_LEVEL) {
                //throw new RuntimeException("GAME CLEARED NOT IMPLEMENTED YET !");
                getSceneManager().setNextScene(SceneManager.ENDING);
            }
            // play next level
            else {
                getSceneManager().setNextScene(SceneManager.PLAYING);
            }
            
        }
    }

    @Override
    public void draw(Graphics2D g) {
        backgroundImage.draw(g, 0, 0);
        hud.draw(g);
        bitmapFont.drawText(g, "- PYRAMID'S MAP -", 7, 3);
        
        drawPyramids(g);
        
        int previousLevel = hudInfo.getPreviousLevel();
        int nextLevel = hudInfo.getLevel();
        if (blinkPreviousPyramid) {
            pyramidAnimation.draw(g, 41 + previousLevel * 30, 108);
            arrowAnimation.draw(g, 50 + previousLevel * 30, 108);
        }
        else if (nextLevel > Game.LAST_LEVEL) {
            goalAnimation.draw(g, 49 + nextLevel * 30, 107);
            arrowAnimation.draw(g, 61 + previousLevel * 30, 108);
        }
        else {
            pyramidAnimation.draw(g, 41 + nextLevel * 30, 108);
            arrowAnimation.draw(g, 61 + previousLevel * 30, 108);
        }
    }
    
    private void drawPyramids(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawLine(35 + 1 * 30, 105, 35 + (Game.LAST_LEVEL + 1) * 30, 105);
        
        for (int p = 1; p <= Game.LAST_LEVEL; p++) {
            pyramidImage.draw(g, 35 + p * 30, 100);
        }
        goalImage.draw(g, 35 + 4 * 30, 100);
    }
    
}
