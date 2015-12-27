package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.ReceivedDamageParams;

public class PlayerOnDamageBehavior implements OnDamageBehavior{
    
    public static final double DAMAGE_IGNORE_TIME = 0.5d;
    
    @Override
    public void invoke(Actor entity, EntityBase damagingEntity, int damage) {
        //Ignore damage if we've recently been hit before
        if(!entity.getDamageIgnoreTimers().containsKey(damagingEntity)){
            entity.setHealthPoints(Math.max(0, entity.getHealth() - damage));
            entity.getDamageIgnoreTimers().put(damagingEntity, DAMAGE_IGNORE_TIME);
            
            ReceivedDamageParams msg_obj = new ReceivedDamageParams(entity, damagingEntity, damage);
            entity.sendBusMessage(new Message(ReceivedDamage, msg_obj));
             
            if(entity.getHealth() == 0){
                entity.sendBusMessage(new Message(Terminate, entity));
                entity.terminate();
            }
        }  
    }
}
