package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class IngameMenu extends AppState{
    private Screen screen;
    private MessageBus messageBus;
    private final String[] options = {
        "Resume",
        "Save",
        "Load",
        "Key",
        "Quit to Main Menu",
        "Quit to Desktop"
    };
    private int selection = 0;
    
    public IngameMenu(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case QuitToDesktop:
                    terminate();
                    break;
                case KeyEvent:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowUp:
                            selection += options.length - 1;
                            selection %= options.length;
                            break;
                        case ArrowDown:
                            ++selection;
                            selection %= options.length;
                            break;
                        case Enter:
                            switch(selection){
                                case 0:
                                    terminate();
                                    break;
                                case 1:
                                    terminate();
                                    break;
                                case 2:
                                    terminate();
                                    break;
                                case 3:
                                    terminate();
                                    break;
                                case 4:
                                    terminate();
                                    break;
                                case 5:
                                    messageBus.enqueue(new Message(Message.Type.QuitToDesktop));
                                    break;
                            }
                            break;
                        case Escape:
                            terminate();
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        
        screen.insertCenter(OptionPanel.render(options, selection));
    }

    @Override
    protected void onEvent(AppStateEvent event) {
        switch(event){
            case TERMINATE:
                terminate();
                break;
        }
    }
    
}
