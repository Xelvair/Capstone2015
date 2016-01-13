package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.Item;
import static capstone2015.game.behavior.MagicWandOnUseBehavior.MAGIC_WAND_USE_TIMEOUT;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnActorParams;
import java.util.Map;
import java.util.TreeMap;

public class TamingScrollOnUseBehavior implements OnUseBehavior{
    public static final double TAMING_SCROLL_USE_TIMEOUT = 1.d;
    
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
        instantiationParams.put("Tamer", user);
        instantiationParams.put("TameCallback", new Runnable(){
            @Override
            public void run() {
                item.terminate();
            }
        });

        SpawnActorParams sep = new SpawnActorParams();
        sep.entityId = EntityFactory.ID_TAMING_SPELL;
        sep.position = user.getPos();
        sep.instantiationParams = instantiationParams;
        sep.parent = user;

        user.sendBusMessage(new Message(Message.Type.SpawnActor, sep));
        
        item.setUseTimeout(TAMING_SCROLL_USE_TIMEOUT);
    }
}
