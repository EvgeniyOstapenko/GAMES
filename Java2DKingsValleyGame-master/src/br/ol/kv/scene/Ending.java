package br.ol.kv.scene;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.entity.HUD;
import br.ol.kv.entity.Player;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.Time;
import br.ol.kv.renderer.BitmapFont;
import br.ol.kv.renderer.Sprite;
import java.awt.Graphics2D;

/**
 * Ending class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Ending extends Scene {
    
    private final HUD hud;
    private final HUDInfo hudInfo;
    private final SoundManager soundManager;
    private final Player player;
    private final BitmapFont bitmapFont;
    private final Sprite background;
    private double waitTime;
    
    public Ending(SceneManager sceneManager) {
        super(sceneManager);
        soundManager = sceneManager.getGame().getSoundManager();
        bitmapFont = sceneManager.getGame().getBitmapFont();
        background = new Sprite("ending_screen.png");
        background.getPivot().setLocation(0, 0);
        hud = new HUD(this);
        hud.setShowFooter(false);
        hudInfo = hud.getHudInfo();
        player = new Player(this, null, null);
    }

    @Override
    public void onEnter() {
        waitTime = -1;
        player.gameCleared();
    }
    
    @Override
    public void update() {
        player.update();
        if (player.isGameClearedFinished() && waitTime < 0) {
            waitTime = Time.getCurrent() + 10;
            hudInfo.addScore(10000);
            soundManager.play(Sounds.NEXT_MAP);
        }
        else if (waitTime > 0 && Time.getCurrent() > waitTime) {
            getSceneManager().getGame().gameOver();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        background.draw(g, 0, 0);
        hud.draw(g);
        if (player.isGameClearedFinished()) {
            bitmapFont.drawText(g, "CONGRATULATIONS", 9, 6);
            bitmapFont.drawText(g, "SPECIAL BONUS  10000", 6, 8);
        }
        player.draw(g);
    }
    
}
