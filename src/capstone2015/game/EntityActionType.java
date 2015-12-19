package capstone2015.game;

public enum EntityActionType {
    NONE,
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_DOWN,
    PICKUP,
    TERMINATE;
    
    public boolean isMoveType(){
        return (   this == MOVE_LEFT 
                || this == MOVE_RIGHT 
                || this == MOVE_UP 
                || this == MOVE_DOWN);
    }
    
    public Direction toDirection(){
        switch(this){
            case MOVE_LEFT:
                return Direction.LEFT;
            case MOVE_RIGHT:
                return Direction.RIGHT;
            case MOVE_UP:
                return Direction.UP;
            case MOVE_DOWN:
                return Direction.DOWN;
            default:
                System.out.println("ERR: couldnt turn action type into direction.");
                return Direction.NONE;
        }
    }
}
