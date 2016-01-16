package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.state.State;

public abstract class ActorState extends State{
    private Actor actor;
    private ActorStateConfig config;
    
    protected ActorState(Actor actor, ActorStateConfig config){
        this.actor = actor;
        this.config = config;
    }
    
    protected Actor getActor(){
        return actor;
    }
    
    protected ActorStateConfig getConfig(){
        return config;
    }
}
