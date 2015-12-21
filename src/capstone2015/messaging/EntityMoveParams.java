package capstone2015.messaging;

import capstone2015.game.ActiveEntity;
import capstone2015.game.Direction;

public class EntityMoveParams {
    private ActiveEntity    entity;
    private Direction       direction;
    
    public EntityMoveParams(ActiveEntity entity, Direction direction){
        this.entity = entity;
        this.direction = direction;
    }

    public ActiveEntity getEntity() {
        return entity;
    }

    public Direction getDirection() {
        return direction;
    }
}
