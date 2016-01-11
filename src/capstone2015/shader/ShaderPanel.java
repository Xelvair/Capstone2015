package capstone2015.shader;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Array2D;
import java.util.Map;

public class ShaderPanel extends Panel{

    private Array2D<ShaderProgram> shaderPrograms;
    private Array2D<Map> characterData;
    
    public ShaderPanel(int x, int y) {
        super(x, y);
        shaderPrograms = new Array2D(x, y);
        shaderPrograms.fill(null);
        characterData = new Array2D(x, y);
        characterData.fill(null);
    }
    
    public void setShaderProgram(int x, int y, ShaderProgram program){
        shaderPrograms.set(x, y, program);
    }
    
    public void setCharacterData(int x, int y, Map<String, Object> data){
        characterData.set(x, y, data);
    }
    
    public ShaderProgram getShaderProgram(int x, int y){
        return shaderPrograms.get(x, y);
    }
    
    public Map<String, Object> getCharacterData(int x, int y){
        return characterData.get(x, y);
    }
    
    public Panel render(){
        Panel p = new Panel(width(), height());
        
        for(int i = 0; i < height(); ++i){
            for(int j = 0; j < width(); ++j){
                if(getShaderProgram(j, i) != null){
                    p.set(j, i, getShaderProgram(j, i).shade(get(j, i), getCharacterData(j, i)));
                } else {
                    p.set(j, i, new TerminalChar(get(j, i)));
                }
            }
        }
        
        return p;
    }
}
