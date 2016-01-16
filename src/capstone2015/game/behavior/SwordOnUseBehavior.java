package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.Item;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnEffectParams;

import java.awt.*;

public class SwordOnUseBehavior implements OnUseBehavior {
    public static final int SWORD_DAMAGE = 5;
    public static final double SWORD_EFFECT_DURATION = 0.2d;
    public static final double SWORD_USE_TIMEOUT = 0.75d;

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
        InflictDamageParams idp1 = new InflictDamageParams();
        idp1.damage = SWORD_DAMAGE;
        idp1.damagingEntity = user;
        idp1.position = user.getPos().add(useDir);
        idp1.teamId = user.getTeamId();

        user.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp1));
        
        InflictDamageParams idp2 = new InflictDamageParams(idp1);
        idp2.position = user.getPos().add(useDir).add(useDir);
        user.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp2));

        /**********************
         * Spawn visible effects
         */
        SpawnEffectParams sep1 = new SpawnEffectParams();
        sep1.duration = SWORD_EFFECT_DURATION;
        sep1.pos = user.getPos().add(useDir);
        sep1.represent = EntityFactory.getProto(EntityFactory.ID_SWORD).entityBaseProto.represent;
        
        user.sendBusMessage(new Message(GameMessage.SPAWN_EFFECT, sep1));
        
        SpawnEffectParams sep2 = new SpawnEffectParams(sep1);
        sep2.pos = user.getPos().add(useDir).add(useDir);
        user.sendBusMessage(new Message(GameMessage.SPAWN_EFFECT, sep2));
        
        user.setUseTimeout(SWORD_USE_TIMEOUT);
    }
}
