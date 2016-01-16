package capstone2015.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StateStack {
    private final List<State> states = new ArrayList<>();
    private boolean lockStatesList = false;
    private final List<State> scheduledAddStates = new ArrayList<>();
    /***********
     * Pushes a new state atop the others
     * @param state
     */
    public void pushState(State state){
        if(lockStatesList){
            scheduledAddStates.add(state);
            return;
        }
        
        /**
         * Set the current top state to blur
         */
        if(!states.isEmpty()){
            State topState = states.get(states.size() - 1);
            topState.setBlur();
        }
        
        states.add(state);
        state.setFocus();
    }
    
    private void flushScheduledAddStates(){
        if(lockStatesList)
            return;
        
        Iterator<State> it = scheduledAddStates.iterator();
        while(it.hasNext()){
            pushState(it.next());
            it.remove();
        }
    }
    
    /*******
     * Sends terminate event to states
     * states are expected to call terminate() themselves
     * and can wait for an asynchronous process to finish by delaying
     * the terminate() call
     */
    public void terminateStates(){
        for(State state : states){
            if(state.isAlive())
                state.terminate();
        }
        cleanStates();
    }
    
    private void cleanStates(){
        Iterator<State> it = states.iterator();
        
        while(it.hasNext()){
            State state = it.next();
            if(!state.isAlive()){
                it.remove();
            }
        }
    }
    
    /**********
     * Replace all states currently in the state stack with this one
     * @param state
     */
    public void emplaceState(State state){
        /**
         * Send terminate event to all current states
         */
        for(State it : states){
            it.terminate();
        }
        states.clear();
        
        states.add(state);
        state.setFocus();
    }
    
    /*************
     * Tick all states, let them do their thing,
     * remove states that have terminated
     * @param timeDelta time since the last tick
     */
    public void tick(double timeDelta){
        this.cleanStates();
        
        if(states.isEmpty()){
            return;
        }

        /**
         * Tick alive states, remove others
         */
        lockStatesList = true;
        for (State state : states) {
            if (state.isAlive()) {
                state.onTick(timeDelta);
            }
        }
        lockStatesList = false;
        flushScheduledAddStates();
        
        /****
         * If a state was removed, another one might have taken the
         * top (in-focus) spot. Set that one's focus to true
         */
        State topState = states.get(states.size() - 1);
        if(topState.isBlur()){
            topState.setFocus();
        }
    }
    
    /**************
     * Checks whether the state stack is empty -> means there is nothing to do anymore
     * @return
     */
    public boolean isEmpty(){
        return states.isEmpty();
    }
}
