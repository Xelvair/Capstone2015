package capstone2015.entity;

import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.graphics.TerminalChar;

public class Tile extends MapEntity{
    
    protected EntityProto proto;
    
    protected OnWalkedOverBehavior onWalkedOverBehavior;
    
    @Override
    public String getName() {
        return proto.entityBaseProto.name;
    }

    @Override
    public String getDescription() {
        return proto.entityBaseProto.description;
    }
    
    @Override
    public TerminalChar getRepresentInvisible() {
        return proto.mapEntityProto.representInvisible;
    }

    @Override
    public boolean isSolid() {
        return proto.mapEntityProto.isSolid;
    }

    @Override
    public boolean isOpaque() {
        return proto.mapEntityProto.isOpaque;
    }

    @Override
    public void onWalkedOver() {
        if(onWalkedOverBehavior != null){
            //onWalkedOverBehavior.invoke();
        }
    }

    @Override
    public TerminalChar getRepresent() {
        return proto.entityBaseProto.represent;
    }

    @Override
    public boolean isEncounterNotified() {
        return proto.mapEntityProto.isEncounterNotified;
    }
    
}
