package br.ol.kv.infra;

import br.ol.kv.ai.PathMap;
import br.ol.kv.audio.SoundManager;
import br.ol.kv.audio.Sounds;
import br.ol.kv.physics.Terrain;
import br.ol.kv.physics.World;
import br.ol.kv.renderer.BitmapFont;
import br.ol.kv.scene.Playing;
import java.awt.Graphics2D;

/**
 * Game class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Game {
    
    public static final int LAST_LEVEL = 3;
    
    private Display display;
    private final Terrain terrain;
    private final World world;
    private final PathMap pathMap;
    private final SceneManager sceneManager;
    private final LevelLoader levelLoader;
    private final HUDInfo hudInfo;
    private final BitmapFont bitmapFont;
    private final SoundManager soundManager;
    private boolean allTreasuresCollected;
    
    public Game() {
        bitmapFont = new BitmapFont("font8x8.png", 16, 16);
        terrain = new Terrain();
        world = new World(terrain);
        pathMap = new PathMap();
        hudInfo = new HUDInfo();
        soundManager = new SoundManager();
        sceneManager = new SceneManager(this);
        levelLoader = new LevelLoader();
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public World getWorld() {
        return world;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public LevelLoader getLevelLoader() {
        return levelLoader;
    }

    public HUDInfo getHudInfo() {
        return hudInfo;
    }

    public BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public boolean isAllTreasuresCollected() {
        return allTreasuresCollected;
    }

    public void setAllTreasuresCollected(boolean allTreasuresCollected) {
        this.allTreasuresCollected = allTreasuresCollected;
    }

    public PathMap getPathMap() {
        return pathMap;
    }

    public void start() {
        soundManager.start();
        soundManager.play(Sounds.SILENCE);
        sceneManager.start();
    }
    
    public void update() {
        sceneManager.update();
    }
    
    public void draw(Graphics2D g) {
        //world.getTerrain().drawDebug(g);
        sceneManager.draw(g);
    }

    private void loadLevel(int level) {
        allTreasuresCollected = false;
        levelLoader.load("level_" + level + ".xml");
        terrain.loadMap(levelLoader);
        world.clear();
        pathMap.create(terrain);
        // create all entities in the Playing scene 
        Playing playingScene = (Playing) sceneManager.getScene(SceneManager.PLAYING);
        playingScene.createAllEntities(levelLoader);
        System.gc();
    }
    
    // game flow
    
    public void startGame() {
        sceneManager.setNextScene(SceneManager.PLAYING);
        hudInfo.reset();
        loadLevel(hudInfo.getLevel());
    }
    
    public void tryNextLife() {
        if (hudInfo.getLives() - 1 <= 0) {
            gameOver();
        }
        else {
            sceneManager.setNextScene(SceneManager.PLAYING);
            hudInfo.decLives();
            loadLevel(hudInfo.getLevel());
        }
    }
    
    public void gameOver() {
        sceneManager.setNextScene(SceneManager.OL_PRESENTS);
        hudInfo.updateHiscore();
        hudInfo.clearScore();
        allTreasuresCollected = false;
    }
    
    public void nextLevel(int nextLevel) {
        sceneManager.setNextScene(SceneManager.NEXT_MAP);
        hudInfo.setLevel(nextLevel);
        if (nextLevel <= Game.LAST_LEVEL) {
            loadLevel(hudInfo.getLevel());
        }
    }

}
