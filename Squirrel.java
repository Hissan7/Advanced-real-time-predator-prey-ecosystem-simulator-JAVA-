import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

public class Squirrel extends Animal {

    private static final Random rand = Randomizer.getRandom();
    private int foodlevel;

    public Squirrel(boolean randomAge, Field field, Location location, Color col) {
        super(field, location, col);  // Call the superclass constructor

        if (randomAge) {
            age = rand.nextInt(maxAge); // Use inherited maxAge
            foodlevel = rand.nextInt(10); // Random food level between 0 and 9
        } else {
            age = 0;
            foodlevel = 8; // Start with full food level for squirrels
        }
    }

    public Squirrel(Field field, Location location, Color color, String gene, boolean isMale) {
            super(field, location, color, gene, isMale);
    }
    
    public void act(List<Animal> newSquirrel) {
        incrementAge();
        updateSickness(); // Handle sickness duration
        spreadDisease(); // Spread disease to nearby animals
        if (isAlive()) {
            giveBirth(newSquirrel);
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

    
    private void giveBirth(List<Animal> newSquirrels) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); // Uses the defined breeding probability mechanism & litter size
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Squirrel young = (Squirrel) this.mateWith(this, field, loc); // Uses the genetic crossover
            newSquirrels.add(young);
        }
    }

    @Override
    protected Animal createOffspring(Field field, Location location, String gene) {
        boolean gender = rand.nextBoolean(); // M or F
        return new Squirrel(field, location, this.getTheColor(), gene, gender);
    }
    
    @Override
    public int getTheFoodValue() {
        return 5; // Squirrels provides 5 food units
    }
    
}
