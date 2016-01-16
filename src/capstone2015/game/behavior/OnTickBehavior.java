package capstone2015.game.behavior;

import capstone2015.entity.Actor;

public interface OnTickBehavior {
    public void invoke(Actor actor, double timeDelta);
}
