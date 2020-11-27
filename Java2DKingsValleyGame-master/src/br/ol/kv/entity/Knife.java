package br.ol.kv.entity;

import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.physics.Body;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Knife class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Knife extends Item {
    
    private boolean throwing = false;
    private int throwingDirection; // always 1 or -1
    private final Point throwingPosition = new Point();
    private boolean rebounding = false;
    
    public Knife(Scene scene, Player player, int col, int row) {
        super(scene, player);
        addKnifeStates();
        addTransitions();
        createBody(col, row);
        addAnimations();
        // initial state
        stateManager.setInitialState("collectable");
    }
    
    private void addKnifeStates() {
        stateManager.addState(new ThrowingState());
        stateManager.addState(new ReboundingState());
    }
    
    private void addTransitions() {
        stateManager.addTransition("collected", "throwing", () -> throwing);
        stateManager.addTransition("throwing", "rebounding", () -> rebounding);
        stateManager.addTransition("rebounding", "collectable", () -> !collected);
    }
    
    private void createBody(int col, int row) {
        body = new Body<>(this, 8 * col + 4, 8 * (row + 1) - 1, 8, 8, 4, 7, 9);
        world.addBody(body);
    }
    
    private void addAnimations() {
        animationManager.addAnimation("collectable", "knife_static.png");
        
        animationManager.addAnimation("collected_left"
                , "knife_player_holding_left_0.png", "knife_player_holding_left_1.png"
                , "knife_player_holding_left_0.png", "knife_player_holding_left_2.png");

        animationManager.addAnimation("collected_right"
                , "knife_player_holding_right_0.png", "knife_player_holding_right_1.png"
                , "knife_player_holding_right_0.png", "knife_player_holding_right_2.png");
        
        animationManager.addAnimation("throwing_left", true, 0.5
                , "knife_rotating_0.png", "knife_rotating_1.png"
                , "knife_rotating_2.png", "knife_rotating_3.png");

        animationManager.addAnimation("throwing_right", true, 0.5
                , "knife_rotating_3.png", "knife_rotating_2.png"
                , "knife_rotating_1.png", "knife_rotating_0.png");
        
        animationManager.addAnimation("rebounding", true, 0.5
                , "knife_rotating_0.png", "knife_rotating_1.png"
                , "knife_rotating_2.png", "knife_rotating_3.png");
    }
    
    @Override
    protected boolean collectedByPlayer() {
        if (player.getHoldingItem() == null 
                && player.getStateManager().isCurrentState("playing")) {
            player.setHoldingItem(this);
            soundManager.play(Sounds.PICKUP);
            hudInfo.addScore(ScoreTable.KNIFE_PICKUP);
            return true;
        }
        else {
            return false;
        }
    }
    
    protected class ThrowingState extends ItemState {
        private final Body<Knife> knifeTmpBody;
        private static final int THROWING_SPEED = 3;
        private int previousX;
        
        public ThrowingState() {
            super("throwing");
            knifeTmpBody = new Body<>(Knife.this, 0, 0, 8, 8, 4, 7, 9);
        }
        
        @Override
        public void onEnter() {
            body.setX(throwingPosition.x);
            body.setY(throwingPosition.y);
            body.setMovable(false);
            body.setVx(throwingDirection * THROWING_SPEED);
            body.setAffectedByGravity(false);
            animationManager.setAnimation("throwing_" + player.getBody().getLastDirection());
        }

        @Override
        public void update() {
            previousX = body.getX();
            body.update();
            super.update();
            checkHitEnemy();
            checkHitWall();
            // checkHitItem();
        }
        
        private void checkHitEnemy() {
            // very simple way to implement CCD (continuous collision detection) for this knife
            knifeTmpBody.setY(body.getY());
            knifeTmpBody.setX(previousX);
            for (int k = 0; k < THROWING_SPEED; k++) {
                knifeTmpBody.setX(knifeTmpBody.getX() + throwingDirection);
                Body<Enemy> enemyBody = world.checkCollision(knifeTmpBody, Enemy.class);
                if (throwing && enemyBody != null && enemyBody.getOwner().isAlive()) {
                    enemyBody.getOwner().kill();
                    throwing = false;
                    rebounding = true;
                    break;
                }
            }
        }
        
        private void checkHitWall() {
            if (throwing && (body.collidingWithTerrainLeft() || body.collidingWithTerrainRight())) {
                throwing = false;
                rebounding = true;
            }
        }

//        private void checkHitItem() {
//            Body<Item> itemBody = world.checkCollision(body, Item.class);
//            if (itemBody != null && !itemBody.getOwner().isCollected()) {
//                throwing = false;
//                rebounding = true;
//            }
//        }
        
    }
    
    protected class ReboundingState extends ItemState {
        public ReboundingState() {
            super("rebounding");
        }
        
        @Override
        public void onEnter() {
            body.setAffectedByGravity(true);
            rebound();
        }
        
        private void rebound() {
            body.setVy(-5);
            body.setVx(throwingDirection * -0.5);
            animationManager.setAnimation("rebounding");
            soundManager.stop(Sounds.HIT);
            soundManager.play(Sounds.HIT);
        }
        
        @Override
        public void onExit() {
            body.reset();
            body.setMovable(true);
        }
        
        @Override
        public void update() {
            body.update();
            super.update(); 
            checkRebound();
            checkReboundingFinished();
        }
        
        private void checkReboundingFinished() {
            body.updateBounds();
            
            boolean collidingWithTerrainFloor = terrain.isRigidByPosition(
                    body.getMiddleXBound(), body.getBottomBound() + 1
                    , body.getMinTerrainCollisionY()) 
                    && (body.getBottomBound() + 1) % 8 == 0;
            
            if (collidingWithTerrainFloor) {
                rebounding = false;
                collected = false;
                body.setX(body.getX() - (body.getX() % 8) + 4);
            }
        }

        private void checkRebound() {
            Body<Item> itemBody = world.checkCollision(body, Item.class);
            if (itemBody != null && !itemBody.getOwner().isCollected()) {
                rebound();
            }
        }
    }

    @Override
    protected void drawCollected(Graphics2D g) {
        if (!player.isKnifeThrowed() && player.getDoorNext() == null) {
            int playerDirection = player.getBody().getLastDirection().equals("left") ? -1 : 1;
            int x = player.getBody().getX() + playerDirection * 4;
            int y = player.getBody().getY();
            animationManager.draw(g, x, y);
        }
    }
    
    @Override
    public void trigger() {
        throwKnife();
    }
    
    private void throwKnife() {
        int playerDirection = player.getBody().getLastDirection().equals("left") ? -1 : 1;
        int x = player.getBody().getX() + playerDirection * 2;
        int y = player.getBody().getY() - 8;
        throwing = true;
        throwingDirection = playerDirection;
        throwingPosition.setLocation(x, y);
        player.setHoldingItem(null);
        soundManager.play(Sounds.THROW);
        hudInfo.addScore(ScoreTable.KNIFE_THROW);
    }
    
}
