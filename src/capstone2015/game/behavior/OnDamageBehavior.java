package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.messaging.MessageBus;

public interface OnDamageBehavior {
     public boolean invoke(Actor entity, EntityBase damagingEntity, int damage);
}
