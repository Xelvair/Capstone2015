package mapmaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapMaker {
    private static final Color COLOR_WALL = new Color(0, 0, 0);
    private static final Color COLOR_ENTRY = new Color(0, 0, 255);
    private static final Color COLOR_EXIT = new Color(0, 255, 0);
    private static final Color COLOR_BONFIRE = new Color(255, 165, 0);
    private static final Color COLOR_RATTLESNAKE = new Color(0, 100, 0);
    private static final Color COLOR_KEY = new Color(255, 255, 0);
    private static final Color COLOR_FLOOR = new Color(255, 255, 255);
    private static final Color COLOR_PLAYER = new Color(0, 255, 255);
    private static final Color COLOR_HEALTH_POTION = new Color(255, 100, 100);
    private static final Color COLOR_SWORD = new Color(160, 160, 160);
    private static final Color COLOR_BOW = new Color(120, 120, 120);
    private static final Color COLOR_ARROW = new Color(90, 90, 90);
    private static final Color COLOR_WATER = new Color(50, 100, 255);
    private static final Color COLOR_WOOD_FLOOR = new Color(100, 50, 20);
    private static final Color COLOR_FAKE_WALL = new Color(30, 30, 30);
    
    public static final int ID_WALL = 0;
    public static final int ID_ENTRY = 1;
    public static final int ID_EXIT = 2;
    public static final int ID_BONFIRE = 3;
    public static final int ID_RATTLESNAKE = 4;
    public static final int ID_KEY = 5;
    public static final int ID_FLOOR = 6;
    public static final int ID_PLAYER = 7;
    public static final int ID_HEALTH_POTION = 8;
    public static final int ID_SWORD = 9;
    public static final int ID_BOW = 10;
    public static final int ID_ARROW = 11;
    public static final int ID_WATER = 12;
    public static final int ID_WOOD_FLOOR = 13;
    public static final int ID_FAKE_WALL = 14;
    
    private static TreeMap<Color, Integer> colorCodes = new TreeMap(new Comparator<Color>(){
        @Override
        public int compare(Color o1, Color o2) {
            int o1sum =   o1.getRed() * 255 * 255
                        + o1.getGreen() * 255
                        + o1.getBlue();
            int o2sum =   o2.getRed() * 255 * 255
                        + o2.getGreen() * 255
                        + o2.getBlue();
            
            if(o1sum > o2sum){
                return 1;
            } else if(o1sum < o2sum){
                return -1;
            } else {
                return 0;
            }
        }
    });
    
    private static Color intToColor(int color){
        return new Color(color );
    };
    
    public static void main(String[] args) {
        String map_name = "";
        
        if(args.length <= 0){
            Scanner s = new Scanner(System.in);
            System.out.print("Enter map file name:");
            map_name = s.next();
        } else {
            map_name = args[0];
        }
        
        MapMaker.colorCodes.put(COLOR_WALL, ID_WALL);
        MapMaker.colorCodes.put(COLOR_ENTRY, ID_ENTRY);
        MapMaker.colorCodes.put(COLOR_EXIT, ID_EXIT);
        MapMaker.colorCodes.put(COLOR_BONFIRE, ID_BONFIRE);
        MapMaker.colorCodes.put(COLOR_RATTLESNAKE, ID_RATTLESNAKE);
        MapMaker.colorCodes.put(COLOR_KEY, ID_KEY);
        MapMaker.colorCodes.put(COLOR_FLOOR, ID_FLOOR);
        MapMaker.colorCodes.put(COLOR_PLAYER, ID_PLAYER);
        MapMaker.colorCodes.put(COLOR_HEALTH_POTION, ID_HEALTH_POTION);
        MapMaker.colorCodes.put(COLOR_SWORD, ID_SWORD);
        MapMaker.colorCodes.put(COLOR_BOW, ID_BOW);
        MapMaker.colorCodes.put(COLOR_ARROW, ID_ARROW);
        MapMaker.colorCodes.put(COLOR_WATER, ID_WATER);
        MapMaker.colorCodes.put(COLOR_WOOD_FLOOR, ID_WOOD_FLOOR);
        MapMaker.colorCodes.put(COLOR_FAKE_WALL, ID_FAKE_WALL);

        
        File mapfile = new File(map_name);
        BufferedImage img = null;
        try{
             img = ImageIO.read(mapfile);
        } catch(IOException e){
            System.out.println(e.getMessage());
            return;
        }
        
        Properties map_props = new Properties();
        
        map_props.setProperty("Width", Integer.toString(img.getWidth()));
        map_props.setProperty("Height", Integer.toString(img.getHeight()));
        
        for(int i = 0; i < img.getHeight(); ++i){
            for(int j = 0; j < img.getWidth(); j++){
                Color c = new Color(img.getRGB(j, i));
                if(colorCodes.containsKey(c)){
                    int tile_id = colorCodes.get(c);
                    
                    switch(tile_id){
                        default:
                            map_props.setProperty("" + j + "," + i, Integer.toString(tile_id));
                            break;
                    }
                } else {
                    map_props.setProperty("" + j + "," + i, Integer.toString(ID_FLOOR));
                }
            }
        }
        
        File out_file = new File(map_name.substring(0, map_name.lastIndexOf(".")) + ".properties");
        FileOutputStream out_file_stream = null;
        try {
             out_file_stream = new FileOutputStream(out_file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        
        try {
            map_props.store(out_file_stream, null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        
        System.out.println("Map file created!");
        
    }
}
