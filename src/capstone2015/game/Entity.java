package capstone2015.game;

import capstone2015.graphics.TerminalChar;

public interface Entity {
    public final int ID_WALL = 0;
    public final int ID_ENTRY = 1;
    public final int ID_EXIT = 2;
    public final int ID_STATIC_OBSTACLE = 3;
    public final int ID_ENEMY = 4;
    public final int ID_KEY = 5;
    public final int ID_FLOOR = 6;
  
    enum PickUpBehavior{
        STATIC,
        PICKS_UP,
        PICKED_UP
    }    
 
    public boolean isTerminate();
    public void onEnter(Entity ent);
    public void onUse(Entity ent);
    public void giveItem(Entity ent);
    public PickUpBehavior getPickupBehavior();
    public void tick();
    public TerminalChar getRepresentVisible();
    public TerminalChar getRepresentInvisible();
}
