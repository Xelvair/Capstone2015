package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.entity.Item;
import capstone2015.game.Direction;
import capstone2015.game.Inventory;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayerOnTickBehavior implements OnTickBehavior{
    public static final double MOVE_TIMEOUT = 0.03f;
    
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
        
        Direction move_dir = Direction.NONE;
        Direction use_dir = Direction.NONE;
        boolean do_use = false;
        
        MessageBus message_bus = entity.getMessageBus();
        
        for(Message m : message_bus){
            switch(m.getType()){
                case KeyEvent:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowLeft:
                            move_dir = Direction.LEFT;
                            break;
                        case ArrowRight:
                            move_dir = Direction.RIGHT;
                            break;
                        case ArrowUp:
                            move_dir = Direction.UP;
                            break;
                        case ArrowDown:
                            move_dir = Direction.DOWN;
                            break;
                        case NormalKey:
                            switch(key.getCharacter()){
                                case 'e': //Pickup item
                                    entity.sendBusMessage(new Message(Pickup, entity));
                                    break;
                                case 'q': //Drop item
                                    entity.sendBusMessage(new Message(Drop, entity));
                                    break;
                                case 'f':
                                    do_use = true;
                                    break;
                                //WASD controls use direction
                                case 'w':
                                    do_use = true;
                                    use_dir = Direction.UP;
                                    break;
                                case 'a':
                                    do_use = true;
                                    use_dir = Direction.LEFT;
                                    break;
                                case 's':
                                    do_use = true;
                                    use_dir = Direction.DOWN;
                                    break;
                                case 'd':
                                    do_use = true;
                                    use_dir = Direction.RIGHT;
                                    break;
                                //Inventory select keys
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    int select_idx = (Character.getNumericValue(key.getCharacter()) - 1) % 9;
                                    entity.getInventory().setSelectIndex(select_idx);
                                    System.out.println(entity.getInventory().getSelectIndex());
                                    break;
                            }
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        if(do_use){
            Item sel_item = entity.getSelectedItem();
            if(sel_item != null){
                sel_item.onUse(entity, use_dir.toVector());
            }
        }
        
        if(entity.canMove() && move_dir != Direction.NONE){
            entity.setMoveTimeout(MOVE_TIMEOUT);
            EntityMoveParams msg_obj = new EntityMoveParams(entity, move_dir);
            entity.sendBusMessage(new Message(EntityMove, msg_obj));
        }
    } 
}
