package capstone2015.entity;

import capstone2015.game.behavior.OnItemDroppedBehavior;
import capstone2015.game.behavior.OnItemPickedUpBehavior;
import capstone2015.game.behavior.OnUseBehavior;

public class ItemProto {
    Class<? extends OnUseBehavior> onUseBehaviorClass;
    Class<? extends OnItemPickedUpBehavior> onItemPickedUpBehaviorClass;
    Class<? extends OnItemDroppedBehavior> onItemDroppedBehaviorClass;
}
