package capstone2015;

import capstone2015.appstate.*;
import capstone2015.geom.*;
import java.util.LinkedList;

public class Capstone2015 {
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        
        Vector2i vec1 = new Vector2i(3, 5);
        Vector2i vec2 = new Vector2i(7, 12);
        
        LinkedList<Vector2i> line = Geom.lineToPoints(
            new Vector2i(3, 2), 
            new Vector2i(7, 8)
        );
        
        for(Vector2i vec : line){
            System.out.println(vec);
        }
        
        while(!asm.isEmpty()){
            Thread.sleep(1000);
        }
    }
}
