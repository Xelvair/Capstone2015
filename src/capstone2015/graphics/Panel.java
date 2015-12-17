package capstone2015.graphics;

import capstone2015.Util;
import capstone2015.geom.Recti;
import capstone2015.geom.Vector2i;
import com.googlecode.lanterna.terminal.Terminal.Color;
import java.util.ArrayList;

public class Panel implements PanelInterface{
    private Tile[]      tiles;
    private Vector2i    size;
    
    public Panel(Vector2i size){
        this.size = size;
        tiles = new Tile[size.getX() * size.getY()];
        for(int i = 0; i < tiles.length; i++){
            tiles[i] = new Tile();
        }
    }

    @Override
    public void set(Vector2i pos, Tile data) {
        if(!checkOffset(pos)){throw new IndexOutOfBoundsException();}
        tiles[bufferOffset(pos)] = data;
    }
    
    /********
     * Attempt set, fails quietly
     * @param pos offset
     * @param data data to set
     */
    @Override
    public void aset(Vector2i pos, Tile data) {
        if(checkOffset(pos)){
            tiles[bufferOffset(pos)] = data;
        }
    }

    @Override
    public void blit(PanelInterface bpanel, Vector2i pos) { 
        int bpanel_width = bpanel.size().getX();
        int bpanel_height = bpanel.size().getY();
        
        int startx = 0; //Math.max(0, pos.getX());
        int starty = 0; //Math.max(0, pos.getY());
        
        int endx = bpanel_width; //Math.min(startx + panel.size().getX(), size.getX());
        int endy = bpanel_height; //Math.min(starty + panel.size().getY(), size.getY());
        
        for(int i = starty; i < endy; i++){
            for(int j = startx; j < endx; j++){
                Tile bpanel_tile = bpanel.get(new Vector2i(j, i));
                Vector2i tile_dest = pos.translate(new Vector2i(j, i));
                aset(tile_dest, bpanel_tile);
            }
        }

    }

    @Override
    public Tile get(Vector2i pos) {
        return tiles[bufferOffset(pos)];
    }

    @Override
    public Vector2i size() {
        return size;
    }
    
    private boolean checkOffset(Vector2i pos){
        return(
               pos.getX() >= 0
            || pos.getX() < size.getX()
            || pos.getY() >= 0
            || pos.getY() < size.getY()
        );
    }
    
    /**
     * Calculates one-dimensional array offset from 2d-vector
     * @param pos 
     * @return 
     */
    private int bufferOffset(Vector2i pos){
        if(!checkOffset(pos)){return -1;}
        return size.getX() * pos.getY() + pos.getX();
    }
    
    @Override
    public String toString(){
        String str = "";
        for(int i = 0; i < size.getY(); i++){
            for(int j = 0; j < size.getX(); j++){
                str += this.get(new Vector2i(j, i)).getCharacter();
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
        int pwidth = Util.longestLine(text);
        int pheight = Math.max(1, Util.countChar('\n', text));
        
        Panel txtp = new Panel(new Vector2i(pwidth, pheight));
        
        int y = 0;
        for(String str : text.split("\r\n|\r|\n")){
            for(int x = 0; x < str.length(); x++){
                Tile t = new Tile(str.charAt(x), fgColor, bgColor);
                txtp.set(new Vector2i(x, y), t);
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
    
}
