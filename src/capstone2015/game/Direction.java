package capstone2015.game;

import capstone2015.geom.Vec2i;

public enum Direction {
    NONE,
    LEFT,
    RIGHT,
    UP,
    DOWN;
    
    public Vec2i toVector(){
        switch(this){
            case LEFT:
                return new Vec2i(-1, 0);
            case RIGHT:
                return new Vec2i(1, 0);
            case UP:
                return new Vec2i(0, -1);
            case DOWN:
                return new Vec2i(0, 1);
            default:
                return new Vec2i(0, 0);
        }
    }
}
