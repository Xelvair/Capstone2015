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
    
    /**********
     * Replace all states currently in the state stack with this one
     * @param state
     */
    public void emplaceState(AppState state){
        /**
         * Send kill event to all current states
         */
        for(AppState it : states){
            it.onEvent(AppStateEvent.KILL);
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
        Iterator<AppState> it = states.iterator();
        
        /**
         * Tick alive states, remove others
         */
        while(it.hasNext()){
            AppState state = it.next();
            if(state.isAlive()){
                state.onTick(timeDelta);
            } else {
                state.onEvent(AppStateEvent.KILL);
                it.remove();
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
