package capstone2015.geom;

public class Vec2i{
    private int x;
    private int y;
    
    public Vec2i(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Vec2i(Vec2i rhs){
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
    public Vec2i scale(int scale){
        return new Vec2i(this.x * scale, this.y * scale);
    };
    
    /***************************
     * Translate vector by another vector
     * This operation is essentially the same
     * as adding two vectors
     * @param translate translate vector
     * @return translated vector
     */
    public Vec2i translate(Vec2i translate){
        return new Vec2i(this.x + translate.x, this.y + translate.y);
    }
    
    /***************************
     * Inverts or negates the vector
     * @return inverted vector
     */
    public Vec2i invert(){
        return new Vec2i(-this.x, -this.y);
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
    public boolean equals(Vec2i rhs){
        return (this.x == rhs.x && this.y == rhs.y);
    }
    
    @Override
    public int hashCode(){
        return this.x * this.y + (this.x - this.y) * (this.x ^ this.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vec2i other = (Vec2i) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
}