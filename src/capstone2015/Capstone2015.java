package capstone2015;

import capstone2015.appstate.*;
import capstone2015.entity.EntityFactory;
import capstone2015.game.RangerMapTraversableAdapter;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.function.Consumer;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    private static boolean isCtrlPressed = false;
    private static boolean isAltPressed = false;

    public static void main(String[] args) throws Exception {        
        AppStateManager asm = new AppStateManager();
        Screen screen = new Screen();
        MessageBus messageBus = new MessageBus();
        
        /****************************
         * Fixing lanterna's not working CTRL and ALT detection
         */
        SwingTerminal terminal = screen.getTerminal();
        terminal.getJFrame().addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)
                    isCtrlPressed = true;
                
                if(e.getKeyCode() == KeyEvent.VK_ALT)
                    isAltPressed = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)
                    isCtrlPressed = false;
                
                if(e.getKeyCode() == KeyEvent.VK_ALT)
                    isAltPressed = true;
            }
            
        });

        /****************************
         * Link the MessageBus to the entity
         * factory for simplified entiy creation
         */
        EntityFactory.setMessageBus(messageBus);

        /****************************
         * Start with the main menu
         */
        asm.pushState(new MainMenu(screen, messageBus));
       
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
                    case PushHelpPageState:
                        asm.pushState(new HelpPageState(screen, messageBus));
                        break;
                    case PushLaunchGameState:
                        asm.pushState(new LaunchGameState(screen, messageBus));
                        break;
                    case GameWon:
                        asm.pushState(new GameWonState(screen, messageBus));
                        break;
                    case LoadGame:
                        asm.pushState(new Game(screen, messageBus, (String)m.getMsgObject()));
                        break;
                    case PushSelectGamesaveState:
                        asm.pushState(new SelectSavegameState(screen, messageBus, (Consumer<String>)m.getMsgObject()));
                        break;
                    case PushUserTextInputState:
                        asm.pushState(new UserTextInputState(screen, messageBus, (Consumer<String>)m.getMsgObject()));
                        break;
                    case QuitToDesktop:
                        asm.terminateStates();
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
                /************************
                 * Let me just fix Lanterna... :/
                 */
                Key fixed_key;
                if(key.getKind() != Key.Kind.NormalKey)
                    fixed_key = new Key(key.getKind(), isCtrlPressed, isAltPressed);
                else
                    fixed_key = new Key(key.getCharacter(), isCtrlPressed, isAltPressed);
                
                /************************
                 * Enqueue fixed key event
                 */
                messageBus.enqueue(new Message(Message.Type.KeyEvent, fixed_key));
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
