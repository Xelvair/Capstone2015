package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.InflictDamage;

public class DamageOnCollisionOnTickBehavior implements OnTickBehavior{
    public static final int DAMAGE = 1;
    
    @Override
    public void invoke(Actor entity, double timeDelta) {
        InflictDamageParams msg_obj = new InflictDamageParams();
        msg_obj.damagingEntity = entity;
        msg_obj.position = entity.getPos();
        msg_obj.damage = DAMAGE;
        msg_obj.teamId = entity.getTeamId();

        entity.sendBusMessage(new Message(InflictDamage, msg_obj));
    }
    
}
