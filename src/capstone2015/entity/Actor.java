package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.Map;
import capstone2015.game.MaskedMapView;
import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnDroppedItemBehavior;
import capstone2015.game.behavior.OnHealBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnPickedUpItemBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Array2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Actor extends MapEntity {
    protected OnWalkedOverBehavior onWalkedOverBehavior;
    protected OnMovedBehavior onMovedBehavior;
    protected OnTickBehavior onTickBehavior;
    protected OnDamageBehavior onDamageBehavior;
    protected OnPickedUpItemBehavior onPickedUpItemBehavior;
    protected OnDroppedItemBehavior onDroppedItemBehavior;
    protected OnHealBehavior onHealBehavior;
    protected MaskedMapView view;
    protected int health;
    protected Vec2i pos;
    protected boolean terminated;
    protected int visionRadius;
    protected HashMap<EntityBase, Double> damageIgnoreTimers = new HashMap<>();
    protected Inventory inventory;
    protected TerminalChar representOverride = null;
    protected double duration = -1.f;
    protected double moveTimeout = 0.f;
    protected double useTimeout = 0.f;

    public void setMap(Map map){
        if(hasVision()){
            view = new MaskedMapView(map);
            if(hasVisionRevealedByDefault())
                view.revealAll();
        }
    }
    
    public boolean hasVisionRevealedByDefault(){
        return proto.actorProto.visionRevealedByDefault;
    }
    
    public boolean hasVision(){
        return (visionRadius > 0);
    }
    
    public void unsetMap(){
        view = null;
    }
    
    public void onVisionUpdate(int x, int y, Array2D<Boolean> visibilityMap){
        if(view != null){
            view.updateVisibilityMask(x, y, visibilityMap);
        }
    }

    public void setUseTimeout(double time){
        useTimeout = time;
    }

    public void setMoveTimeout(double time){
        moveTimeout = time;
    }

    public void decreaseUseTimeout(double time){
        useTimeout = Math.max(0.d, useTimeout - time);
    }

    public void decreaseMoveTimeout(double time){
        moveTimeout = Math.max(0.d, moveTimeout - time);
    }

    public boolean canMove(){
        return moveTimeout == 0.f;
    }

    public boolean canUse(){
        return useTimeout == 0.f;
    }

    public double getDuration(){return duration;}

    public void setDuration(double duration){this.duration = duration;}

    public MaskedMapView getView(){
        return view;
    }
    
    public Vec2i getPos(){
        return pos;
    }

    public int getXPos(){
        return pos.getX();
    }

    public int getYPos(){
        return pos.getY();
    }

    public void terminate() {
        terminated = true;
    }

    public boolean isPickupable() {
        return proto.actorProto.pickupable;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public boolean hasInventory() {
        return (inventory != null);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    public HashMap<EntityBase, Double> getDamageIgnoreTimers() {
        return damageIgnoreTimers;
    }

    public int getTeamId(){return proto.actorProto.teamId;}

    public int freeInventorySlotCount() {
        if (inventory != null) {
            return inventory.freeSlotsCount();
        } else {
            return 0;
        }
    }
    
    public Item getSelectedItem(){
        if(inventory != null){
            return inventory.getSelectedItem();
        } else {
            return null;
        }
    }

    public boolean hasFreeInventorySlot() {
        return freeInventorySlotCount() > 0;
    }

    public boolean addItem(Item item) {
        if (inventory != null) {
            inventory.add(item);
            return true;
        } else {
            return false;
        }
    }

    public Item getInventorySlot(int slotIdx) {
        if (inventory != null) {
            return inventory.get(slotIdx);
        } else {
            return null;
        }
    }

    @Override
    public boolean isEncounterNotified() {
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
    public SolidType getSolidType() {
        return proto.mapEntityProto.solidType;
    }

    @Override
    public boolean isOpaque() {
        return proto.mapEntityProto.isOpaque;
    }

    @Override
    public TerminalChar getRepresent() {
        if(representOverride == null)
            return proto.entityBaseProto.represent;
        else
            return representOverride;
    }

    public void setRepresentOverride(TerminalChar representOverride){
        this.representOverride = representOverride;
    }

    public void disableRepresentOverride(){
        this.representOverride = null;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return proto.actorProto.maxHealth;
    }

    public boolean isInvulnerable(){return proto.actorProto.maxHealth < 0;}
    
    public void heal(Item source, int heal) {
        if(onHealBehavior != null){
            onHealBehavior.invoke(this, source, heal);
        }
        health = Math.min(health + heal, getMaxHealth());
    }

    @Override
    public void onWalkedOver() {
        if (onWalkedOverBehavior != null) {
            //onWalkedOverBehavior.invoke(this);
        }
    }

    public void onDroppedItem(Item item) {
        if (onDroppedItemBehavior != null) {
            onDroppedItemBehavior.invoke(this, item);
        }
    }

    public void onPickedUpItem(Item item) {
        if (onPickedUpItemBehavior != null) {
            onPickedUpItemBehavior.invoke(this, item);
        }
    }

    public void onPickedUpItemFailedNoSpace(Item item) {
        if (onPickedUpItemBehavior != null) {
            onPickedUpItemBehavior.invokeFailedNoSpace(this, item);
        }
    }

    public void onMoved(ArrayList<MapEntity> entitiesOnPosition) {
        if (onMovedBehavior != null) {
            onMovedBehavior.invoke(this, entitiesOnPosition);
        }
    }

    public void onTick(double timeDelta) {
        decreaseMoveTimeout(timeDelta);
        decreaseUseTimeout(timeDelta);
        if(inventory != null){
            inventory.tick();
        }
        if (onTickBehavior != null) {
            onTickBehavior.invoke(this, timeDelta);
        }
    }

    public boolean onDamage(Actor damagingEntity, int damage) {
        if (onDamageBehavior != null) {
            return onDamageBehavior.invoke(this, damagingEntity, damage);
        }
        return false;
    }

    public void setHealthPoints(int healthPoints) {
        this.health = healthPoints;
    }

    public void setXPos(int xPos) {
        pos.setX(xPos);
    }

    public void setYPos(int yPos) {
        pos.setY(yPos);
    }

    public void setPos(Vec2i pos) {
        this.pos = pos;
    }

    public void setVisionRadius(int visionRadius) {
        this.visionRadius = visionRadius;
    }
}
