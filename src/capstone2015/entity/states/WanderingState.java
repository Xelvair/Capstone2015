package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.entity.SolidType;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WanderingState extends ActorState{
    
    public WanderingState(Actor actor) {
        super(actor);
    }

    @Override
    public void onTick(double timeDelta) {
        if(isBlur())
            return;

        /********************
         * Check if we can move, and then find a direction to go in
         */
        if(getActor().canMove()){
            List<Direction> directions = Arrays.asList(Direction.values());
            Collections.shuffle(directions);

            for(Direction dir : directions){
                Vec2i new_pos = getActor().getPos().add(dir.toVector());

                if(!getActor().getView().inBounds(new_pos))
                    continue;
                
                SolidType entity_solid_type = getActor().getSolidType();
                SolidType newpos_solid_type = getActor().getView().getSolidTypeAt(new_pos);

                if(!entity_solid_type.collidesWith(newpos_solid_type)){
                    EntityMoveParams emp = new EntityMoveParams();
                    emp.entity = getActor();
                    emp.direction = dir;
                    getActor().sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));

                    getActor().setMoveTimeout(getActor().getWanderingMoveTimeout());

                    break;
                }
            }
        }
    }
}
