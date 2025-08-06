import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

public class Deer extends Animal {
    
    private static final Random rand = Randomizer.getRandom();

    private int foodlevel;

    public Deer(boolean randomAge, Field field, Location location, Color col) {
        super(field, location, col);  // Calling superclass contructor
        //maxAge = 70; // Set species-specific max age
        if (randomAge) {
            age = rand.nextInt(maxAge); // Use inherited maxAge
            foodlevel = rand.nextInt(10); // A random no between 0 and 9 
        } else {
            age = 0;
            foodlevel = 12; // Start with a full food level for a deer 
        }
    }
    
    public Deer(Field field, Location location, Color color, String gene, boolean isMale) {
            super(field, location, color, gene, isMale);
        }
        
    public void act(List<Animal> newDeer) {
        incrementAge();
        updateSickness(); // Handle sickness duration
        spreadDisease(); // Spread disease to nearby animals
        if (isAlive()) {
            giveBirth(newDeer);
            Location newLocation = getField().getFreeAdjacentLocation(getLocation());
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead(); // if overcrowding
            }
        }
        }

    @Override
    protected int getMaxAge() {
        return maxAge; // Use the species-specific max age
    }

    
    private void giveBirth(List<Animal> newDeers) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); // Uses genetically defined breeding probability and litter size
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Deer young = (Deer) this.mateWith(this, field, loc); // Uses genetic crossover
            newDeers.add(young);
        }
    }

    @Override
    protected Animal createOffspring(Field field, Location location, String gene) {
        boolean gender = rand.nextBoolean(); // Random gender for offspring
        return new Deer(field, location, this.getTheColor(), gene, gender);
    }
    
    @Override
    public int getTheFoodValue() {
        return 15; // Deer provide 15 food units
    }

}
