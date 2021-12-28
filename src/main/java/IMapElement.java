import javafx.scene.image.Image;

public interface IMapElement {

    String getDirection(String dir);

    Vector2d getPosition();

    String toString();

    Image getImage();
}