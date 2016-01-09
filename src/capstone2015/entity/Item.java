package capstone2015.entity;

import capstone2015.game.behavior.OnItemDroppedBehavior;
import capstone2015.game.behavior.OnItemPickedUpBehavior;
import capstone2015.game.behavior.OnUseBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;

public class Item extends EntityBase{   
    protected OnUseBehavior onUseBehavior;
    protected OnItemPickedUpBehavior onItemPickedUpBehavior;
    protected OnItemDroppedBehavior onItemDroppedBehavior;
    private boolean terminated = false;
    private double useTimeout = 0.f;

    public void terminate(){
        terminated = true;
    }
    
    public boolean isTerminated(){
        return terminated;
    }
    
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
    
    public void setUseTimeout(double useTimeout){
        this.useTimeout = useTimeout;
    }
    
    public boolean canUse(){
        return this.useTimeout == 0.f;
    }
    
    public void onTick(double timeDelta){
        useTimeout = Math.max(0.f, useTimeout - timeDelta);
    }
    
    public boolean isUsable(){
        return onUseBehavior != null;
    }
    
    public void onUse(Actor user, Vec2i target){
        if(onUseBehavior != null){
            onUseBehavior.invoke(this, user, target);
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
