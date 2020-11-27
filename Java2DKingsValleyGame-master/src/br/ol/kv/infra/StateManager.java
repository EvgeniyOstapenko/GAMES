package br.ol.kv.infra;

import br.ol.kv.infra.StateTransition.Condition;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * StateManager class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class StateManager<T> {
    
    private final Map<String, State> states = new HashMap<>();
    private final Map<String, StateTransition> transitions = new HashMap<>();
    
    private final T entity;
    private State currentState;

    public StateManager(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    public State getCurrentState() {
        return currentState;
    }
    
    public boolean isCurrentState(String name) {
        if (currentState == null) {
            return false;
        }
        return currentState.getName().equals(name);
    }

    public Map<String, State> getStates() {
        return states;
    }

    public void addState(State state) {
        states.put(state.getName(), state);
        state.setStateManager(this);
    }

    public void setInitialState(String stateName) {
        currentState = states.get(stateName);
        currentState.onEnter();
    }
    
    public void addTransition(String sourceStateStr, String destinationStateStr, Condition condition) {
        State sourceState = states.get(sourceStateStr);
        State destinationState = states.get(destinationStateStr);
        
        StateTransition<T> stateTransition 
                = new StateTransition<>(entity, sourceState, destinationState, condition);
        
        sourceState.addTransition(stateTransition);
    }
    
    public Map<String, StateTransition> getTransitions() {
        return transitions;
    }
    
    public void update() {
        if (currentState != null) {
            State state = currentState.checkTransitions();
            if (state != null) {
                currentState.onExit();
                currentState = state;
                currentState.onEnter();
            }
            currentState.update();
        }
    }

    public void draw(Graphics2D g) {
        if (currentState != null) {
            currentState.draw(g);
        }
    }

}
