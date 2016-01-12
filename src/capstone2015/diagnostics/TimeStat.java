package capstone2015.diagnostics;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class TimeStat {
    private static long last_time_nsec = System.nanoTime();
    private static Map<String, Long> states = new TreeMap();
    private static Stack<String> state_stack = new Stack();
    
    private static void flush(){
        long elapsed_time_nsec = System.nanoTime() - last_time_nsec;
        last_time_nsec = System.nanoTime();
        
        String cur_state = state_stack.isEmpty() ? "Other" : state_stack.peek();
        
        if(!states.containsKey(cur_state))
            states.put(cur_state, 0L);
        
        states.put(cur_state, states.get(cur_state) + elapsed_time_nsec);
    }
    
    public static void enterState(String state){
        flush();
        state_stack.push(state);
    }
    
    public static void leaveState(String state){
        flush();
        String left_state = state_stack.pop();
        if(!left_state.equals(state))
            System.out.println("WARNING TimeStat: Was in state " + left_state + " but instructed to leave state " + state + " instead.");
    }
    
    public static Map<String, Long> getStateSummary(){
        flush();
        Map<String, Long> summary = new TreeMap();
        summary.putAll(states);
        return summary;
    }
    
    public static long getElapsedTime(){
        flush();
        long elapsed_time = 0L;
        
        for(String state_name : states.keySet()){
            elapsed_time += states.get(state_name);
        }
        
        return elapsed_time;
    }
    
    public static void reset(){
        for(String state_name : states.keySet()){
            states.put(state_name, 0L);
        }
        last_time_nsec = System.nanoTime();
    }
}
