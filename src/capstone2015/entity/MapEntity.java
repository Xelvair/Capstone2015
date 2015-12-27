package capstone2015.entity;

import capstone2015.graphics.TerminalChar;

public abstract class MapEntity extends EntityBase{   
    public abstract boolean isEncounterNotified();
    public abstract TerminalChar getRepresentInvisible();
    public abstract boolean isSolid();
    public abstract boolean isOpaque();
    public abstract void onWalkedOver();
}
