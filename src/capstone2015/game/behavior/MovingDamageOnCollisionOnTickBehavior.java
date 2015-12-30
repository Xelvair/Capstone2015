package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.entity.EntityFactory;
import capstone2015.game.Direction;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.EntityMove;
import static capstone2015.messaging.Message.Type.InflictDamage;
import capstone2015.pathfinding.AStar;

import java.util.*;

public class MovingDamageOnCollisionOnTickBehavior implements OnTickBehavior{

    public static final int DAMAGE = 1;
    public static final float MOVE_TIMEOUT = 0.7f;
    public static final int RANDOM_MOVE_RADIUS = 10;
            
    private LinkedList<Vec2i> path = new LinkedList<>();
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        /*********
         * Decrease damage ignore timers
         */

        HashMap<EntityBase, Double> damage_ignore_timers = entity.getDamageIgnoreTimers();

        for(Iterator<Map.Entry<EntityBase, Double>> it = damage_ignore_timers.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<EntityBase, Double> entry = it.next();
            double damage_timer = entry.getValue();
            double new_damage_timer = Math.max(0, damage_timer - timeDelta);

            if(new_damage_timer == 0.f){
                it.remove();
            } else {
                entry.setValue(new_damage_timer);
            }
        }

        /********************
         * Inflict damage at our current location
         */
        InflictDamageParams msg_obj = new InflictDamageParams();
        msg_obj.damagingEntity = entity;
        msg_obj.position = entity.getPos();
        msg_obj.damage = DAMAGE;
        msg_obj.teamId = entity.getTeamId();

        entity.sendBusMessage(new Message(InflictDamage, msg_obj));

        /************************
         * If we can't move yet, exit
         */
        if(!entity.canMove())
            return;

        /***************************
         * If there's a potential target for us, and we aren't yet aiming for that spot,
         * pathfind to that location
         */
        ArrayList<Actor> targets = entity.getView().getActorsById(EntityFactory.ID_PLAYER);

        if(targets.size() > 0 && (path.isEmpty() || !path.peekLast().equals(targets.get(0).getPos()))){
            MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView(), entity.getSolidType());
            path = AStar.find(mta, entity.getPos(), targets.get(0).getPos());
        }

        /*********************************
         * If no target was found, find a random spot to go to
         */
        while(path == null || path.isEmpty()){
            Random rand = new Random();
            Vec2i random_point = entity.getPos().add(new Vec2i(rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS,
                    rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS));

            if(entity.getView().inBounds(random_point) && !entity.getView().getSolidTypeAt(random_point).collidesWith(entity.getSolidType())){
                MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView(), entity.getSolidType());
                path = AStar.find(mta, entity.getPos(), random_point);
            }
        }

        /*****************
         * Go to the previously determined spot
         */
        Vec2i pos = entity.getPos();
        Vec2i target_pos = path.pollFirst();

        Vec2i dirvec = target_pos.add(pos.invert());

        Direction dir;

        if(dirvec.equals(new Vec2i(1, 0))){
            dir = Direction.RIGHT;
        } else if(dirvec.equals(new Vec2i(-1, 0))){
            dir = Direction.LEFT;
        } else if(dirvec.equals(new Vec2i(0, -1))){
            dir = Direction.UP;
        } else {
            dir = Direction.DOWN;
        }

        EntityMoveParams emp = new EntityMoveParams();
        emp.entity = entity;
        emp.direction = dir;
        entity.sendBusMessage(new Message(EntityMove, emp));
        entity.setMoveTimeout(MOVE_TIMEOUT);
    }
    
}
