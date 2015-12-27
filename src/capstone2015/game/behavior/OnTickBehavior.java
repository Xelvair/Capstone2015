package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.messaging.MessageBus;

public interface OnTickBehavior {
    public void invoke(Actor entity, double timeDelta);
}
