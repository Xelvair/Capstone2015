package capstone2015.game;

import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.util.ArrayList;

public class MapRenderer {
  public static final int ITEM_DISPLAY_SWITCHTIME = 500;
    
  public static Panel render(Map map, Recti renderRect){
    Panel p = new Panel(renderRect.getWidth(), renderRect.getHeight());
    
    for(int i = 0; i < renderRect.getHeight(); i++){
      for(int j = 0; j < renderRect.getWidth(); j++){
        int map_x = j + renderRect.getLeft();
        int map_y = i + renderRect.getTop();
        if(map.inBounds(map_x, map_y)){
          ArrayList<Entity> entities = map.getEntitiesAt(map_x, map_y);
          if(entities.size() > 1){ //If we need to display something else than the tilemap
              int current_time_msec = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
              int shownEntityIndex = 1 + (current_time_msec / ITEM_DISPLAY_SWITCHTIME) % (entities.size() - 1);
              
              p.set(j, i, entities.get(shownEntityIndex).getRepresentVisible());
          } else {
            p.set(j, i, entities.get(0).getRepresentVisible());
          }
        } else {
          p.set(j, i, new TerminalChar());
        }
      }
    }
    
    return p;
  }
  
  public static Panel renderPlayerCentered(Map map, int width, int height){
    int left = 0;
    int top = 0;
      
    PositionedEntity player = null;
    if((player = map.getPlayer()) != null){
        left = player.getXPos() - width / 2;
        top = player.getYPos() - height / 2;
    }
    
    return render(map, new Recti(left, top, width, height));
  }
}
