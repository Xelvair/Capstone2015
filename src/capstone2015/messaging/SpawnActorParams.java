package capstone2015.messaging;

import capstone2015.entity.EntityBase;
import capstone2015.geom.Vec2i;

import java.util.Map;

public class SpawnActorParams {
    public int entityId;
    public Vec2i position;
    public Map<String, Object> instantiationParams;
    public EntityBase parent;
}
