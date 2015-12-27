package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.MapEntity;
import capstone2015.messaging.MessageBus;
import java.util.ArrayList;

public interface OnMovedBehavior {
    public void invoke(Actor entity, ArrayList<MapEntity> entitiesAtPos);
}
