package capstone2015.game.behavior;

import capstone2015.entity.Actor;

public interface OnTamedBehavior {
    public void invoke(Actor entity, Actor tamer, boolean wasSuccess);
}
