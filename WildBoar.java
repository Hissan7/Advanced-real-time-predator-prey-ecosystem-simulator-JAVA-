import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color; 

/**
 * This is the wild boar class
 * Wild boars, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling and Jeffery Raphael
 * @version 2025.02.10
 */

public class WildBoar extends Animal {

    private static final Random rand = Randomizer.getRandom();
    private int foodlevel;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public WildBoar(boolean randomAge, Field field, Location location, Color col) {
        super(field, location, col);  // Call the superclass constructor
    
        if (randomAge) {
            age = rand.nextInt(maxAge); // Use inherited maxAge
            foodlevel = rand.nextInt(10); // Random food level between 0 and 9
        } else {
            age = 0;
            foodlevel = 10; // Start with full food level for boar 
        }
    }
    
    public WildBoar(Field field, Location location, Color color, String gene, boolean isMale) {
            super(field, location, color, gene, isMale);
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Animal> newWildBoars) {
        incrementAge();
        updateSickness(); // Handle sickness duration
        spreadDisease(); // Spread disease to nearby animals
        if(isAlive()) {
            giveBirth(newWildBoars);            
            // Try to move into a free location.
            Location newLocation = getField().getFreeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // if theres overcrowding.
                setDead();
            }
        }
    }

    @Override
    protected int getMaxAge() {
        return maxAge; // Use the species-specific max age
    }

    
    /**
     * Check whether or not this boar is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newWildBoars) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); // Uses genetically defined breeding probability and litter size
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            WildBoar young = (WildBoar) this.mateWith(this, field, loc); // Uses genetic crossover
            newWildBoars.add(young);
        }
    }
        
    @Override
    protected Animal createOffspring(Field field, Location location, String gene) {
        boolean gender = rand.nextBoolean(); // Random gender for offspring
        return new WildBoar(field, location, this.getTheColor(), gene, gender);
    }
    
    @Override
    public int getTheFoodValue() {
        return 9; // food value of boar is 9 
    }
}