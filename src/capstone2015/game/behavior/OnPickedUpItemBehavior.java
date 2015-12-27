package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;

public interface OnPickedUpItemBehavior {
    public void invoke(Actor entity, Item item);
    public void invokeFailedNoSpace(Actor entity, Item item);
}
