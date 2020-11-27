package br.ol.kv.entity;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Entity;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.State;
import br.ol.kv.infra.StateManager;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import java.awt.Graphics2D;

/**
 * Item class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public abstract class Item extends Entity {

    protected Player player;
    protected final World world;
    protected final Terrain terrain;
    protected final StateManager<Item> stateManager;
    protected final AnimationManager animationManager;
    protected final SoundManager soundManager;
    protected final HUDInfo hudInfo;
    protected Body<Entity> body;
    protected boolean collected;
    protected boolean dropped;

    public Item(Scene scene, Player player) {
        super(scene);
        this.player = player;
        soundManager = scene.getSceneManager().getGame().getSoundManager();
        hudInfo = scene.getSceneManager().getGame().getHudInfo();
        world = scene.getSceneManager().getGame().getWorld();
        terrain = world.getTerrain();
        stateManager = new StateManager<>(this);
        addItemStates();
        addTransitions();
        animationManager = new AnimationManager();
        // initial state
        stateManager.setInitialState("collectable");
    }

    private void addItemStates() {
        stateManager.addState(new CollectableState());
        stateManager.addState(new CollectedState());
        stateManager.addState(new DroppingState());
    }
    
    private void addTransitions() {
        stateManager.addTransition("collectable", "collected", () -> collected);
        stateManager.addTransition("collected", "dropping", () -> dropped);
        stateManager.addTransition("dropping", "collectable", () -> !dropped);
    }

    public boolean isCollected() {
        return collected;
    }

    @Override
    public void update() {
        stateManager.update();
    }
    
    private void updateCheckCollected() {
        if (collected) {
            return;
        }
        Body<Player> playerBody = world.checkCollision(body, Player.class);
        if (playerBody != null && !playerBody.collidingWithTerrain()) {
            collected = collectedByPlayer();
        }
    }
    
    protected boolean collectedByPlayer() {
        return true;
    }
    
    public void trigger() {
        // override
    }

    public void drop() {
        // override
    }
    
    @Override
    public void draw(Graphics2D g) {
        stateManager.draw(g);
    }

    protected class ItemState extends State<Item> {
        public ItemState(String name) {
            super(name);
        }

        @Override
        public void update() {
            setAnimation();
            animationManager.update();
        }
        
        protected void setAnimation() {
            // override
        }
        
        @Override
        public void draw(Graphics2D g) {
            animationManager.draw(g, body.getX(), body.getY());
            // body.drawDebug(g);
        }
    }
    
    protected class CollectableState extends ItemState {
        public CollectableState() {
            super("collectable");
        }

        @Override
        public void update() {
            updateCheckCollected();
            super.update(); 
        }
        
        @Override
        public void onEnter() {
            dropped = false;
            animationManager.setAnimation("collectable");
        }
    }
    
    protected class CollectedState extends ItemState {
        public CollectedState() {
            super("collected");
        }

        @Override
        public void update() {
            if (player.getBody().getVx() != 0) {
                super.update(); 
            }
            animationManager.setAnimation("collected_" + player.getBody().getLastDirection());
        }

        @Override
        public void draw(Graphics2D g) {
            drawCollected(g);
        }
    }
    
    protected void drawCollected(Graphics2D g) {
        animationManager.draw(g, player.getBody().getX(), player.getBody().getY());
    }

    protected class DroppingState extends ItemState {
        public DroppingState() {
            super("dropping");
        }

        @Override
        public void onEnter() {
            int direction = player.getBody().getLastDirection().equals("left") ? -1 : 1;
            body.setX(player.getBody().getX());
            body.setY(player.getBody().getY());
            body.setVx(direction);
            body.setVy(-3);
            body.setMovable(false);
            soundManager.play(Sounds.DROPPED);
            animationManager.setAnimation("dropping");
        }

        @Override
        public void onExit() {
            body.setMovable(true);
        }
        
        @Override
        public void update() {
            body.update();
            super.update();
            if (body.collidingWithTerrainFloor()) {
                dropped = false;
            }
        }
    }
    
}
