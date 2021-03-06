package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import capstone2015.pathfinding.AStar;
import capstone2015.util.Util;
import java.util.LinkedList;
import java.util.Random;

public class GetInRangeOfState extends ActorState{
    
    private double range;
    private Vec2i lastTargetPos;
    private LinkedList<Vec2i> path;
    Actor target;
    
    public GetInRangeOfState(Actor actor, Actor target, Double range) {
        super(actor);
        
        this.target = target;
        this.range = range;

        getActor().capMoveTimeout(actor.getGetInRangeMoveTimeout());
    }

    @Override
    public void onTick(double timeDelta) {
                   if(isBlur()){
                return;
            }
            
            if(!getActor().canMove())
                return;               
            
            /***********
             * If we're in range, exit
             */
            if(target.getPos().deltaOrthoMagnitude(getActor().getPos()) <= range){
                terminate();
                return;
            }
            
            /***********
             * If we have no path or it is invalid, regenerate
             */
            if(        lastTargetPos == null 
                    || !lastTargetPos.equals(target.getPos()) 
                    || path == null 
                    || path.isEmpty() 
                    || getActor().getPos().deltaOrthoMagnitude(path.peekFirst()) > 1
            ){
                Random rand = new Random();
                MapTraversableAdapter mta = new MapTraversableAdapter(getActor().getView(), getActor().getSolidType());
                path = AStar.find(mta, getActor().getPos(), target.getPos(), (double)range);
                lastTargetPos = new Vec2i(target.getPos());
                
                if(path == null){
                    terminate();
                    return;
                }
            }
            
            if(path.size() == 0){
                terminate();
                return;
            }
            
            Vec2i pos = getActor().getPos();
            Vec2i target_pos = path.pollFirst();

            Vec2i dirvec = target_pos.subtract(pos);

            Direction dir = Util.toDirection(dirvec);

            EntityMoveParams emp = new EntityMoveParams();
            emp.entity = getActor();
            emp.direction = dir;
            getActor().sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));
            getActor().setMoveTimeout(getActor().getGetInRangeMoveTimeout());
    }    
}
