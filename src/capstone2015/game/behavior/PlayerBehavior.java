package capstone2015.game.behavior;

import capstone2015.game.EntityAction;
import capstone2015.game.EntityActionType;
import capstone2015.game.EntityBehavior;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

public class PlayerBehavior extends EntityBehavior{
    public static final double MOVE_TIMEOUT = 0.05f;
    
    private enum MoveDirection{
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
    
    private double moveTimeoutCounter = 0;
    
    @Override
    public EntityAction onTick(double timeDelta, MessageBus messageBus) {
        moveTimeoutCounter = Math.max(0.f, moveTimeoutCounter - timeDelta);
        
        EntityActionType action = EntityActionType.NONE;
        
        for(Message m : messageBus){
            switch(m.getType()){
                case KeyEvent:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowLeft:
                            action = EntityActionType.MOVE_LEFT;
                            break;
                        case ArrowRight:
                            action = EntityActionType.MOVE_RIGHT;
                            break;
                        case ArrowUp:
                            action = EntityActionType.MOVE_UP;
                            break;
                        case ArrowDown:
                            action = EntityActionType.MOVE_DOWN;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        
        if(moveTimeoutCounter == 0.f && action.isMoveType()){
            moveTimeoutCounter = MOVE_TIMEOUT;
            return new EntityAction(action, null);
        }
        return new EntityAction(EntityActionType.NONE, null);
    }
    
}
