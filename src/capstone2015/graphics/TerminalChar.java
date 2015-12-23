package capstone2015.graphics;

import java.awt.Color;

public class TerminalChar {
    private char    character;
    private Color   fgColor;
    private Color   bgColor;

    public TerminalChar(TerminalChar tc){
        this.character = tc.character;
        this.fgColor = tc.fgColor;
        this.bgColor = tc.bgColor;
    }
    
    public TerminalChar(char character, Color fgColor, Color bgColor){
        this.character = character;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }
    
    public TerminalChar(char character, Color fgColor){
        this(character, fgColor, Color.BLACK);
    }
    
    public TerminalChar(char character){
        this(character, Color.WHITE, Color.BLACK);
    }
    
    public TerminalChar(){
        this(' ', Color.WHITE, Color.BLACK);
    }
    
    public char getCharacter(){return this.character;}
    public Color getFGColor(){return this.fgColor;}
    public Color getBGColor(){return this.bgColor;}
    
    public TerminalChar setCharacter(char character){this.character = character; return this;}
    public TerminalChar setFGColor(Color fgColor){this.fgColor = fgColor; return this;}
    public TerminalChar setBGColor(Color bgColor){this.bgColor = bgColor; return this;}
    
    public boolean equals(TerminalChar rhs){
        return (   this.getCharacter() == rhs.getCharacter()
                && this.getFGColor() == rhs.getFGColor()
                && this.getBGColor() == rhs.getBGColor());
    }
}
