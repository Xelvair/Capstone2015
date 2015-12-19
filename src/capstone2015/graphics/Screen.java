package capstone2015.graphics;

import capstone2015.geom.Recti;
import capstone2015.geom.Vector2i;
import capstone2015.util.Array2D;
import capstone2015.util.Array2DInterface;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import java.awt.Color;
import com.googlecode.lanterna.terminal.Terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;

public class Screen implements Array2DInterface<TerminalChar>{

    private Terminal terminal;
    private final ArrayList<Array2D<TerminalChar>> buffers = new ArrayList<>();
    private int width;
    private int height;
    private int membuf = 0;
    private boolean scheduleRealloc = false;
    
    public Screen(){
        this.terminal = TerminalFacade.createSwingTerminal();
        reallocBuffers();
        terminal.setCursorVisible(false);
        terminal.enterPrivateMode();
        terminal.addResizeListener(new ResizeListener(){

            @Override
            public void onResized(TerminalSize ts) {
                onTerminalResize();
            }
    
        });
    }
    
    public Key readInput(){
        return terminal.readInput();
    }
    
    public void clear(){
        terminal.clearScreen();
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                buffers.get(getScreenBufId()).set(j, i, new TerminalChar());
            }
        }
    }
    
    private void onTerminalResize(){
        scheduleRealloc = true;
    }

    private void reallocBuffers(){
        buffers.clear();
        this.width = terminal.getTerminalSize().getColumns();
        this.height = terminal.getTerminalSize().getRows();
        buffers.add(new Array2D<>(width, height)); //buf1
        buffers.add(new Array2D<>(width, height)); //buf2
    }
    
    public void flip(){      
        if(scheduleRealloc){
            reallocBuffers();
            scheduleRealloc = false;
        }
        
        //TODO: make more efficient
        try{   
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    TerminalChar s_tile = buffers.get(getMemBufId()).get(j, i);
                    if(s_tile != null && shouldRedraw(j, i)){
                        terminal.moveCursor(j, i);
                        
                        Color fgColor = s_tile.getFGColor();
                        Color bgColor = s_tile.getBGColor();
                        
                        terminal.applyForegroundColor(fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue());
                        terminal.applyBackgroundColor(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
                        terminal.putCharacter(s_tile.getCharacter());
                    }
                }
            }
        } catch(ArrayIndexOutOfBoundsException e){
            System.out.println("WARN: OutOfBounds on terminal write!");
        }
        
        membuf = (membuf + 1) % 2; //switch buffers
       
    }
    
    private boolean shouldRedraw(int x, int y){
        TerminalChar membuf_tile = buffers.get(getMemBufId()).get(x, y);
        TerminalChar screenbuf_tile = buffers.get(getScreenBufId()).get(x, y);
        
        if(membuf_tile == null){ 
            //If there is no tile in the membuf, dont draw
            return false;
        } else if(screenbuf_tile == null){ 
            //If the screenbuf tile was not written to yet, draw
            return true;
        } else if(membuf_tile.equals(screenbuf_tile)){
            //If tiles are the same, skip draw
            return false;
        } else {
            //If tiles are different, redraw
            return true;
        }
    }
    
    private int getScreenBufId(){return (membuf + 1) % 2;}
    private int getMemBufId(){return membuf;}
    private TerminalChar getScreenBuf(int x, int y){
        return buffers.get(getScreenBufId()).get(x, y);
    }
    private TerminalChar getMemBuf(int x, int y){
        return buffers.get(getMemBufId()).get(x, y);
    }
    private void setScreenBuf(int x, int y, TerminalChar tile){
        buffers.get(getScreenBufId()).set(x, y, tile);
    }
    private void setMemBuf(int x, int y, TerminalChar tile){
        buffers.get(getMemBufId()).set(x, y, tile);
    }
    
    @Override
    public TerminalChar get(int x, int y) {
        return getScreenBuf(x, y);
    }

    @Override
    public void set(int x, int y, TerminalChar tile) {
        setMemBuf(x, y, tile);
    }

    @Override
    public void insert(Array2DInterface<TerminalChar> data, int x, int y) {
        buffers.get(getMemBufId()).insert(data, x, y);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    public void close() {
        terminal.exitPrivateMode();
        terminal = null;
    }

    @Override
    public boolean inBounds(int x, int y) {
      return new Recti(0, 0, width, height).contains(new Vector2i(x, y));
    }

    @Override
    public void insertCenterHorizontally(Array2D<TerminalChar> array, int y) {
        buffers.get(getMemBufId()).insertCenterHorizontally(array, y);
    }

    @Override
    public void insertCenterVertically(Array2D<TerminalChar> array, int x) {
        buffers.get(getMemBufId()).insertCenterVertically(array, x);
    }

    @Override
    public void insertCenter(Array2D<TerminalChar> array) {
        buffers.get(getMemBufId()).insertCenter(array);
    }
    
}
