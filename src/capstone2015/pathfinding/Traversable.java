package capstone2015.pathfinding;

import capstone2015.entity.SolidType;

import java.util.LinkedList;

public interface Traversable<T extends Comparable<T>>{
    LinkedList<TraversableTransition<T>> getIncidentalEdges(TraversableNode<T> node);
    LinkedList<TraversableNode<T>> getAdjacentNodes(TraversableNode<T> node);
    float calculateHeuristic(T start, T target);
}
