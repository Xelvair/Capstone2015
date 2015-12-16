package capstone2015.geom;

public class Vector2i{
    private int x;
    private int y;
    
    public Vector2i(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Vector2i(Vector2i rhs){
        this.x = rhs.x;
        this.y = rhs.y;
    }
    //////////////////////////
    // SETTERS    
    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}
    
    //////////////////////////////
    // GETTERS
    public int getX(){return this.x;}
    public int getY(){return this.y;}
    
    /***************************
     * Scales a vector by a factor
     * @param scale scale factor
     * @return scaled vector
     */
    public Vector2i scale(int scale){
        return new Vector2i(this.x * scale, this.y * scale);
    };
    
    /***************************
     * Transform vector by another vector
     * This operation is essentially the same
     * as adding two vectors
     * @param transform transform vector
     * @return transformed vector
     */
    public Vector2i transform(Vector2i transform){
        return new Vector2i(this.x + transform.x, this.y + transform.y);
    }
    
    /***************************
     * Inverts or negates the vector
     * @return inverted vector
     */
    public Vector2i invert(){
        return new Vector2i(-this.x, -this.y);
    }
    
    /***************************
     * Override for toString operator
     * @return stringified vector
     */
    @Override 
    public String toString(){
        return "{" + this.x + ", " + this.y + "}";
    }
    
    /**************************
     * Override for equals operator
     * @param rhs vector to compare to
     * @return TRUE if both are the equal, FALSE if not
     */
    public boolean equals(Vector2i rhs){
        return (this.x == rhs.x && this.y == rhs.y);
    }
}
