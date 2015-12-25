package capstone2015.pathfinding;

import capstone2015.geom.Vec2i;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Dijkstra {
    public static <T extends Comparable> LinkedList<T> find(Traversable traversable, T start, T target){
        boolean target_found = false;
        LinkedList<TraversableNode<T>> nodes_closed = new LinkedList<>();
        LinkedList<TraversableNode<T>> nodes_open = new LinkedList<>();
        
        nodes_open.add(new TraversableNode(start, 0, null)); //Add starting node, no distance no previous node
        
        while(!nodes_open.isEmpty()){
            Collections.sort(nodes_open, new Comparator<TraversableNode<T>>(){
                @Override
                public int compare(TraversableNode<T> o1, TraversableNode<T> o2) {
                    float result1 = traversable.calculateHeuristic(o1.getNodeVal(), target);
                    float result2 = traversable.calculateHeuristic(o2.getNodeVal(), target);
                    float result =  result1 - result2;
                    if(result < 0.f){
                        return -1;
                    } else if(result > 0.f){
                        return 1;
                    } else {
                        return 0;
                    }
                }
            
            });
            TraversableNode<T> cur_node = nodes_open.pollFirst();
            nodes_closed.add(cur_node);
            
            //If final node was found, all required nodes are in nodes_closed
            //And ready to be searched for a path in reverse order
            if(cur_node.equals(new TraversableNode(target, 0.f, null))){
                target_found = true;
                break;
            }
            
            LinkedList<TraversableTransition<T>> transitions = traversable.getIncidentalEdges(cur_node);
            
            for(TraversableTransition transition : transitions){
                T end_value = (T)transition.getEndValue();
                
                /**********************************
                 * Skip nodes that are already in one of our lists
                 * When searching, we do not have to care about the distance
                 * or the preceding node, because the TraversableNode compareTo
                 * method only comparey by endNode, which is exactly what we want
                 */
                if(nodes_closed.contains(new TraversableNode(end_value, 0.f, null)))
                    continue;
                   
                /**********************************
                 * If the transitionable node is still in the open list,
                 * there is a chance we found a way to get ther with a lower
                 * distance value. If so, update distance on that node
                 */
                TraversableNode<T> end_node_comp_dummy = new TraversableNode(end_value, 0.f, null);
                if(nodes_open.contains(end_node_comp_dummy)){
                    TraversableNode<T> list_node = nodes_open.get(nodes_open.indexOf(end_node_comp_dummy));
                    
                    
                    if(transition.getDistance() + cur_node.getDistance() < list_node.getDistance()){
                        list_node.setDistance(transition.getDistance() + cur_node.getDistance());
                        list_node.setPrevNode(cur_node);
                    }
                    continue;
                }
                
                nodes_open.add(new TraversableNode(transition.getEndValue(), 
                                                   transition.getDistance() + cur_node.getDistance(), 
                                                   cur_node));
            }
        }
        
        System.out.println("Dijkstra finished with " + nodes_closed.size() + " considered nodes.");
        System.out.println("(" + nodes_open.size() + " still in open list.)");
        
        if(target_found){
            LinkedList<T> path = new LinkedList<>();
            TraversableNode<T> cur_node = nodes_closed.get(nodes_closed.indexOf(new TraversableNode(target, 0.f, null)));
            
            System.out.println(nodes_closed.size());
            
            while(cur_node.getNodeVal().compareTo(start) != 0){
                path.addFirst(cur_node.getNodeVal());
                cur_node = cur_node.getPrevNode();
            }
            
            return path;
        } else {
            return null;
        }
    }
}
