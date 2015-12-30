package capstone2015.pathfinding;

import capstone2015.entity.SolidType;

import java.util.LinkedList;

public interface Traversable<T extends Comparable<T>>{
    public LinkedList<TraversableTransition<T>> getIncidentalEdges(TraversableNode<T> node);
    public float calculateHeuristic(T start, T target);
}
