package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.states.GetInRangeOfState;
import capstone2015.entity.states.RangerAttackState;
import capstone2015.entity.states.WanderingState;
import capstone2015.game.ActorEventGenerator;
import capstone2015.game.ActorMessage;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.state.StateSlot;
import java.util.Map;

public class FireImpOnTickBehavior implements OnTickBehavior{    
    
    private Actor actor;
    private Actor target;
    
    private StateSlot stateSlot;
    private MessageBus localMessageBus;
    private ActorEventGenerator eventGenerator;
    
    public FireImpOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        stateSlot = new StateSlot(new WanderingState(actor));
        localMessageBus = new MessageBus();
        eventGenerator = new ActorEventGenerator(actor, localMessageBus);
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
                    actor.setTarget((Actor)m.getMsgObject());
                    stateSlot.setState(new RangerAttackState(actor));
                    break;
                case ActorMessage.ACTOR_LEFT_VISION:
                    if(m.getMsgObject() == actor.getTarget()){
                        stateSlot.removeState();
                        actor.setTarget(null);
                    }
                    break;
                case ActorMessage.SELF_LEFT_OUTER_STRAY:
                    stateSlot.setState(new GetInRangeOfState(actor, actor.getInnerStray()));
                    actor.setTarget(actor.getLeader());
                    break;
                case ActorMessage.ACTOR_ENTERED_INNER_STRAY:
                    if(stateSlot.getActiveState() instanceof GetInRangeOfState){
                        actor.setTarget(null);
                        stateSlot.removeState();
                    }
                    break;
            }
        }
        
        if(actor.getTarget() == null && actor.hasLeader() && !isInInnerStrayRange()){
            actor.setTarget(actor.getLeader());
            stateSlot.setState(new GetInRangeOfState(actor, actor.getInnerStray()));
        }
    }

    private boolean isInInnerStrayRange(){
        if(!actor.hasLeader())
            return true;
        
        return actor.getLeader().getPos().deltaMagnitude(actor.getPos()) < actor.getInnerStray();
    }
    
    private boolean isInOuterStrayRange(){
        if(!actor.hasLeader())
            return true;
        
        return actor.getLeader().getPos().deltaMagnitude(actor.getPos()) < actor.getOuterStray();
    }
}
