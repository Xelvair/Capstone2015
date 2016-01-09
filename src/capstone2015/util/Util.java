package capstone2015.util;

import capstone2015.game.Direction;
import capstone2015.geom.Vec2i;

import java.util.Collection;
import java.util.List;

public class Util {
    public static int countChar(char c, String str){
        int nchars = 0;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == c){
                nchars++;
            }
        }
        return nchars;
    }
    
    public static int longestLine(String str){
        int maxlen = 0;
        String[] lines = str.split("\r\n|\r|\n");
        for(String line : lines){
            maxlen = Math.max(maxlen, line.length());
        }
        return maxlen;
    }
    
    public static <T extends CharSequence> int maxLength(List<T> elems){
        int longest_elem = 0;
        for(T elem : elems){
            longest_elem = Math.max(longest_elem, elem.length());
        }
        return longest_elem;
    }

    public static Direction toDirection(Vec2i vec){
        if(vec.equals(new Vec2i(0, 0))){
            return Direction.NONE;
        } else if(vec.equals(new Vec2i(-1, 0))){
            return Direction.LEFT;
        } else if(vec.equals(new Vec2i(1, 0))){
            return Direction.RIGHT;
        } else if(vec.equals(new Vec2i(0, -1))){
            return Direction.UP;
        } else if(vec.equals(new Vec2i(0, 1))){
            return Direction.DOWN;
        } else {
            throw new RuntimeException("Failed to generate direction from vector!" + vec);
        }
    }
    
    public static int cap(int val, int min, int max){
        if(val < min)
            return min;
        if(val > max)
            return max;
        return val;
    }
}
