package capstone2015.game;

import capstone2015.entity.SolidType;
import capstone2015.geom.Vec2i;
import capstone2015.pathfinding.Traversable;
import capstone2015.pathfinding.TraversableTransition;
import capstone2015.pathfinding.TraversableNode;
import java.util.LinkedList;

public class MapTraversableAdapter implements Traversable<Vec2i>{

    private MapInterface map;
    private SolidType traverserSolidType;

    public MapTraversableAdapter(MapInterface map, SolidType traverserSolidType){
        this.map = map;
        this.traverserSolidType = traverserSolidType;
    }
    
    @Override
    public LinkedList<TraversableTransition<Vec2i>> getIncidentalEdges(TraversableNode<Vec2i> node) {
        Direction[] dirs = new Direction[]{
            Direction.LEFT, 
            Direction.RIGHT, 
            Direction.UP, 
            Direction.DOWN
        };
        
        LinkedList<TraversableTransition<Vec2i>> incidental_edge_list = new LinkedList<>();
        
        Vec2i node_vec = node.getNodeVal();
        
        for(Direction dir : dirs){
            Vec2i adjacent_node = node_vec.add(dir.toVector());
            if(map.inBounds(adjacent_node) && !map.getSolidTypeAt(adjacent_node).collidesWith(traverserSolidType)){
                incidental_edge_list.add(new TraversableTransition(adjacent_node, 1));
            }
        }
        
        return incidental_edge_list;
    }

    @Override
    public float calculateHeuristic(Vec2i start, Vec2i target) {
        Vec2i delta = target.add(start.invert());
        return (float)Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
    }
}
