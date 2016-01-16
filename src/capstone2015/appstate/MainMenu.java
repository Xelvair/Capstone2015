package capstone2015.appstate;

import capstone2015.game.GameMessage;
import capstone2015.state.State;
import capstone2015.game.panel.OptionPanel;
import capstone2015.game.panel.TitleScreenPanel;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;
import java.util.function.Consumer;

public class MainMenu extends State{

    private Screen screen;
    private MessageBus messageBus;

    private String[] options = {
        "Start Game",
        "Load Game",
        "Help/Legend",
        "Quit to Desktop"
    };
    private OptionPanel optionPanel;
    
    public MainMenu(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;

        OptionPanel.Config menu_config = new OptionPanel.Config();
        menu_config.bgColor = Color.BLACK;
        menu_config.borderColor = Color.BLACK;

        optionPanel = new OptionPanel(options, menu_config, 0);
    }
    
    private void draw(){
        Panel p_background = Panel.fillPanel(screen.width(), screen.height(), new TerminalChar());
        Panel p_title = TitleScreenPanel.render();
        Panel p_options = optionPanel.render();
        
        int menu_width = Math.max(p_title.width(), p_options.width());
        int menu_height = p_title.height() + p_options.height();
        
        Panel p_menu = new Panel(menu_width, menu_height);
        
        p_menu.insertCenter(Panel.fillPanel(p_menu.width(), p_menu.height(), new TerminalChar()));
        p_menu.insertCenterHorizontally(p_title, 0);
        p_menu.insertCenterHorizontally(p_options, p_title.height());
        
        screen.insertCenter(p_background);
        screen.insertCenter(p_menu);
    }
    
    private void handleKeyEvent(Key key){
        switch(key.getKind()){
            case ArrowUp:
                optionPanel.prevSelection();
                break;
            case ArrowDown:
                optionPanel.nextSelection();
                break;
            case Escape:
                terminate();
                break;
            case Enter:
                switch(optionPanel.getSelection()){
                    case 0:
                        messageBus.enqueue(new Message(GameMessage.PUSH_LAUNCH_GAME_STATE));
                        break;
                    case 1:
                        Consumer<String> callback_func = (String s) -> {
                            if(s == null)
                                return;

                            messageBus.enqueue(new Message(GameMessage.LOAD_GAME, s));
                        };
                        messageBus.enqueue(new Message(GameMessage.PUSH_SELECT_GAMESAVE_STATE, callback_func));
                        break;
                    case 2:
                        messageBus.enqueue(new Message(GameMessage.PUSH_HELP_PAGE_STATE));
                        break;
                    case 3:
                        terminate();
                        break;
                }
                break;
        }
    }
    
    @Override
    protected void onTick(double timeDelta) {
        if(isFocus()){
            for(Message m : messageBus){
                switch(m.getType()){
                    case GameMessage.KEY_EVENT:
                        handleKeyEvent((Key)m.getMsgObject());
                        break;
                }
            }
        }
        
        draw();
        
    }
    
}
