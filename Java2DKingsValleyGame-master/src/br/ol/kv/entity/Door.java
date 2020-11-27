package br.ol.kv.entity;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Entity;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Keyboard;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.World;
import br.ol.kv.renderer.Sprite;
import com.sun.glass.events.KeyEvent;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Door class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Door extends Entity {
    
    private final Player player;
    
    private final int nextLevel;
    
    private final int switchCol;
    private final int switchRow;

    private final int switchX;
    private final int switchY;
    private final int doorX;
    private final int doorY;
    
    private final World world;
    private final Body doorSwitchBody;
    private final Body doorBody;
    
    private final Rectangle clipArea;
    
    private final Sprite doorBackground;
    private final AnimationManager doorAnimationManager;
    private final AnimationManager switchAnimationManager;
    
    private final SoundManager soundManager;
    private final HUDInfo hudInfo;
    
    private boolean open;
    private boolean visible = true;
    
    public Door(Scene scene, Player player, int nextLevel, int switchCol, int switchRow) {
        super(scene);
        this.player = player;
        this.nextLevel = nextLevel;
        this.switchCol = switchCol;
        this.switchRow = switchRow;
        switchX = switchCol * 8;
        switchY = switchRow * 8;
        doorX = (switchCol + 2) * 8;
        doorY = switchRow * 8;
        doorBackground = new Sprite("door_background.png");
        clipArea = new Rectangle();
        doorAnimationManager = new AnimationManager();
        switchAnimationManager = new AnimationManager();
        soundManager = getScene().getSceneManager().getGame().getSoundManager();
        hudInfo = getScene().getSceneManager().getGame().getHudInfo();
        world = getScene().getSceneManager().getGame().getWorld();
        doorSwitchBody = new Body<>(this, switchX, switchY, 8, 8, 0, 0, 9);
        doorBody = new Body<>(this, doorX, doorY, 8 * 2, 8 * 3, 0, 0, 9);
        world.addBody(doorSwitchBody);
        world.addBody(doorBody);
        addAnimations();
        // initial state
        if (nextLevel == 0) {
            open = true;
            visible = true;
            doorAnimationManager.setAnimation("door_open", true);
            switchAnimationManager.setAnimation("switch_on");
        }
        else {
            open = false;
            visible = false;
            doorAnimationManager.setAnimation("door_closed", true);
            switchAnimationManager.setAnimation("switch_off");
        }
    }
    
    private void addAnimations() {
        // door switch
        
        switchAnimationManager.addAnimation("switch_on", true, 0.5
                , "door_switch_on_0.png", "door_switch_on_1.png");
        
        switchAnimationManager.addAnimation("switch_off", true, 0.5
                , "door_switch_off_0.png", "door_switch_off_1.png");

        // door
        
        doorAnimationManager.addAnimation("door_open", "door_animation_0.png");
        doorAnimationManager.addAnimation("door_closed", "door_animation_2.png");
        
        doorAnimationManager.addAnimation("door_opening", false, 0.05
                ,"door_animation_2.png" , "door_animation_1.png"
                , "door_animation_0.png", "door_animation_0.png");
        
        doorAnimationManager.addAnimation("door_closing", false, 0.05
                ,"door_animation_0.png", "door_animation_1.png"
                , "door_animation_2.png", "door_animation_2.png");
    }

    public int getNextLevel() {
        return nextLevel;
    }

    public int getSwitchCol() {
        return switchCol;
    }

    public int getSwitchRow() {
        return switchRow;
    }

    public int getSwitchX() {
        return switchX;
    }

    public int getSwitchY() {
        return switchY;
    }

    public int getDoorX() {
        return doorX;
    }

    public int getDoorY() {
        return doorY;
    }

    public boolean isVisible() {
        return visible;
    }

    public Rectangle getClipArea() {
        int w = doorBackground.getImage().getWidth();
        int h = doorBackground.getImage().getHeight();
        clipArea.setBounds(doorX - 16, doorY, w + 16, h);
        return clipArea;
    }

    @Override
    public void update() {
        // show if all treasures collected
        if (nextLevel != 0 && !visible 
                && getScene().getSceneManager().getGame().isAllTreasuresCollected()) {
            visible = true;
        }
        if (!visible) {
            return;
        }
        if (open) {
            switchAnimationManager.setAnimation("switch_on");
        }
        else {
            switchAnimationManager.setAnimation("switch_off");
        }
        switchAnimationManager.update();
        doorAnimationManager.update();
        updateDoor();
    }
    
    private void updateDoor() {
        // switch activated, open door
        if (!open) {
            Body<Player> playerBody = world.checkCollision(doorSwitchBody, Player.class);
            if (playerBody != null) {
                open();
                hudInfo.addScore(ScoreTable.DOOR_SWITCH);
            }
        }
        // player enter door
        else if (isCompletelyOpened()) {
            Body<Player> playerBody = world.checkCollision(doorBody, Player.class);
            if (playerBody != null && player.getBody().collidingWithTerrainFloor()
                    && Keyboard.isKeyDown(KeyEvent.VK_UP) && player.getDoorNext() == null) {
                player.enterDoorNext(this);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) {
            return;
        }
        doorBackground.draw(g, doorX + 8, doorY + 23);
        switchAnimationManager.draw(g, switchX + 4, switchY + 9);
        doorAnimationManager.draw(g, doorX + 8, doorY + 23);
    }
    
    public void drawAfter(Graphics2D g) {
        if (!visible) {
            return;
        }
        if (nextLevel != 0 
                && doorAnimationManager.getCurrentAnimation().getName().equals("door_closing")) {
            doorAnimationManager.draw(g, doorX + 8, doorY + 23);
        }
    }

    public boolean isOpen() {
        return open;
    }
    
    public void open() {
        open = true;
        doorAnimationManager.setAnimation("door_opening", true);
        soundManager.play(Sounds.DOOR);
    }

    public void close() {
        open = false;
        doorAnimationManager.setAnimation("door_closing", true);
        if (nextLevel == getScene().getSceneManager().getGame().getHudInfo().getLevel() + 1) {
            soundManager.play(Sounds.RIGHT_DOOR);
        }
        else {
            soundManager.play(Sounds.DOOR);
        }
    }
    
    public boolean isCompletelyClosed() {
        return !open && doorAnimationManager.getCurrentAnimation().isFinished();
    }

    public boolean isCompletelyOpened() {
        return open && doorAnimationManager.getCurrentAnimation().isFinished();
    }
    
    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }
    
}
