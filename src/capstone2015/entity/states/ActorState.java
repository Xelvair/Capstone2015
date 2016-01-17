package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.state.State;

public abstract class ActorState extends State{
    private Actor actor;
    
    protected ActorState(Actor actor){
        this.actor = actor;
    }
    
    protected Actor getActor(){
        return actor;
    }
}
