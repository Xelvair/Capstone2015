package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.state.State;

public abstract class ActorState extends State{
    private Actor actor;
    private int hash;
    
    protected ActorState(Actor actor){
        this.actor = actor;
    }
    
    protected Actor getActor(){
        return actor;
    }
    
    protected void setHash(int hash){
        this.hash = hash;
    }
    
    @Override
    public int hashCode(){
        return hash;
    }
}
