package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.game.RangerMapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnActorParams;
import capstone2015.pathfinding.AStar;
import capstone2015.pathfinding.Traversable;
import capstone2015.util.Util;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class RangerAttackState extends ActorState{
    
    private LinkedList<Vec2i> path;
    private Vec2i lastTargetPos;
    
    public RangerAttackState(Actor actor){
        super(actor);
        
        getActor().capMoveTimeout(actor.getAttackMoveTimeout());
    }

    @Override
    protected void onTick(double timeDelta) {
        if(isBlur())
            return;
        
        /***************
         * If the target is on our side now, quit
         */
        if(getActor().getTarget().getTeamId() == getActor().getTeamId()){
            terminate();
        }
        
        /***************
         * Shoot at target or walk towards it
         */
        Vec2i target_pos = getActor().getTarget().getPos();
        if(getActor().getTarget().getPos().subtract(getActor().getPos()).isOrthogonal()){
            /*****************************
             * Do nothing if we can't attack yet
             */
            if(!getActor().canUse())
                return;
            /*****************************
             * If we're right next to the closest target, deal damage to it
             */               
            SpawnActorParams msg_obj = new SpawnActorParams();
            msg_obj.entityId = EntityFactory.ID_FIRE_BOLT;
            msg_obj.parent = getActor();
            msg_obj.position = new Vec2i(getActor().getPos());

            Map<String, Object> instantiation_params = new TreeMap();
            instantiation_params.put("ShootDirection", Util.toDirection(target_pos.subtract(getActor().getPos())));
            instantiation_params.put("TeamIdOverride", getActor().getTeamId());

            msg_obj.instantiationParams = instantiation_params;

            getActor().sendBusMessage(new Message(GameMessage.SPAWN_ACTOR, msg_obj));
            getActor().setUseTimeout(getActor().getAttackTimeout());
            return;
        } else {
            /****************************
             * Else, pathfind to it
             */
            if(!getActor().canMove())
                return;

            if(path == null || path.size() == 0) {
                //Only recalculate path if path is empty or we're not already aiming for that entity
                Traversable traversable = new RangerMapTraversableAdapter(getActor().getView(), getActor().getSolidType(), getActor().getAttackRange());
                path = AStar.find(traversable, getActor().getPos(), target_pos, 0.f);
                
                if(path == null){
                    terminate();
                    return;
                }
            }
        }
        
        /*****************
         * Go to the previously determined spot
         */
        Vec2i pos = new Vec2i(getActor().getPos());
        Vec2i move_target_pos = new Vec2i(path.pollFirst());

        Vec2i dirvec = move_target_pos.subtract(pos);
        
        Direction dir;
        
        if(dirvec.equals(new Vec2i(1, 0))){
            dir = Direction.RIGHT;
        } else if(dirvec.equals(new Vec2i(-1, 0))){
            dir = Direction.LEFT;
        } else if(dirvec.equals(new Vec2i(0, -1))){
            dir = Direction.UP;
        } else if(dirvec.equals(new Vec2i(0, 1))){
            dir = Direction.DOWN;
        } else {
            path.clear();
            return;
        }

        EntityMoveParams emp = new EntityMoveParams();
        emp.entity = getActor();
        emp.direction = dir;
        getActor().sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));
        getActor().setMoveTimeout(getActor().getAttackMoveTimeout());
    }
}
