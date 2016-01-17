package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.states.ActorState;
import capstone2015.entity.states.ActorStateSignature;
import capstone2015.entity.states.RattlesnakeStateDeterminer;
import java.util.Map;

public class RattlesnakeOnTickBehavior implements OnTickBehavior{   
    private ActorState state;
    private RattlesnakeStateDeterminer stateDeterminer;
 
    public RattlesnakeOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        this.stateDeterminer = new RattlesnakeStateDeterminer(actor);
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
