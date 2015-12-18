package capstone2015.game;

import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.util.ArrayList;

public class MapRenderer {
  public static Panel render(Map map, Recti renderRect){
    Panel p = new Panel(renderRect.getWidth(), renderRect.getHeight());
    
    for(int i = 0; i < renderRect.getHeight(); i++){
      for(int j = 0; j < renderRect.getWidth(); j++){
        int map_x = j + renderRect.getLeft();
        int map_y = i + renderRect.getTop();
        if(map.inBounds(map_x, map_y)){
          ArrayList<Entity> entities = map.getEntitiesAt(map_x, map_y);
          p.set(j, i, entities.get(0).getRepresentVisible());
        } else {
          p.set(j, i, new TerminalChar());
        }
      }
    }
    
    return p;
  }
}
