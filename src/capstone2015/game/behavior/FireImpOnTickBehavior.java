package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.states.ActorStateConfig;
import capstone2015.entity.states.GetInRangeOfState;
import capstone2015.entity.states.RangerAttackState;
import capstone2015.entity.states.WanderingState;
import capstone2015.game.ActorEventGenerator;
import capstone2015.game.ActorMessage;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.state.StateSlot;
import java.util.Map;

public class FireImpOnTickBehavior implements OnTickBehavior, ActorStateConfig{    
    
    private Actor actor;
    private Actor target;
    
    private StateSlot stateSlot;
    private MessageBus localMessageBus;
    private ActorEventGenerator eventGenerator;
    
    public FireImpOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        stateSlot = new StateSlot(new WanderingState(actor, this));
        localMessageBus = new MessageBus();
        eventGenerator = new ActorEventGenerator(actor, localMessageBus, this);
        this.actor = actor;
    }
    
    @Override
    public void invoke(Actor entity, double timeDelta){
        localMessageBus.refresh();
        eventGenerator.tick();
        stateSlot.tick(timeDelta);
        
        for(Message m : localMessageBus){
            switch(m.getType()){
                case ActorMessage.NEW_CLOSEST_ENEMY:
                    setTarget((Actor)m.getMsgObject());
                    stateSlot.setState(new RangerAttackState(actor, this));
                    break;
                case ActorMessage.ACTOR_LEFT_VISION:
                    if(m.getMsgObject() == getTarget()){
                        stateSlot.removeState();
                        setTarget(null);
                    }
                    break;
                case ActorMessage.SELF_LEFT_OUTER_STRAY:
                    stateSlot.setState(new GetInRangeOfState(actor, this, getInnerStray()));
                    setTarget(actor.getLeader());
                    break;
                case ActorMessage.ACTOR_ENTERED_INNER_STRAY:
                    if(stateSlot.getActiveState() instanceof GetInRangeOfState){
                        setTarget(null);
                        stateSlot.removeState();
                    }
                    break;
            }
        }
        
        if(getTarget() == null && actor.hasLeader() && !isInInnerStrayRange()){
            setTarget(actor.getLeader());
            stateSlot.setState(new GetInRangeOfState(actor, this, getInnerStray()));
        }
    }

    private boolean isInInnerStrayRange(){
        if(!actor.hasLeader())
            return true;
        
        return actor.getLeader().getPos().deltaMagnitude(actor.getPos()) < getInnerStray();
    }
    
    private boolean isInOuterStrayRange(){
        if(!actor.hasLeader())
            return true;
        
        return actor.getLeader().getPos().deltaMagnitude(actor.getPos()) < getOuterStray();
    }
    
    @Override
    public double getOuterStray() {return 15.d;}

    @Override
    public double getInnerStray() {return 7.d;}

    @Override
    public int getAttackDamage() {return 1;}

    @Override
    public double getAttackTimeout() {return .65d;}

    @Override
    public double getInRangeMoveTimeout() {return .225d;}

    @Override
    public double getAttackMoveTimeout() {return .75d;}

    @Override
    public double getWanderingMoveTimeout() {return 3.d;}

    @Override
    public Actor getTarget() {
        return target;
    }

    @Override
    public void setTarget(Actor target) {
        this.target = target;
    }

    @Override
    public int getAttackRange() {
        return 7;
    }
}
