package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.game.Direction;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerOnTickBehavior implements OnTickBehavior{
    public static final double MOVE_TIMEOUT = 0.05f;

    private double moveTimeoutCounter = 0;
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        /*********
         * Decrease damage ignore timers
         */
        
        HashMap<EntityBase, Double> damage_ignore_timers = entity.getDamageIgnoreTimers();
        
        for(Iterator<Map.Entry<EntityBase, Double>> it = damage_ignore_timers.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<EntityBase, Double> entry = it.next();
            double damage_timer = entry.getValue();
            double new_damage_timer = Math.max(0, damage_timer - timeDelta);
            
            if(new_damage_timer == 0.f){
                it.remove();
            } else {
                entry.setValue(new_damage_timer);
            }
        }
        
        /********
         * Check for movement
         */
        moveTimeoutCounter = Math.max(0.f, moveTimeoutCounter - timeDelta);
        
        Direction direction = Direction.NONE;
        
        MessageBus message_bus = entity.getMessageBus();
        
        for(Message m : message_bus){
            switch(m.getType()){
                case KeyEvent:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowLeft:
                            direction = Direction.LEFT;
                            break;
                        case ArrowRight:
                            direction = Direction.RIGHT;
                            break;
                        case ArrowUp:
                            direction = Direction.UP;
                            break;
                        case ArrowDown:
                            direction = Direction.DOWN;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        
        if(moveTimeoutCounter == 0.f && direction != Direction.NONE){
            moveTimeoutCounter = MOVE_TIMEOUT;
            EntityMoveParams msg_obj = new EntityMoveParams(entity, direction);
            entity.sendBusMessage(new Message(EntityMove, msg_obj));
        }
    } 
}
