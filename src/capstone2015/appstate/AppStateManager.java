package capstone2015.appstate;

import java.util.ArrayList;
import java.util.Iterator;

public class AppStateManager {
    private final ArrayList<AppState> states = new ArrayList<>();
    
    /***********
     * Pushes a new state atop the others
     * @param state
     */
    public void pushState(AppState state){
        /**
         * Set the current top state to blur
         */
        if(!states.isEmpty()){
            AppState topState = states.get(states.size() - 1);
            topState.setBlur();
        }
        
        states.add(state);
        state.setFocus();
    }
    
    /*******
     * Sends terminate event to states
     * states are expected to call terminate() themselves
     * and can wait for an asynchronous process to finish by delaying
     * the terminate() call
     */
    public void terminateStates(){
        for(AppState state : states){
            state.onEvent(AppStateEvent.TERMINATE);
        }
        cleanStates();
    }
    
    /******
     * Sends kill event to states and kills states
     * instantly after. States may not be able to finish 
     * asynchronous processes
     */
    public void killStates(){
        for(AppState state : states){
            state.onEvent(AppStateEvent.KILL);
            state.terminate();
        }
        cleanStates();
    }
    
    private void cleanStates(){
        Iterator<AppState> it = states.iterator();
        
        while(it.hasNext()){
            AppState state = it.next();
            if(!state.isAlive()){
                it.remove();
            }
        }
    }
    
    /**********
     * Replace all states currently in the state stack with this one
     * @param state
     */
    public void emplaceState(AppState state){
        /**
         * Send terminate event to all current states
         */
        for(AppState it : states){
            it.onEvent(AppStateEvent.TERMINATE);
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
        for (AppState state : states) {
            if (state.isAlive()) {
                state.onTick(timeDelta);
            }
        }
        
        /****
         * If a state was removed, another one might have taken the
         * top (in-focus) spot. Set that one's focus to true
         */
        AppState topState = states.get(states.size() - 1);
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
