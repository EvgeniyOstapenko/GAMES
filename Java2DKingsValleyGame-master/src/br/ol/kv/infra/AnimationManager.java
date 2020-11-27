package br.ol.kv.infra;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * AnimationManager class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class AnimationManager {

    private Animation currentAnimation;
    private Map<String, Animation> animations = new HashMap<>();

    public void addAnimation(String name, String ... resources) {
        addAnimation(name, true, 0.3, resources);
    }
    
    public void addAnimation(String name, boolean loop, double frameIncrementation, String ... resources) {
        Animation animation = new Animation(name, loop, frameIncrementation);
        animation.addFrames(resources);
        animations.put(name, animation);
        if (currentAnimation == null) {
            currentAnimation = animation;
        }
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }
    
    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public void setAnimation(String name) {
        currentAnimation = animations.get(name);
    }

    public void setAnimation(String name, boolean restartFrame) {
        currentAnimation = animations.get(name);
        if (restartFrame) {
            currentAnimation.setCurrentFrame(0);
        }
    }
    
    public void setCurrentFrame(double currentFrame) {
        currentAnimation.setCurrentFrame(currentFrame);
    }
    
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
        }
    }

    public void draw(Graphics2D g, int x, int y) {
        if (currentAnimation != null) {
            currentAnimation.draw(g, x, y);
        }
    }

    public void replaceColor(int srcColor, int dstColor) {
        for (Animation animation : animations.values()) {
            animation.replaceColor(srcColor, dstColor);
        }
    }
    
}
