package capstone2015.appstate;

import capstone2015.game.panel.OptionPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class SelectSavegameState extends AppState{

    private Screen screen;
    private MessageBus messageBus;
    private String[] savegames;
    private String[] savegames_represent;
    private OptionPanel optionPanel;
    private Consumer<String> callbackFunc;

    public SelectSavegameState(Screen screen, MessageBus messageBus, Consumer<String> callbackFunc){
        this.screen = screen;
        this.messageBus = messageBus;
        this.callbackFunc = callbackFunc;

        File map_folder = new File("./savegame/");

        /*Because toArray is bad and Java generics are bad...*/
        ArrayList<String> map_list = new ArrayList<>();
        for(File map_file : map_folder.listFiles()){
            String map_name = map_file.getAbsolutePath();
            map_list.add(map_name);
        }

        savegames = new String[map_list.size()];
        savegames_represent = new String[map_list.size()];

        for(int i = 0; i < map_list.size(); i++){
            savegames[i] = map_list.get(i);

            FileInputStream save_file;
            try {
                save_file = new FileInputStream(map_list.get(i));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            Properties props = new Properties();
            try {
                props.load(save_file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            long store_time = Long.parseLong(props.getProperty("store_timestamp"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            String filename_short = savegames[i].substring(savegames[i].lastIndexOf("/") + 1, savegames[i].lastIndexOf("."));

            savegames_represent[i] = filename_short + " - " + props.getProperty("loaded_map_name") + " - " + sdf.format(new Date(store_time));

        }

        OptionPanel.Config panel_cfg = new OptionPanel.Config();
        panel_cfg.heading = "Select a Save File";

        optionPanel = new OptionPanel(savegames_represent, panel_cfg, 0);
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
                            String savegame_path = savegames[optionPanel.getSelection()];
                            callbackFunc.accept(savegame_path);
                            terminate();
                            break;
                        case Escape:
                            callbackFunc.accept(null);
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
