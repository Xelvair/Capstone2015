package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.io.File;
import java.util.ArrayList;

import static capstone2015.messaging.Message.Type.PushGameState;

public class LoadSavegameState extends AppState{

    private Screen screen;
    private MessageBus messageBus;
    private String[] savegames;
    private OptionPanel optionPanel;

    public LoadSavegameState(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;

        File map_folder = new File("./savegame/");

        /*Because toArray is bad and Java generics are bad...*/
        ArrayList<String> map_list = new ArrayList<>();
        for(File map_file : map_folder.listFiles()){
            String map_name = map_file.getName();
            map_name = map_name.substring(0, map_name.lastIndexOf('.'));
            map_list.add(map_name);
        }

        savegames = new String[map_list.size()];

        for(int i = 0; i < map_list.size(); i++){
            savegames[i] = map_list.get(i);
        }

        OptionPanel.Config panel_cfg = new OptionPanel.Config();
        panel_cfg.heading = "Select a Save File";

        optionPanel = new OptionPanel(savegames, panel_cfg, 0);
    }

    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case KeyEvent:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowUp:
                            optionPanel.prevSelection();
                            break;
                        case ArrowDown:
                            optionPanel.nextSelection();
                            break;
                        case Enter:
                            String savegame_path = "./savegame/" + savegames[optionPanel.getSelection()] + ".properties";
                            messageBus.enqueue(new Message(PushGameState, savegame_path));
                            terminate();
                            break;
                        case Escape:
                            terminate();
                            break;
                    }
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
