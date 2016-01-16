package capstone2015.entity.states;

import capstone2015.entity.Actor;

public interface ActorStateConfig {
    public double getOuterStray();
    public double getInnerStray();
    public int getAttackDamage();
    public double getAttackTimeout();
    public double getInRangeMoveTimeout();
    public double getAttackMoveTimeout();
    public double getWanderingMoveTimeout();
    public Actor getTarget();
    public void setTarget(Actor target);
    public int getAttackRange();
}
