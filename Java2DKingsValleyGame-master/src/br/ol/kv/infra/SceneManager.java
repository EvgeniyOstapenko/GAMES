package br.ol.kv.infra;

import br.ol.kv.scene.Ending;
import br.ol.kv.scene.Initializing;
import br.ol.kv.scene.NextMap;
import br.ol.kv.scene.OLPresents;
import br.ol.kv.scene.Playing;
import br.ol.kv.scene.Title;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SceneManager {
    
    public static final String INITIALIZING = "INITIALIZING";
    public static final String OL_PRESENTS = "OL_PRESENTS";
    public static final String TITLE = "TITLE";
    public static final String NEXT_MAP = "NEXT_MAP";
    public static final String PLAYING = "PLAYING";
    public static final String ENDING = "ENDING";
    
    private final Game game;
    private final SceneTransition transition;
    private int transitionState = -1;
    
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene currentScene;
    private Scene nextScene;
    private final BufferedImage currentPrintScreen;
    private double waitTime;
    
    public SceneManager(Game game) {
        this.game = game;
        transition = new SceneTransition();
        
        int w = Display.SCREEN_WIDTH;
        int h = Display.SCREEN_HEIGHT;
        int type = BufferedImage.TYPE_INT_RGB;
        currentPrintScreen = new BufferedImage(w, h, type);
        
        createAllScenes();
    }
    
    private void createAllScenes() {
        scenes.put(INITIALIZING, new Initializing(this));
        scenes.put(OL_PRESENTS, new OLPresents(this));
        scenes.put(TITLE, new Title(this));
        scenes.put(NEXT_MAP, new NextMap(this));
        scenes.put(PLAYING, new Playing(this));
        scenes.put(ENDING, new Ending(this));
    }

    public Game getGame() {
        return game;
    }
    
    public void start() {
        setNextScene(INITIALIZING);
        //setNextScene(ENDING);
        //setNextScene(NEXT_MAP);
    }
    
    public Scene getScene(String name) {
        return scenes.get(name);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public Scene getNextScene() {
        return nextScene;
    }
    
    private void updateCurrentPrintScreen() {
        Graphics2D g = (Graphics2D) currentPrintScreen.getGraphics();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, Display.SCREEN_WIDTH, Display.SCREEN_HEIGHT);
        if (currentScene != null) {
            currentScene.draw(g);
        }
    }
    
    public void setNextScene(String name) {
        Graphics2D g = (Graphics2D) currentPrintScreen.getGraphics();
        g.drawImage(getGame().getDisplay().getOffscreen(), 0, 0, null);
        //updateCurrentPrintScreen();
        
        nextScene = scenes.get(name);
        transitionState = 0;
        transition.close();
    }
    
    public void update() {
        if (currentScene != null && currentScene.isPaused()) {
            return;
        }
        
        switch (transitionState) {
            case 0:
                transition.update();
                if (transition.isFinished()) {
                    transitionState = 1;
                    transition.open();
                    if (currentScene != null) {
                        currentScene.onExit();
                    }
                    currentScene = nextScene;
                    currentScene.onEnter();
                    nextScene = null;
                    updateCurrentPrintScreen();
                    waitTime = Time.getCurrent() + 1; 
                }
                break;
            case 1:
                if (Time.getCurrent() < waitTime) {
                    return;
                }
                transitionState = 2;
            case 2:
                transition.update();
                if (transition.isFinished()) {
                    transitionState = 3;
                }
                break;
            case 3:
                if (currentScene != null) {
                    currentScene.update();
                }
        }
    }
    
    public void draw(Graphics2D g) {
        switch (transitionState) {
            case 0:
            case 1:
            case 2:
                g.drawImage(currentPrintScreen, 0, 0, null);
                transition.draw(g);
                break;
            case 3:
                if (currentScene != null) {
                    currentScene.draw(g);
                }
        }
    }
    
}
