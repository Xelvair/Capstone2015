package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.pathfinding.AStar;
import capstone2015.util.Util;
import java.util.LinkedList;

public class MeleeAttackState extends ActorState{
    
    private LinkedList<Vec2i> path;
    private Vec2i lastTargetPos;
    private Actor target;
    
    public MeleeAttackState(Actor actor, Actor target) {
        super(actor);
        
        this.target = target;
            
        getActor().capMoveTimeout(actor.getAttackMoveTimeout());
    }

    @Override
    public void onTick(double timeDelta) {
        if(isBlur())
            return;
            
        /***************
         * If the target is on our side now, quit
         */
        if(target.getTeamId() == getActor().getTeamId()){
            terminate();
        }

        /***************
         * If we're right next to our target, attack it if we can attack
         * else
         * go towards that spot if we can move
         */
        if(getActor().getPos().deltaOrthoMagnitude(target.getPos()) == 1){
            if(!getActor().canUse())
                return;

            InflictDamageParams msg_obj = new InflictDamageParams();
            msg_obj.damagingEntity = getActor();
            msg_obj.position = target.getPos();
            msg_obj.damage = getActor().getAttackDamage();
            msg_obj.teamId = getActor().getTeamId();

            getActor().sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, msg_obj));
            getActor().setUseTimeout(getActor().getAttackTimeout());
            return;
        } else if(getActor().canMove()){
            /*********
             * Fix invalid or non existing path
             */
            if(   path == null 
               || path.size() <= 0 
               || path.get(0).deltaOrthoMagnitude(getActor().getPos()) != 1 
               || !lastTargetPos.equals(target.getPos())
            ){
                MapTraversableAdapter mta = new MapTraversableAdapter(getActor().getView(), getActor().getSolidType());
                path = AStar.find(mta, getActor().getPos(), target.getPos(), 1.f);
                lastTargetPos = new Vec2i(target.getPos());

                if(path == null){
                    terminate();
                    return;
                }
            }

            Vec2i pos = getActor().getPos();
            Vec2i target_pos = path.pollFirst();

            Vec2i dirvec = target_pos.subtract(pos);

            Direction dir = Util.toDirection(dirvec);

            EntityMoveParams emp = new EntityMoveParams();
            emp.entity = getActor();
            emp.direction = dir;
            getActor().sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));
            getActor().setMoveTimeout(getActor().getAttackMoveTimeout());
            getActor().setUseTimeout(getActor().getAttackTimeout());
        }
    }
}
