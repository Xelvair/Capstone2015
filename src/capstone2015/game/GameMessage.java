package capstone2015.game;

public class GameMessage {
    public static final int KEY_EVENT = 1;                      //A Lanterna key event has happened
    public static final int PUSH_INGAME_MENU_STATE = 2;         //Ingame menu should be displayed
    public static final int PUSH_HELP_PAGE_STATE = 3;           //Help page should be displayed
    public static final int PUSH_LAUNCH_GAME_STATE = 4;         //Game launcher should be opened
    public static final int PUSH_ALERT_STATE = 5;               //Alert state should be pushed
    public static final int TERMINATE_GAME_STATE = 6;           //GameState needs to be terminated
    public static final int RECEIVED_DAMAGE = 7;                //Entity has received damage
    public static final int PUSH_NOTIFICATION = 8;              //New notification should be pushed
    public static final int ENTITY_MOVE = 9;                    //Entity wants to move
    public static final int ENTITY_MOVE_FAILED = 10;             //Entity was denied its move because of a collision
    public static final int INFLICT_DAMAGE = 11;                //When something is dealing damage on an area
    public static final int ATTEMPT_KEY_USAGE = 12;             //Key trying to open a door
    public static final int PICKUP = 13;                        //Entity wants to pick something up
    public static final int DROP = 14;                          //Entity wants to drop an item
    public static final int SPAWN_EFFECT = 15;                  //An effect entity should be spawned on the map
    public static final int SPAWN_ACTOR = 16;                   //An entity should be spawned on the map
    public static final int ATTEMPT_TAME = 17;                  //A taming attempt is being made
    public static final int TAMED = 18;                         //Creature tame was attempted
    public static final int PICKED_UP = 19;                     //An item was picked up
    public static final int TERMINATE = 20;                     //Entity has terminated
    public static final int GAME_WON = 21;                      //Game has been won
    public static final int GAME_LOST = 22;                     //Game is lost
    public static final int SAVE_GAME = 23;                     //Game should be saved to file
    public static final int LOAD_GAME = 24;                     //Game should be loaded from file
    public static final int PUSH_SELECT_GAMESAVE_STATE = 25;    //Push the savegame loader
    public static final int PUSH_USER_TEXT_INPUT_STATE = 26;    //Push the user input reader
    public static final int QUIT_TO_DESKTOP = 27;               //Game has to close
}
