package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.*;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.io.File;
import java.util.ArrayList;

public class LaunchGameState extends AppState{

    private Screen screen;
    private MessageBus messageBus;
    
    private String[] maps;
    private int mapCount;
    private OptionPanel optionPanel;
    
    public LaunchGameState(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;
        
        File map_folder = new File("./maps/");
        
        /*Because toArray is bad and Java generics are bad...*/
        ArrayList<String> map_list = new ArrayList<>();
        for(File map_file : map_folder.listFiles()){
            String map_name = map_file.getName();
            map_name = map_name.substring(0, map_name.lastIndexOf('.'));
            map_list.add(map_name);
        }
        
        maps = new String[map_list.size()];
        
        for(int i = 0; i < map_list.size(); i++){
            maps[i] = map_list.get(i);
        }
        
        mapCount = map_list.size();

        OptionPanel.Config options_cfg = new OptionPanel.Config();
        options_cfg.heading = "Select a map";

        optionPanel = new OptionPanel(maps, options_cfg, 0);
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
                String map_path = "./maps/" + maps[optionPanel.getSelection()] + ".properties";
                messageBus.enqueue(new Message(LoadGame, map_path));
                terminate();
                break;
        }
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case KeyEvent:
                    handleKeyEvent((Key)m.getMsgObject());
                    break;
            }
        }
        

        
        Panel p_options = optionPanel.render();
        
        screen.insertCenter(p_options);
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
