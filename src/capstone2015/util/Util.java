package capstone2015.util;

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
}
