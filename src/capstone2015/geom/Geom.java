package capstone2015.geom;

import java.util.HashSet;
import java.util.LinkedList;

public class Geom {
    /********
     * Generate points on a line with bresenham's algorithm.
     * Guarantees that the line is only 1 unit thick on every segment
     * Imagine wanting to draw a line between two points (3, 2) and (7, 8),
     * The algorithm will generate the points as follows:
     *   123456789
     *  +---------
     * 1|  
     * 2|  X
     * 3|   X
     * 4|   X
     * 5|    X
     * 6|     X
     * 7|     X
     * 8|      X
     * 9|
     * @param start
     * @param end
     * @return 
     */
    public static LinkedList<Vec2i> lineToPoints(
        Vec2i start, 
        Vec2i end
    ){
        /**
         * Determine the longer component of the vector
         * diff = end - start
         */
        Vec2i diff = end.translate(start.invert());
        
        
        int x_sign = (diff.getX() == 0 ? 0 : diff.getX() / Math.abs(diff.getX()));
        int y_sign = (diff.getY() == 0 ? 0 : diff.getY() / Math.abs(diff.getY()));
        /**
         * Generate points along longer component
         */
        LinkedList<Vec2i> linePoints = new LinkedList<>();
        if(Math.abs(diff.getX()) >= Math.abs(diff.getY())){
            for(int i = 0; i != diff.getX(); i += x_sign){
                double coef = (double)i / (double)diff.getX();
                int y_component = (int)Math.round((double)diff.getY() * coef);
                Vec2i startOffset = new Vec2i(i, y_component);
                linePoints.add(start.translate(startOffset));
            }
        } else {
            for(int i = 0; i != diff.getY(); i += y_sign){
                double coef = (double)i / (double)diff.getY();
                int x_component = (int)Math.round((double)diff.getX() * coef);
                Vec2i startOffset = new Vec2i(x_component, i);
                linePoints.add(start.translate(startOffset));
            }
        }
        
        return linePoints;
    }
    
    public static final int CIRCLE_STEPS = 1000;
    
    public static LinkedList<Vec2i> generateCircle(Vec2i center, int radius){
        HashSet<Vec2i> circle_points = new HashSet<>();
        
        /***********
         * Generate lines between segments of a circle.
         * The circle is being split into <CIRCLE_STEPS> parts,
         * and then a point is drawn at each part while eliminating
         * duplicate points
         */
        for(int i = 0; i < CIRCLE_STEPS; i++){
            double rad = i * ((Math.PI * 2) / CIRCLE_STEPS);
            
            Vec2i pos = new Vec2i((int)Math.round((double)radius * Math.cos(rad)), 
                                  (int)Math.round((double)radius * Math.sin(rad)));
            pos = pos.translate(center);
            
            circle_points.add(pos);
        }
        
        return new LinkedList<>(circle_points);
    }
}
