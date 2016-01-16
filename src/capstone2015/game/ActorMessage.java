package capstone2015.game;

public class ActorMessage {
    public static final int NEW_CLOSEST_ENEMY = 1; //A new enemy is now closest to the actor
    public static final int CLOSEST_ENEMY_TICK = 2; //Ticking the event handler with the current closest enemy
    public static final int ACTOR_ENTERED_VISION = 3; //Another actor entered the actors vision
    public static final int ACTOR_LEFT_VISION = 4; //Another actor left the actors vision
    public static final int ACTOR_ENTERED_INNER_STRAY = 5; //Another actor entered the inner stray range
    public static final int ACTOR_ENTERED_OUTER_STRAY = 6; //Another actor entered the outer stray range
    public static final int ACTOR_LEFT_INNER_STRAY = 7; //Another actor left the inner stray range
    public static final int ACTOR_LEFT_OUTER_STRAY = 8; //Another actor left the outer stray range
    public static final int SELF_LEFT_OUTER_STRAY = 9; //Actor left his outer stray range
    public static final int SELF_ENTERED_OUTER_STRAY = 10; //Actor entered his outer stray range
    public static final int SELF_LEFT_INNER_STRAY = 11; //Actor left his inner stray range
    public static final int SELF_ENTERED_INNER_STRAY = 12; //Actor entered his inner stray range
}
