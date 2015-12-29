package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;

public class SwordOnUseBehavior implements OnUseBehavior {
    public static final int SWORD_DAMAGE = 3;

    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        if(useDir.equals(new Vec2i(0, 0))){
            return;
        }
        if(useDir.orthoMagnitude() > 1){
            return;
        }

        InflictDamageParams idp = new InflictDamageParams();
        idp.damage = SWORD_DAMAGE;
        idp.damagingEntity = user;
        idp.position = user.getPos().add(useDir);
        idp.teamId = user.getTeamId();

        user.sendBusMessage(new Message(Message.Type.InflictDamage, idp));
    }
}
