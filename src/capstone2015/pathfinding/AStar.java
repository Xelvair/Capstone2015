package capstone2015.pathfinding;

import capstone2015.diagnostics.TimeStat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
    public static <T extends Comparable<T>> LinkedList<T> find(Traversable<T> traversable, T start, T target){
        return find(traversable, start, target, Float.NEGATIVE_INFINITY);
    }
    public static <T extends Comparable<T>> LinkedList<T> find(Traversable<T> traversable, T start, T target, double minHeuristic){
        TimeStat.enterState("AI.Pathfinding");
        long start_time = System.currentTimeMillis();
        boolean target_found = false;
        HashMap<TraversableNode<T>, TraversableNode<T>> nodes_closed = new HashMap<>();
        LinkedList<TraversableNode<T>> nodes_open = new LinkedList<>();
        TraversableNode<T> final_node = null;
        
        nodes_open.add(new TraversableNode<T>(start, 0, null)); //Add starting node, no distance no previous node

        while(!nodes_open.isEmpty()){
            /********************
             * Sort nodes by their "relvance" to finding the correct path
             * relevance is calculated as follows:
             * actual distance from start to node + estimated distance from node to target
             */
            Collections.sort(nodes_open, new Comparator<TraversableNode<T>>(){
                @Override
                public int compare(TraversableNode<T> o1, TraversableNode<T> o2) {
                    float result1 = o1.getDistance() + traversable.calculateHeuristic(o1.getNodeVal(), target);
                    float result2 = o2.getDistance() + traversable.calculateHeuristic(o2.getNodeVal(), target);
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
            nodes_closed.put(cur_node, cur_node);

            /************************
             * If we're only looking for adjacent positions,
             * scan all adjacencies for targetness #ripenglish
             */
            if(traversable.calculateHeuristic(cur_node.getNodeVal(), target) <= minHeuristic){
                target_found = true;
                final_node = cur_node;
                break;
            }

            /************************
             * If final node was found, all required nodes are in nodes_closed
             * And ready to be searched for a path in reverse order
             */
            if(cur_node.equals(new TraversableNode<T>(target, 0.f, null))){
                target_found = true;
                final_node = cur_node;
                break;
            }

            LinkedList<TraversableTransition<T>> transitions = traversable.getIncidentalEdges(cur_node);
            
            for(TraversableTransition<T> transition : transitions){
                T end_value = (T)transition.getEndValue();
                
                /**********************************
                 * Skip nodes that are already in one of our lists
                 * When searching, we do not have to care about the distance
                 * or the preceding node, because the TraversableNode compareTo
                 * method only comparey by endNode, which is exactly what we want
                 */
                if(nodes_closed.containsKey(new TraversableNode<T>(end_value, 0.f, null)))
                    continue;
                   
                /**********************************
                 * If the transitionable node is still in the open list,
                 * there is a chance we found a way to get ther with a lower
                 * distance value. If so, update distance on that node
                 */
                TraversableNode<T> end_node_comp_dummy = new TraversableNode<T>(end_value, 0.f, null);
                if(nodes_open.contains(end_node_comp_dummy)){
                    TraversableNode<T> list_node = nodes_open.get(nodes_open.indexOf(end_node_comp_dummy));
                    
                    
                    if(transition.getDistance() + cur_node.getDistance() < list_node.getDistance()){
                        list_node.setDistance(transition.getDistance() + cur_node.getDistance());
                        list_node.setPrevNode(cur_node);
                    }
                    continue;
                }
                
                nodes_open.add(new TraversableNode<T>(transition.getEndValue(),
                                                      transition.getDistance() + cur_node.getDistance(),
                                                      cur_node));
            }
        }
        
        //System.out.println("AStar finished in " + (System.currentTimeMillis() - start_time) + "ms with " + nodes_closed.size() + " considered nodes.");
        //System.out.println("(" + nodes_open.size() + " still in open list.)");

        /******************************
         * If a target was found, traverse nodes in reverse order and return traversed nodes
         * If not, return null
         */
        LinkedList<T> path = null;
        if(target_found && final_node != null){
            path = new LinkedList<>();
            
            while(final_node.getNodeVal().compareTo(start) != 0){
                path.addFirst(final_node.getNodeVal());
                final_node = final_node.getPrevNode();
            }
        }
        TimeStat.leaveState("AI.Pathfinding");
        return path;
    }
}
