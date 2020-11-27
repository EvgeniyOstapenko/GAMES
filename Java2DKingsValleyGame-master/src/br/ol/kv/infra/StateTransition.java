package br.ol.kv.infra;

/**
 * StateTransition class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class StateTransition<T> {
    
    public interface Condition {
        public boolean check();
    }
    
    protected final T entity;
    private String name;
    private State sourceState;
    private State destinationState;
    private final Condition condition;
    
    public StateTransition(T entity, State sourceState, State destinationState, Condition condition) {
        this.entity = entity;
        this.sourceState = sourceState;
        this.destinationState = destinationState;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getSourceState() {
        return sourceState;
    }

    public void setSourceState(State sourceState) {
        this.sourceState = sourceState;
    }

    public State getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(State destinationState) {
        this.destinationState = destinationState;
    }

    public boolean checkCondition() {
        return condition.check();
    }

}
