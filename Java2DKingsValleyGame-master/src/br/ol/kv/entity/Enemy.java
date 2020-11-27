package br.ol.kv.entity;

import br.ol.kv.ai.PathFinder;
import br.ol.kv.ai.PathMap;
import br.ol.kv.ai.PathMap.Node;
import br.ol.kv.audio.Sounds;
import br.ol.kv.infra.Keyboard;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.infra.Time;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Enemy class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Enemy extends Actor {

    private static final int ORIGINAL_COLOR = -581600;
    
    // type 1 = just move randomly
    // type 2 = chase player
    private final int type;
    
    private boolean firstRessurection = true;
    private final int startCol;
    private final int startRow;
    
    private final Player player;
    private final PathFinder pathFinder;
    
    private boolean killed;
    private double reviveTime;
    
    private boolean randomMode;
    private double randomModeTime;
    private Point randomTargetGridPosition;
    
    public Enemy(Scene scene, Player player, int type, int startCol, int startRow) {
        super(scene);
        this.type = type;
        this.startCol = startCol;
        this.startRow = startRow;
        
        loadAnimations();
        setColor();
        addEnemyStates();
        addEnemyTransitions();
        
        // change horizontal velocity for enemy
        // IMPORTANT: in this implementation, updateMoveAI() will work correctly 
        //            only with body.horizontalVx = 1
        body.setHorizontalVx(1);
        
        // AI
        this.player = player;
        PathMap pathMap = scene.getSceneManager().getGame().getPathMap();
        pathFinder = new PathFinder(pathMap);
        
        // initial state
        stateManager.setInitialState("empty");
        reviveTime = Time.getCurrent() + 3 * Math.random();
    }

    private void setColor() {
        if (type == 1) {
            animationManager.replaceColor(ORIGINAL_COLOR, Color.WHITE.getRGB());
        }
        else if (type == 2) {
            // color already ok
        }
    }
    
    private void addEnemyStates() {
        stateManager.addState(new EmptyState());
        stateManager.addState(new DeadState());
        stateManager.addState(new ResurrectingState());
    }
    
    private void addEnemyTransitions() {
        stateManager.addTransition("empty", "resurrecting"
                , () -> player.getStateManager().isCurrentState("playing") 
                        && Time.getCurrent() > reviveTime);
        
        stateManager.addTransition("playing", "dead", () -> killed);
        
        stateManager.addTransition("dead", "resurrecting"
                , () -> Time.getCurrent() > reviveTime 
                        && player.isPlaying() && !player.isKilled());
        
        stateManager.addTransition("resurrecting", "playing"
                , () -> animationManager.getCurrentAnimation().isFinished() 
                        && player.isPlaying() && !player.isKilled());
    }
    
    public PathFinder getPathFinder() {
        return pathFinder;
    }

    private void loadAnimations() {
        animationManager.addAnimation("idle_right", "mummy_right_1.png");
        animationManager.addAnimation("idle_left", "mummy_left_1.png");
        
        animationManager.addAnimation("walk_right", "mummy_right_0.png"
                , "mummy_right_1.png", "mummy_right_2.png", "mummy_right_1.png");
        
        animationManager.addAnimation("walk_left", "mummy_left_0.png"
                , "mummy_left_1.png", "mummy_left_2.png", "mummy_left_1.png");
        
        animationManager.addAnimation("jump_right", "mummy_right_0.png");
        animationManager.addAnimation("jump_left", "mummy_left_0.png");
        
        animationManager.addAnimation("killed", false, 0.1
                , "mummy_killed_0.png", "mummy_killed_1.png");

        animationManager.addAnimation("resurrecting", false, 0.05
                , "mummy_resurrecting_0.png", "mummy_resurrecting_1.png"
                , "mummy_resurrecting_0.png", "mummy_resurrecting_1.png"
                , "mummy_resurrecting_0.png"
        );
    }
    
    @Override
    public void update() {
        // updateDebug();
        updatePlaying();
        super.update();
    }

    private void updateDebug() {
        if (Keyboard.isKeyDown(KeyEvent.VK_K)) {
            kill();
        }

        if (Keyboard.isKeyDown(KeyEvent.VK_P)) {
            getScene().pause(5);
        }
    }
    
    private void updatePlaying() {
        if (!player.isPlaying() || !getStateManager().isCurrentState("playing")) {
            return;
        }
        updateMoveAI();
        updateCheckPlayerCaught();
    }
    
    private void updateCheckPlayerCaught() {
        if (killed) {
            return;
        }
        World world = body.getWorld();
        Body<Player> playerBody = world.checkCollision(body, Player.class);
        if (playerBody != null) {
            playerBody.getOwner().kill();
        }
    }
    
    private void updateMoveAI() {
        // workaround: when the player is at specific point on the stair,
        // enemy can sometimes not reach the player
        int dx = Math.abs(player.getBody().getX() - body.getX());
        int dy = Math.abs(player.getBody().getY() - body.getY());
        if (dx < 8 && dy < 8) {
            return;
        }
        
        switch (type) {
            // random walk
            case 1:
                int enemyRow = (int) body.getX() / Terrain.TILE_SIZE;
                int enemyCol = (int) body.getY() / Terrain.TILE_SIZE;
                if (randomTargetGridPosition == null 
                        || (enemyCol == randomTargetGridPosition.x 
                            && enemyRow == randomTargetGridPosition.y)
                        || Time.getCurrent() > randomModeTime) {
                    setRandomMode(10);
                }
                int rx = randomTargetGridPosition.x * Terrain.TILE_SIZE;
                int ry = randomTargetGridPosition.y * Terrain.TILE_SIZE;
                pathFinder.updateDistances(rx, ry);
                break;
                
            // chase player    
            case 2:
                if (randomMode && Time.getCurrent() < randomModeTime) {
                    int rx2 = randomTargetGridPosition.x * Terrain.TILE_SIZE;
                    int ry2 = randomTargetGridPosition.y * Terrain.TILE_SIZE;
                    pathFinder.updateDistances(rx2, ry2);
                }
                else {
                    randomMode = false;
                    pathFinder.setBlockingEnemiesEnabled(true);
                    int playerX = player.getBody().getX();
                    int playerY = player.getBody().getY();
                    pathFinder.updateDistances(playerX, playerY);
                }
                break;
        }
        
        if (isJumping()) {
            return;
        }

        int gridX = body.getX() / Terrain.TILE_SIZE;
        int gridY = body.getY() / Terrain.TILE_SIZE;
        Node node = pathFinder.getMap().get(gridX, gridY);
        
        // random jump (terrain id = 9)
        int terrainId = pathFinder.getMap().getTerrain().getIdByGrid(gridX, gridY);
        if (terrainId == 9 && Math.random() < 0.05) {
            body.moveJump();
            if (body.getVx() == 0 && Math.random() < 0.5) {
                body.moveLeft();
            }
            else if (body.getVx() == 0) {
                body.moveRight();
            }
            return;
        }
        
        // check only at specific points
        if ((body.getVx() > 0 && body.getX() % 8 != 4)
                || (body.getVx() < 0 && body.getX() % 8 != 3)
                || (body.getY() % 8 != 7)) {
            return;
        }
        
        int closestIndex = -1;
        int closestDistance = Integer.MAX_VALUE;
        int direction = 1;
        for (int i = 0; i < PathFinder.DIRECTION_VECTORS.length; i++) {
            Point directionVector = PathFinder.DIRECTION_VECTORS[i];
            if (node.isDirectionAllowed(direction)) {
                int nx = gridX + directionVector.x; 
                int ny = gridY + directionVector.y; 
                if ((pathFinder.getDistance(nx, ny) == closestDistance & Math.random() < 0.5)
                        || (pathFinder.getDistance(nx, ny) < closestDistance)) {
                    closestDistance = pathFinder.getDistance(nx, ny);
                    closestIndex = i;
                }
            }
            direction *= 2;
        }
        
        body.resetMovement();
        if (closestIndex >= 0 && closestDistance > 0) {
            if (PathFinder.DIRECTION_VECTORS[closestIndex].x > 0) {
                body.moveRight();
            }
            else if (PathFinder.DIRECTION_VECTORS[closestIndex].x < 0) {
                body.moveLeft();
            }
            
            if (PathFinder.DIRECTION_VECTORS[closestIndex].y > 0) {
                body.moveDown();
            }
            else if (PathFinder.DIRECTION_VECTORS[closestIndex].y < 0) {
                body.moveUp();
            }
            
            if ((body.isMoveLeft() && body.isMoveUp() && node.jumpLeft)
                    || (body.isMoveRight() && body.isMoveUp() && node.jumpRight)) {
                body.moveJump();
            }
        }
        else {
            setRandomMode(type == 2 ? 2 : 10);
        }
    }
    
    private void setRandomMode(double maxTime) {
        randomMode = true;
        randomTargetGridPosition = pathFinder.getMap().getRandomPlatformLocation();
        randomModeTime = Time.getCurrent() + 1 + maxTime * Math.random(); // only for type 2
        pathFinder.setBlockingEnemiesEnabled(false);
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!player.isPlaying()) {
            return;
        }
        super.draw(g);
    }
    
    protected class DeadState extends ActorState {
        public DeadState() {
            super("dead");
        }
        
        @Override
        public void onEnter() {
            body.resetMovement();
            animationManager.setAnimation("killed", true);
        }
    }

    protected class ResurrectingState extends ActorState {
        public ResurrectingState() {
            super("resurrecting");
        }
        
        @Override
        public void onEnter() {
            animationManager.setAnimation("resurrecting", true);
            body.reset();
            if (firstRessurection) {
                body.setX(startCol * Terrain.TILE_SIZE + 4);
                body.setY(startRow * Terrain.TILE_SIZE + Terrain.TILE_SIZE - 1);
                firstRessurection = false;
            }
            // adjust x
            int col = body.getX() / Terrain.TILE_SIZE;
            int ax = col * Terrain.TILE_SIZE + 4;
            body.setX(ax);
            // coliding with brick
            while (body.collidingWithTerrain()) {
                body.setY(body.getY() - 1);
            }
            soundManager.play(Sounds.SPAWN);
        }

        @Override
        public void onExit() {
            killed = false;
        }
    }
    
    public void kill() {
        if (killed) {
            return;
        }
        killed = true;
        reviveTime = Time.getCurrent() + 3 + 3 * Math.random();
        hudInfo.addScore(type == 1 ? ScoreTable.ENEMY_1_HIT : ScoreTable.ENEMY_2_HIT);
    }
    
    public boolean isAlive() {
        return stateManager.isCurrentState("playing");
    }
    
}
