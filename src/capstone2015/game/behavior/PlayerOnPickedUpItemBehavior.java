package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.PushNotificationParams;
import java.awt.Color;

public class PlayerOnPickedUpItemBehavior implements OnPickedUpItemBehavior{

    @Override
    public void invoke(Actor entity, Item item) {
        PushNotificationParams pnp = new PushNotificationParams();
        pnp.notification = "You pick up a " + item.getName();
        pnp.color = item.getRepresent().getFGColor();
        entity.sendBusMessage(new Message(PushNotification, pnp));
    }

    @Override
    public void invokeFailedNoSpace(Actor entity, Item item) {
        PushNotificationParams pnp = new PushNotificationParams();
        pnp.notification = "Cannot pick up " + item.getName() + ", no inventory space.";
        pnp.color = Color.WHITE;
        entity.sendBusMessage(new Message(PushNotification, pnp));
    }
    
}
