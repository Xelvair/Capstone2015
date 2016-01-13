package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.MapEntity;
import capstone2015.entity.SolidType;
import capstone2015.entity.Tile;
import capstone2015.geom.Geom;
import capstone2015.geom.Vec2i;
import capstone2015.util.Array2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class EntityMapView implements MapInterface{

    private Map map;
    private Array2D<Boolean> revealMask;
    private Vec2i viewer_pos;
    private int visionRadius;
    
    public EntityMapView(Map map, int visionRadius){
        this.map = map;
        this.visionRadius = visionRadius;
        revealMask = new Array2D<>(map.width(), map.height());
        revealMask.fill(false);
        viewer_pos = new Vec2i(0, 0);
    }
    
    public boolean hasRevealed(Vec2i pos){
        return hasRevealed(pos.getX(), pos.getY());
    }
    public boolean hasRevealed(int x, int y){
        return revealMask.get(x, y);
    }
    
    public void revealAll(){
        revealMask.fill(true);
    }
    
    public void setViewerPos(Vec2i pos){
        viewer_pos = new Vec2i(pos);
    }
    
    @Override
    public Tile getTileAt(int x, int y) {
        if(revealMask.get(x, y)){
            revealMask.set(new Vec2i(x, y), true);
            return map.getTileAt(x,y);
        }
        return null;
    }

    public boolean hasLineOfSight(int x, int y){
        return hasLineOfSight(new Vec2i(x, y));
    }
    public boolean hasLineOfSight(Vec2i pos){
        return hasLineOfSight(pos, true);
    }
    public boolean hasLineOfSight(Vec2i pos, boolean direct_los){
        if(viewer_pos.deltaMagnitude(pos) >= (visionRadius))
            return false;
        
        if(viewer_pos.deltaOrthoMagnitude(pos) <= 1)
            return true;
        
        LinkedList<Vec2i> line_points = Geom.lineToPoints(viewer_pos, pos);
        
        line_points.removeFirst();
        
        boolean has_line_of_sight = true;
        for(Vec2i point : line_points){
            if(map.isOpaqueAt(point))
                has_line_of_sight = false;
        }
        
        if(direct_los && !has_line_of_sight && map.getTileAt(pos).isOpaque()){
            Vec2i[] neighbors = {
                pos.add(new Vec2i(1, 0)),
                pos.add(new Vec2i(-1, 0)),
                pos.add(new Vec2i(0, 1)),
                pos.add(new Vec2i(0, -1))
            };
            
            for(int i = 0; i < 4; ++i){
                if(map.inBounds(neighbors[i]) && !map.getTileAt(neighbors[i]).isOpaque()){
                    if(hasLineOfSight(neighbors[i], false)){
                        has_line_of_sight = true;
                        break;
                    }
                }
            }
        }
        
        return has_line_of_sight;
    }
    
    @Override
    public ArrayList<Actor> getActorsAt(int x, int y) {
        if(hasLineOfSight(new Vec2i(x, y))){
            revealMask.set(new Vec2i(x, y), true);
            return map.getActorsAt(x, y);
        }
        return null;
    }

    @Override
    public LinkedList<Actor> getActors() {
        LinkedList<Actor> visible_actors = new LinkedList<Actor>();
        
        for(Actor a : map.getActors()){
            if(hasLineOfSight(a.getPos())){
                revealMask.set(a.getPos(), true);
                visible_actors.add(a);
            }
        }
        
        return visible_actors;
    }

    @Override
    public ArrayList<MapEntity> getMapEntitiesAt(int x, int y) {
        if(!hasRevealed(x, y) && !hasLineOfSight(x, y))
            return new ArrayList<MapEntity>();
            
        ArrayList<MapEntity> map_entities = new ArrayList<>();
        
        map_entities.add(map.getTileAt(x, y));
        
        if(hasLineOfSight(x, y)){
            revealMask.set(new Vec2i(x, y), true);
            map_entities.addAll(map.getActorsAt(x, y));
        }
        
        return map_entities;
    }

    @Override
    public int width() {
        return map.width();
    }

    @Override
    public int height() {
        return map.height();
    }
}
