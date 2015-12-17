package capstone2015.graphics;

import capstone2015.util.Array2D;
import capstone2015.util.Array2DInterface;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;

public class Screen implements Array2DInterface<Tile>{

    private Terminal terminal;
    private final ArrayList<Array2D<Tile>> buffers = new ArrayList<>();
    private int width;
    private int height;
    private int membuf = 0;
    
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
                buffers.get(getScreenBufId()).set(j, i, new Tile());
            }
        }
    }
    
    private void onTerminalResize(){
        reallocBuffers();
    }

    private void reallocBuffers(){
        buffers.clear();
        this.width = terminal.getTerminalSize().getColumns();
        this.height = terminal.getTerminalSize().getRows();
        buffers.add(new Array2D<>(width, height)); //buf1
        buffers.add(new Array2D<>(width, height)); //buf2
    }
    
    public void flip(){      
        membuf = (membuf + 1) % 2; //switch buffers
        
        terminal.clearScreen();
        
        //TODO: make more efficient
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                Tile tile = buffers.get(getScreenBufId()).get(j, i);
                
                if(tile != null){
                    terminal.moveCursor(j, i);
                    terminal.applyForegroundColor(tile.getFGColor());
                    terminal.applyBackgroundColor(tile.getBGColor());
                    terminal.putCharacter(tile.getCharacter());
                }
            }
        }
    }
    private int getScreenBufId(){return (membuf + 1) % 2;}
    private int getMemBufId(){return membuf;}
    private Tile getScreenBuf(int x, int y){
        return buffers.get(getScreenBufId()).get(x, y);
    }
    private Tile getMemBuf(int x, int y){
        return buffers.get(getMemBufId()).get(x, y);
    }
    private void setScreenBuf(int x, int y, Tile tile){
        buffers.get(getScreenBufId()).set(x, y, tile);
    }
    private void setMemBuf(int x, int y, Tile tile){
        buffers.get(getMemBufId()).set(x, y, tile);
    }
    
    @Override
    public Tile get(int x, int y) {
        return getScreenBuf(x, y);
    }

    @Override
    public void set(int x, int y, Tile tile) {
        setMemBuf(x, y, tile);
    }

    @Override
    public void insert(int x, int y, Array2DInterface<Tile> data) {
        buffers.get(getMemBufId()).insert(x, y, data);
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
    
}
