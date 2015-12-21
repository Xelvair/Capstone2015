package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import capstone2015.messaging.ReceivedDamageParams;
import java.util.HashMap;

public class PlayerOnDamageBehavior implements OnDamageBehavior{
    
    public static final double DAMAGE_IGNORE_TIME = 0.5d;
    
    @Override
    public void invoke(ActiveEntity entity, ActiveEntity damagingEntity, int damage, MessageBus messageBus) {
        
        //Ignore damage if we've recently been hit before
        if(!entity.getDamageIgnoreTimers().containsKey(damagingEntity)){
            entity.setHealthPoints(Math.max(0, entity.getHealthPoints() - damage));
            entity.getDamageIgnoreTimers().put(damagingEntity, DAMAGE_IGNORE_TIME);
            
            ReceivedDamageParams msg_obj = new ReceivedDamageParams(entity, damagingEntity, damage);
            messageBus.enqueue(new Message(ReceivedDamage, msg_obj));
             
            if(entity.getHealthPoints() == 0){
                messageBus.enqueue(new Message(Terminate, entity));
                entity.terminate();
            }
        }  
    }
}
