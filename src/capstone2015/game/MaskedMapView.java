package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.MapEntity;
import capstone2015.entity.Tile;
import static capstone2015.game.VisionType.Invisible;
import static capstone2015.game.VisionType.Visible;
import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.util.Array2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MaskedMapView implements MapInterface{

    private Map map;
    private Array2D<VisionType> visionMask;
    
    public MaskedMapView(Map map){
        this.map = map;
        visionMask = new Array2D<VisionType>(map.width(), map.height());
        visionMask.fill(VisionType.Invisible);
    }
    
    public VisionType getVisionAt(int x, int y){
        return visionMask.get(x, y);
    }
    
    public void revealAll(){
        visionMask.fill(VisionType.Revealed);
    }
    
    private void setVisibleToRevealed(){
        for(int i = 0; i < visionMask.height(); ++i){
            for(int j = 0; j < visionMask.width(); ++j){
                if(visionMask.get(j, i) == VisionType.Visible){
                    visionMask.set(j, i, VisionType.Revealed);
                }
            }
        }
    }
    /***************************************************************************
     * Sets all previously visible or revealed positions to revealed
     * and sets the marked positions from visibilityMask to visible
     * @param x horizontal start of the visibility mask
     * @param y vertical start of the visibility mask
     * @param visibilityMask the vismask (TRUE for visible, FALSE for not visible)
     */
    public void updateVisibilityMask(int x, int y, Array2D<Boolean> visibilityMask){
        setVisibleToRevealed();
        Recti this_rect = new Recti(0, 0, width(), height());
        Recti vismask_rect = new Recti(x, y, visibilityMask.width(), visibilityMask.height());
        
        Recti intersect_rect = this_rect.intersect(vismask_rect);
        
        for(int i = 0; i < intersect_rect.getHeight(); ++i){
            for(int j = 0; j < intersect_rect.getWidth(); ++j){
                Vec2i intersect_pos = new Vec2i(j, i);
                
                Vec2i vismask_pos = vismask_rect.toRel(intersect_rect.toAbs(intersect_pos));
                Vec2i this_pos = this_rect.toRel(intersect_rect.toAbs(intersect_pos));
                
                if(visibilityMask.get(vismask_pos))
                    visionMask.set(this_pos, Visible);
            }
        }
    }
    
    @Override
    public Tile getTileAt(int x, int y) {
        if(visionMask.get(x, y).tileVisible())
            return map.getTileAt(x,y);
        return null;
    }

    @Override
    public ArrayList<Actor> getActorsAt(int x, int y) {
        if(visionMask.get(x, y).entitiesVisible())
            return map.getActorsAt(x, y);
        return null;
    }

    @Override
    public LinkedList<Actor> getActors() {
        LinkedList<Actor> visible_actors = new LinkedList<Actor>();
        for(int i = 0; i < visionMask.height(); ++i){
            for(int j = 0; j < visionMask.width(); ++j){
                if(visionMask.get(j, i) == VisionType.Visible)
                    visible_actors.addAll(map.getActorsAt(j, i));
            }
        }
        return visible_actors;
    }

    @Override
    public ArrayList<MapEntity> getMapEntitiesAt(int x, int y) {
        if(getVisionAt(x, y) == Invisible)
            return null;
            
        ArrayList<MapEntity> map_entities = new ArrayList<>();
        
        if(visionMask.get(x, y).tileVisible())
            map_entities.add(map.getTileAt(x, y));
        
        if(visionMask.get(x, y).entitiesVisible())
            map_entities.addAll(map.getActorsAt(x, y));
        
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

    public ArrayList<Actor> getActorsById(int actorId){
        ArrayList actors = new ArrayList<Actor>();
        for(Actor a  : this.getActors()){
            if(a.getProto().id == actorId){
                actors.add(a);
            }
        }
        return actors;
    }
}
