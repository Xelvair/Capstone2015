package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.MapEntity;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Array2D;
import java.util.ArrayList;

public class MapRenderer {
  public static final int ITEM_DISPLAY_SWITCHTIME = 500;
  
  public static Panel render(Map map, Recti renderRect){
    Panel p = new Panel(Math.max(0, renderRect.getWidth()), Math.max(0, renderRect.getHeight()));
    
    Array2D<Boolean> vision_mask = VisionMaskGenerator.generate(map, renderRect);

    map.applyVisionMask(vision_mask, renderRect.getLeft(), renderRect.getTop());
    
    for(int i = 0; i < renderRect.getHeight(); i++){
      for(int j = 0; j < renderRect.getWidth(); j++){
        int map_x = j + renderRect.getLeft();
        int map_y = i + renderRect.getTop();
        if(map.inBounds(map_x, map_y) && vision_mask.get(j, i) == true){
          ArrayList<MapEntity> entities = map.getEntitiesAt(map_x, map_y);
          if(entities.size() > 1){ //If we need to display something else than the tilemap
              int current_time_msec = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
              int shownEntityIndex = 1 + (current_time_msec / ITEM_DISPLAY_SWITCHTIME) % (entities.size() - 1);
              
              p.set(j, i, entities.get(shownEntityIndex).getRepresent());
          }  else {
            p.set(j, i, entities.get(0).getRepresent());
          }
        } else if(map.inBounds(map_x, map_y) && map.isRevealed(map_x, map_y)){
          p.set(j, i, map.getEntitiesAt(map_x, map_y).get(0).getRepresentInvisible());
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
      
    Actor player = null;
    if((player = map.getPlayer()) != null){
        left = player.getXPos() - width / 2;
        top = player.getYPos() - height / 2;
    }
    
    return render(map, new Recti(left, top, width, height));
  }
}
