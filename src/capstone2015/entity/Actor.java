package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnDroppedItemBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnPickedUpItemBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import java.util.ArrayList;
import java.util.HashMap;

public class Actor extends MapEntity{

    protected EntityProto proto;
    protected OnWalkedOverBehavior onWalkedOverBehavior;
    protected OnMovedBehavior onMovedBehavior;
    protected OnTickBehavior onTickBehavior;
    protected OnDamageBehavior onDamageBehavior;
    protected OnPickedUpItemBehavior onPickedUpItemBehavior;
    protected OnDroppedItemBehavior onDroppedItemBehavior;
    protected int health;
    protected Vec2i pos;
    protected boolean terminate;
    protected int visionRadius;
    protected HashMap<EntityBase, Double> damageIgnoreTimers = new HashMap<>();
    protected Inventory inventory;
    
    public Vec2i getPos(){
        return pos;
    }
    
    public int getXPos(){
        return pos.getX();
    }
    
    public int getYPos(){
        return pos.getY();
    }
    
    public void terminate(){
        terminate = true;
    }
    
    public boolean isPickupable(){
        return proto.actorProto.pickupable;
    }
    
    public boolean isTerminate(){
        return terminate;
    }
    
    public boolean hasInventory(){
        return (inventory != null);
    }
    
    public Inventory getInventory(){
        return inventory;
    }
    
    public int getVisionRadius(){
        return visionRadius;
    }
    
    public HashMap<EntityBase, Double> getDamageIgnoreTimers(){
        return damageIgnoreTimers;
    }
    
    public int freeInventorySlotCount(){
        if(inventory != null){
            return inventory.freeSlotsCount();
        } else {
            return 0;
        }
    }
    
    public boolean hasFreeInventorySlot(){
        return freeInventorySlotCount() > 0;
    }
    
    public boolean addItem(Item item){
        if(inventory != null){
            inventory.add(item);
            return true;
        } else {
            return false;
        }
    }
    
    public Item getInventorySlot(int slotIdx){
        if(inventory != null){
            return inventory.get(slotIdx);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean isEncounterNotified(){
        return proto.mapEntityProto.isEncounterNotified;
    }
    
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
    public TerminalChar getRepresent() {
        return proto.entityBaseProto.represent;
    }
    
    public int getHealth(){
        return health;
    }
    
    public int getMaxHealth() {
        return proto.actorProto.maxHealth;
    }
    
    
    @Override
    public void onWalkedOver() {
        if(onWalkedOverBehavior != null){
            //onWalkedOverBehavior.invoke(this);
        }
    }
    
    public void onDroppedItem(Item item){
        if(onDroppedItemBehavior != null){
            onDroppedItemBehavior.invoke(this, item);
        }
    }
    
    public void onPickedUpItem(Item item){
        if(onPickedUpItemBehavior != null){
            onPickedUpItemBehavior.invoke(this, item);
        }
    }
    
    public void onPickedUpItemFailedNoSpace(Item item){
        if(onPickedUpItemBehavior != null){
            onPickedUpItemBehavior.invokeFailedNoSpace(this, item);
        }
    }
    
    public void onMoved(ArrayList<MapEntity> entitiesOnPosition){
        if(onMovedBehavior != null){
            onMovedBehavior.invoke(this, entitiesOnPosition);
        }
    }
    
    public void onTick(double timeDelta){
        if(onTickBehavior != null){
            onTickBehavior.invoke(this, timeDelta);
        }
    }
    
    public void onDamage(Actor damagingEntity, int damage){
        if(onDamageBehavior != null){
            onDamageBehavior.invoke(this, damagingEntity, damage);
        }
    }
    
    public void setHealthPoints(int healthPoints){
        this.health = healthPoints;
    }
    
    public void setXPos(int xPos){
        pos.setX(xPos);
    }
    
    public void setYPos(int yPos){
        pos.setY(yPos);
    }
    
    public void setPos(Vec2i pos){
        this.pos = pos;
    }
    
    public void setVisionRadius(int visionRadius){
        this.visionRadius = visionRadius;
    }
}
