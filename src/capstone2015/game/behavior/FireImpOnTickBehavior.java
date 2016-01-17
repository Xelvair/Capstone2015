package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.states.ActorState;
import capstone2015.entity.states.ActorStateSignature;
import capstone2015.entity.states.FireImpStateDeterminer;
import java.util.Map;

public class FireImpOnTickBehavior implements OnTickBehavior{    
    
    private ActorState state;
    private FireImpStateDeterminer stateDeterminer;
 
    public FireImpOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        this.stateDeterminer = new FireImpStateDeterminer(actor);
    }
    
    @Override
    public void invoke(Actor actor, double timeDelta) {     
       ActorStateSignature state_signature = stateDeterminer.determineState();
       
       if(state == null || state.hashCode() != state_signature.hashCode()){
           state = state_signature.createInstance();
       }

       state.onTick(timeDelta);
    }
}
