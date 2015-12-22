package capstone2015.entity;

import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.graphics.TerminalChar;

public class MapEntityProto {
    public TerminalChar representInvisible;
    public boolean isSolid;
    public boolean isOpaque;
    public boolean isEncounterNotified;
    public Class<? extends OnWalkedOverBehavior> onWalkedOverBehaviorClass;
}
