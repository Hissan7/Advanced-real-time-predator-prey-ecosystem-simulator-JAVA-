import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.LinkedList;
import javafx.scene.paint.Color; 

/**
 * A simple model of a Bear.
 * Bears age, move, eat, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2025.02.10
 */

public class Bear extends Animal {

    private static final int PREY_FOOD_VALUE = 15;
    private static final Random rand = Randomizer.getRandom();
    
    private int foodLevel;
    
    
    /**
     * Create a Bear. A Bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field
     */
    public Bear(boolean randomAge, Field field, Location location, Color col) {
        super(field, location, col);
        
        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(PREY_FOOD_VALUE);  
        }
        else {
            age = 0;
            foodLevel = PREY_FOOD_VALUE; //15
        }
    }
    
    public Bear(Field field, Location location, Color color, String gene, boolean isMale) {
            super(field, location, color, gene, isMale);
    }

    
    /**
     * This is what the Bear does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born Bears.
     */
    public void act(List<Animal> newBears) {
        incrementAge();
        updateSickness();
        incrementHunger();

        if (isAlive()) {
            spreadDisease(); // Only spread disease if the animal is still alive
            giveBirth(newBears); // Attempt to reproduce

            // Move towards a source of food if found
            Location newLocation = findFood();
        
        if (newLocation == null) {
            // if thee isnt any food then move to a free location
            newLocation = getField().getFreeAdjacentLocation(getLocation());
        }

        if (newLocation != null) {
            setLocation(newLocation); // Move to the new location
        } else {
            setDead(); // will die if theres overcrowding 
            }
        }
    }

    
    /**
     * Make this Bear more hungry. This could result in the Bear's death.
     */
    private void incrementHunger() {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    @Override
    protected int getMaxAge() {
        return maxAge; // Use the species-specific max age
    }

    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if (foodLevel >= PREY_FOOD_VALUE-3) {
            return null; //skip hunting if predator is mostly full
        }
        
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Animal animal = field.getObjectAt(where);
            //check if animal's a prey or not 
            if (animal instanceof WildBoar || animal instanceof Deer || animal instanceof Squirrel) {
                if (animal.isAlive()) {
                    animal.setDead();
                    foodLevel = animal.getTheFoodValue(); // Consume prey and gain food units
                    // Place a plant at the eaten prey's location immediately
                    field.place(new Plant(), where.getRow(), where.getCol());
                    return where;
                }
            }
        }
        return null;
    }

    
    /**
     * Check whether or not this Bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBears A list to return newly born bears.
     */
    private void giveBirth(List<Animal> newBears) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); // Uses genetically defined breeding probability and litter size
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bear young = (Bear) this.mateWith(this, field, loc); // Uses genetic crossover
            newBears.add(young);
        }
    }   

    
    @Override
    protected Animal createOffspring(Field field, Location location, String gene) {
        boolean gender = rand.nextBoolean(); // Random gender for offspring
        return new Bear(field, location, this.getTheColor(), gene, gender);
    }
}