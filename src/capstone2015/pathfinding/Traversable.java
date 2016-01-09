package capstone2015.pathfinding;

import java.util.LinkedList;

public interface Traversable<T extends Comparable<T>>{
    LinkedList<TraversableTransition<T>> getIncidentalEdges(TraversableNode<T> node);
    float calculateHeuristic(T start, T target);
}
