package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.PushNotificationParams;

public class PlayerOnDroppedItemBehavior implements OnDroppedItemBehavior{

    @Override
    public void invoke(Actor entity, Item item) {
        PushNotificationParams pnp = new PushNotificationParams();
        pnp.notification = "You drop your " + item.getName() + ".";
        pnp.color = item.getRepresent().getFGColor();
        entity.sendBusMessage(new Message(PushNotification, pnp));
    }
    
}
