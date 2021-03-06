package capstone2015.entity;

import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnDroppedItemBehavior;
import capstone2015.game.behavior.OnHealBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnPickedUpItemBehavior;
import capstone2015.game.behavior.OnTamedBehavior;
import capstone2015.game.behavior.OnTickBehavior;

import java.util.Map;
import java.util.function.BiConsumer;

public class ActorProto {
    public static final int TEAM_NONE = 0;
    public static final int TEAM_PLAYER = 1;
    public static final int TEAM_DUNGEON = 2;

    public Class<? extends OnTickBehavior> onTickBehaviorClass;
    public Class<? extends OnMovedBehavior> onMovedBehaviorClass;
    public Class<? extends OnDamageBehavior> onDamageBehaviorClass;
    public Class<? extends OnPickedUpItemBehavior> onPickedUpItemBehaviorClass;
    public Class<? extends OnDroppedItemBehavior> onDroppedItemBehaviorClass;
    public Class<? extends OnHealBehavior> onHealBehaviorClass;
    public Class<? extends OnTamedBehavior> onTamedBehaviorClass;
    public BiConsumer<Actor, Map<String, Object>> onInstantiationFunction;
    public int visionRadius;
    public boolean visionRevealedByDefault;
    public Object maxHealth; //int or int[] (for leveled creatures)
    public boolean pickupable;
    public int inventorySize;
    public int teamId;
    public double tameMinChance;
    public double tameMaxChance;
    public double outerStray;
    public double innerStray;
    public Object attackDamage; //int or int[] (for leveled creatures)
    public Object attackTimeout; //double or double[] (for leveled creatures);
    public double getInRangeMoveTimeout;
    public Object attackMoveTimeout; //double or double[] (for leveled creatures)
    public double wanderingMoveTimeout;
    public int attackRange; 
}
