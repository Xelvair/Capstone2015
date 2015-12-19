package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateManager;
import capstone2015.appstate.Game;
import capstone2015.appstate.IngameMenu;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        Screen screen = new Screen();
        MessageBus messageBus = new MessageBus();

        asm.pushState(new Game(screen, messageBus, "level.properties")); 
       
        long lastClock = System.currentTimeMillis();
        while(!asm.isEmpty()){
            /****************************
             * Calculate exact time since last cycle
             */
            long deltatime_msec = System.currentTimeMillis() - lastClock;
            lastClock = System.currentTimeMillis();
            
            /****************************
             * Handle messages on the buffer
             */
            for(Message m : messageBus){
                switch(m.getType()){
                    case PushIngameMenuState:
                        asm.pushState(new IngameMenu(screen, messageBus));
                        break;
                    default:
                        break;
                }
            }
            /****************************
             * Poll for key events and refresh message bus,
             * essentially loading the key events from this cycle
             * plus the game events from the previous cycle
             */
            Key key;
            while((key = screen.readInput()) != null){
                messageBus.enqueue(new Message(Message.Type.KeyEvent, key));
            }
            messageBus.refresh();
            
            /****************************
             * Tick the state machine
             */
            asm.tick((double)deltatime_msec / 1000.d);
            /****************************
             * Draw the screen
             */
            screen.flip();
            /****************************
             * Wait until next cycle
             */
            Thread.sleep(FRAME_TIME);
        }
        
        screen.close();
    }
}
