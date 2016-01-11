package capstone2015.entity;

import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.graphics.TerminalChar;
import java.util.Map;
import java.util.function.BiFunction;

public class MapEntityProto {
    public TerminalChar representInvisible;
    public SolidType solidType;
    public boolean isOpaque;
    public boolean isEncounterNotified;
    public Class<? extends OnWalkedOverBehavior> onWalkedOverBehaviorClass;
    public int shaderType;
}
