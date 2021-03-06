package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.Item;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnEffectParams;

public class SwordOnUseBehavior implements OnUseBehavior {
    public static final int     SWORD_DAMAGE = 5;
    public static final double  SWORD_EFFECT_DURATION = 0.2d;
    public static final double  SWORD_USE_TIMEOUT = 0.75d;

    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        if(useDir.equals(new Vec2i(0, 0))){
            return;
        }
        if(useDir.orthoMagnitude() > 1){
            return;
        }

        if(!user.canUse())
            return;

        /**********************
         * Deal damage
         */
        InflictDamageParams idp = new InflictDamageParams();
        idp.damage = SWORD_DAMAGE;
        idp.damagingEntity = user;
        idp.position = user.getPos().add(useDir);
        idp.teamId = user.getTeamId();

        user.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp));

        /**********************
         * Spawn visible effects
         */
        SpawnEffectParams sep = new SpawnEffectParams();
        sep.duration = SWORD_EFFECT_DURATION;
        sep.pos = user.getPos().add(useDir);
        sep.represent = EntityFactory.getProto(EntityFactory.ID_SWORD).entityBaseProto.represent;
        
        user.sendBusMessage(new Message(GameMessage.SPAWN_EFFECT, sep));

        user.setUseTimeout(SWORD_USE_TIMEOUT);
    }
}
