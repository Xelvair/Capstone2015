package capstone2015.pathfinding;

import capstone2015.geom.Vec2i;

public class TraversableTransition<T extends Comparable> {
    private T endValue;
    private float distance;

    public TraversableTransition(T endNode, float distance) {
        this.endValue = endNode;
        this.distance = distance;
    }
    
    public T getEndValue(){
        return endValue;
    }
    
    public float getDistance(){
        return distance;
    }
}
