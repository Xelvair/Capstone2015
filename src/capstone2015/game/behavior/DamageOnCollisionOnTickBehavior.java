package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.InflictDamage;
import capstone2015.messaging.MessageBus;

public class DamageOnCollisionOnTickBehavior implements OnTickBehavior{
    public static final int DAMAGE = 1;
    
    @Override
    public void invoke(ActiveEntity entity, double timeDelta, MessageBus messageBus) {
        InflictDamageParams msg_obj = new InflictDamageParams(
                entity, 
                new Vec2i(entity.getXPos(), entity.getYPos()), 
                DAMAGE
        );
        messageBus.enqueue(new Message(InflictDamage, msg_obj));
    }
    
}
