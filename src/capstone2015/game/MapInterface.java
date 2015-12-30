package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.MapEntity;
import capstone2015.entity.SolidType;
import capstone2015.entity.Tile;
import capstone2015.geom.Vec2i;
import java.util.ArrayList;
import java.util.LinkedList;

public interface MapInterface {
    public Tile getTileAt(int x, int y);
    public default Tile getTileAt(Vec2i pos){
        return getTileAt(pos.getX(), pos.getY());
    }
    
    public ArrayList<Actor> getActorsAt(int x, int y);
    public default ArrayList<Actor> getActorsAt(Vec2i pos){
        return getActorsAt(pos.getX(), pos.getY());
    }

    public LinkedList<Actor> getActors();

    public ArrayList<MapEntity> getMapEntitiesAt(int x, int y);
    public default ArrayList<MapEntity> getMapEntitiesAt(Vec2i pos){
        return getMapEntitiesAt(pos.getX(), pos.getY());
    }
    
    public int width();
    public int height();
    
    public default boolean inBounds(Vec2i pos){
        return inBounds(pos.getX(), pos.getY());
    }
    public default boolean inBounds(int x, int y){
        return (   0 <= x && x < width()
                && 0 <= y && y < height());
    }
    
    public default SolidType getSolidTypeAt(Vec2i pos){
        return getSolidTypeAt(pos.getX(), pos.getY());
    }
    public default SolidType getSolidTypeAt(int x, int y){
        SolidType solid_max = SolidType.GHOST;

        ArrayList<MapEntity> local_entities = getMapEntitiesAt(x, y);
        for(MapEntity e : local_entities){
            solid_max = SolidType.max(solid_max, e.getSolidType());
        }
        return solid_max;
    }
    
    public default boolean isOpaqueAt(Vec2i pos){
        return isOpaqueAt(pos.getX(), pos.getY());
    }
    public default boolean isOpaqueAt(int x, int y){
        boolean is_opaque = false;

        ArrayList<MapEntity> local_entities = getMapEntitiesAt(x, y);
        for(MapEntity e : local_entities){
            if(e.isOpaque()){
                is_opaque = true;
            }
        }
        return is_opaque;
    }
}
