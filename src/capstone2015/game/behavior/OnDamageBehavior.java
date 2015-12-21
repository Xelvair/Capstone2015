package capstone2015.game.behavior;

import capstone2015.game.ActiveEntity;
import capstone2015.messaging.MessageBus;

public interface OnDamageBehavior {
     public void invoke(ActiveEntity entity, ActiveEntity damagingEntity, int damage, MessageBus messageBus);
}
