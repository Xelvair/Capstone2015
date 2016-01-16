package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.game.GameMessage;
import capstone2015.messaging.Message;

public class DefaultOnDamageBehavior implements OnDamageBehavior{
    
    public static final double DAMAGE_IGNORE_TIME = 0.5d;
    
    @Override
    public boolean invoke(Actor entity, EntityBase damagingEntity, int damage) {
        //Ignore damage if we've recently been hit before
        if(!entity.getDamageIgnoreTimers().containsKey(damagingEntity)){
            entity.setHealthPoints(Math.max(0, entity.getHealth() - damage));
            entity.getDamageIgnoreTimers().put(damagingEntity, DAMAGE_IGNORE_TIME);
             
            if(entity.getHealth() == 0){
                entity.sendBusMessage(new Message(GameMessage.TERMINATE, entity));
                entity.terminate();
            }

            return true;
        }
        return false;
    }
}
