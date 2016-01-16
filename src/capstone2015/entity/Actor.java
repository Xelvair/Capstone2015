package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.Map;
import capstone2015.game.EntityMapView;
import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnDroppedItemBehavior;
import capstone2015.game.behavior.OnHealBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnPickedUpItemBehavior;
import capstone2015.game.behavior.OnTamedBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.game.behavior.OnWalkedOverBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Actor extends MapEntity {
    protected OnWalkedOverBehavior onWalkedOverBehavior;
    protected OnMovedBehavior onMovedBehavior;
    protected OnTickBehavior onTickBehavior;
    protected OnDamageBehavior onDamageBehavior;
    protected OnPickedUpItemBehavior onPickedUpItemBehavior;
    protected OnDroppedItemBehavior onDroppedItemBehavior;
    protected OnHealBehavior onHealBehavior;
    protected OnTamedBehavior onTamedBehavior;
    protected EntityMapView view;
    protected int health;
    protected Actor leader;
    protected Vec2i pos;
    protected boolean terminated;
    protected int visionRadius;
    protected HashMap<EntityBase, Double> damageIgnoreTimers = new HashMap<>();
    protected Inventory inventory;
    protected TerminalChar representOverride = null;
    protected int teamIdOverride = -1;
    protected double duration = -1.f;
    protected double moveTimeout = 0.f;
    protected double useTimeout = 0.f;
    protected List<Actor> followers = new LinkedList();
    
    public void setMap(Map map){
        if(hasVision()){
            view = new EntityMapView(map, proto.actorProto.visionRadius);
            view.setViewerPos(pos);
            if(hasVisionRevealedByDefault())
                view.revealAll();
        }
    }
    
    public List<Actor> getFollowers(){
        return followers;
    }
    
    public void addFollower(Actor follower){
        followers.add(follower);
    }
    
    public void removeFollower(Actor follower){
        followers.remove(follower);
    }
    
    public void refreshFollowers(){
        Iterator<Actor> it = followers.iterator();
        while(it.hasNext()){
            Actor follower = it.next();
            if(follower.isTerminated())
                it.remove();
        }
    }
    
    public void setLeader(Actor leader){
        this.leader = leader;
    }
    
    public Actor getLeader(){
        return leader;
    }
    
    public boolean hasLeader(){
        return leader != null;
    }
    
    public double getTameMinChance(){
        return proto.actorProto.tameMinChance;
    }
    
    public double getTameMaxChance(){
        return proto.actorProto.tameMaxChance;
    }
    
    public boolean isTameable(){
        return getTameMaxChance() > 0.f;
    }
    
    public void setTeamIdOverride(int teamId){
        teamIdOverride = teamId;
    }

    public void resetTeamIdOverride(){
        teamIdOverride = -1;
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

    public void setUseTimeout(double time){
        useTimeout = time;
    }
    
    public void resetUseTimeout(){
        useTimeout = 0.d;
    }

    public void setMoveTimeout(double time){
        moveTimeout = time;
    }
    
    public void resetMoveTimeout(){
        moveTimeout = 0.d;
    }
    
    public void capMoveTimeout(double cap){
        moveTimeout = Math.min(moveTimeout, cap);
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

    public EntityMapView getView(){
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

    public int getTeamId(){
        if(teamIdOverride >= 0)
            return teamIdOverride;
        else
            return proto.actorProto.teamId;
    }

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

    public void onTamed(boolean success, Actor tamer){
        if(onTamedBehavior != null){
            onTamedBehavior.invoke(this, tamer, success);
        }
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
        if(view != null){
            view.setViewerPos(pos);
        }
        if (onMovedBehavior != null) {
            onMovedBehavior.invoke(this, entitiesOnPosition);
        }
    }

    public void onTick(double timeDelta) {
        /***************************
         * Decrease damage ignore timers
         */
        HashMap<EntityBase, Double> damage_ignore_timers = getDamageIgnoreTimers();

        for(Iterator<java.util.Map.Entry<EntityBase, Double>> it = damage_ignore_timers.entrySet().iterator(); it.hasNext(); ) {
            java.util.Map.Entry<EntityBase, Double> entry = it.next();
            double damage_timer = entry.getValue();
            double new_damage_timer = Math.max(0, damage_timer - timeDelta);

            if(new_damage_timer == 0.f){
                it.remove();
            } else {
                entry.setValue(new_damage_timer);
            }
        }
        
        /***************************
         * Decrease other timers
         */
        decreaseMoveTimeout(timeDelta);
        decreaseUseTimeout(timeDelta);
        
        /***************************
         * Check for terminated followers
         */
        refreshFollowers();
        
        /***************************
         * Tick inventory
         */
        if(inventory != null){
            inventory.tick(timeDelta);
        }
        
        /***************************
         * Call behavior
         */
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

    @Override
    public int getShaderType(){
        return proto.mapEntityProto.shaderType;
    }
}
