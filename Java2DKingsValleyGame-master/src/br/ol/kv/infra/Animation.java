package br.ol.kv.infra;

import br.ol.kv.renderer.Sprite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Animation class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Animation {
    
    private final String name;
    private double currentFrame;
    private final double frameIncrementation;
    private final List<Sprite> frames;
    private boolean loop;
    
    public Animation(String name, boolean loop, double frameIncrementation) {
        this.name = name;
        this.loop = loop;
        this.frameIncrementation = frameIncrementation;
        this.frames = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(double currentFrame) {
        this.currentFrame = currentFrame;
    }
    
    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void addFrames(String ... resources) {
        for (String resource : resources) {
            Sprite sprite = new Sprite(resource);
            frames.add(sprite);
        }
    }
    
    public void update() {
        currentFrame += frameIncrementation;
    }
    
    public boolean isFinished() {
        return !loop && currentFrame > frames.size() - 1;
    }
    
    public void draw(Graphics2D g, int x, int y) {
        if (frames.isEmpty()) {
            return;
        }
        double drawFrame = currentFrame;
        if (isFinished()) {
            drawFrame = frames.size() - 1;
        }
        Sprite sprite = frames.get(((int) drawFrame) % frames.size());
        sprite.draw(g, x, y);
    }
    
    public void replaceColor(int srcColor, int dstColor) {
        for (Sprite sprite : frames) {
            sprite.replaceColor(srcColor, dstColor);
        }
    }
    
}
