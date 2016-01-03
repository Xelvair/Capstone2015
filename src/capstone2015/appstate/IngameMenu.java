package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.util.function.Consumer;

public class IngameMenu extends AppState{
    private Screen screen;
    private MessageBus messageBus;
    private final String[] options = {
        "Resume",
        "Save Game",
        "Load Game",
        "Help/Legend",
        "Quit to Main Menu",
        "Quit to Desktop"
    };
    private OptionPanel optionPanel = new OptionPanel(options, new OptionPanel.Config(), 0);
    
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
                    if(isBlur()){
                        break; //Dont check key events if we're not the top state
                    }
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowUp:
                            optionPanel.prevSelection();
                            break;
                        case ArrowDown:
                            optionPanel.nextSelection();
                            break;
                        case Enter:
                            switch(optionPanel.getSelection()){
                                case 0:
                                    terminate();
                                    break;
                                case 1: {
                                    Consumer<String> callback_func = (String s) -> {
                                        if (s == null)
                                            return;

                                        //Amend string for full qualified URI
                                        s = "./savegame/" + s + ".properties";

                                        messageBus.enqueue(new Message(Message.Type.SaveGame, s));
                                    };
                                    messageBus.enqueue(new Message(Message.Type.PushUserTextInputState, callback_func));
                                    break;
                                }
                                case 2:
                                {
                                    Consumer<String> callback_func = (String s) -> {
                                        if (s == null)
                                            return;

                                        messageBus.enqueue(new Message(Message.Type.LoadGame, s));
                                        terminate();
                                    };
                                    messageBus.enqueue(new Message(PushSelectGamesaveState, callback_func));
                                    break;
                                }
                                case 3:
                                    messageBus.enqueue(new Message(Message.Type.PushHelpPageState));
                                    break;
                                case 4:
                                    messageBus.enqueue(new Message(TerminateGameState));
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
        
        screen.insertCenter(optionPanel.render());
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
