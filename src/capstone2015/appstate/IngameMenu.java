package capstone2015.appstate;

import capstone2015.game.GameMessage;
import capstone2015.state.State;
import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.util.function.Consumer;

public class IngameMenu extends State{
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
    public void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case GameMessage.QUIT_TO_DESKTOP:
                    terminate();
                    break;
                case GameMessage.KEY_EVENT:
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

                                        messageBus.enqueue(new Message(GameMessage.SAVE_GAME, s));
                                    };
                                    messageBus.enqueue(new Message(GameMessage.PUSH_USER_TEXT_INPUT_STATE, callback_func));
                                    break;
                                }
                                case 2:
                                {
                                    Consumer<String> callback_func = (String s) -> {
                                        if (s == null)
                                            return;

                                        messageBus.enqueue(new Message(GameMessage.LOAD_GAME, s));
                                        terminate();
                                    };
                                    messageBus.enqueue(new Message(GameMessage.PUSH_SELECT_GAMESAVE_STATE, callback_func));
                                    break;
                                }
                                case 3:
                                    messageBus.enqueue(new Message(GameMessage.PUSH_HELP_PAGE_STATE));
                                    break;
                                case 4:
                                    messageBus.enqueue(new Message(GameMessage.TERMINATE_GAME_STATE));
                                    terminate();
                                    break;
                                case 5:
                                    messageBus.enqueue(new Message(GameMessage.QUIT_TO_DESKTOP));
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
}
