package br.ol.kv.entity;

import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.physics.Body;
import java.awt.Graphics2D;

/**
 * Pickaxe class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Pickaxe extends Item {
    
    private boolean consumed;
    
    public Pickaxe(Scene scene, Player player, int col, int row) {
        super(scene, player);
        addPickaxeStates();
        addTransitions();
        createBody(col, row);
        addAnimations();
        // initial state
        stateManager.setInitialState("collectable");
    }
    
    private void addPickaxeStates() {
        
    }
    
    private void addTransitions() {
        
    }

    private void createBody(int col, int row) {
        body = new Body<>(this, 8 * col + 4, 8 * (row + 1) - 1, 8, 8, 4, 7, 9);
        world.addBody(body);
    }
    
    private void addAnimations() {
        animationManager.addAnimation("collectable", "pickaxe_collectable.png");
        animationManager.addAnimation("dropping", "pickaxe_collectable.png");
        
        animationManager.addAnimation("collected_left"
                , "pickaxe_player_holding_left_0.png", "pickaxe_player_holding_left_1.png"
                , "pickaxe_player_holding_left_0.png", "pickaxe_player_holding_left_2.png");

        animationManager.addAnimation("collected_right"
                , "pickaxe_player_holding_right_0.png", "pickaxe_player_holding_right_1.png"
                , "pickaxe_player_holding_right_0.png", "pickaxe_player_holding_right_2.png");
    }
    
    @Override
    protected boolean collectedByPlayer() {
        if (player.getHoldingItem() == null 
                && player.getStateManager().isCurrentState("playing")) {
            player.setHoldingItem(this);
            soundManager.play(Sounds.PICKUP);
            // hudInfo.addScore(Scores.PICKAXE_PICKUP);
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    protected void drawCollected(Graphics2D g) {
        if (!consumed && player.getDoorNext() == null) {
            int playerDirection = player.getBody().getLastDirection().equals("left") ? -1 : 1;
            int x = player.getBody().getX() - playerDirection * 5;
            int y = player.getBody().getY();
            animationManager.draw(g, x, y);
        }
    }

    @Override
    public void trigger() {
        dig();
    }
    
    private void dig() {
        consumed = true;
        player.setHoldingItem(null);
        hudInfo.addScore(ScoreTable.PICKAXE_DIG);
    }

    @Override
    public void drop() {
        player.setHoldingItem(null);
        dropped = true;
        collected = false;
    }
    
}
