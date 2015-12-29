package capstone2015.messaging;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;

public class ReceivedDamageParams {
    public Actor damagedEntity;
    public EntityBase damagingEntity;
    public int damage;
}
