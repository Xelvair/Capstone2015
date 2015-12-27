package capstone2015.game;

public enum VisionType {
    Visible,
    Revealed,
    Invisible;
    
    public boolean tileVisible(){
        return (this == Visible || this == Revealed);
    }
    
    public boolean entitiesVisible(){
        return (this == Visible);
    }
}
