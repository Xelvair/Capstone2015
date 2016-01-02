package capstone2015.messaging;

public class Message {
    public enum Type{
        KeyEvent,               //A Lanterna key event has happened
        PushGameState,          //Game should be started / displayed
        PushIngameMenuState,    //Ingame menu should be displayed
        PushKeyPageState,       //Key page should be displayed
        PushLaunchGameState,    //Game launcher should be opened
        TerminateGameState,     //GameState needs to be terminated
        ReceivedDamage,         //Entity has received damage
        PushNotification,       //New notification should be pushed
        EntityMove,             //Entity wants to move
        EntityMoveFailed,       //Entity was denied its move because of a collision
        InflictDamage,          //When something is dealing damage on an area
        AttemptKeyUsage,        //Key trying to open a door
        Pickup,                 //Entity wants to pick something up
        Drop,                   //Entity wants to drop an item
        SpawnEffect,            //An effect entity should be spawned on the map
        SpawnActor,             //An entity should be spawned on the map
        Terminate,              //Entity has terminated
        GameWon,                //Game has been won
        SaveGame,               //Game should be saved to file
        LoadGame,               //Game should be loaded from file
        PushLoadSavegameState,  //Push the savegame loader
        QuitToDesktop;          //Game has to close
    }
    
    private Type type;
    private Object msgObject;
    
    public Message(Type type, Object msgObject){
        this.type = type;
        this.msgObject = msgObject;
    }
    public Message(Type type){
        this(type, null);
    }
    
    public Type getType(){
        return this.type;
    }
    
    public Object getMsgObject(){
        return this.msgObject;
    }
}
