package br.ol.kv.entity;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.AnimationManager;
import br.ol.kv.infra.Entity;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.State;
import br.ol.kv.infra.StateManager;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.World;
import java.awt.Graphics2D;

/**
 * Gate class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Gate extends Entity {
    
    private final Player player;
    private final Body<Gate> body;
    private final AnimationManager animationManager;
    private final StateManager<Gate> stateManager;
    private final SoundManager soundManager;
    private int allowedDirection = 1; // 1=right, -1=left
    private int pushingCount;
    private boolean activated;
    
    public Gate(Scene scene, Player player, int col, int row) {
        super(scene);
        this.player = player;
        soundManager = scene.getSceneManager().getGame().getSoundManager();
        World world = scene.getSceneManager().getGame().getWorld();
        int x = col * 8 + 4;
        int y = row * 8 + 7; 
        body = new Body<>(this, x, y, 11, 24, 6, 23, 9);
        world.addBody(body);
        animationManager = new AnimationManager();
        stateManager = new StateManager(this);
        addAnimations();
        addGateStates();
        addTransactions();
        // initial state
        stateManager.setInitialState("static");
    }

    public Body getBody() {
        return body;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public StateManager<Gate> getStateManager() {
        return stateManager;
    }

    private void addAnimations() {
        animationManager.addAnimation("gate_left", "gate_0.png");

        animationManager.addAnimation("gate_right", "gate_4.png");

        animationManager.addAnimation("gate_rotating_left", false, 0.25
                , "gate_0.png", "gate_1.png", "gate_2.png", "gate_3.png", "gate_4.png", "gate_4.png");

        animationManager.addAnimation("gate_rotating_right", false, 0.25
                , "gate_4.png", "gate_3.png", "gate_2.png", "gate_1.png", "gate_0.png", "gate_0.png");
    }
    
    private void addGateStates() {
        stateManager.addState(new StaticState());
        stateManager.addState(new RotatingState());
    }
    
    private void addTransactions() {
        stateManager.addTransition("static", "rotating", () -> activated);
        stateManager.addTransition("rotating", "static", () -> !activated);
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
    
    protected class GateState extends State<Gate> {
        public GateState(String name) {
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

    protected class StaticState extends GateState {
        public StaticState() {
            super("static");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("gate_" + (allowedDirection == -1 ? "left" : "right"));
        }

        @Override
        public void update() {
            super.update(); 
            
            // player pushing gate ?
            World world = body.getWorld();
            Body<Player> playerBody = world.checkCollision(body, Player.class);
            if (playerBody != null 
                    && player.getBody().collidingWithTerrainFloor()
                    && ((player.getBody().isMoveLeft() && allowedDirection == -1)
                    || (player.getBody().isMoveRight() && allowedDirection == 1))) {
                pushingCount++;
                // System.out.println("pushing count = " + pushingCount);
            }   
            else {
                pushingCount = 0;
            }
            
            if (pushingCount == 20) {
                activated = true;
            }
        }
        
        
    }
    
    protected class RotatingState extends GateState {
        public RotatingState() {
            super("rotating");
        }
        
        @Override
        public void onEnter() {
            pushingCount = 0;
            animationManager.setAnimation("gate_rotating_" + (allowedDirection == -1 ? "left" : "right"), true);
            player.setGate(Gate.this);
            soundManager.play(Sounds.GATE);
        }

        @Override
        public void onExit() {
            allowedDirection *= -1;
            player.setGate(null);
        }

        @Override
        public void update() {
            super.update(); 
            World world = body.getWorld();
            Body<Player> playerBody = world.checkCollision(body, Player.class);
            if (animationManager.getCurrentAnimation().isFinished() 
                    && playerBody == null) {
                activated = false;
            }
            else {
                player.getBody().setX(player.getBody().getX() + allowedDirection);
            }
        }
        
    }
    
}
