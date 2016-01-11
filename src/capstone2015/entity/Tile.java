package capstone2015.entity;

import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.graphics.TerminalChar;
import capstone2015.shader.ShaderProgram;
import java.util.Map;
import java.util.function.BiFunction;

public class Tile extends MapEntity{
    
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
    public SolidType getSolidType() {
        return proto.mapEntityProto.solidType;
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

    @Override
    public int getShaderType(){
        return proto.mapEntityProto.shaderType;
    }
    
}
