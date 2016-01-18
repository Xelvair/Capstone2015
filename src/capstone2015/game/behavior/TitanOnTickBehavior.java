package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.states.ActorState;
import capstone2015.entity.states.ActorStateSignature;
import capstone2015.entity.states.TitanStateDeterminer;
import java.util.Map;

public class TitanOnTickBehavior implements OnTickBehavior{
    private ActorState state;
    private TitanStateDeterminer stateDeterminer;
 
    public TitanOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        this.stateDeterminer = new TitanStateDeterminer(actor);
    }
    
    @Override
    public void invoke(Actor actor, double timeDelta) {     
       ActorStateSignature state_signature = stateDeterminer.determineState(state, timeDelta);
       
       if(
            state_signature != null 
            && 
            (state == null || state.hashCode() != state_signature.hashCode())
       ){
           state = state_signature.createInstance();
       }

       state.onTick(timeDelta);
    }
}
