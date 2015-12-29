package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.geom.Vec2i;

public interface OnUseBehavior {
    public void invoke(Item item, Actor user, Vec2i useDir);
}
