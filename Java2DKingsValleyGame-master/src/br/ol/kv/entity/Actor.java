package br.ol.kv.entity;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Entity;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.State;
import br.ol.kv.infra.StateManager;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.World;
import java.awt.Graphics2D;

/**
 * Actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public abstract class Actor extends Entity {
    
    protected final Body<Entity> body;
    protected final AnimationManager animationManager;
    protected final StateManager<Actor> stateManager;
    protected final SoundManager soundManager;
    protected final HUDInfo hudInfo;
    
    public Actor(Scene scene) {
        super(scene);
        World world = scene.getSceneManager().getGame().getWorld();
        body = new Body<>(this, 0, 0, 6, 14, 3, 13, 9);
        world.addBody(body);
        animationManager = new AnimationManager();
        stateManager = new StateManager(this);
        soundManager = scene.getSceneManager().getGame().getSoundManager();
        hudInfo = scene.getSceneManager().getGame().getHudInfo();
        addAnimations();
        addStates();
        // initial state
        stateManager.setInitialState("empty");
    }

    public Body getBody() {
        return body;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public StateManager<Actor> getStateManager() {
        return stateManager;
    }

    private void addAnimations() {
        animationManager.addAnimation("empty", "empty.png");
    }
    
    private void addStates() {
        stateManager.addState(new EmptyState());
        stateManager.addState(new PlayingState());
    }
    
    @Override
    public void update() {
        stateManager.update();
    }
    
    @Override
    public void draw(Graphics2D g) {
        stateManager.draw(g);
    }
    
    public boolean isJumping() {
        return !body.isOnStair() && !body.collidingWithTerrainFloor();
    }
    
    protected class ActorState extends State<Actor> {
        public ActorState(String name) {
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

    protected class EmptyState extends ActorState {
        public EmptyState() {
            super("empty");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("empty");
        }
    }
    
    // general idle, walk and jump states
    protected class PlayingState extends ActorState {
        public PlayingState() {
            super("playing");
        }

        @Override
        public void setAnimation() {
            String animation;
            if (isJumping()) {
                animation = "jump";
            }
            else if (body.isMoveLeft() || body.isMoveRight())  {
                animation = "walk";
            }
            else {
                animation = "idle";
            }
            animationManager.setAnimation(animation + "_" + body.getLastDirection());
        }

        @Override
        public void update() {
            body.update();
            super.update();
        }
    }
    
}
