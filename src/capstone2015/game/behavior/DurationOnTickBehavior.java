package capstone2015.game.behavior;

import capstone2015.entity.Actor;

public class DurationOnTickBehavior implements OnTickBehavior{

    @Override
    public void invoke(Actor entity, double timeDelta) {
        /*************************
         * Negative duration means the entity lasts forever.
         * Why did you even attach this behavior to it?
         */
        if(entity.getDuration() < 0.d){
            return;
        }

        entity.setDuration(Math.max(0.d, entity.getDuration() - timeDelta));

        if(entity.getDuration() == 0.f){
            entity.terminate();
        }
    }
}
