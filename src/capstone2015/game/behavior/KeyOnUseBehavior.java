package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.AttemptKeyUsageParams;
import capstone2015.messaging.Message;

public class KeyOnUseBehavior implements OnUseBehavior{

    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        AttemptKeyUsageParams msg_obj = new AttemptKeyUsageParams();
        msg_obj.user = user;
        msg_obj.key = item;
        msg_obj.useDir = useDir;
        user.sendBusMessage(new Message(GameMessage.ATTEMPT_KEY_USAGE, msg_obj));
    }
}
