package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.SolidType;
import capstone2015.game.Direction;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.EntityMove;
import static capstone2015.messaging.Message.Type.InflictDamage;
import capstone2015.pathfinding.AStar;
import capstone2015.state.State;
import capstone2015.state.StateMachine;
import capstone2015.util.Util;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RattlesnakeOnTickBehavior implements OnTickBehavior{
    
    public static final double STRAY_OUTER = 12.d;
    public static final double STRAY_INNER = 5.d;
    
    class AttackState extends State{
        
        public static final int ATTACK_DAMAGE = 1;
        public static final double ATTACK_TIMEOUT = 0.5d;
        public static final double ATTACK_MOVE_TIMEOUT = 0.225d;

        private Actor entity;
        private StateMachine stateMachine;
        private Actor target;
        private LinkedList<Vec2i> path;
        private Vec2i lastTargetPos;
        
        public AttackState(Actor entity, StateMachine stateMachine, Actor target){
            this.entity = entity;
            this.stateMachine = stateMachine;
            this.target = target;
            
            entity.resetMoveTimeout();
        }
        
        @Override
        protected void onTick(double timeDelta) {
            if(isBlur())
                return;
            
            /***************
             * If we can't see our target anymore, quit
             */
            if(!entity.getView().getActors().contains(target)){
                terminate();
            }
            
            /***************
             * If the target is on our side now, quit
             */
            if(target.getTeamId() == entity.getTeamId()){
                terminate();
            }
            
            /***************
             * Check if our target is still the closest target,
             * else go attack the closest one
             */
            List<Actor> targets = entity.getView().getActors().stream().filter(
                    a -> (!a.isInvulnerable() && a.getTeamId() != entity.getTeamId())
            ).collect(Collectors.toList());
            
            if(targets.size() > 0){
                targets.sort(new Comparator<Actor>(){
                    @Override
                    public int compare(Actor o1, Actor o2) {
                        int o1_dist = entity.getPos().deltaOrthoMagnitude(o1.getPos());
                        int o2_dist = entity.getPos().deltaOrthoMagnitude(o2.getPos());
                        
                        int result = Util.clamp(o1_dist - o2_dist, -1, 1);
                        return result;
                    }
                });
                
                Actor closest_target = targets.get(0);
                if(!closest_target.equals(target)){
                    stateMachine.pushState(new AttackState(entity, stateMachine, closest_target));
                    terminate();
                    return;
                }
            }
            
            /***************
             * If we're right next to our target, attack it if we can attack
             * else
             * go towards that spot if we can move
             */
            if(entity.getPos().deltaOrthoMagnitude(target.getPos()) == 1){
                if(!entity.canUse())
                    return;
                
                InflictDamageParams msg_obj = new InflictDamageParams();
                msg_obj.damagingEntity = entity;
                msg_obj.position = target.getPos();
                msg_obj.damage = ATTACK_DAMAGE;
                msg_obj.teamId = entity.getTeamId();

                entity.sendBusMessage(new Message(InflictDamage, msg_obj));
                entity.setUseTimeout(ATTACK_TIMEOUT);
                return;
            } else if(entity.canMove()){
                /*********
                 * Fix invalid or non existing path
                 */
                if(   path == null 
                   || path.size() <= 0 
                   || path.get(0).deltaOrthoMagnitude(entity.getPos()) != 1 
                   || !lastTargetPos.equals(target.getPos())
                ){
                    MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView(), entity.getSolidType());
                    path = AStar.find(mta, entity.getPos(), target.getPos(), 1.f);
                    lastTargetPos = new Vec2i(target.getPos());
                    
                    if(path == null){
                        terminate();
                        return;
                    }
                }
                
                Vec2i pos = entity.getPos();
                Vec2i target_pos = path.pollFirst();

                Vec2i dirvec = target_pos.subtract(pos);

                Direction dir = Util.toDirection(dirvec);

                EntityMoveParams emp = new EntityMoveParams();
                emp.entity = entity;
                emp.direction = dir;
                entity.sendBusMessage(new Message(EntityMove, emp));
                entity.setMoveTimeout(ATTACK_MOVE_TIMEOUT);
                entity.setUseTimeout(ATTACK_TIMEOUT);
            }
        }
    }
    
    class GetInRangeOf extends State{
        public static final double GETINRANGE_MOVE_TIMEOUT = 0.1d;
        
        private Actor entity;
        private StateMachine stateMachine;
        private Actor target;
        private double range;
        private Vec2i lastTargetPos;
        private LinkedList<Vec2i> path;

        public GetInRangeOf(Actor entity, StateMachine stateMachine, Actor target, double range){
            this.entity = entity;
            this.stateMachine = stateMachine;
            this.target = target;
            this.range = range;
            
            entity.resetMoveTimeout();
        }
        
        @Override
        protected void onTick(double timeDelta) {
            if(isBlur()){
                return;
            }
            
            if(!entity.canMove())
                return;               
            
            /***********
             * If we're in range, exit
             */
            if(target.getPos().deltaOrthoMagnitude(entity.getPos()) <= range){
                terminate();
                return;
            }
            
            /***********
             * If we have no path or it is invalid, regenerate
             */
            if(lastTargetPos == null || !lastTargetPos.equals(target.getPos()) || path == null || path.isEmpty()){
                Random rand = new Random();
                MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView(), entity.getSolidType());
                path = AStar.find(mta, entity.getPos(), target.getPos(), (double)range);
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
            
            Vec2i pos = entity.getPos();
            Vec2i target_pos = path.pollFirst();

            Vec2i dirvec = target_pos.subtract(pos);

            Direction dir = Util.toDirection(dirvec);

            EntityMoveParams emp = new EntityMoveParams();
            emp.entity = entity;
            emp.direction = dir;
            entity.sendBusMessage(new Message(EntityMove, emp));
            entity.setMoveTimeout(GETINRANGE_MOVE_TIMEOUT);
        }
    }
    
    class WanderingState extends State{
        public static final double WANDER_MOVE_TIMEOUT = 3.d;
        
        private Actor entity;
        private StateMachine stateMachine;
        
        public WanderingState(Actor entity, StateMachine stateMachine){
            this.entity = entity;
            this.stateMachine = stateMachine;
        }
               
        @Override
        protected void onTick(double timeDelta) {
            if(isBlur())
                return;
            
            if(entity.getLeader() != null && entity.getPos().deltaMagnitude(entity.getLeader().getPos()) > STRAY_INNER){
                stateMachine.pushState(new GetInRangeOf(entity, stateMachine, entity.getLeader(), STRAY_INNER));
            }
            
            List<Actor> targets = entity.getView().getActors().stream().filter(
                    a -> (!a.isInvulnerable() && a.getTeamId() != entity.getTeamId())
            ).collect(Collectors.toList());
            
            if(targets.size() > 0){
                targets.sort(new Comparator<Actor>(){
                    @Override
                    public int compare(Actor o1, Actor o2) {
                        int o1_dist = entity.getPos().deltaOrthoMagnitude(o1.getPos());
                        int o2_dist = entity.getPos().deltaOrthoMagnitude(o2.getPos());
                        
                        return Util.clamp(-1, 1, o1_dist - o2_dist);
                    }
                });
                
                stateMachine.pushState(new AttackState(entity, stateMachine, targets.get(0)));
                return;
            }
            
            /********************
             * Check if we can move, and then find a direction to go in
             */
            if(entity.canMove()){
                List<Direction> directions = Arrays.asList(Direction.values());
                Collections.shuffle(directions);
                
                for(Direction dir : directions){
                    Vec2i new_pos = entity.getPos().add(dir.toVector());
                    
                    SolidType entity_solid_type = entity.getSolidType();
                    SolidType newpos_solid_type = entity.getView().getSolidTypeAt(new_pos);
                    
                    if(!entity_solid_type.collidesWith(newpos_solid_type)){
                        EntityMoveParams emp = new EntityMoveParams();
                        emp.entity = entity;
                        emp.direction = dir;
                        entity.sendBusMessage(new Message(EntityMove, emp));
                        
                        entity.setMoveTimeout(WANDER_MOVE_TIMEOUT);
                        
                        break;
                    }
                }
            }
        }
    }
    
    private StateMachine stateMachine;
    
    public RattlesnakeOnTickBehavior(){
        stateMachine = new StateMachine();
    }
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        if(stateMachine.isEmpty())
            stateMachine.pushState(new WanderingState(entity, stateMachine));
        
        stateMachine.tick(timeDelta);
    }
}
