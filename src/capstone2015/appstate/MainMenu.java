package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.game.panel.TitlePanel;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class MainMenu extends AppState{

    private Screen screen;
    private MessageBus messageBus;
    
    private int selection = 0;
    private String[] options = {
        "Start Game",
        "Save Game",
        "Load Game",
        "Key",
        "Quit to Desktop"
    };
    
    public MainMenu(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;
    }
    
    private void draw(){
        OptionPanel.Config menu_config = new OptionPanel.Config();
        menu_config.bgColor = Color.BLACK;
        menu_config.borderColor = Color.BLACK;
        
        Panel p_background = Panel.fillPanel(screen.width(), screen.height(), new TerminalChar());
        Panel p_title = TitlePanel.render();
        Panel p_options = OptionPanel.render(options, menu_config, selection);
        
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
                selection += options.length - 1;
                selection %= options.length;
                break;
            case ArrowDown:
                ++selection;
                selection %= options.length;
                break;
            case Escape:
                terminate();
                break;
            case Enter:
                switch(selection){
                    case 0:
                        messageBus.enqueue(new Message(PushGameState, "level.properties"));
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        messageBus.enqueue(new Message(PushKeyPageState));
                        break;
                    case 4:
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
                    case KeyEvent:
                        handleKeyEvent((Key)m.getMsgObject());
                        break;
                }
            }
        }
        
        draw();
        
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
