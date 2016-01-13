package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.game.Direction;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.game.RangerMapTraversableAdapter;
import static capstone2015.game.behavior.MovingDamageOnCollisionOnTickBehavior.RANDOM_MOVE_RADIUS;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.EntityMove;
import static capstone2015.messaging.Message.Type.SpawnActor;
import capstone2015.messaging.SpawnActorParams;
import capstone2015.pathfinding.AStar;
import capstone2015.pathfinding.Traversable;
import capstone2015.util.Util;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FireImpOnTickBehavior implements OnTickBehavior{
    public static final double MOVE_TIMEOUT = 0.75f;
    public static final double ATTACK_TIMEOUT = 0.65f;
    
    private LinkedList<Vec2i> path = new LinkedList<>();
    private Vec2i targetPosition = null;
    
    @Override
    public void invoke(Actor entity, double timeDelta){                
        /***************************
         * Determine closest target
         */
        
        List<Actor> targets = entity.getView().getActors().stream().filter(
                a -> (!a.isInvulnerable() && a.getTeamId() != entity.getTeamId())
        ).collect(Collectors.toList());

        Vec2i closest_target_pos = null;
        for(Actor target : targets){
            if(closest_target_pos == null) {
                closest_target_pos = new Vec2i(target.getPos());
                continue;
            }

            if(target.getPos().deltaOrthoMagnitude(entity.getPos()) < closest_target_pos.deltaOrthoMagnitude(entity.getPos())){
                closest_target_pos = new Vec2i(target.getPos());
            }
        }
        
        /***************************
         * If there's a potential target for us, and we aren't yet aiming for that spot,
         * either deal damage if it's close to our position, or pathfind to that location
         */
        if(closest_target_pos != null){
            if(closest_target_pos.subtract(entity.getPos()).isOrthogonal()){
                /*****************************
                 * Do nothing if we can't attack yet
                 */
                if(!entity.canUse())
                    return;
                /*****************************
                 * If we're right next to the closest target, deal damage to it
                 */               
                SpawnActorParams msg_obj = new SpawnActorParams();
                msg_obj.entityId = EntityFactory.ID_FIRE_BOLT;
                msg_obj.parent = entity;
                msg_obj.position = new Vec2i(entity.getPos());
                
                Map<String, Object> instantiation_params = new TreeMap();
                instantiation_params.put("ShootDirection", Util.toDirection(closest_target_pos.subtract(entity.getPos())));
                instantiation_params.put("TeamIdOverride", entity.getTeamId());
                
                msg_obj.instantiationParams = instantiation_params;

                entity.sendBusMessage(new Message(SpawnActor, msg_obj));
                entity.setUseTimeout(ATTACK_TIMEOUT);
                return;
            } else {
                /****************************
                 * Else, pathfind to it
                 */
                if(!entity.canMove())
                    return;
                
                if(path == null ||path.size() == 0 || targetPosition == null || !targetPosition.equals(closest_target_pos)) {
                    //Only recalculate path if path is empty or we're not already aiming for that entity
                    Traversable traversable = new RangerMapTraversableAdapter(entity.getView(), entity.getSolidType(), 7);
                    path = AStar.find(traversable, entity.getPos(), closest_target_pos, 0.f);
                    targetPosition = new Vec2i(closest_target_pos);
                }
            }
        }
        
        if(!entity.canMove())
            return;
        
        /*********************************
         * If no target was found, find a random spot to go to
         */
        int n_trials = 10;
        while((path == null || path.isEmpty())){
            if(n_trials-- <= 0)
                return;
            
            Random rand = new Random();
            Vec2i random_point = entity.getPos().add(new Vec2i(rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS,
                    rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS));

            if(entity.getView().inBounds(random_point) && !entity.getView().getSolidTypeAt(random_point).collidesWith(entity.getSolidType())){
                Traversable traversable = new MapTraversableAdapter(entity.getView(), entity.getSolidType());
                path = AStar.find(traversable, entity.getPos(), random_point);
                targetPosition = null;
            }
        }
        
        /*****************
         * Go to the previously determined spot
         */
        Vec2i pos = new Vec2i(entity.getPos());
        Vec2i target_pos = new Vec2i(path.pollFirst());

        Vec2i dirvec = target_pos.subtract(pos);
        
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
        emp.entity = entity;
        emp.direction = dir;
        entity.sendBusMessage(new Message(EntityMove, emp));
        entity.setMoveTimeout(MOVE_TIMEOUT);
    }
}
