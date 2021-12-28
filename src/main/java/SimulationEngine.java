//import javafx.application.Platform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.io.FileNotFoundException;
import java.util.*;

public class SimulationEngine implements IEngine,Runnable,IUpdateAnimals{

    private IWorldMap map;
    private Multimap<Vector2d,Animal> animals = ArrayListMultimap.create();
    private List<IUpdateAnimals> positionsObserver = new ArrayList<>();
    private int moveDelay;
    private int energyToMove;
    private int startEnergy;
    private int animalNum;


    public SimulationEngine(IWorldMap map, int animalNum, int energyTOMove, int startEnergy) throws FileNotFoundException {
        this.map = map;
        this.animalNum = animalNum;
        this.energyToMove = energyTOMove;
        this.startEnergy = startEnergy;

        for (int i=0; i< this.animalNum;i++) {
            Vector2d tmp = new Vector2d(0,0).newRandom(this.map.up().x, this.map.up().y);
            Animal temp = new Animal(this.map, tmp, startEnergy);
            if (this.map.place(temp)) {
                this.animals.put(tmp, temp);
            }
        }
    }
    public void addObserver(IUpdateAnimals observer) {
        this.positionsObserver.add(observer);
    }

    public void removeObserver(IUpdateAnimals observer) {
        this.positionsObserver.remove(observer);
    }

    public void setMoveDelay(int delay){ this.moveDelay = delay;}

    @Override
    public void run() {
//        Animal one = new Animal(this.map, new Vector2d(0,0), 200);
//        this.map.place(one);
//        this.animals.put(new Vector2d(0,0), one);


        while(true) {
            if(this.animals.isEmpty()){
                System.out.println("Empty");
                return;
            }
            Multimap<Vector2d,Animal> newAnimals = ArrayListMultimap.create();

            for (Vector2d key : this.animals.keySet()) {
                for (Animal ani : this.animals.get(key)) {
                    ani.move(this.energyToMove);
                    Vector2d newPosition = ani.getPosition();
                    newAnimals.put(newPosition, ani);
                    for (IUpdateAnimals observ : positionsObserver) {
                        observ.positionChanged();
                    }
                }
            }
            this.animals = newAnimals;
            this.deleteDeadAnimals();
            this.eatGrass();
            this.reproduction();
            this.map.plant();


            try {
                Thread.sleep(moveDelay);

            } catch (InterruptedException except) {
                System.out.println("Exception");
            }
        }
    }

    public void deleteDeadAnimals(){
        Map<Vector2d, Animal> animalsToDelete= new HashMap<>();
        for (Vector2d key : this.animals.keySet()) {
            for (Animal ani : this.animals.get(key)) {
                if(ani.getEnergy() <=0){
                    animalsToDelete.put(key, ani);

                }
            }
        }
        for(Vector2d key: animalsToDelete.keySet()){
            Animal ani = animalsToDelete.get(key);
            this.animals.remove(key, ani);
            this.map.deleteDeadAnimals(key, ani);
        }
    }

    public void eatGrass(){
        ArrayList<Vector2d> toDelete = new ArrayList<>();
        Map<Vector2d, Grass> temp = this.map.getGrass();
        for(Vector2d key: temp.keySet()){
            if(!animals.get(key).isEmpty()){
                toDelete.add(key);
                for(Animal ani: animals.get(key)){
                    ani.eat();
                    break;
                }
            }
        }
        for(Vector2d key: toDelete){
            this.map.deleteGrass(key);
        }


    }

    public void reproduction(){
        for(Vector2d key: animals.keySet()){
            if(animals.get(key).size() > 1){
                Animal max1 = null;
                Animal max2 = null;
                int i=0;
                for(Animal ani:animals.get(key)){
                    if(i==0) max1 = ani;
                    if(i==1) max2 = ani;
                    if(i>1){
                        if(ani.getEnergy() > max1.getEnergy() && max1.getEnergy() <= max2.getEnergy()){
                            max1 = ani;
                        }
                        else if(ani.getEnergy() > max2.getEnergy() && max1.getEnergy() >= max2.getEnergy()){
                            max2 = ani;
                        }
                    }
                    i++;
                }
                if(max1.getEnergy() >= max2.getEnergy() && max2.getEnergy() >= max2.getMax_energy()*0.5)
                    performReproduction(max2, max1);
                else if(max1.getEnergy() < max2.getEnergy() && max1.getEnergy() >= max1.getMax_energy()*0.5) {
                    performReproduction(max1, max2);
                }
            }

        }
    }

    public void performReproduction(Animal weak, Animal strong){
        Genotype weakGen = weak.getGenotype();
        Genotype strongGen = strong.getGenotype();
        int weakEn = weak.getEnergy();
        int strongEn = strong.getEnergy();
        int max = weak.getMax_energy();
        Vector2d position = weak.getPosition();
        int energy = (int) (weak.getEnergy()/4 + strong.getEnergy()/4);

        Random rand = new Random();
        int side = rand.nextInt(2);
        int part = (int) 32*strongEn/(strongEn+weakEn);

        Animal newBorn = new Animal(weak.getMap(), position, energy, max, part, side, weakGen.getGenotype(), strongGen.getGenotype());
        this.map.place(newBorn);
        this.animals.put(position, newBorn);
        weak.addChild();
        strong.addChild();
        weak.loseEnergy();
        strong.loseEnergy();
    }

    @Override
    public void positionChanged() {

    }
}