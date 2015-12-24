package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;

public interface OnUseBehavior {
    public void invoke(Item item, Actor user);
}
