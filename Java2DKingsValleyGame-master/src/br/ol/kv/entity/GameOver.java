package br.ol.kv.entity;

import br.ol.kv.infra.Entity;
import br.ol.kv.infra.Game;
import br.ol.kv.infra.Scene;
import br.ol.kv.renderer.BitmapFont;
import java.awt.Graphics2D;

/**
 * GameOver class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class GameOver extends Entity {
    
    private final BitmapFont bitmapFont;
    private boolean visible;
    
    public GameOver(Scene scene) {
        super(scene);
        Game game = scene.getSceneManager().getGame();
        bitmapFont = game.getBitmapFont();
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) {
            return;
        }
        bitmapFont.drawText(g, "               ", 8, 10);
        bitmapFont.drawText(g, "   GAME OVER   ", 8, 11);
        bitmapFont.drawText(g, "               ", 8, 12);
    }
    
    public void show() {
        visible = true;
    }
    
}
