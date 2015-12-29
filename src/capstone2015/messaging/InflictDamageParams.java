package capstone2015.messaging;

import capstone2015.entity.Actor;
import capstone2015.geom.Vec2i;

public class InflictDamageParams {
    public Actor damagingEntity;
    public Vec2i position;
    public int damage;
    public int teamId;
}
