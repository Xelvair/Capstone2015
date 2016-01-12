package capstone2015.game;

import capstone2015.diagnostics.TimeStat;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.MapEntity;
import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.shader.ShaderPanel;
import capstone2015.shader.ShaderProgram;
import capstone2015.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class MapRenderer {
  public static final int ITEM_DISPLAY_SWITCHTIME = 500;
  
  /*******************************
     * SHADER PROCEDURES
     */
  
    private static BiFunction<TerminalChar, java.util.Map<String, Object>, TerminalChar> 
    passthroughShader =
    (TerminalChar in, java.util.Map<String, Object> data) -> {
        return new TerminalChar(in);
    };
    
    private static BiFunction<TerminalChar, java.util.Map<String, Object>, TerminalChar> 
    bonfireShader =
    (TerminalChar in, java.util.Map<String, Object> data) -> {
        if(   !data.containsKey("abs_pos_x")
           || !data.containsKey("abs_pos_y")){
            throw new RuntimeException("Failed to run shader: insufficient parameters.");
        }
        
        Color[] color_list = new Color[]{
            new Color(255, 253, 120),
            new Color(255, 149, 26),
            new Color(252, 144, 7),
            new Color(255, 218, 93),
            new Color(230, 120, 4),
            new Color(255, 144, 12)
        };
        
        Vec2i pos = new Vec2i((int)data.get("abs_pos_x"), (int)data.get("abs_pos_y"));
        
        final int SEQUENCE_DURATION_MSEC = 250;
        
        Random rand = new Random(Util.hash(pos.hashCode()) + System.currentTimeMillis() / SEQUENCE_DURATION_MSEC);
        
        return new TerminalChar(in.getCharacter(), color_list[rand.nextInt(color_list.length)] ,in.getBGColor());
    };
    
    private static BiFunction<TerminalChar, java.util.Map<String, Object>, TerminalChar> 
    colorVariationShader =
    (TerminalChar in, java.util.Map<String, Object> data) -> {
        if(   !data.containsKey("abs_pos_x")
           || !data.containsKey("abs_pos_y")
           || !data.containsKey("max_offset")){
            throw new RuntimeException("Failed to run shader: insufficient parameters.");
        }

        Vec2i pos = new Vec2i((int)data.get("abs_pos_x"), (int)data.get("abs_pos_y"));
        
        Random rand = new Random(Util.hash(pos.hashCode()));

        int max_offset = (int)data.get("max_offset");

        float rand_offset = rand.nextFloat() * 2.f - 1.f;

        int int_offset = (int)(rand_offset * max_offset);

        Color bg_col = new Color(in.getBGColor().getRGB());
        Color fg_col = new Color(in.getFGColor().getRGB());

        Color new_bg_col = new Color(
                Util.clamp(bg_col.getRed() + int_offset, 0, 255),
                Util.clamp(bg_col.getGreen() + int_offset, 0, 255),
                Util.clamp(bg_col.getBlue() + int_offset, 0, 255)
        );

        Color new_fg_col = new Color(
                Util.clamp(fg_col.getRed() + int_offset, 0, 255),
                Util.clamp(fg_col.getGreen() + int_offset, 0, 255),
                Util.clamp(fg_col.getBlue() + int_offset, 0, 255)
        );

        return new TerminalChar(in.getCharacter(), new_fg_col, new_bg_col);
    };
    
    private static BiFunction<TerminalChar, java.util.Map<String, Object>, TerminalChar> 
    waterShader =
    (TerminalChar in, java.util.Map<String, Object> data) -> {
        if(   !data.containsKey("abs_pos_x")
           || !data.containsKey("abs_pos_y")
           || !data.containsKey("max_offset")){
            throw new RuntimeException("Failed to run shader: insufficient parameters.");
        }
        final int SAMPLE_COUNT = 10;
        final int SEQUENCE_COUNT = 10000;
        final int SEQUENCE_DURATION_MSEC = 350;
        
        long time_msec = System.currentTimeMillis();
        int sequence = (int)((time_msec / SEQUENCE_DURATION_MSEC) % SEQUENCE_COUNT);

        Vec2i pos = new Vec2i((int)data.get("abs_pos_x"), (int)data.get("abs_pos_y"));
        
        Random rand = new Random();
        
        int seed = Util.hash(pos.hashCode());
        float avg_val = 0.f;

        for(int k = 0; k < SAMPLE_COUNT; k++){
            rand.setSeed(seed + (sequence - k % SEQUENCE_COUNT) * 24232);
            float cur_sample = rand.nextFloat();
            avg_val = (cur_sample / (float)SAMPLE_COUNT) + ((float)SAMPLE_COUNT - 1.f) / (float)SAMPLE_COUNT * avg_val ;
        }
        
        avg_val = avg_val * 2.f - 1.f;
        
        int max_offset = (int)data.get("max_offset");

        int int_offset = (int)(avg_val * max_offset);

        Color bg_col = new Color(in.getBGColor().getRGB());
        Color fg_col = new Color(in.getFGColor().getRGB());

        Color new_bg_col = new Color(
                Util.clamp(bg_col.getRed() + int_offset, 0, 255),
                Util.clamp(bg_col.getGreen() + int_offset, 0, 255),
                Util.clamp(bg_col.getBlue() + int_offset, 0, 255)
        );

        Color new_fg_col = new Color(
                Util.clamp(fg_col.getRed() + int_offset * 2, 0, 255),
                Util.clamp(fg_col.getGreen() + int_offset * 2, 0, 255),
                Util.clamp(fg_col.getBlue() + int_offset * 2, 0, 255)
        );

        return new TerminalChar(in.getCharacter(), new_fg_col, new_bg_col);
    };
  
    public static Panel render(MaskedMapView map, Recti renderRect){
        TimeStat.enterState("Rendering");
        /****************************
         * Create shader programs
         */
        ShaderProgram[] shader_programs = new ShaderProgram[EntityFactory.SHADER_COUNT];
        shader_programs[EntityFactory.SHADER_NONE] = new ShaderProgram();
        shader_programs[EntityFactory.SHADER_NONE].setShaderProc(passthroughShader);
        
        shader_programs[EntityFactory.SHADER_COLOR_VARIATION] = new ShaderProgram();
        shader_programs[EntityFactory.SHADER_COLOR_VARIATION].setShaderProc(colorVariationShader);
        shader_programs[EntityFactory.SHADER_COLOR_VARIATION].setUniform("max_offset", 3);
        
        shader_programs[EntityFactory.SHADER_WATER] = new ShaderProgram();
        shader_programs[EntityFactory.SHADER_WATER].setShaderProc(waterShader);
        shader_programs[EntityFactory.SHADER_WATER].setUniform("max_offset", 100);
        
        shader_programs[EntityFactory.SHADER_BONFIRE] = new ShaderProgram();
        shader_programs[EntityFactory.SHADER_BONFIRE].setShaderProc(bonfireShader);
        
        /****************************
         * Render map
         */

        ShaderPanel p = new ShaderPanel(Math.max(0, renderRect.getWidth()), Math.max(0, renderRect.getHeight()));

        for(int i = 0; i < renderRect.getHeight(); i++){
          for(int j = 0; j < renderRect.getWidth(); j++){
            java.util.Map<String, Object> character_shader_data = new TreeMap();
              
            int map_x = j + renderRect.getLeft();
            int map_y = i + renderRect.getTop();
            
            character_shader_data.put("abs_pos_x", map_x);
            character_shader_data.put("abs_pos_y", map_y);
            
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
                  p.setShaderProgram(j, i, shader_programs[entities.get(shownEntityIndex).getShaderType()]);
              }  else {
                p.set(j, i, entities.get(0).getRepresent());
                p.setShaderProgram(j, i, shader_programs[entities.get(0).getShaderType()]);
              }
            } else if(map.inBounds(map_x, map_y) && map.getVisionAt(map_x, map_y).tileVisible()){
                MapEntity e = map.getMapEntitiesAt(map_x, map_y).get(0);
                p.set(j, i, e.getRepresentInvisible());
                p.setShaderProgram(j, i, shader_programs[e.getShaderType()]);
            } else {
              p.set(j, i, new TerminalChar());
            }
            
            p.setCharacterData(j, i, character_shader_data);
          }
        }

        TimeStat.leaveState("Rendering");
        return p.render();
    }
}
