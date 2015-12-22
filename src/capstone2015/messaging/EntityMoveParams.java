package capstone2015.messaging;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;

public class EntityMoveParams {
    private Actor           entity;
    private Direction       direction;
    
    public EntityMoveParams(Actor entity, Direction direction){
        this.entity = entity;
        this.direction = direction;
    }

    public Actor getEntity() {
        return entity;
    }

    public Direction getDirection() {
        return direction;
    }
}
