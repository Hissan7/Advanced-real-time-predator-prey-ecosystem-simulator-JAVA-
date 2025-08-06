import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.paint.Color; 

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes, Michael Kölling and Jeffery Raphael
 * @version 2025.02.10
 */

public class Simulator {

    private static final double BEAR_CREATION_PROBABILITY = 0.02;
    private static final double WOLF_CREATION_PROBABILITY = 0.01;
    private static final double WILDBOAR_CREATION_PROBABILITY = 0.08;
    private static final double DEER_CREATION_PROBABILITY = 0.06;
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.09;
    
    private static final double TOTAL_PROBABILITY = 
    BEAR_CREATION_PROBABILITY+WOLF_CREATION_PROBABILITY+
    WILDBOAR_CREATION_PROBABILITY+DEER_CREATION_PROBABILITY+SQUIRREL_CREATION_PROBABILITY;
    
    //Bear has red squares
    //Wolf has pink squares
    //Wild boar has grey squares
    //Squirrel has purple squares 
    //Deer has blue squares


    private List<Animal> animals;
    private Field field;
    private int step;
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal
     */
    public void simulateOneStep() {
        step++;
        List<Animal> newAnimals = new ArrayList<>();        

        for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if (animal != null) { //check if animal exists before calling act()
                animal.act(newAnimals);
                if (!animal.isAlive()) {
                    it.remove();
            }
        }
    }
        animals.addAll(newAnimals);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() { 
        
        Random rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                double randomValue = rand.nextDouble();
                if (randomValue <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location, Color.RED);
                    animals.add(bear);
                } else if (randomValue <= BEAR_CREATION_PROBABILITY + WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    animals.add(wolf); //wolf is pink 
                } else if (randomValue <= BEAR_CREATION_PROBABILITY + WOLF_CREATION_PROBABILITY + WILDBOAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    WildBoar wildboar = new WildBoar(true, field, location, Color.GREY);
                    animals.add(wildboar); 
                } else if (randomValue <= BEAR_CREATION_PROBABILITY + WOLF_CREATION_PROBABILITY + WILDBOAR_CREATION_PROBABILITY + DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location, Color.BLUE);
                    animals.add(deer); 
                } else if (randomValue <= TOTAL_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location, Color.PURPLE);
                    animals.add(squirrel);
                }
                // else leave the location empty.
            }
        }
        field.growPlants();
    }   
    


    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    public void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    

    public Field getField() {
        return field;
    }

    public int getStep() {
        return step;
    }
}