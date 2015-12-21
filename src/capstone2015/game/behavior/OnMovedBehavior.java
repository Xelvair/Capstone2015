package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.game.Entity;
import capstone2015.messaging.MessageBus;
import java.util.ArrayList;

public interface OnMovedBehavior {
    public void invoke(ActiveEntity entity, ArrayList<Entity> entitiesAtPos, MessageBus messageBus);
}
