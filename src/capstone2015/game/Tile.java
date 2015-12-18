package capstone2015.game;

import capstone2015.graphics.TerminalChar;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Tile implements Entity{
  
    protected boolean solid;
    protected boolean opaque;
    protected PickUpBehavior pickupBehavior;
    protected boolean canPlace;
    protected TerminalChar representVisible;
    protected TerminalChar representInvisible;
    
    protected Tile(){}
    
    public Tile(Tile t){
      this.solid = t.solid;
      this.opaque = t.opaque;
      this.pickupBehavior = t.pickupBehavior;
      this.canPlace = t.canPlace;
      this.representVisible = t.representVisible;
      this.representInvisible = t.representInvisible;
    }
    
    @Override
    public void onEnter(Entity ent) {
        
    }

    @Override
    public void onUse(Entity ent) {
        
    }

    @Override
    public void giveItem(Entity e) {
        throw new UnsupportedOperationException("Tiles can't pick things up.");
    }

    @Override
    public PickUpBehavior getPickupBehavior() {
        return this.pickupBehavior;
    }

    @Override
    public TerminalChar getRepresentVisible() {
        return this.representVisible;
    }
    
    @Override
    public TerminalChar getRepresentInvisible() {
        return this.representInvisible;
    }

    @Override
    public void tick() {
        
    }
    
    /********* STATIC METHODS *************/
    static Tile[] s_tiles;
    
    private static void initTiles(){
        s_tiles = new Tile[50];
        int tile_id;
        
        //WALL
        tile_id = 0;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = true;
        s_tiles[tile_id].opaque = true;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = true;
        s_tiles[tile_id].representVisible = new TerminalChar(' ', Color.WHITE, Color.WHITE);
        s_tiles[tile_id].representInvisible = new TerminalChar(' ', Color.WHITE, Color.WHITE);
        
        //ENTRY
        tile_id = 1;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = false;
        s_tiles[tile_id].opaque = false;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = false;
        s_tiles[tile_id].representVisible = new TerminalChar('\u2343', Color.BLUE, Color.BLACK);
        s_tiles[tile_id].representInvisible = new TerminalChar('\u2343', Color.BLUE, Color.BLACK);
        
        //EXIT
        tile_id = 2;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = true;
        s_tiles[tile_id].opaque = true;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = true;
        s_tiles[tile_id].representVisible = new TerminalChar('\u2344', Color.GREEN, Color.BLACK);
        s_tiles[tile_id].representInvisible = new TerminalChar('\u2344', Color.GREEN, Color.BLACK);
        
        //STATIC OBSTACLE
        tile_id = 3;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = true;
        s_tiles[tile_id].opaque = true;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = true;
        s_tiles[tile_id].representVisible = new TerminalChar('\u2344', Color.GREEN, Color.BLACK);
        s_tiles[tile_id].representInvisible = new TerminalChar('\u2344', Color.GREEN, Color.BLACK);
        
        //DYNAMIC OBSTACLE IS NEVER A TILE
        
        //KEY
        tile_id = 5;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = false;
        s_tiles[tile_id].opaque = false;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = false;
        s_tiles[tile_id].representVisible = new TerminalChar('\u26bf', Color.YELLOW, Color.BLACK);
        s_tiles[tile_id].representInvisible = new TerminalChar('\u26bf', Color.YELLOW, Color.BLACK);
        
        //FLOOR
        tile_id = 6;
        s_tiles[tile_id] = new Tile();
        s_tiles[tile_id].solid = false;
        s_tiles[tile_id].opaque = false;
        s_tiles[tile_id].pickupBehavior = PickUpBehavior.STATIC;
        s_tiles[tile_id].canPlace = false;
        s_tiles[tile_id].representVisible = new TerminalChar('.', Color.WHITE, Color.BLACK);
        s_tiles[tile_id].representInvisible = new TerminalChar(' ', Color.WHITE, Color.BLACK);
    }
    
    public static Tile create(int tileId){
        if(s_tiles == null){
            initTiles();
        }
        if(tileId >= s_tiles.length || s_tiles[tileId] == null){
            System.out.println("ERR: Requested invalid tile #" + tileId);
        }
        return new Tile(s_tiles[tileId]);
    }

  @Override
  public boolean isTerminate() {
    return false; //Tiles never get terminated
  }
}
