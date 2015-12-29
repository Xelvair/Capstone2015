package capstone2015.game.behavior;

import capstone2015.entity.Actor;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MovingDamageOnCollisionOnTickBehavior implements OnTickBehavior{

    public static final int DAMAGE = 1;
    public static final float MOVE_TIMEOUT = 1.f;
    public static final int RANDOM_MOVE_RADIUS = 10;
            
    private LinkedList<Vec2i> path = new LinkedList<>();
    private float moveTimeout = 0.f;
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        /********************
         * Inflict damage at our current location
         */
        InflictDamageParams msg_obj = new InflictDamageParams(
                entity, 
                new Vec2i(entity.getXPos(), entity.getYPos()), 
                DAMAGE
        );
        entity.sendBusMessage(new Message(InflictDamage, msg_obj));

        /************************
         * If we can't move yet, decrement move timeout and exit
         */
        if(moveTimeout > 0.f){
            moveTimeout = (float) Math.max(0.f, moveTimeout - timeDelta);
            return;
        }

        /***************************
         * If there's a potential target for us, and we aren't yet aiming for that spot,
         * pathfind to that location
         */
        ArrayList<Actor> targets = entity.getView().getActorsById(EntityFactory.ID_PLAYER);

        if(targets.size() > 0 && (path.isEmpty() || !path.peekLast().equals(targets.get(0).getPos()))){
            MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView());
            path = AStar.find(mta, entity.getPos(), targets.get(0).getPos());
        }

        /*********************************
         * If no target was found, find a random spot to go to
         */
        while(path.isEmpty()){
            Random rand = new Random();
            Vec2i random_point = entity.getPos().add(new Vec2i(rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS,
                    rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS));

            if(entity.getView().inBounds(random_point) && !entity.getView().isSolidAt(random_point)){
                MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView());
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

        EntityMoveParams emp = new EntityMoveParams(entity, dir);
        entity.sendBusMessage(new Message(EntityMove, emp));
        moveTimeout = MOVE_TIMEOUT;
    }
    
}
