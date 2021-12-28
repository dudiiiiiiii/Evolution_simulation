import com.google.common.collect.Multimap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.*;


public class App extends Application implements IUpdateAnimals{
    private IWorldMap map;
    private IWorldMap restrictedMap;
    private GridPane gridPane = new GridPane();
    private GridPane gridpane2 = new GridPane();
    private VBox hbox = new VBox();
    private VBox vbox1 = new VBox();
    private VBox main = new VBox();
    private HBox hbox2 = new HBox();

    public void createGrid(GridPane curr) throws FileNotFoundException {
        curr.setGridLinesVisible(false);
        curr.getChildren().clear();

        curr.setGridLinesVisible(true);
        Vector2d top = this.map.up();
        Vector2d bottom = this.map.down();

        Integer width = top.x- bottom.x + 2;
        Integer height = top.y- bottom.y + 2;

        for(Integer i=0; i < height-1; i++) {
            curr.getRowConstraints().add(new RowConstraints(40));
        }

        for(Integer i =0; i< width-1; i++){
            curr.getColumnConstraints().add(new ColumnConstraints(40));
        }

        Multimap<Vector2d, Animal> animalList = this.map.getAnimalList();
        Collection<Vector2d> keys = animalList.keySet();
        for(Vector2d key: keys){
            Collection<Animal> temp = animalList.get(key);
            for(Animal ani: temp){
                GuiElementBox guiElementBox = new GuiElementBox(ani);
                curr.add(guiElementBox.getVBox(), -bottom.x+key.x, top.y-key.y);
                curr.setHalignment(guiElementBox.getVBox(), HPos.CENTER);
                break;
            }

        }

        Map<Vector2d, Grass> grass = this.map.getGrass();
        for(Vector2d key: grass.keySet()){
            GuiElementBox guiElementBox = new GuiElementBox(grass.get(key));
            curr.add(guiElementBox.getVBox(), -bottom.x + grass.get(key).getPosition().x, top.y-key.y);
            curr.setHalignment(guiElementBox.getVBox(), HPos.CENTER);
        }


        this.gridPane = curr;
    }

    public void updateGrid(GridPane curr) throws FileNotFoundException{
        curr.getChildren().clear();
        Vector2d top = this.map.up();
        Vector2d bottom = this.map.down();


        curr.setGridLinesVisible(false);
        Integer width = top.x- bottom.x + 2;
        Integer height = top.y- bottom.y + 2;

        for(int i=0; i < height;i++){
            for(int j=0; j < width; j++){
                Vector2d currPos = new Vector2d(i,j);
                Grass tmp = this.map.grassAt(currPos);
                if(tmp != null){
                    GuiElementBox guiElementBox = new GuiElementBox(tmp);
                    curr.add(guiElementBox.getVBox(), i,j);
                    curr.setHalignment(guiElementBox.getVBox(), HPos.CENTER);
                }
                Multimap<Vector2d,Animal> temp = this.map.getAnimalList();
                Collection<Animal> tmpA =  temp.get(currPos);

                if(!tmpA.isEmpty()){
                    Animal animal = tmpA.iterator().next();
                    GuiElementBox guiElementBox = new GuiElementBox(animal);
                    curr.add(guiElementBox.getVBox(), i, j);
                    curr.setHalignment(guiElementBox.getVBox(), HPos.CENTER);
                }
            }
        }

        curr.setGridLinesVisible(true);
        this.gridPane = curr;
    }


    @Override
    public void init() throws FileNotFoundException {


        hbox.setSpacing(30);
        VBox buttons = new VBox();
        Button startButton = new Button("Start");
        buttons.getChildren().add(startButton);
        buttons.setAlignment(Pos.CENTER);
        hbox.getChildren().add(buttons);
        HBox height = new HBox();
        TextField heighttext = new TextField();
        height.getChildren().add(new Label("Height: "));
        height.getChildren().add(heighttext);
        height.setAlignment(Pos.CENTER);
        HBox width = new HBox();
        TextField widthtext = new TextField();
        width.getChildren().add(new Label("Width: "));
        width.getChildren().add(widthtext);
        width.setAlignment(Pos.CENTER);
        HBox startEn = new HBox();
        TextField startEntext = new TextField();
        startEn.getChildren().add(new Label("Starting energy: "));
        startEn.getChildren().add(startEntext);
        startEn.setAlignment(Pos.CENTER);
        HBox moveEn = new HBox();
        TextField movetext = new TextField();
        moveEn.getChildren().add(new Label("Energy to move: "));
        moveEn.getChildren().add(movetext);
        moveEn.setAlignment(Pos.CENTER);
        HBox plantEn = new HBox();
        TextField plantEntext = new TextField();
        plantEn.getChildren().add(new Label("Energy from plant: "));
        plantEn.getChildren().add(plantEntext);
        plantEn.setAlignment(Pos.CENTER);
        HBox jungle = new HBox();
        TextField jungletext = new TextField();
        jungle.getChildren().add(new Label("Jungle ratio: "));
        jungle.getChildren().add(jungletext);
        jungle.setAlignment(Pos.CENTER);


        hbox.getChildren().add(height);
        hbox.getChildren().add(width);
        hbox.getChildren().add(startEn);
        hbox.getChildren().add(moveEn);
        hbox.getChildren().add(plantEn);
        hbox.getChildren().add(jungle);
        hbox.setAlignment(Pos.CENTER);



        try {
            startButton.setOnAction((event) -> {

                String heightMap = heighttext.getText();
                System.out.println(heightMap);


                IWorldMap map = null;
                try {
                    map = new GrassField(15 , Integer.parseInt(widthtext.getText()), Integer.parseInt(heighttext.getText()), Double.parseDouble(jungletext.getText()), Integer.parseInt(plantEntext.getText()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                this.map = map;
                //IWorldMap mapRestr = new RestrictedField(10, 10, 10, 0.2);
                //this.restrictedMap = mapRestr;

                SimulationEngine engine = null;
                try {
                    engine = new SimulationEngine( map, 20, Integer.parseInt(movetext.getText()), Integer.parseInt(startEntext.getText()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //SimulationEngine engine2 = new SimulationEngine( map, 20, 3, 200);
                try {
                    createGrid(gridPane);
                    //createGrid(gridpane2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                engine.setMoveDelay(40);
                //engine2.setMoveDelay(40);
                engine.addObserver(this);
                //engine2.addObserver(this);
                Thread engineThread = new Thread(engine);
                //Thread engine2Thread = new Thread(engine2);


                hbox.setAlignment(Pos.TOP_LEFT);
                hbox.getChildren().clear();
                hbox.setSpacing(20);
                hbox.getChildren().add(gridPane);
                //hbox2.getChildren().add(gridpane2);
                HBox temp = new HBox();
                temp.getChildren().add(new Label("Animals alive: "));
                temp.getChildren().add(new Label(String.valueOf(this.map.getAnimalsAlive())));
                vbox1.getChildren().add(temp);
                HBox temp1 = new HBox();
                temp1.getChildren().add(new Label("Amount of grass on map: "));
                temp1.getChildren().add(new Label(String.valueOf(this.map.getAmountGrass())));
                vbox1.getChildren().add(temp1);
                HBox temp2 = new HBox();
                temp2.getChildren().add(new Label("Current dominant gen: "));
                temp2.getChildren().add(new Label(Arrays.toString(this.map.getDominant())));
                vbox1.getChildren().add(temp2);
                HBox temp3 = new HBox();
                temp3.getChildren().add(new Label("Avg energy: "));
                temp3.getChildren().add(new Label(String.valueOf(this.map.getAverageEnergy())));
                vbox1.getChildren().add(temp3);
                HBox temp4 = new HBox();
                temp4.getChildren().add(new Label("Avg life span: "));
                temp4.getChildren().add(new Label(String.valueOf(this.map.avgLifeSpan())));
                vbox1.getChildren().add(temp4);
                HBox temp5 = new HBox();
                temp5.getChildren().add(new Label("Avg num of children: "));
                temp5.getChildren().add(new Label(String.valueOf(this.map.avgNumChildren())));
                vbox1.getChildren().add(temp5);
                hbox.getChildren().add(vbox1);
//                main.getChildren().add(hbox);
//                main.getChildren().add(hbox2);
                gridPane.setGridLinesVisible(true);
                engineThread.start();
//                engine2Thread.start();


            });
        }
        catch(IllegalArgumentException except) {
            System.out.println("Illegal");
        }
    }




    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(hbox, 600, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    @Override
    public void positionChanged() {
        Platform.runLater(()-> {
            try {
                updateGrid(this.gridPane);
                hbox.getChildren().clear();
                vbox1.getChildren().clear();
                hbox.getChildren().add(gridPane);
                HBox temp = new HBox();
                temp.getChildren().add(new Label("Animals alive: "));
                temp.getChildren().add(new Label(String.valueOf(this.map.getAnimalsAlive())));
                vbox1.getChildren().add(temp);
                HBox temp1 = new HBox();
                temp1.getChildren().add(new Label("Amount of grass on map: "));
                temp1.getChildren().add(new Label(String.valueOf(this.map.getAmountGrass())));
                vbox1.getChildren().add(temp1);
                HBox temp2 = new HBox();
                temp2.getChildren().add(new Label("Current dominant gen: "));
                temp2.getChildren().add(new Label(Arrays.toString(this.map.getDominant())));
                vbox1.getChildren().add(temp2);
                HBox temp3 = new HBox();
                temp3.getChildren().add(new Label("Avg energy: "));
                temp3.getChildren().add(new Label(String.valueOf(this.map.getAverageEnergy())));
                vbox1.getChildren().add(temp3);
                HBox temp4 = new HBox();
                temp4.getChildren().add(new Label("Avg life span: "));
                temp4.getChildren().add(new Label(String.valueOf(this.map.avgLifeSpan())));
                vbox1.getChildren().add(temp4);
                HBox temp5 = new HBox();
                temp5.getChildren().add(new Label("Avg num of children: "));
                temp5.getChildren().add(new Label(String.valueOf(this.map.avgNumChildren())));
                vbox1.getChildren().add(temp5);
                hbox.getChildren().add(vbox1);
                //main.getChildren().add(hbox);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}