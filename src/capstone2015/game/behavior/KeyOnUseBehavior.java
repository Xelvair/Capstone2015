package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.messaging.AttemptKeyUsageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;

public class KeyOnUseBehavior implements OnUseBehavior{

    @Override
    public void invoke(Item item, Actor user) {
        AttemptKeyUsageParams msg_obj = new AttemptKeyUsageParams();
        msg_obj.user = user;
        msg_obj.key = item;
        user.sendBusMessage(new Message(AttemptKeyUsage, msg_obj));
    }
}
