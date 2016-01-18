package capstone2015.entity.states;

import capstone2015.entity.Actor;

public class DoNothingState extends ActorState{

    public DoNothingState(Actor actor) {
        super(actor);
    }
    
    @Override
    public void onTick(double timeDelta) {
        //null
    }
}
