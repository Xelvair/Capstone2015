package capstone2015.pathfinding;

public class TraversableNode<T extends Comparable<T>> implements Comparable<TraversableNode<T>>{
    private TraversableNode<T> prevNode;
    private T nodeVal;
    private float distance;

    public TraversableNode(T nodeVal){
        this(nodeVal, 0, null);
    }
    public TraversableNode(T nodeVal, float distance, TraversableNode<T> prevNode){
        this.nodeVal = nodeVal;
        this.prevNode = prevNode;
        this.distance = distance;
    }
    
    public T getNodeVal() {
        return nodeVal;
    }
    
    public TraversableNode<T> getPrevNode(){
        return prevNode;
    }
    
    public void setPrevNode(TraversableNode<T> prevNode){
        this.prevNode = prevNode;
    }
    
    public float getDistance(){
        return distance;
    }
    
    public void setDistance(float distance){
        this.distance = distance;
    }
    
    @Override
    public int compareTo(TraversableNode<T> o) {
        return nodeVal.compareTo(o.getNodeVal());
    }

    @Override
    public boolean equals(Object rhs){
        return nodeVal.equals(((TraversableNode<T>)rhs).getNodeVal());
    }
    
    @Override
    public int hashCode(){
        return nodeVal.hashCode();
    }
}
