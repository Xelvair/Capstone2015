package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.messaging.MessageBus;
import java.util.HashMap;

public class PlayerOnDamageBehavior implements OnDamageBehavior{
    
    public static final double DAMAGE_IGNORE_TIME = 0.5d;
    
    @Override
    public void invoke(ActiveEntity entity, ActiveEntity damagingEntity, int damage, MessageBus messageBus) {
        
        //Ignore damage if we've recently been hit before
        if(!entity.getDamageIgnoreTimers().containsKey(damagingEntity)){
            entity.setHealthPoints(Math.max(0, entity.getHealthPoints() - damage));
            entity.getDamageIgnoreTimers().put(damagingEntity, DAMAGE_IGNORE_TIME);
            
            System.out.println("Player hit! HP left: " + entity.getHealthPoints());
            
            if(entity.getHealthPoints() == 0){
                entity.terminate();
            }
        }  
    }
}
