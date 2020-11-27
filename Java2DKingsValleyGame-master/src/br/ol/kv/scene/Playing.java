package br.ol.kv.scene;

import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.entity.BrickDigging;
import br.ol.kv.entity.Door;
import br.ol.kv.entity.Enemy;
import br.ol.kv.entity.GameOver;
import br.ol.kv.entity.Gate;
import br.ol.kv.entity.HUD;
import br.ol.kv.entity.Knife;
import br.ol.kv.entity.Pickaxe;
import br.ol.kv.entity.Player;
import br.ol.kv.entity.Treasure;
import br.ol.kv.infra.Camera;
import br.ol.kv.infra.HUDInfo;
import br.ol.kv.infra.LevelLoader;
import br.ol.kv.infra.Scene;
import br.ol.kv.infra.SceneManager;
import br.ol.kv.infra.ScoreTable;
import br.ol.kv.infra.StateManager;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import br.ol.kv.renderer.TerrainRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Playing class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Playing extends Scene {

    private final SoundManager soundManager;
    private final HUDInfo hudInfo;
    private final StateManager<Playing> stateManager;
    private final World world;
    private final TerrainRenderer terrainRenderer;

    private Player player;
    private BrickDigging brickDigging;
    private GameOver gameOver;
    private HUD hud;
    private Enemy enemy;
    private Camera camera;
    
    private final List<Treasure> treasures;
    private final List<Door> doors;
    
    public Playing(SceneManager sceneManager) {
        super(sceneManager);
        stateManager = new StateManager<>(this);
        soundManager = sceneManager.getGame().getSoundManager();
        hudInfo = sceneManager.getGame().getHudInfo();
        world = sceneManager.getGame().getWorld();
        terrainRenderer = new TerrainRenderer();
        treasures = new ArrayList<>();
        doors = new ArrayList<>();
    }
    
    public void createAllEntities(LevelLoader levelLoader) {
        treasures.clear();
        doors.clear();
        brickDigging = new BrickDigging(this);
        gameOver = new GameOver(this);
        hud = new HUD(this);
        player = new Player(this, brickDigging, gameOver);
        clearAllEntities();
        loadAllEntities(levelLoader);
        addEntity(brickDigging);
        addEntity(player);
        addEntity(camera = new Camera(this, player));
    }
    
    private void loadAllEntities(LevelLoader levelLoader) {
        int nextLevel = getSceneManager().getGame().getHudInfo().getLevel() + 1;
        int previousLevel = getSceneManager().getGame().getHudInfo().getLevel() - 1;
        // door player start
        for (Point doorPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_DOOR_PLAYER_START)) {
            Door door = new Door(this, player, 0, doorPosition.x, doorPosition.y);
            addEntity(door);
            doors.add(door);
            player.setDoorStart(door);
        }
        // door next level
        for (Point doorPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_DOOR_NEXT_LEVEL)) {
            Door door = new Door(this, player, nextLevel, doorPosition.x, doorPosition.y);
            addEntity(door);
            doors.add(door);
        }
        // door previous level
        for (Point doorPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_DOOR_PREVIOUS_LEVEL)) {
            Door door = new Door(this, player, previousLevel, doorPosition.x, doorPosition.y);
            addEntity(door);
            doors.add(door);
        }
        // door player start, next level
        for (Point doorPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_DOOR)) {
            Door doorPlayerStart = new Door(this, player, 0, doorPosition.x, doorPosition.y);
            Door doorNextLevel= new Door(this, player, nextLevel, doorPosition.x, doorPosition.y);
            addEntity(doorPlayerStart);
            addEntity(doorNextLevel);
            doors.add(doorPlayerStart);
            doors.add(doorNextLevel);
            player.setDoorStart(doorPlayerStart);
        }
        // gate
        for (Point gatePosition : levelLoader.getAllEntities(LevelLoader.ENTITY_GATE)) {
            Gate gate = new Gate(this, player, gatePosition.x, gatePosition.y);
            addEntity(gate);
            // convert brick to unbreakable bricks
            Terrain terrain = world.getTerrain();
            terrain.set(gatePosition.x, gatePosition.y - 2, 8);
            terrain.set(gatePosition.x, gatePosition.y - 1, 8);
            terrain.set(gatePosition.x, gatePosition.y, 8);
        }
        
        // treasures
        for (Point treasuresPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_TREASURE)) {
            Treasure treasure = new Treasure(this, player, treasuresPosition.x, treasuresPosition.y);
            addEntity(treasure);
            treasures.add(treasure);
        }
        // pickaxe
        for (Point pickaxePosition : levelLoader.getAllEntities(LevelLoader.ENTITY_PICKAXE)) {
            addEntity(new Pickaxe(this, player, pickaxePosition.x, pickaxePosition.y));
        }
        // knife
        for (Point knifePosition : levelLoader.getAllEntities(LevelLoader.ENTITY_KNIFE)) {
            addEntity(new Knife(this, player, knifePosition.x, knifePosition.y));
        }

        // Enemy type 1 (random move)
        for (Point enemyPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_ENEMY_1)) {
            Enemy enemy1 = new Enemy(this, player, 1, enemyPosition.x, enemyPosition.y);
            addEntity(enemy1);
        }
        
        List<Enemy> enemiesType2 = new ArrayList<>();
        // Enemy type 2 (chase player)
        for (Point enemyPosition : levelLoader.getAllEntities(LevelLoader.ENTITY_ENEMY_2)) {
            Enemy enemy2 = new Enemy(this, player, 2, enemyPosition.x, enemyPosition.y);
            addEntity(enemy2);
            enemiesType2.add(enemy2);
            enemy = enemy2;
        }
        // set ai path finder blocking enemies type 2
        for (Enemy e1 : enemiesType2) {
            for (Enemy e2 : enemiesType2) {
                if (e1 == e2) {
                    continue;
                }
                e1.getPathFinder().addBlockingEnemy(e2);
            }
        }
    }

    public Camera getCamera() {
        return camera;
    }
    
    @Override
    public void update() {
        if (checkGameOver()) {
            return;
        }
        checkAllTreasuresCollected();
        super.update();
    }

    private boolean checkGameOver() {
        if (hudInfo.getLives() == 1 && player.isKilled()) {
            hudInfo.setLives(0); // just to show correctly
            gameOver.show();
            soundManager.play(Sounds.GAME_OVER);
            pause(8);
            return true;
        }
        return false;
    }
    
    private void checkAllTreasuresCollected() {
        if (getSceneManager().getGame().isAllTreasuresCollected()) {
            return;
        }
        for (Treasure treasure : treasures) {
            if (!treasure.isCollected()) {
                return;
            }
        }
        getSceneManager().getGame().setAllTreasuresCollected(true);
        soundManager.pause(Sounds.PLAYING, 3);
        soundManager.play(Sounds.COLLECTED_ALL); 
        hudInfo.addScore(ScoreTable.ALL_TREASURES_COLLECTED);
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.translate(-camera.getBody().getX(), 0);
        //world.getTerrain().drawDebug(g);
        //enemy.getPathFinder().drawDebug(g);
        terrainRenderer.draw(g, world.getTerrain(), camera);
        g.translate(camera.getBody().getX(), 0);
        
        hud.draw(g);
        
        g.translate(-camera.getBody().getX(), 0);
        super.draw(g);
        
        // workaround: to paint the closing doors on top of the player
        for (Door door : doors) {
            door.drawAfter(g);
        }
        g.translate(camera.getBody().getX(), 0);
        
        gameOver.draw(g);
    }
    
}
