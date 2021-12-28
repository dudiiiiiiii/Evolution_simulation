//import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Animal implements IMapElement {
    private MapDirection orientation;
    private Vector2d position;
    private IWorldMap map;
    private List<IPositionChangeObserver> positions = new ArrayList<>();
    private Genotype genotype;
    private int energy;
    private Image image;
    private int max_energy;
    private int lifeSpan = 0;
    private int numChildren;

    public Animal(){
    }

    public Animal(IWorldMap map){
        this.map = map;
    }

    public Animal(IWorldMap map, Vector2d initialPosition, int energy) throws FileNotFoundException {
        this.map = map;
        this.position = initialPosition;
        this.orientation = MapDirection.getRandom();
        this.genotype = new Genotype();
        this.energy = energy;
        this.max_energy = energy;
        this.image = new Image(new FileInputStream("src/main/resources/animal.png"));
        this.numChildren = 0;
    }

    public Animal(IWorldMap map, Vector2d position, int energy, int maxEnergy, int howMany, int leftRight, int[] weakGen, int[] strongGen){
        this.map = map;
        this.position = position;
        this.max_energy = maxEnergy;
        this.energy = energy;
        this.orientation = MapDirection.getRandom();
        this.genotype = new Genotype(howMany, leftRight, weakGen, strongGen);
        this.numChildren = 0;
        if(this.energy > maxEnergy*0.5) {
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(this.energy <= maxEnergy*0.1){
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal_low.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal_med.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String toString(){
        return switch (this.orientation) {
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

    public Image getImage(){
        return this.image;
    }

    public void addChild(){
        this.numChildren += 1;
    }

    public int getNumChildrem(){
        return this.numChildren;
    }

    public IWorldMap getMap() {
        return map;
    }

    public int getLifeSpan(){
        return this.lifeSpan;
    }

    public int getMax_energy(){return this.max_energy;}

    public Vector2d getPosition(){
        return this.position;
    }

    public MapDirection getOrient() {return this.orientation;}

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public boolean orient(MapDirection orientt) {
        return orientt == this.orientation;
    }

    public void move(int energyToMove) {
        Random rand = new Random();
        int dir = this.genotype.getGenotype()[rand.nextInt(32)];
        Vector2d newPos = this.position;
        Vector2d prev = this.position;
        switch (dir) {
            case 0: {
                newPos = newPos.add(this.orientation.toUnitVector());
                break;
            }
            case 4: {
                newPos = newPos.subtract(this.orientation.toUnitVector());
                break;
            }
            default: {
                for (int i = 0; i < dir; i++) {
                    this.orientation = this.orientation.next();
                }
            }
        }

        if(this.map.canMoveTo(newPos)){
            if(this.map.isInBounds(newPos)){
                this.position = newPos;
            }
            else {
                newPos = this.map.newPos(newPos);
                this.position = newPos;
            }
            this.positionChanged(prev, newPos);
        }


        int prevEnergy = this.energy;
        this.energy -= energyToMove;
        this.lifeSpan += 1;

        this.updateImage(prevEnergy);

    }

    public void updateImage(int prevEnergy){
        if(prevEnergy > 0.5*(max_energy) && this.energy <= 0.5 * max_energy && this.energy > 0.1*max_energy){
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal_med.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(prevEnergy <= 0.5 * max_energy && prevEnergy > 0.1*max_energy && this.energy <= 0.1*max_energy){
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal_low.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(prevEnergy <= 0.1*max_energy && this.energy > 0.1*max_energy && this.energy <= 0.5*max_energy){
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal_med.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(prevEnergy <= 0.5 * max_energy && prevEnergy > 0.1*max_energy && this.energy > 0.5*max_energy){
            try {
                this.image = new Image(new FileInputStream("src/main/resources/animal.png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addObserver(IPositionChangeObserver observer) {
        this.positions.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.positions.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for(IPositionChangeObserver x: positions) {
            x.positionChanged(oldPosition, newPosition);
        }
    }
    public int getEnergy(){
        return this.energy;
    }

    @Override
    public String getDirection(String dir) {
        return "src/main/resources/animal_low.png";
    }

    public void eat(){
        this.energy += this.map.getPlantEnergy();
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public void loseEnergy(){
        this.energy = (int) this.energy*3/4;
    }
}