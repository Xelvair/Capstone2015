package capstone2015.entity;

import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnDroppedItemBehavior;
import capstone2015.game.behavior.OnHealBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnPickedUpItemBehavior;
import capstone2015.game.behavior.OnTickBehavior;

public class ActorProto {
    public Class<? extends OnTickBehavior> onTickBehaviorClass;
    public Class<? extends OnMovedBehavior> onMovedBehaviorClass;
    public Class<? extends OnDamageBehavior> onDamageBehaviorClass;
    public Class<? extends OnPickedUpItemBehavior> onPickedUpItemBehaviorClass;
    public Class<? extends OnDroppedItemBehavior> onDroppedItemBehaviorClass;
    public Class<? extends OnHealBehavior> onHealBehaviorClass;
    public int visionRadius;
    public int maxHealth;
    public boolean pickupable;
    public int inventorySize;
}
