package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.ReceivedDamageParams;
import capstone2015.util.Util;
import java.util.Map;

public class MagicBoltOnTickBehavior implements OnTickBehavior{

    public static int MAGIC_BOLT_DAMAGE = 1;
    public static double MAGIC_BOLT_MOVE_TIMEOUT = 0.03f;
    
    private Direction flightDirection;
    private int teamId;
    
    public MagicBoltOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        if(
               instantiationParams.containsKey("ShootDirection")
            && instantiationParams.containsKey("TeamId")
        ){
            flightDirection = Util.toDirection((Vec2i)instantiationParams.get("ShootDirection"));
            teamId = (int)instantiationParams.get("TeamId");
        }
    }

    @Override
    public void invoke(Actor entity, double timeDelta) {
        for(Message m : entity.getMessageBus()){
            switch(m.getType()){
                case GameMessage.ENTITY_MOVE_FAILED:
                {
                    Actor a = (Actor)m.getMsgObject();
                    if(a == entity){
                        entity.terminate();
                        return;
                    }
                    break;
                }
                case GameMessage.RECEIVED_DAMAGE:
                {
                    ReceivedDamageParams rdp = (ReceivedDamageParams)m.getMsgObject();
                    if(rdp.damagingActor == entity){
                        entity.terminate();
                        return;
                    }
                    break;
                }
            }
        }
        
        /*************************
         * Check immediate surroundings for damageable actors
         * and damage them
         */       
        for(Actor a : entity.getView().getActors()){
            Vec2i delta_vec = a.getPos().subtract(entity.getPos());
            if(delta_vec.orthoMagnitude() <= 1){
                InflictDamageParams idp = new InflictDamageParams();
                idp.damage = MAGIC_BOLT_DAMAGE;
                idp.position = a.getPos();
                idp.damagingEntity = entity;
                idp.teamId = teamId;
                
                entity.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp));
            }
        }

        if(!entity.canMove())
            return;
        
        /**************************
         * Check if we should change our trajectory
         */
        Vec2i target_delta = null;
        float xy_coef_min = Float.MAX_VALUE;
        for(Actor a : entity.getView().getActors()){
            if(a.getTeamId() != entity.getTeamId() && !a.isInvulnerable()){
                Vec2i enemy_pos = a.getPos();
                Vec2i this_pos = entity.getPos();
                Vec2i dir_vec = flightDirection.toVector();
                Vec2i delta_vec = enemy_pos.subtract(this_pos);
                
                if(dir_vec.dotProduct(delta_vec) > 0){
                    switch(flightDirection){
                        case LEFT:
                        case RIGHT:
                        {
                            if(Math.abs(delta_vec.getX()) > Math.abs(delta_vec.getY()))
                                continue;
                            
                            float this_xy_coef = (float)delta_vec.getX() / (float)delta_vec.getY();
                            if(this_xy_coef < xy_coef_min){
                                xy_coef_min = this_xy_coef;
                                target_delta = delta_vec;
                            }
                            break;
                        }
                        case UP:
                        case DOWN:
                        {
                            if(Math.abs(delta_vec.getY()) > Math.abs(delta_vec.getX()))
                                continue;
                            
                            float this_xy_coef =  (float)delta_vec.getY() / (float)delta_vec.getX();
                            if(this_xy_coef < xy_coef_min){
                                xy_coef_min = this_xy_coef;
                                target_delta = delta_vec;
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        /**************************
         * If a reachable target was found, fix trajectory to hit it
         */
        if(target_delta != null){
            Direction trajectory_fix_dir = Direction.NONE;
            switch(flightDirection){
                case LEFT:
                case RIGHT:
                    if(target_delta.getY() < 0)
                        trajectory_fix_dir = Direction.UP;
                    else if(target_delta.getY() > 0)
                        trajectory_fix_dir = Direction.DOWN;
                    break;
                case UP:
                case DOWN:
                    if(target_delta.getX() < 0)
                        trajectory_fix_dir = Direction.LEFT;
                    else if(target_delta.getX() > 0)
                        trajectory_fix_dir = Direction.RIGHT;
                    break;
            }
            
            if(trajectory_fix_dir != Direction.NONE){
                EntityMoveParams emp = new EntityMoveParams();
                emp.direction = trajectory_fix_dir;
                emp.entity = entity;

                entity.sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));
            }
        }
        
        /*********************
         * Do normal move as well
         */
        EntityMoveParams emp = new EntityMoveParams();
        emp.direction = flightDirection;
        emp.entity = entity;

        entity.sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));

        entity.setMoveTimeout(MAGIC_BOLT_MOVE_TIMEOUT);
    }
    
}
