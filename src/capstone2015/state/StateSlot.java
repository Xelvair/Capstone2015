package capstone2015.state;

public class StateSlot {
    private State defaultState;
    private State slotState;
    
    public StateSlot(State defaultState){
        this.defaultState = defaultState;
        this.defaultState.setFocus();
        this.slotState = null;
    }
    
    public State getActiveState(){
        return (slotState == null ? defaultState : slotState);
    }
    
    public void tick(double timeDelta){
        if(slotState != null){
            slotState.onTick(timeDelta);
            if(!slotState.isAlive()){
                slotState.terminate();
                slotState = null;
                defaultState.setFocus();
            }
        } else {
            defaultState.onTick(timeDelta);
            if(!defaultState.isAlive())
                throw new RuntimeException("Default state terminated!");
        }
    }
    
    public void setState(State state){
        if(slotState != null)
            slotState.terminate();
        
        defaultState.setBlur();
        slotState = state;
        slotState.setFocus();
    }
    
    public void removeState(){
        if(slotState == null)
            return;
        
        slotState.terminate();
        slotState = null;
        defaultState.setFocus();
    }
}
