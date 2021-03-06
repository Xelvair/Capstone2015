package capstone2015.messaging;

import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;

public class SpawnEffectParams {
    public TerminalChar represent;
    public double duration;
    public Vec2i pos;
    
    public SpawnEffectParams(){}
    
    public SpawnEffectParams(SpawnEffectParams other){
        represent = other.represent;
        duration = other.duration;
        pos = other.pos;
    }
}
