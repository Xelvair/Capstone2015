package capstone2015.graphics;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class Tile {
    private char    character;
    private Color   fgColor;
    private Color   bgColor;

    public Tile(char character, Color fgColor, Color bgColor){
        this.character = character;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }
    
    public Tile(char character, Color fgColor){
        this(character, fgColor, Color.BLACK);
    }
    
    public Tile(char character){
        this(character, Color.WHITE, Color.BLACK);
    }
    
    public Tile(){
        this(' ', Color.WHITE, Color.BLACK);
    }
    
    public char getCharacter(){return this.character;}
    public Color getFGColor(){return this.fgColor;}
    public Color getBGColor(){return this.bgColor;}
}
