package capstone2015.game;

import capstone2015.geom.Vector2i;

public enum Direction {
    NONE,
    LEFT,
    RIGHT,
    UP,
    DOWN;
    
    public Vector2i toVector(){
        switch(this){
            case LEFT:
                return new Vector2i(-1, 0);
            case RIGHT:
                return new Vector2i(1, 0);
            case UP:
                return new Vector2i(0, -1);
            case DOWN:
                return new Vector2i(0, 1);
            default:
                return new Vector2i(0, 0);
        }
    }
}
