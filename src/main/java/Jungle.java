public class Jungle {
    private Vector2d lowerLeft;
    private Vector2d upperRight;

    public Jungle(Vector2d left, Vector2d right){
        this.lowerLeft = left;
        this.upperRight = right;
    }

    public boolean isInJungle(Vector2d poss){
        return this.lowerLeft.precedes(poss) && this.upperRight.follows(poss);
    }

    public Vector2d getUpperRight(){
        return this.upperRight;
    }

    public Vector2d getLowerLeft(){
        return this.lowerLeft;
    }
}
