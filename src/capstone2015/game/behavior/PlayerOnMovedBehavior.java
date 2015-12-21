package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.game.Entity;
import capstone2015.game.EntityProto;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import java.util.ArrayList;

public class PlayerOnMovedBehavior implements OnMovedBehavior{
    @Override
    public void invoke(ActiveEntity entity, ArrayList<Entity> entitiesAtPos, MessageBus messageBus) {
        for(Entity e : entitiesAtPos){
            EntityProto e_proto = EntityProto.get(e.getId());
            
            if(e_proto.isEncounterNotified()){
                messageBus.enqueue(new Message(PlayerEncounter, e));
            }
        }
    }
}
