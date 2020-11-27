package br.ol.kv.infra;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * State class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class State<T> {

    private final String name;
    protected T entity;
    protected StateManager stateManager;
    private final List<StateTransition> transitions = new ArrayList<>();
    
    public State(String name) {
        this.name = name;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
        this.entity = (T) stateManager.getEntity();
    }

    public String getName() {
        return name;
    }

    public List<StateTransition> getTransitions() {
        return transitions;
    }

    public void addTransition(StateTransition transition) {
        transitions.add(transition);
    }
        
    public State checkTransitions() {
        for (StateTransition transition : transitions) {
            if (transition.checkCondition()) {
                return transition.getDestinationState();
            }
        }
        return null;
    }
    
    public void onEnter() {
        // override your method here
    }
    
    public void onExit() {
        // override your method here
    }
    
    public void update() {
        // override your method here
    }
    
    public void draw(Graphics2D g) {
        // override your method here
    }
    
}
