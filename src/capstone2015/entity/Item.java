package capstone2015.entity;

import capstone2015.game.behavior.OnItemDroppedBehavior;
import capstone2015.game.behavior.OnItemPickedUpBehavior;
import capstone2015.game.behavior.OnUseBehavior;
import capstone2015.graphics.TerminalChar;

public class Item extends EntityBase{
    protected EntityProto proto;
    
    protected OnUseBehavior onUseBehavior;
    protected OnItemPickedUpBehavior onItemPickedUpBehavior;
    protected OnItemDroppedBehavior onItemDroppedBehavior;

    @Override
    public TerminalChar getRepresent() {
        return proto.entityBaseProto.represent;
    }

    @Override
    public String getName() {
        return proto.entityBaseProto.name;
    }

    @Override
    public String getDescription() {
        return proto.entityBaseProto.description;
    }
    
    public void onUse(){
        if(onUseBehavior != null){
            //onUseBehavior.invoke();
        }
    }
    
    public void onItemPickedUp(Actor pickupper){
        if(onItemPickedUpBehavior != null){
            onItemPickedUpBehavior.invoke(pickupper);
        }
    }
    
    public void onItemDropped(){
        if(onItemDroppedBehavior != null){
            onItemDroppedBehavior.invoke(this);
        }
    }
}
