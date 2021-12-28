import java.io.FileNotFoundException;
import java.util.*;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class RestrictedField  implements IWorldMap, IPositionChangeObserver{
    private Map<Vector2d,Grass> poss = new HashMap<>();
    private Multimap<Vector2d, Animal> animals = ArrayListMultimap.create();
    private Vector2d bottomLeft;
    private Vector2d upperRight;
    private Jungle jungle;
    private int animalsAlive;
    private int amountGrass;
    private int deadAnimals;
    private int deadLifeSpan;
    private int sumChildren;
    private int plantEnergy;



    public RestrictedField(int num, int width, int height, double jungleRatio, int plantEnergy) throws FileNotFoundException {
        Random rand = new Random();
        this.bottomLeft = new Vector2d(0,0);
        this.upperRight = new Vector2d(width-1, height-1);
        this.jungle = new Jungle(new Vector2d((int) ((width-1)*jungleRatio), (int)((height-1)*jungleRatio)), new Vector2d((int) ((width-1)*(1-jungleRatio)), (int)((height-1)*(1-jungleRatio))));
        this.animalsAlive = 0;
        this.amountGrass = num;
        this.sumChildren = 0;
        this.plantEnergy = plantEnergy;

        for(int i=0; i< num; i++){
            int flag = 1;
            Vector2d tmp = new Vector2d(rand.nextInt(width-1), rand.nextInt(height-1));

            if(!isOccupied(tmp)){
                i--;
                flag = 0;
            }
            if(flag == 1) {
                Grass x = new Grass(tmp);
                this.poss.put(tmp,x);

            }
        }

        getGrass1();
    }
    public int[] getDominant(){
        Map<int[], Integer> dominant = new HashMap<>();

        for(Vector2d key: animals.keySet()){
            for(Animal animal: animals.get(key)){
                dominant.putIfAbsent(animal.getGenotype().getGenotype(), 1);
                if(dominant.get(animal.getGenotype().getGenotype())!= null){
                    int tmp = dominant.get(animal.getGenotype().getGenotype());
                    dominant.put(animal.getGenotype().getGenotype(), tmp+1);
                }
            }
        }

        int[] max = null;
        Integer maxi = 0;
        for(int[] gen: dominant.keySet()){
            if(dominant.get(gen) > maxi){
                maxi = dominant.get(gen);
                max = gen;
            }
        }
        return max;
    }

    public double getAverageEnergy(){
        int sumEnergy = 0;
        this.sumChildren = 0;
        for(Vector2d key: animals.keySet()){
            for(Animal animal: animals.get(key)) {
                sumEnergy += animal.getEnergy();
                this.sumChildren += animal.getNumChildrem();
            }
        }
        return (double)sumEnergy/this.animalsAlive;
    }

    public double avgLifeSpan(){
        if(this.deadAnimals == 0){
            return 0;
        }
        return (double)this.deadLifeSpan/this.deadAnimals;
    }

    public double avgNumChildren(){
        return (double)this.sumChildren/this.animalsAlive;
    }

    public void plant(){
        Random rand = new Random();
        Grass jungle;
        Grass around;
        boolean inJungle = true;
        boolean aroundJungle = true;
        for(int i=this.jungle.getLowerLeft().x; i< this.jungle.getUpperRight().x; i++){
            for(int j=this.jungle.getLowerLeft().y; j<this.jungle.getUpperRight().y; j++){
                Vector2d tmp = new Vector2d(i,j);
                if(this.animals.get(tmp).isEmpty() && this.poss.get(tmp) == null) inJungle = false;
            }
        }

        for(int i=0; i < this.upperRight.x; i++){
            for(int j=0; j< this.upperRight.y; j++){
                Vector2d tmp = new Vector2d(i,j);
                if(!(tmp.x <= this.jungle.getUpperRight().x && tmp.x >= this.jungle.getLowerLeft().x && tmp.y <= this.jungle.getUpperRight().y && tmp.y >= this.jungle.getLowerLeft().y) && this.animals.get(tmp).isEmpty() && this.poss.get(tmp) == null){
                    aroundJungle = false;
                }
            }
        }

        if(!inJungle){
            for(int i=0 ; i<1; i++) {
                while (!inJungle) {
                    Vector2d tmp = new Vector2d(rand.nextInt(this.jungle.getUpperRight().x - this.jungle.getLowerLeft().x+1) + this.jungle.getLowerLeft().x, rand.nextInt(this.jungle.getUpperRight().y - this.jungle.getLowerLeft().y+1) + this.jungle.getLowerLeft().y);
                    if (this.animals.get(tmp).isEmpty() && this.poss.get(tmp) == null) {
                        inJungle = true;
                        jungle = new Grass(tmp);
                        this.poss.put(tmp, jungle);
                        amountGrass+= 1;
                    }
                }
                inJungle = false;
            }
        }

        if(!aroundJungle){
            for(int i=0 ; i<1; i++){
                while(!aroundJungle){
                    Vector2d tmp = new Vector2d(rand.nextInt(this.upperRight.x+1), rand.nextInt(this.upperRight.y+1));
                    if(!(tmp.x <= this.jungle.getUpperRight().x && tmp.x >= this.jungle.getLowerLeft().x && tmp.y <= this.jungle.getUpperRight().y && tmp.y >= this.jungle.getLowerLeft().y) && this.animals.get(tmp).isEmpty() && this.poss.get(tmp) == null){
                        aroundJungle = true;
                        around = new Grass(tmp);
                        this.poss.put(tmp, around);
                        amountGrass+=1;
                    }
                }
                aroundJungle = false;
            }
        }

    }

    public int getAnimalsAlive(){
        return this.animalsAlive;
    }

    public int getAmountGrass(){
        return this.amountGrass;
    }

    public void deleteDeadAnimals(Vector2d pos, Animal animal){
        animal.removeObserver(this);
        this.animals.remove(pos, animal);
        this.animalsAlive-=1;
        this.deadLifeSpan += animal.getLifeSpan();
        this.deadAnimals += 1;
        this.sumChildren -= animal.getNumChildrem();
    }

    public Vector2d up(){
        return this.upperRight;
    }

    public Vector2d down(){
        return this.bottomLeft;
    }

    public List<Grass> getGrass1(){
        poss = this.poss;
        return null;
    }

    public Grass grassAt(Vector2d pos){
        return this.poss.get(pos);
    }

    public Multimap<Vector2d, Animal> getAn(){
        return animals;
    }

    public String toString() {

        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(bottomLeft, upperRight);
    }

    public boolean canMoveTo(Vector2d position) {
        return position.follows(bottomLeft) && position.precedes(upperRight);
    }

    public boolean place(Animal animal) {
        if (canMoveTo(animal.getPosition())) {
            this.animals.put(animal.getPosition(),animal);
            animal.addObserver(this);
            this.animalsAlive += 1;
            return true;
        }
        throw new IllegalArgumentException(animal.getPosition() + " is not legal move specification");
        //return false;
    }

    public boolean isOccupiedByAnimal(Vector2d pos) {
        return this.animals.get(pos) != null;
    }

    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    public Object objectAt(Vector2d position) {
        if(this.animals.get(position)!= null) {
            return this.animals.get(position);
        }

        for(Vector2d key: this.poss.keySet()) {
            if(this.poss.get(key).getPosition().equals(position)) {
                return this.poss.get(key);
            }
        }

        return null;
    }

    public Boolean isInBounds(Vector2d pos){
        return pos.precedes(upperRight) && pos.follows(bottomLeft);
    }

    @Override
    public int getPlantEnergy() {
        return this.plantEnergy;
    }

    @Override
    public Vector2d newPos(Vector2d pos) {
        int x = pos.x;
        int y = pos.y;
        if(pos.x < 0) x += this.upperRight.x + 1;
        else if(pos.x > this.upperRight.x)x -= (this.upperRight.x+1);

        if(pos.y<0) y += this.upperRight.y + 1;
        else if(pos.y > this.upperRight.y) y -= (this.upperRight.y+1);
        return new Vector2d(x, y);
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        Animal animalWhichMoved = new Animal(this);
        if(!oldPosition.equals(newPosition)){
            Collection<Animal> oldPositionAnimals = animals.get(oldPosition);
            for(Animal animal: oldPositionAnimals){
                if(animal.getPosition().equals(newPosition)){
                    animalWhichMoved = animal;
                }
            }
            animals.remove(oldPosition, animalWhichMoved);
            animals.put(newPosition, animalWhichMoved);
        }
    }

    public Multimap<Vector2d, Animal> getAnimalList(){
        return this.animals;
    }

    public Map<Vector2d, Grass> getGrass(){
        return this.poss;
    }

    public void deleteGrass(Vector2d pos){
        this.poss.remove(pos);
        this.amountGrass -= 1;
    }

}

