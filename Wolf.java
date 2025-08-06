import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * A model of a wolf.
 * Wolves age, move, eat prey, and die.
 * Prey includes Rabbit, Deer, and Squirrel.
 */
public class Wolf extends Animal {

    private static final int PREY_FOOD_VALUE = 9;    
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location) {
        super(field, location, Color.PINK); // Set wolf colour to Pink
        if (randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(PREY_FOOD_VALUE);
        } else {
            age = 0;
            foodLevel = PREY_FOOD_VALUE; //max food lvl for wolf
        }
    }
    
    public Wolf(Field field, Location location, Color color, String gene, boolean isMale) {
            super(field, location, color, gene, isMale);
    }
    
    /**
     * Wolves hunt prey, age, breed, and die.
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<Animal> newWolves) {
        incrementAge();
        updateSickness();
        incrementHunger();

        if (isAlive()) {
            spreadDisease(); // Only spread disease if the animal is still alive
            giveBirth(newWolves); // then it will attempt to reproduce

            // Move towards sources of food if found
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

    @Override
    protected int getMaxAge() {
        return maxAge; // Use max age corresponding to the species 
    }

    /**
     * Reduce the wolf's food level. It dies if it reaches zero.
     */
    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for adjacent prey (Wild boar, Deer, or Squirrel).
     * Only the first live prey encountered will be eaten.
     * @return The location of the food found, or null if not found.
     */
    private Location findFood() {
        if (foodLevel >= PREY_FOOD_VALUE-3) {
            return null; //skip hunting if predator is mostly full
        }
        
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Animal animal = field.getObjectAt(where);
            if (animal instanceof WildBoar || animal instanceof Deer || animal instanceof Squirrel) {
                if (animal.isAlive()) {
                    animal.setDead();
                    foodLevel = animal.getTheFoodValue(); // Consume prey and gain food units
                    field.place(new Plant(), where.getRow(), where.getCol());
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Handle breeding of wolves.
     * New wolves are placed in the adjacent free location.
     * @param newWolves A List to return newly born wolves.
     */
    private void giveBirth(List<Animal> newWolves) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); // Uses genetically defined breeding probability and litter size
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Wolf young = (Wolf) this.mateWith(this, field, loc); // Uses genetic crossover
            newWolves.add(young);
        }
    }

    
    @Override
    protected Animal createOffspring(Field field, Location location, String gene) {
        boolean gender = rand.nextBoolean(); // either male or female. 
        return new Wolf(field, location, this.getTheColor(), gene, gender);
    }
    
}
