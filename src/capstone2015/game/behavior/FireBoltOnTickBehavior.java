package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import static capstone2015.game.behavior.ArrowOnTickBehavior.ARROW_DAMAGE;
import static capstone2015.game.behavior.ArrowOnTickBehavior.ARROW_MOVE_TIMEOUT;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.ReceivedDamageParams;
import capstone2015.util.Util;
import java.util.Map;

public class FireBoltOnTickBehavior implements OnTickBehavior{
    private Direction shootDirection;
    private int moveSequence = 0;
    
    public static final int DAMAGE = 1;
    public static final double MOVE_TIMEOUT = 0.1f;
    
    public FireBoltOnTickBehavior(Map<String, Object> instantiationParams){
        shootDirection = (Direction)instantiationParams.get("ShootDirection");
    }
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        for(Message m : entity.getMessageBus()){
            switch(m.getType()){
                case EntityMoveFailed:
                {
                    Actor a = (Actor)m.getMsgObject();
                    if(a == entity){
                        entity.terminate();
                        return;
                    }
                    break;
                }
                case ReceivedDamage:
                {
                    ReceivedDamageParams rdp = (ReceivedDamageParams)m.getMsgObject();
                    if(rdp.damagingEntity == entity){
                        entity.terminate();
                        return;
                    }
                    break;
                }
            }
        }
        
        InflictDamageParams idp = new InflictDamageParams();
        idp.damage = DAMAGE;
        idp.position = entity.getPos();
        idp.damagingEntity = entity;
        idp.teamId = entity.getTeamId();

        entity.sendBusMessage(new Message(Message.Type.InflictDamage, idp));

        if(!entity.canMove())
            return;

        EntityMoveParams emp = new EntityMoveParams();
        emp.direction = shootDirection;
        emp.entity = entity;

        entity.sendBusMessage(new Message(Message.Type.EntityMove, emp));

        entity.setMoveTimeout(MOVE_TIMEOUT);
    }
}
