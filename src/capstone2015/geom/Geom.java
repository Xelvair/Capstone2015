package capstone2015.geom;

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
    public static LinkedList<Vector2i> lineToPoints(
        Vector2i start, 
        Vector2i end
    ){
        /**
         * Determine the longer component of the vector
         * diff = end - start
         */
        Vector2i diff = end.translate(start.invert());
        
        /**
         * Generate points along longer component
         */
        LinkedList<Vector2i> linePoints = new LinkedList<>();
        if(diff.getX() >= diff.getY()){
            for(int i = 0; i <= diff.getX(); i++){
                double coef = (double)i / (double)diff.getX();
                int y_component = (int)Math.round((double)diff.getY() * coef);
                Vector2i startOffset = new Vector2i(i, y_component);
                linePoints.add(start.translate(startOffset));
            }
        } else {
            for(int i = 0; i <= diff.getY(); i++){
                double coef = (double)i / (double)diff.getY();
                int x_component = (int)Math.round((double)diff.getX() * coef);
                Vector2i startOffset = new Vector2i(x_component, i);
                linePoints.add(start.translate(startOffset));
            }
        }
        
        return linePoints;
    }
}
