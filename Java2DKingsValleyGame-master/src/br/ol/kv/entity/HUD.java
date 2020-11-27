package br.ol.kv.entity;

import br.ol.kv.infra.Entity;
import br.ol.kv.infra.Game;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Scene;
import br.ol.kv.renderer.BitmapFont;
import java.awt.Graphics2D;

/**
 * HUD class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HUD extends Entity {
    
    private final HUDInfo hudInfo;
    private final BitmapFont bitmapFont;
    private boolean showFooter = true;
    
    public HUD(Scene scene) {
        super(scene);
        Game game = scene.getSceneManager().getGame();
        bitmapFont = game.getBitmapFont();
        hudInfo = game.getHudInfo();
    }

    public HUDInfo getHudInfo() {
        return hudInfo;
    }

    public boolean isShowFooter() {
        return showFooter;
    }

    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
    }

    @Override
    public void draw(Graphics2D g) {
        bitmapFont.drawText(g, " SCORE-" + hudInfo.getScoreStr(), 0, 0);
        bitmapFont.drawText(g, "HI-" + hudInfo.getHiscoreStr(), 14, 0);
        bitmapFont.drawText(g, "REST-" + hudInfo.getLivesStr() + " ", 24, 0);
        if (showFooter) {
            //bitmapFont.drawText(g, "(C) O.L.", 1, 23);
            //bitmapFont.drawText(g, "PYRAMID-" + hudInfo.getLevelStr(), 11, 23);
            //bitmapFont.drawText(g, "ESC=QUIT" + hudInfo.getLevelStr(), 24, 23);
            bitmapFont.drawText(g, " PYRAMID-" + hudInfo.getLevelStr(), 0, 23);
            bitmapFont.drawText(g, "      Z=DROP ESC=QUIT " + hudInfo.getLevelStr(), 10, 23);
        }
    }
    
}
