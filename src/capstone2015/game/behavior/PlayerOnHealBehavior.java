package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.game.GameMessage;
import capstone2015.messaging.Message;
import capstone2015.messaging.PushNotificationParams;

public class PlayerOnHealBehavior implements OnHealBehavior{

    @Override
    public void invoke(Actor entity, EntityBase source, int heal) {
        PushNotificationParams pnp = new PushNotificationParams();
        pnp.notification = source.getName() + " heals you for " + heal + " HP";
        pnp.color = source.getRepresent().getFGColor();
        entity.sendBusMessage(new Message(GameMessage.PUSH_NOTIFICATION, pnp));
    }
    
}
