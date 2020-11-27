package br.ol.kv.entity;

import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Keyboard;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.infra.Time;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;

/**
 * Player class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Player extends Actor {
    
    private Door doorStart;
    private Door doorNext;
    
    private Gate gate;
    
    private final BrickDigging brickDigging;
    private final GameOver gameOver;
    
    private Item holdingItem;
    private boolean killed;
    private boolean knifeThrowed;
    private boolean digging;
    private boolean gameClearedFinished;
    private boolean fallingSoundPlayed;
    
    public Player(Scene scene, BrickDigging brickDigging, GameOver gameOver) {
        super(scene);
        this.brickDigging = brickDigging;
        this.gameOver = gameOver;
        addStates();
        addTransitions();
        loadAnimations();
    }

    private void addStates() {
        stateManager.addState(new InitState());
        stateManager.addState(new DoorOutState());
        stateManager.addState(new DoorInState());
        stateManager.addState(new ThrowingKnifeState());
        stateManager.addState(new DiggingState());
        stateManager.addState(new PassingGateState());
        stateManager.addState(new GameClearedState());
    }

    private void addTransitions() {
        stateManager.addTransition("init", "door_out", () -> { 
            InitState initState = (InitState) stateManager.getStates().get("init");
            return initState.startTime > 0 && Time.getCurrent() - initState.startTime > 0.5;
        });
        
        stateManager.addTransition("door_out", "playing", () -> !doorStart.isVisible());
        stateManager.addTransition("playing", "throwing_knife", () -> knifeThrowed);
        stateManager.addTransition("playing", "digging", () -> digging);
        stateManager.addTransition("throwing_knife", "playing", () -> animationManager.getCurrentAnimation().isFinished());
        stateManager.addTransition("digging", "playing", () -> animationManager.getCurrentAnimation().isFinished() || !brickDigging.isDigging());
        stateManager.addTransition("playing", "door_in", () -> doorNext != null);
        stateManager.addTransition("playing", "passing_gate", () -> gate != null);
        stateManager.addTransition("passing_gate", "playing", () -> gate == null);
    }

    private void loadAnimations() {
        animationManager.addAnimation("idle_right", "player_right_1.png");
        animationManager.addAnimation("idle_left", "player_left_1.png");
        
        animationManager.addAnimation("walk_right", "player_right_0.png"
                , "player_right_1.png", "player_right_0.png", "player_right_2.png");
        
        animationManager.addAnimation("walk_left", "player_left_0.png"
                , "player_left_1.png", "player_left_0.png", "player_left_2.png");
        
        animationManager.addAnimation("jump_right", "player_right_0.png");
        animationManager.addAnimation("jump_left", "player_left_0.png");
        
        animationManager.addAnimation("throwing_knife_left", false, 0.2
                , "player_throwing_knife_left_0.png", "player_throwing_knife_left_1.png"
                , "player_throwing_knife_left_1.png", "player_throwing_knife_left_1.png");
        
        animationManager.addAnimation("throwing_knife_right", false, 0.2
                , "player_throwing_knife_right_0.png", "player_throwing_knife_right_1.png"
                , "player_throwing_knife_right_1.png", "player_throwing_knife_right_1.png");

        animationManager.addAnimation("digging_left", false, 0.25
                , "player_digging_left_0.png", "player_digging_left_1.png"
                , "player_digging_left_0.png", "player_digging_left_1.png"
                , "player_digging_left_0.png", "player_digging_left_1.png"
                , "player_digging_left_0.png", "player_digging_left_1.png"
                , "player_digging_left_0.png", "player_digging_left_1.png"
                , "player_digging_left_0.png", "player_digging_left_1.png" );
        
        animationManager.addAnimation("digging_right", false, 0.25
                , "player_digging_right_0.png", "player_digging_right_1.png"
                , "player_digging_right_0.png", "player_digging_right_1.png"
                , "player_digging_right_0.png", "player_digging_right_1.png"
                , "player_digging_right_0.png", "player_digging_right_1.png"
                , "player_digging_right_0.png", "player_digging_right_1.png"
                , "player_digging_right_0.png", "player_digging_right_1.png" );
    }

    public void gameCleared() {
        stateManager.setInitialState("game_cleared");
        update();
    }

    public boolean isGameClearedFinished() {
        return gameClearedFinished;
    }
    
    public Door getDoorStart() {
        return doorStart;
    }

    public void setDoorStart(Door doorStart) {
        this.doorStart = doorStart;
        // initial state
        stateManager.setInitialState("init");
    }

    public Door getDoorNext() {
        return doorNext;
    }

    public void enterDoorNext(Door doorNext) {
        this.doorNext = doorNext;
    }

    public Item getHoldingItem() {
        return holdingItem;
    }

    public void setHoldingItem(Item holdingItem) {
        this.holdingItem = holdingItem;
    }

    public Gate getGate() {
        return gate;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }

    public boolean isKnifeThrowed() {
        return knifeThrowed;
    }

    public boolean isDigging() {
        return digging;
    }

    public boolean isPlaying() {
        return stateManager.isCurrentState("playing")
                || stateManager.isCurrentState("throwing_knife")
                || stateManager.isCurrentState("digging")
                || stateManager.isCurrentState("passing_gate");
    }
    
    @Override
    public void update() {
        if (killed) {
            // game over moved to Playing scene
            getScene().getSceneManager().getGame().tryNextLife();
            return;
        }
        
        if (stateManager.isCurrentState("playing")) {
            updateQuit();
            updateMoveKeyboardControl();
            checkFalling();
        }
        
        super.update();
    }
    
    private void updateQuit() {
        if (Keyboard.isKeyDown(com.sun.glass.events.KeyEvent.VK_ESCAPE)) {
            kill();
        }
    }
    
    private void updateMoveKeyboardControl() {
        body.resetMovement();
        
        if (Keyboard.isKeyDown(KeyEvent.VK_LEFT)) {
            body.moveLeft();
        }
        else if (Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) {
            body.moveRight();
        }

        if (Keyboard.isKeyDown(KeyEvent.VK_UP)) {
            body.moveUp();
        }
        else if (Keyboard.isKeyDown(KeyEvent.VK_DOWN)) {
            body.moveDown();
        }

        // drop pickaxe
        if (Keyboard.isKeyPressedOnce(KeyEvent.VK_Z) && !body.isOnStair()) {
            if (holdingItem != null && holdingItem instanceof Pickaxe && body.collidingWithTerrainFloor()) {
                int playerDirection = body.getLastDirection().equals("left") ? -1 : 1;
                int playerCol = body.getX() / Terrain.TILE_SIZE;
                int brickCol = playerCol + playerDirection;
                int brickRow = body.getY() / Terrain.TILE_SIZE + 1;
                if (body.getWorld().getTerrain().canDrop(playerCol, brickCol, brickRow)) {
                    holdingItem.drop();
                }
            }
        }
        // use item or jump
        else if (Keyboard.isKeyPressedOnce(KeyEvent.VK_SPACE) && !body.isOnStair()) {
            if (holdingItem != null && holdingItem instanceof Knife && body.collidingWithTerrainFloor()) {
                knifeThrowed = true;
            }
            else if (holdingItem != null && holdingItem instanceof Pickaxe && body.collidingWithTerrainFloor()) {
                int playerDirection = body.getLastDirection().equals("left") ? -1 : 1;
                int playerCol = body.getX() / Terrain.TILE_SIZE;
                int brickCol = playerCol + playerDirection;
                int brickRow = body.getY() / Terrain.TILE_SIZE + 1;
                digging = body.getWorld().getTerrain().canDig(playerCol, brickCol, brickRow);
            }
            else {
                body.moveJump();
            }
        }
    }

    public void checkFalling() {
        if (!fallingSoundPlayed && !body.isOnStair() 
                && body.getVy() > World.GRAVITY 
                && body.getY() > (body.getMinTerrainCollisionY() + body.getPivotY() + body.getMinTerrainCollisionRangeY())) {
            soundManager.play(Sounds.FALL);
            fallingSoundPlayed = true;
        }
        else if (fallingSoundPlayed && body.collidingWithTerrainFloor()) {
            soundManager.stop(Sounds.FALL);
            fallingSoundPlayed = false;
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (holdingItem != null 
                && (holdingItem instanceof Knife || holdingItem instanceof Pickaxe) ) {
            holdingItem.draw(g);
        }
    }
    
    protected class InitState extends ActorState {
        public double startTime = -1;
        
        public InitState() {
            super("init");
        }

        @Override
        public void onEnter() {
            animationManager.setAnimation("idle_left");
            body.setX(doorStart.getDoorX() + 8);
            body.setY(doorStart.getDoorY() + 16);
        }
        
        @Override
        public void update() {
            if (startTime < 0) {
                startTime = Time.getCurrent();
            }
        }
    }
    
    protected class DoorOutState extends ActorState {
        public DoorOutState() {
            super("door_out");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("walk_left", true);
        }

        @Override
        public void onExit() {
            soundManager.play(Sounds.PLAYING, true);
        }
        
        @Override
        public void update() {
            animationManager.update();
            if (!body.collidingWithTerrainFloor()) {
                body.setX(body.getX() - 1);
                body.setY(body.getY() + 1);
            }
            else if (doorStart.isOpen()) {
                doorStart.close();
                animationManager.setAnimation("idle_left");
            }
            else if (doorStart.isCompletelyClosed()) {
                doorStart.hide();
            }
        }

        @Override
        public void draw(Graphics2D g) {
            Shape originalClip = g.getClip();
            g.setClip(doorStart.getClipArea());
            super.draw(g); 
            g.setClip(originalClip);
        }
        
        
    }

    protected class DoorInState extends ActorState {
        private boolean xPositionCorrect;
        private int doorCloseXPosition;
        private boolean visible;
        private boolean useDoorClip;
        
        public DoorInState() {
            super("door_in");
        }
        
        @Override
        public void onEnter() {
            visible = true;
            xPositionCorrect = false;
            useDoorClip = false;
            holdingItem = null;
            soundManager.stopAll();
            hudInfo.addScore(ScoreTable.DOOR_IN);
        }

        @Override
        public void update() {
            if (!xPositionCorrect) {
                int targetX = doorNext.getDoorX() + 8;
                int dif = targetX - body.getX();
                if (dif > 0) {
                    body.setX(body.getX() + 1);
                    animationManager.setAnimation("walk_right");
                }
                else if (dif < 0) {
                    body.setX(body.getX() - 1);
                    animationManager.setAnimation("walk_left");
                }
                else {
                    useDoorClip = true;
                    xPositionCorrect = true;
                    doorCloseXPosition = doorNext.getDoorX() + 19;
                    doorNext.close();
                }
            }
            else if (body.getX() < doorCloseXPosition) {
                body.setX(body.getX() + 1);
                body.setY(body.getY() - 1);
                animationManager.setAnimation("walk_right");
            }
            else if (!doorNext.isCompletelyClosed()) {
                visible = false;
            }
            else {
                getScene().getSceneManager().getGame().nextLevel(doorNext.getNextLevel());
                return;
            }
            animationManager.update();
        }

        @Override
        public void draw(Graphics2D g) {
            if (visible) {
                Shape originalClip = g.getClip();
                if (useDoorClip) {
                    g.setClip(doorNext.getClipArea());
                }
                super.draw(g); 
                g.setClip(originalClip);
            }
        }
    }
    
    protected class ThrowingKnifeState extends ActorState {
        public ThrowingKnifeState() {
            super("throwing_knife");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("throwing_knife_" + body.getLastDirection(), true);
        }
        
        @Override
        public void onExit() {
            knifeThrowed = false;
            // holdingItem = null;
        }

        @Override
        public void update() {
            if (animationManager.getCurrentAnimation().getCurrentFrame() > 1 
                    && holdingItem != null && holdingItem instanceof Knife) {
                holdingItem.trigger();
            }
            super.update(); 
        }
    }

    protected class DiggingState extends ActorState {
        public DiggingState() {
            super("digging");
        }
        
        @Override
        public void onEnter() {
            holdingItem.trigger();
            animationManager.setAnimation("digging_" + body.getLastDirection(), true);
            int playerDirection = body.getLastDirection().equals("left") ? -1 : 1;
            int col = body.getX() / Terrain.TILE_SIZE + playerDirection;
            int row = body.getY() / Terrain.TILE_SIZE + 1;
            brickDigging.dig(col, row);
        }
        
        @Override
        public void onExit() {
            digging = false;
        }

        @Override
        public void update() {
            super.update(); 
        }
    }

    protected class PassingGateState extends ActorState {
        public PassingGateState() {
            super("passing_gate");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("idle_" + body.getLastDirection(), true);
        }
    }

    protected class GameClearedState extends ActorState {
        private boolean walking;
        private boolean jumping;
        
        public GameClearedState() {
            super("game_cleared");
        }
        
        @Override
        public void onEnter() {
            gameClearedFinished = false;
            body.setX(248);
            body.setY(151);
            body.setMovable(false);
            walking = true;
            jumping = false;
        }

        @Override
        public void onExit() {
            body.setMovable(true);
        }
        
        @Override
        public void update() {
            super.update(); 
            
            if (jumping && body.getY() > 85 && body.getVy() > 0) {
                jumping = false;
                gameClearedFinished = true;
            }
            else if (jumping) {
                body.setVy(body.getVy() + World.GRAVITY);
                body.setY((int) (body.getY() + body.getVy()));
                body.setX(body.getX() - 1);
            }
            else if (walking && body.getX() > 152) {
                body.setX(body.getX() - 1);
                animationManager.setAnimation("walk_left");
            }
            else if (walking) {
                walking = false;
                jumping = true;
                body.setVy(-8.5);
                animationManager.setAnimation("jump_left");
            }
        }
    }
    
    public void kill() {
        if (killed) {
            return;
        }
        killed = true;
        soundManager.stopAll();
        soundManager.play(Sounds.CAUGHT);
        getScene().pause(3);
    }

    public boolean isKilled() {
        return killed;
    }
    
}
