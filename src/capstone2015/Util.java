package capstone2015;

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
}
