import java.util.Random;

public enum MapDirection {
    NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

    public static MapDirection getRandom(){
        int tmp = new Random().nextInt(8);
        return switch (tmp) {
            case 0 -> NORTH;
            case 1 -> NORTH_EAST;
            case 2 -> EAST;
            case 3 -> SOUTH_EAST;
            case 4 -> SOUTH;
            case 5 -> SOUTH_WEST;
            case 6 -> WEST;
            case 7 -> NORTH_WEST;
            default -> NORTH;
        };
    }

    public String toString(){
        return switch (this) {
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case EAST -> "E";
            case SOUTH_EAST -> "SE";
            case SOUTH -> "S";
            case SOUTH_WEST -> "SW";
            case WEST -> "W";
            case NORTH_WEST -> "NW";
        };
    }

    public MapDirection next(){
        return switch (this) {
            case NORTH -> NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH -> SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH;
        };
    }

    public MapDirection previous(){
        return switch (this) {
            case NORTH -> NORTH_WEST;
            case NORTH_EAST -> NORTH;
            case EAST -> NORTH_EAST;
            case SOUTH_EAST -> EAST;
            case SOUTH -> SOUTH_EAST;
            case SOUTH_WEST -> SOUTH;
            case WEST -> SOUTH_WEST;
            case NORTH_WEST -> WEST;
        };
    }

    public Vector2d toUnitVector(){
        switch(this){
            case NORTH : return new Vector2d(0,1);
            case SOUTH : return new Vector2d(0,-1);
            case WEST : return new Vector2d(-1,0);
            case EAST : return new Vector2d(1,0);
            case NORTH_EAST: return new Vector2d(1,1);
            case NORTH_WEST: return new Vector2d(-1,1);
            case SOUTH_EAST: return new Vector2d(1,-1);
            case SOUTH_WEST: return new Vector2d(-1,-1);
            default: return new Vector2d(0,0);
        }
    }
}
