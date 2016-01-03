package capstone2015.graphics;

import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.util.Util;
import capstone2015.util.Array2D;
import java.awt.Color;
import java.util.LinkedList;

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

    @Override
    public Panel subArray(Recti subRect){
        Panel sub_array = new Panel(subRect.getWidth(), subRect.getHeight());
        for(int i = 0; i < subRect.getHeight(); ++i){
            for(int j = 0; j < subRect.getWidth(); ++j){
                Vec2i rel_pos = new Vec2i(j, i);
                Vec2i abs_pos = subRect.toAbs(rel_pos);
                sub_array.set(rel_pos, this.get(abs_pos));
            }
        }
        return sub_array;
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
    
    public static Panel borderPanel(int width, int height, Color borderColor, Color bgColor){
        TerminalChar border_char = new TerminalChar(' ', Color.WHITE, borderColor);
        TerminalChar bg_char = new TerminalChar(' ', Color.WHITE, bgColor);
        
        Panel p = Panel.fillPanel(width, height, border_char);
        p.insertCenter(Panel.fillPanel(width - 2, height - 2, bg_char));
        
        return p;
    }

    public static Panel concatVertically(LinkedList<Panel> panels){
        int max_w = 0;
        int sum_h = 0;

        for(Panel panel : panels){
            max_w = Math.max(panel.width(), max_w);
            sum_h += panel.height();
        }

        Panel p = new Panel(max_w, sum_h);

        int y_accum = 0;
        for(Panel panel : panels){
            p.insert(panel, 0, y_accum);
            y_accum += panel.height();
        }

        return p;
    }

    public static Panel concatHorizontally(LinkedList<Panel> panels){
        int sum_w = 0;
        int max_h = 0;

        for(Panel panel : panels){
            max_h = Math.max(panel.height(), max_h);
            sum_w += panel.width();
        }

        Panel p = new Panel(sum_w, max_h);

        int x_accum = 0;
        for(Panel panel : panels){
            p.insert(panel, x_accum, 0);
            x_accum += panel.width();
        }

        return p;
    }
    
}
