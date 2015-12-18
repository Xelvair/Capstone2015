package capstone2015.map;

import capstone2015.graphics.TerminalChar;

public interface Entity {
    enum PickUpBehavior{
        STATIC,
        PICKS_UP,
        PICKED_UP
    }    
 
    public void onEnter(Entity ent);
    public void onUse(Entity ent);
    public void giveItem(Entity ent);
    public PickUpBehavior getPickupBehavior();
    public void tick();
    public TerminalChar getRepresentVisible();
    public TerminalChar getRepresentInvisible();
}
