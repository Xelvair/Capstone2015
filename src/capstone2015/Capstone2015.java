package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateEvent;
import capstone2015.appstate.AppStateManager;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        asm.pushState(new AppState(){

            @Override
            protected void onTick(double timeDelta) {
                System.out.println("tick");
            }

            @Override
            protected void onEvent(AppStateEvent event) {
                System.out.println("event: " + event.toString());
            }
            
        });
        Terminal terminal = TerminalFacade.createSwingTerminal();
        terminal.enterPrivateMode();
        while(!asm.isEmpty()){
            terminal.clearScreen();
            asm.tick(0.f);
            Thread.sleep(FRAME_TIME);
        }
        terminal.exitPrivateMode();
    }
}
