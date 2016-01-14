package capstone2015;

import capstone2015.state.StateMachine;
import capstone2015.appstate.*;
import capstone2015.diagnostics.TimeStat;
import capstone2015.entity.EntityFactory;
import capstone2015.game.panel.DiagnosticsPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import java.util.function.Consumer;

public class Capstone2015 {
    
    private static boolean isCtrlPressed = false;
    private static boolean isAltPressed = false;
    private static boolean showDiagnostics = false;

    private static StateMachine appStates ;
    private static Screen screen;
    private static MessageBus messageBus;
    
    public static void main(String[] args) throws Exception {        
        appStates = new StateMachine();
        screen = new Screen();
        messageBus = new MessageBus();
        
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
                
                if(e.getKeyCode() == KeyEvent.VK_F1)
                    showDiagnostics = !showDiagnostics;
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
        appStates.pushState(new MainMenu(screen, messageBus));
       
        long lastClock = System.currentTimeMillis();

        while(!appStates.isEmpty()){
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
                        appStates.pushState(new IngameMenu(screen, messageBus));
                        break;
                    case PushHelpPageState:
                        appStates.pushState(new HelpPageState(screen, messageBus));
                        break;
                    case PushLaunchGameState:
                        appStates.pushState(new LaunchGameState(screen, messageBus));
                        break;
                    case GameWon:
                        appStates.pushState(new GameWonState(screen, messageBus));
                        break;
                    case LoadGame:
                        appStates.pushState(new Game(screen, messageBus, (String)m.getMsgObject()));
                        break;
                    case PushSelectGamesaveState:
                        appStates.pushState(new SelectSavegameState(screen, messageBus, (Consumer<String>)m.getMsgObject()));
                        break;
                    case PushUserTextInputState:
                        appStates.pushState(new UserTextInputState(screen, messageBus, (Consumer<String>)m.getMsgObject()));
                        break;
                    case QuitToDesktop:
                        appStates.terminateStates();
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
            appStates.tick((double)deltatime_msec / 1000.d);

            /****************************
             * Draw diagnostics window if toggled
             */
            if(showDiagnostics)
                screen.insert(DiagnosticsPanel.render(), -1, -1);
            
            Map<String, Long> time_stat_summary = TimeStat.getStateSummary();
            if(TimeStat.getElapsedTime() > 20000000){
                System.out.println("WARN: Stall occurred: " + TimeStat.getElapsedTime() / 1000000 + "msec.");
                System.out.println(time_stat_summary);
            }
            
            TimeStat.reset();
            
            /****************************
             * Draw the screen
             */
            screen.flip();

            /****************************
             * Wait until next cycle
             */
            TimeStat.enterState("OS");
            Thread.sleep(10);
            TimeStat.leaveState("OS");
        }
        
        screen.close();
        
        System.exit(0);
    }
}
