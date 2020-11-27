package br.ol.kv.scene;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Keyboard;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.Time;
import br.ol.kv.renderer.BitmapFont;
import br.ol.kv.renderer.Sprite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * Title class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Title extends Scene {

    private final SoundManager soundManager;
    private final BitmapFont bitmapFont;
    private final Sprite image;
    private boolean showText;
    private double waitTime;
    private boolean gameStarted;
    private double gameStartedTime;
    
    public Title(SceneManager sceneManager) {
        super(sceneManager);
        soundManager = sceneManager.getGame().getSoundManager();
        bitmapFont = sceneManager.getGame().getBitmapFont();
        image = new Sprite("title_screen.png");
        image.getPivot().setLocation(0, 0);
    }
    
    @Override
    public void onEnter() {
        waitTime = -1;
        gameStarted = false;
        showText = false;
    }
    
    @Override
    public void update() {
        if (!showText) {
            showText = true;
        }
        else if (waitTime < 0) {
            waitTime = Time.getCurrent() + 15;            
        }
        else if (Time.getCurrent() > waitTime) {
            getSceneManager().setNextScene(SceneManager.OL_PRESENTS);
        }
        else if (!gameStarted && Keyboard.isKeyPressedOnce(KeyEvent.VK_SPACE)) {
            gameStarted = true;
            gameStartedTime = Time.getCurrent() + 3;
            soundManager.play(Sounds.START_GAME);
        }
        else if (gameStarted && Time.getCurrent() > gameStartedTime) {
            getSceneManager().getGame().startGame();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        image.draw(g, 0, 0);
        
        if (!showText) {
            return;
        }
        
        bitmapFont.drawText(g, "(C) KONAMI 1985", 9, 13);
        bitmapFont.drawText(g, "(C) O.L. 2018", 9, 14);
        if (gameStarted) {
            boolean blink = (int) (Time.getCurrent() * 10) % 2 == 0;
            if (blink) {
                bitmapFont.drawText(g, "              ", 9, 18);
            }
            else {
                bitmapFont.drawText(g, "  PLAY START  ", 9, 18);
            }
        }
        else {
            bitmapFont.drawText(g, "PUSH SPACE KEY", 9, 18);
        }
    }
    
}
