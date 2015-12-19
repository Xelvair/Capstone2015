package capstone2015.game.behavior;

import capstone2015.game.EntityAction;
import capstone2015.game.EntityActionType;
import capstone2015.game.EntityBehavior;

public class PlayerBehavior extends EntityBehavior{
    public static final double MOVE_TIMEOUT = 0.2f;
    
    private double moveTimeoutCounter = 0;
    @Override
    public EntityAction onTick(double timeDelta) {
        moveTimeoutCounter = Math.max(0.f, moveTimeoutCounter - timeDelta);
        
        if(moveTimeoutCounter == 0.f){
            moveTimeoutCounter = MOVE_TIMEOUT;
            return new EntityAction(EntityActionType.MOVE_RIGHT, null);
        }
        return new EntityAction(EntityActionType.NONE, null);
    }
    
}
