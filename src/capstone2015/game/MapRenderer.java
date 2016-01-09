package capstone2015.game;

import capstone2015.entity.MapEntity;
import capstone2015.entity.Tile;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

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

              /*****************************
               * Character and foreground color are determined by the actor,
               * background color is determined by the tile
               */
              TerminalChar tile = new TerminalChar(
                      entities.get(shownEntityIndex).getRepresent().getCharacter(),
                      entities.get(shownEntityIndex).getRepresent().getFGColor(),
                      entities.get(0).getRepresent().getBGColor()
              );

              p.set(j, i, tile);
          }  else {
            Random rand = new Random(map_x * map_y);
              
            TerminalChar tile_represent = entities.get(0).getRepresent();
            int color_variation_max = ((Tile)entities.get(0)).getColorVariation();
            int color_variation = rand.nextInt(color_variation_max * 2) - color_variation_max;
            Color tile_represent_bg_color = new Color(
                    Util.cap(tile_represent.getBGColor().getRed() + color_variation, 0, 255),
                    Util.cap(tile_represent.getBGColor().getGreen() + color_variation, 0, 255),
                    Util.cap(tile_represent.getBGColor().getBlue() + color_variation, 0, 255)
            );
            p.set(j, i, new TerminalChar(tile_represent.getCharacter(), tile_represent.getFGColor(), tile_represent_bg_color));
          }
        } else if(map.inBounds(map_x, map_y) && map.getVisionAt(map_x, map_y).tileVisible()){
            Random rand = new Random(map_x * map_y);

            TerminalChar tile_represent = map.getMapEntitiesAt(map_x, map_y).get(0).getRepresentInvisible();
            int color_variation_max = ((Tile)map.getMapEntitiesAt(map_x, map_y).get(0)).getColorVariation();
            int color_variation = rand.nextInt(color_variation_max * 2) - color_variation_max;
            Color tile_represent_bg_color = new Color(
                    Util.cap(tile_represent.getBGColor().getRed() + color_variation, 0, 255),
                    Util.cap(tile_represent.getBGColor().getGreen() + color_variation, 0, 255),
                    Util.cap(tile_represent.getBGColor().getBlue() + color_variation, 0, 255)
            );
            p.set(j, i, new TerminalChar(tile_represent.getCharacter(), tile_represent.getFGColor(), tile_represent_bg_color));  
        } else {
          p.set(j, i, new TerminalChar());
        }
      }
    }
    
    return p;
  }
}
