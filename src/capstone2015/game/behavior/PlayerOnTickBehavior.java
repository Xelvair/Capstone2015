package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.game.Direction;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.util.HashMap;

public class PlayerOnTickBehavior implements OnTickBehavior{
    public static final double MOVE_TIMEOUT = 0.05f;

    private double moveTimeoutCounter = 0;
    
    @Override
    public void invoke(ActiveEntity entity, double timeDelta, MessageBus messageBus) {
        /*********
         * Decrease damage ignore timers
         */
        HashMap<ActiveEntity, Double> damage_ignore_timers = entity.getDamageIgnoreTimers();
        for(ActiveEntity key : damage_ignore_timers.keySet()){
            double damage_timer = damage_ignore_timers.get(key);
            double new_damage_timer = Math.max(0, damage_timer - timeDelta);
            
            if(new_damage_timer == 0.f){
                damage_ignore_timers.remove(key);
            } else {
                damage_ignore_timers.put(key, new_damage_timer);
            }
        }
        
        /********
         * Check for movement
         */
        moveTimeoutCounter = Math.max(0.f, moveTimeoutCounter - timeDelta);
        
        Direction direction = Direction.NONE;
        
        for(Message m : messageBus){
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
            messageBus.enqueue(new Message(EntityMoveEvent, msg_obj));
        }
    } 
}
