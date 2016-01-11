package capstone2015.shader;

import capstone2015.graphics.TerminalChar;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

public class ShaderProgram {
    private BiFunction<TerminalChar, Map<String, Object>, TerminalChar> shaderProc;
    private Map<String, Object> uniforms = new TreeMap();
    
    public void setUniform(String name, Object val){
        uniforms.put(name, val);
    }
    
    public void setShaderProc(BiFunction<TerminalChar, Map<String, Object>, TerminalChar> shaderProc){
        this.shaderProc = shaderProc;
    }
    
    public TerminalChar shade(TerminalChar in){
        return shade(in, null);
    }
    public TerminalChar shade(TerminalChar in, Map<String, Object> data){
        if(shaderProc == null){
            System.out.println("Failed to run shader: no procedure set!");
            return null;
        }
        
        Map char_data = new TreeMap();
        if(data != null)
            char_data.putAll(data);
        if(uniforms != null)
            char_data.putAll(uniforms);
        
        return shaderProc.apply(in, char_data);
    }
}
