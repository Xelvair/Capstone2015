package capstone2015.game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TutorialTracker extends Properties{    
    private final String fileName = "tutorial_track.properties";
    
    public TutorialTracker(){
        boolean file_loaded = false;
        try{
            FileInputStream tutorial_tracker_file;
            tutorial_tracker_file = new FileInputStream(fileName);
            try {
                this.load(tutorial_tracker_file);
                file_loaded = true;
            } catch (IOException ex) {
                System.err.println("Failed to load tutorial tracker file.");
            }
        } catch(FileNotFoundException e){
            System.err.println("Failed to load tutorial tracker file.");
        }
        
        if(!file_loaded){
            this.setProperty("PickedUpWeapon", "0");
            this.setProperty("PickedUpKey", "0");
            this.setProperty("PickedUpBow", "0");
            this.setProperty("PickedUpArrow", "0");
            this.setProperty("PickedUpConsumable", "0");
            this.setProperty("PickedUpTamingScroll", "0");
        }
    }
    
    public boolean hasDone(String entry){
        return this.getProperty(entry).equals("1");
    }
    
    public void setDone(String entry){
        this.setProperty(entry, "1");
    }
    
    public void store(){
        FileOutputStream tutorial_tracker_file;
        try {
            tutorial_tracker_file = new FileOutputStream(fileName);
            
            try {
                this.store(tutorial_tracker_file, null);
            } catch (IOException ex) {
                System.err.println("Failed to store tutorial tracker file.");
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to store tutorial tracker file.");
        }
    }
}
