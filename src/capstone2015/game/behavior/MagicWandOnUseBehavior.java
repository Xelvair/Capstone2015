package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.Item;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnActorParams;
import java.util.Map;
import java.util.TreeMap;

public class MagicWandOnUseBehavior implements OnUseBehavior{

    public static final double MAGIC_WAND_USE_TIMEOUT = 1.f;
    
    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        /*******************
         * Respect use timeout
         */
        if(!item.canUse())
            return;
        
        /*******************
        * Only allow directional use
        */
        if(useDir.orthoMagnitude() <= 0)
            return;

        Map<String, Object> instantiationParams = new TreeMap<>();
        instantiationParams.put("ShootDirection", useDir);
        instantiationParams.put("TeamId", user.getTeamId());

        SpawnActorParams sep = new SpawnActorParams();
        sep.entityId = EntityFactory.ID_MAGIC_BOLT;
        sep.position = user.getPos();
        sep.instantiationParams = instantiationParams;
        sep.parent = user;

        user.sendBusMessage(new Message(GameMessage.SPAWN_ACTOR, sep));
        
        item.setUseTimeout(MAGIC_WAND_USE_TIMEOUT);
    }
}