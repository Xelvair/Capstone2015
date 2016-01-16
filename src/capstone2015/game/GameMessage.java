package capstone2015.game;

public class GameMessage {
    public static final int KEY_EVENT = 1;                      //A Lanterna key event has happened
    public static final int PUSH_INGAME_MENU_STATE = 2;         //Ingame menu should be displayed
    public static final int PUSH_HELP_PAGE_STATE = 3;           //Help page should be displayed
    public static final int PUSH_LAUNCH_GAME_STATE = 4;         //Game launcher should be opened
    public static final int TERMINATE_GAME_STATE = 5;           //GameState needs to be terminated
    public static final int RECEIVED_DAMAGE = 6;                //Entity has received damage
    public static final int PUSH_NOTIFICATION = 7;              //New notification should be pushed
    public static final int ENTITY_MOVE = 8;                    //Entity wants to move
    public static final int ENTITY_MOVE_FAILED = 9;             //Entity was denied its move because of a collision
    public static final int INFLICT_DAMAGE = 10;                //When something is dealing damage on an area
    public static final int ATTEMPT_KEY_USAGE = 11;             //Key trying to open a door
    public static final int PICKUP = 12;                        //Entity wants to pick something up
    public static final int DROP = 13;                          //Entity wants to drop an item
    public static final int SPAWN_EFFECT = 14;                  //An effect entity should be spawned on the map
    public static final int SPAWN_ACTOR = 15;                   //An entity should be spawned on the map
    public static final int ATTEMPT_TAME = 16;                  //A taming attempt is being made
    public static final int TAMED = 17;                         //Creature tame was attempted
    public static final int TERMINATE = 18;                     //Entity has terminated
    public static final int GAME_WON = 19;                      //Game has been won
    public static final int SAVE_GAME = 20;                     //Game should be saved to file
    public static final int LOAD_GAME = 21;                     //Game should be loaded from file
    public static final int PUSH_SELECT_GAMESAVE_STATE = 22;    //Push the savegame loader
    public static final int PUSH_USER_TEXT_INPUT_STATE = 23;    //Push the user input reader
    public static final int QUIT_TO_DESKTOP = 24;               //Game has to close
}
