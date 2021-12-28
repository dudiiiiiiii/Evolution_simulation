import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Grass implements IMapElement{
    private Vector2d position;
    private Image image;

    public Grass(Vector2d position) {

        this.position = position;
        try {
            this.image = new Image(new FileInputStream("src/main/resources/grass.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public String toString() {
        return "*";
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public String getDirection(String dir) {
        switch(dir) {
            case "*" -> {
                return "src/main/resources/grass.png";
            }
            default -> {
                return "none";
            }
        }
    }
}