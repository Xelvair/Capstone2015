package capstone2015.geom;

public class Vec2i implements Comparable<Vec2i>{
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
     * @param rhs add vector
     * @return added(translatedI vector
     */
    public Vec2i add(Vec2i rhs){
        return new Vec2i(this.x + rhs.x, this.y + rhs.y);
    }

    /*********************
     * Subtracts another vector from this vector
     * @param rhs the other vector
     * @return new subtracted vector
     */
    public Vec2i subtract(Vec2i rhs){return new Vec2i(this.x - rhs.x, this.y - rhs.y);}
    
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

    public int deltaOrthoMagnitude(Vec2i rhs){
        return this.subtract(rhs).orthoMagnitude();
    }

    public int orthoMagnitude() {return Math.abs(this.x) + Math.abs(this.y);}
    
    public int dotProduct(Vec2i rhs){
        return this.x * rhs.x + this.y * rhs.y;
    }
    
    @Override
    public int hashCode(){
        return  this.y * 46370 + this.x;
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

    @Override
    public int compareTo(Vec2i o) {
        if(this.y < o.y){
            return -1;
        } else if(this.y == o.y && this.x < o.x){
            return -1;
        } else if(this.x == o.x && this.y == o.y){
            return 0;
        } else {
            return 1;
        }
    }
}
