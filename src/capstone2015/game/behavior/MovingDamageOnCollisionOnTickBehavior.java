package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.game.MapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.EntityMove;
import static capstone2015.messaging.Message.Type.InflictDamage;
import capstone2015.pathfinding.Dijkstra;
import java.util.LinkedList;
import java.util.Random;

public class MovingDamageOnCollisionOnTickBehavior implements OnTickBehavior{

    public static final int DAMAGE = 1;
    public static final float MOVE_TIMEOUT = 1.f;
    public static final int RANDOM_MOVE_RADIUS = 7;
            
    private LinkedList<Vec2i> path = new LinkedList<>();
    private float moveTimeout = 0.f;
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        InflictDamageParams msg_obj = new InflictDamageParams(
                entity, 
                new Vec2i(entity.getXPos(), entity.getYPos()), 
                DAMAGE
        );
        entity.sendBusMessage(new Message(InflictDamage, msg_obj));
        
        if(moveTimeout > 0.f){
            moveTimeout = (float) Math.max(0.f, moveTimeout - timeDelta);
        } else if(!path.isEmpty()) {
            Vec2i pos = entity.getPos();
            Vec2i target_pos = path.pollFirst();
            
            Vec2i dirvec = target_pos.translate(pos.invert());
            
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
        } else {
            Random rand = new Random();
            Vec2i random_point = entity.getPos().translate(new Vec2i(rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS, 
                                                                     rand.nextInt(RANDOM_MOVE_RADIUS * 2) - RANDOM_MOVE_RADIUS));
            
            if(entity.getView().inBounds(random_point) && !entity.getView().isSolidAt(random_point)){
                MapTraversableAdapter mta = new MapTraversableAdapter(entity.getView());
                path = Dijkstra.find(mta, entity.getPos(), random_point);
            }
        }
    }
    
}
