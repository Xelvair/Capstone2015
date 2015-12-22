package capstone2015.entity;

import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnTickBehavior;

public class ActorProto {
    public Class<? extends OnTickBehavior> onTickBehaviorClass;
    public Class<? extends OnMovedBehavior> onMovedBehaviorClass;
    public Class<? extends OnDamageBehavior> onDamageBehaviorClass;
    public int visionRadius;
    public int maxHealth;
}
