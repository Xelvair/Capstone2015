package capstone2015.game;

import capstone2015.messaging.MessageBus;

public abstract class EntityBehavior {    
    public abstract EntityAction onTick(double deltaTime, MessageBus messageBus);
}
