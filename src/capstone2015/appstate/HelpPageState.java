package capstone2015.appstate;

import capstone2015.state.State;
import capstone2015.entity.EntityFactory;
import capstone2015.game.GameMessage;
import capstone2015.game.panel.ControlsPanel;
import capstone2015.game.panel.HelpPanel;
import capstone2015.game.panel.HowToPlayPanel;
import capstone2015.game.panel.HudExplanationPanel;
import capstone2015.game.panel.KeyPanels;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.awt.*;
import java.util.LinkedList;

public class HelpPageState extends State {
    private Screen screen;
    private MessageBus messageBus;
    private HelpPanel helpPanel = new HelpPanel();

    public HelpPageState(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;

        int[] items_in_legend = new int[]{
                EntityFactory.ID_WALL,
                EntityFactory.ID_FAKE_WALL,
                EntityFactory.ID_WATER,
                EntityFactory.ID_WOOD_FLOOR,
                EntityFactory.ID_FERN,
                EntityFactory.ID_ENTRY,
                EntityFactory.ID_EXIT,
                EntityFactory.ID_BONFIRE,
                EntityFactory.ID_RATTLESNAKE,
                EntityFactory.ID_PLAYER,
                EntityFactory.ID_HEALTH_POTION,
                EntityFactory.ID_SWORD,
                EntityFactory.ID_BOW,
                EntityFactory.ID_ARROW,
                EntityFactory.ID_MAGIC_WAND,
                EntityFactory.ID_TAMING_SCROLL
        };

        LinkedList<Panel> legend_panels = new LinkedList<>();
        for(int entity_id : items_in_legend){
            legend_panels.add(KeyPanels.render(entity_id));
        }

        Panel legend_panel = Panel.concatVertically(legend_panels);

        helpPanel.addPanel("How to Play", HowToPlayPanel.render());
        helpPanel.addPanel("Controls", ControlsPanel.render());
        helpPanel.addPanel("HUD", HudExplanationPanel.render());
        helpPanel.addPanel("Legend", legend_panel);
    }

    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case GameMessage.KEY_EVENT:
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case ArrowLeft:
                            helpPanel.prevPanel();
                            break;
                        case ArrowRight:
                            helpPanel.nextPanel();
                            break;
                        case ArrowUp:
                            helpPanel.scrollUp();
                            break;
                        case ArrowDown:
                            helpPanel.scrollDown();
                            break;
                        case Escape:
                            terminate();
                            break;
                    }
                    break;
            }
        }

        screen.insertCenter(helpPanel.render());
    }
}
