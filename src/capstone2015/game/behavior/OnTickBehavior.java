package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.messaging.MessageBus;

public interface OnTickBehavior {
    public void invoke(ActiveEntity entity, double timeDelta, MessageBus messageBus);
}
