package capstone2015.graphics;

import capstone2015.util.Util;
import capstone2015.util.Array2D;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Panel extends Array2D<TerminalChar>{
  
    public Panel(int x, int y){
        super(x, y);
    }
    
    @Override
    public String toString(){
        String str = "";
        for(int i = 0; i < height(); i++){
            for(int j = 0; j < width(); j++){
                str += this.get(j, i).getCharacter();
            }
            str += "\n";
        }
        return str;
    }
    
    /********************** STATIC FUNCTIONS **************************/
    /**
     * Creates a Panel with text in it
     * @param text text for the panel, can be multiline
     * @param fgColor foregound color for the panel
     * @param bgColor background color for the panel
     * @return 
     */
    public static Panel textPanel(String text, Color fgColor, Color bgColor){
        String[] split_text = text.split("\r\n|\r|\n");
        
        int pwidth = Util.longestLine(text);
        int pheight = split_text.length;
        
        Panel txtp = new Panel(pwidth, pheight);
        
        /*******
         * Fill panel with data from the string
         */
        int y = 0;
        for(String str : split_text){
            for(int x = 0; x < pwidth; x++){
                TerminalChar t;
                if(x < str.length()){
                    t = new TerminalChar(str.charAt(x), fgColor, bgColor);                    
                } else {
                    t = new TerminalChar(' ', fgColor, bgColor);
                }
                txtp.set(x, y, t);
            }
            y++;
        }
        
        return txtp;
    }
    public static Panel textPanel(String text, Color fgColor){
        return textPanel(text, fgColor, Color.WHITE);
    }
    public static Panel textPanel(String text){
        return textPanel(text, Color.BLACK);
    }
    
    /*******
     * Creates a panel filled with a single type of tile
     * @param width width of the panel
     * @param height height of the panel
     * @param tile tile to fill panel with
     * @return
     */
    public static Panel fillPanel(int width, int height, TerminalChar tile){
        Panel p = new Panel(width, height);
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                p.set(j, i, tile);
            }
        }
        
        return p;
    }
    
}
