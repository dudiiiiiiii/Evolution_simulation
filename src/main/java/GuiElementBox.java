import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class GuiElementBox {
    private VBox vbox;


    public GuiElementBox(IMapElement element) throws FileNotFoundException {

            ImageView imageView = new ImageView(element.getImage());
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            this.vbox = new VBox();
            this.vbox.getChildren().add(imageView);
            this.vbox.setAlignment(Pos.CENTER);
    }

    public VBox getVBox(){
        return this.vbox;
    }

    public void addLabel(IMapElement element){
        this.vbox.getChildren().add(new Label(element.getPosition().toString()));
        this.vbox.setAlignment(Pos.CENTER);
    }

    public void addLabelGrass(){
        this.vbox.getChildren().add(new Label("grass"));
        this.vbox.setAlignment(Pos.CENTER);
    }





}