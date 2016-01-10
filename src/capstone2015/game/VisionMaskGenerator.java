package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.geom.Geom;
import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.util.Array2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VisionMaskGenerator {
    public static final int SEARCH_AREA_GROW = 10;
    
    public static Array2D<Boolean> generate(Map map, Recti area){
        return generate(map, area, null);
    }
    public static Array2D<Boolean> generate(Map map, Recti area, Actor viewer){
        Recti map_rect = new Recti(0, 0, map.width(), map.height());
        Recti dest_rect = new Recti(0, 0, area.getWidth(), area.getHeight());
        
        /************
         * Calculate the search area.
         * Grow the search area because there might be vision-giving 
         * entities outside of the drawn area
         * Make sure the grown search area doesn't exceed  
         * the bounds of the map either
         */
        Recti search = area.grow(new Vec2i(SEARCH_AREA_GROW, SEARCH_AREA_GROW));
        search = search.intersect(map_rect);
        
        /**************
         * Collect all entities that have vision.
         * Since some entities don't have a position stored in them
         * (because they are tiles on the map), we have to restore
         * that position information. Creating a PositionedEntity
         * through the copy constructor is dangerous IMO.
         */
        ArrayList<Actor> vision_entities = new ArrayList<>();
        
        if(viewer == null){
            for(int i = search.getTop(); i <= search.getBottom(); i++){
                for(int j = search.getLeft(); j <= search.getRight(); j++){
                    //INEFFICIENT AS HELL, PLS CHANGE
                    ArrayList<Actor> local_entities = map.getActorsAt(j, i);
                    int vision_radius_max = 0;
                    Actor vision_radius_max_e = null;

                    for(Actor e : local_entities){
                        if(e.getVisionRadius() > vision_radius_max){
                            vision_radius_max = e.getVisionRadius();
                            vision_radius_max_e = e;
                        }
                    }

                    if(vision_radius_max > 0 && vision_radius_max_e != null){
                        vision_entities.add(vision_radius_max_e);
                    }
                }
            }
        } else {
            vision_entities.add(viewer);
        }
        
        /********
         * Generate lines between all points of the vision circle
         * of each entity. Then traverse and set the visibility mask
         * until a non-opaque point on the map is reached
         */        
        Array2D<Boolean> vision_mask = new Array2D<Boolean>(
                Math.max(0, area.getWidth()), 
                Math.max(0, area.getHeight())
        );
        
        for(int i = 0; i < vision_mask.height(); i++){
            for(int j = 0; j < vision_mask.width(); j++){
                vision_mask.set(j, i, false);
            }
        }
        
        for(Actor e : vision_entities){
            Vec2i entity_pos = e.getPos();
            Actor entity = e;
            int entity_vision_radius = entity.getVisionRadius();
            
            /** *************
             * Go from the center out to the endpoints of the circle
             * and set visited tiles visible until an opaque tile was
             * encountered.
             */
            LinkedList<Vec2i> circle_points = Geom.generateCircle(entity_pos, entity_vision_radius);
            for(Vec2i circle_point : circle_points){
                LinkedList<Vec2i> line_points = Geom.lineToPoints(entity_pos, circle_point);
                for(Vec2i line_point : line_points){
                    if(!map.inBounds(line_point.getX(), line_point.getY())){
                        continue;
                    }
                    
                    Vec2i vision_mask_pos = area.toRel(line_point);
                    
                    if(!vision_mask.inBounds(vision_mask_pos)){
                        continue;
                    }
                    
                    vision_mask.set(vision_mask_pos.getX(), vision_mask_pos.getY(), true);
                    
                    if(map.isOpaqueAt(line_point.getX(), line_point.getY())){
                        break;
                    }
                    
                    /* Also make any adjacent walls visible */
                    List<Direction> directions = Arrays.asList(
                            Direction.LEFT,
                            Direction.UP,
                            Direction.RIGHT,
                            Direction.DOWN
                    );
                    
                    for(Direction direction : directions){
                        Vec2i surrounding_point = line_point.add(direction.toVector());
                        vision_mask_pos = area.toRel(surrounding_point);
                        if(   map.inBounds(surrounding_point)
                           && vision_mask.inBounds(vision_mask_pos)
                           && map.getMapEntitiesAt(surrounding_point).get(0).isOpaque()
                        ){
                            vision_mask.set(vision_mask_pos, true);
                        }
                    }
                    
                }
            }
        }
        
        return vision_mask;
    }
}
