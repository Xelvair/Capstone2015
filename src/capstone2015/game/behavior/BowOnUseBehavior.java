package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.Item;
import capstone2015.game.GameMessage;
import capstone2015.game.Inventory;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnActorParams;

import java.util.TreeMap;
import java.util.Map;

public class BowOnUseBehavior implements OnUseBehavior{
    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        /*******************
         * Only allow directional use
         */
        if(useDir.orthoMagnitude() <= 0)
            return;

        Inventory user_inv = user.getInventory();

        int arrow_idx = user_inv.findById(EntityFactory.ID_ARROW);

        if(arrow_idx < 0)
            return;

        Item arrow = user_inv.get(arrow_idx);

        Map<String, Object> instantiationParams = new TreeMap<>();
        instantiationParams.put("ShootDirection", useDir);
        instantiationParams.put("TeamId", user.getTeamId());

        SpawnActorParams sep = new SpawnActorParams();
        sep.entityId = EntityFactory.ID_ARROW;
        sep.position = user.getPos();
        sep.instantiationParams = instantiationParams;
        sep.parent = user;

        user.sendBusMessage(new Message(GameMessage.SPAWN_ACTOR, sep));

        arrow.terminate();
    }
}
