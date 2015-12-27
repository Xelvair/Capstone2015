package capstone2015.pathfinding;

import java.util.LinkedList;

public interface Traversable<T extends Comparable>{
    public LinkedList<TraversableTransition<T>> getIncidentalEdges(TraversableNode<T> node);
    public float calculateHeuristic(T start, T target);
}
