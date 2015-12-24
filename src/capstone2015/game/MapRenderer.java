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
  
  public static Panel render(MaskedMapView map, Recti renderRect){
    Panel p = new Panel(Math.max(0, renderRect.getWidth()), Math.max(0, renderRect.getHeight()));
    
    for(int i = 0; i < renderRect.getHeight(); i++){
      for(int j = 0; j < renderRect.getWidth(); j++){
        int map_x = j + renderRect.getLeft();
        int map_y = i + renderRect.getTop();
        if(map.inBounds(map_x, map_y) && map.getVisionAt(map_x, map_y).entitiesVisible()){
          ArrayList<MapEntity> entities = map.getMapEntitiesAt(map_x, map_y);
          if(entities.size() > 1){ //If we need to display something else than the tilemap
              int current_time_msec = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
              int shownEntityIndex = 1 + (current_time_msec / ITEM_DISPLAY_SWITCHTIME) % (entities.size() - 1);
              
              p.set(j, i, entities.get(shownEntityIndex).getRepresent());
          }  else {
            p.set(j, i, entities.get(0).getRepresent());
          }
        } else if(map.inBounds(map_x, map_y) && map.getVisionAt(map_x, map_y).tileVisible()){
          p.set(j, i, map.getMapEntitiesAt(map_x, map_y).get(0).getRepresentInvisible());
        } else {
          p.set(j, i, new TerminalChar());
        }
      }
    }
    
    return p;
  }
}
