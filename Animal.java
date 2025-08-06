import java.util.List;
import javafx.scene.paint.Color; 
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes, Michael Kölling and Jeffery Raphael
 * @version 2025.02.10
 */

public abstract class Animal {
    
    // Random generator for mutations and gene generation
    public static final Random rand = new Random();
    
    // Genetic attributes
    private String gene; // A 14-digit string representing genetic information
    private boolean isMale; // Gender of the animal is true for male, false for female

    protected int breedingAge;
    protected int maxAge;
    private boolean alive;
    protected double animalsBreedingProbability;
    protected int litterSize;
    protected double animalsDiseaseProbability;
    protected double metabolism;
    protected int age;
    protected int foodLevel;
    private Field field;
    private Location location;
    private Color color = Color.BLACK;
    private boolean isSick;
    private int sicknessDuration;
    private static final int MAX_SICKNESS_DURATION = 10; // Duration of sickness in steps    

    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    
    public Animal(Field field, Location location, Color col) {
        alive = true;
        this.field = field;
        setLocation(location);
        setColor(col);
        this.gene = generateRandomGene();
        this.isMale = rand.nextBoolean(); // Randomly assign a gender
        parseGene(this.gene); // the gene string is parsed into the stats
        this.age = 0;
        this.isSick = Math.random() < this.animalsDiseaseProbability;
        this.sicknessDuration = isSick ? MAX_SICKNESS_DURATION : 0;
    }
    
    public Animal(Field field, Location location, Color col, String gene, boolean isMale) {
        //all attributes etc.
        alive = true;
        this.field = field;
        setLocation(location);
        setColor(col);
        this.gene = gene;
        this.isMale = isMale;
        parseGene(this.gene);
        this.age = 0;
        this.isSick = Math.random() < this.animalsDiseaseProbability;
        this.sicknessDuration = isSick ? MAX_SICKNESS_DURATION : 0;
    }
        
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive() {
        return alive;
    }
    
    /**
         * Make this animal act - that is: make it do
         * whatever it wants/needs to do.
         * @param newAnimals A list to receive newly born animals.
         */
    
    abstract public void act(List<Animal> newAnimals);
        //act methods have seperately been added for each animal class 
        
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        alive = false;
        if (location != null) {
            field.clear(location);
            field.place(new Plant(), location.getRow(), location.getCol()); // Replace with a plant
            location = null;
            field = null;
        }
    }
    
    protected void incrementAge() {
        // Increase age faster if the animal is sick. Disease kills off the animal quicker 
        if (isSick) {
            age += 2; // Double the aging speed
        } else {
            age += 1; // Healthy animals have normal aging
        }

        // Check if the animal has exceeded its maximum age
        if (age > getMaxAge()) {
            setDead();
        }
    }
    
    protected Location findPlant() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
    
        for (Location loc : adjacent) {
            if (field.getPlantAt(loc) != null) {  // If a plant is found
                return loc; // Return the location of the plant
            }
        }
        return null; // No plant found nearby
    }

    private void parseGene(String gene) {
        this.breedingAge = Math.max(12, Math.min(90, Integer.parseInt(gene.substring(0, 2))));
        this.maxAge = Math.max(10, Math.min(120, Integer.parseInt(gene.substring(2, 4))));
        this.animalsBreedingProbability = Math.min(0.50, Integer.parseInt(gene.substring(4, 6)) / 100.0);
        this.litterSize = Math.max(1, Math.min(12, Integer.parseInt(gene.substring(6, 8))));
        this.animalsDiseaseProbability = Math.min(0.5, Integer.parseInt(gene.substring(8, 10)) / 100.0);
        this.metabolism = Math.max(0.25, Math.min(1.0, Integer.parseInt(gene.substring(10, 12)) / 10.0));
    }
    
    private String generateRandomGene() {
        Random rand = new Random();
        StringBuilder geneBuilder = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            geneBuilder.append(rand.nextInt(10)); // Rand digit 0-9
        }
        return geneBuilder.toString();
    }

    public Animal mateWith(Animal partner, Field field, Location location) {
        if (this.isMale == partner.isMale) return null; // Only opposite genders mate

        String parent1Gene = this.gene.substring(0, 7);
        String parent2Gene = partner.gene.substring(7);

        String childGene = parent1Gene + parent2Gene;
        childGene = mutateGene(childGene);

        return createOffspring(field, location, childGene);
    }
    
    private String mutateGene(String gene) {
        Random rand = new Random();
        StringBuilder mutatedGene = new StringBuilder(gene);
        for (int i = 0; i < gene.length(); i++) {
            if (rand.nextDouble() < 0.1) { // 10% chance  mutation
                int digit = Character.getNumericValue(gene.charAt(i));
                digit += rand.nextBoolean() ? 1 : -1; // Add or subtract 1
                digit = Math.max(0, Math.min(9, digit)); // Keep digit in 0-9
                mutatedGene.setCharAt(i, Character.forDigit(digit, 10));
            }
        }
        return mutatedGene.toString();
    }

    protected abstract Animal createOffspring(Field field, Location location, String gene);
        //individual methods are added for each animal

    protected int getMaxAge() {
        return maxAge; // Will be overridden by subclasses with species-specific values
    }

    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation() {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
        // Only place the animal if there's no plant already
        if (field.getPlantAt(newLocation) == null) {
            field.place(this, newLocation);
        }
    }
    
    public boolean isSick() {
        return isSick;
    }

    public void becomeSick() {
        isSick = true;
        sicknessDuration = MAX_SICKNESS_DURATION;
    }

    public void spreadDisease() {
        if (!isAlive() || field == null) {
            return; // Do not spread disease if the animal is dead or field is null
        }

        List<Animal> neighbors = field.getLivingNeighbours(getLocation());
        for (Animal neighbor : neighbors) {
            if (neighbor != null && neighbor.getClass() == this.getClass() && !neighbor.isSick()) {
                if (Math.random() < 0.3) { // a 30% chance to infect nearby animals
                neighbor.becomeSick();
                }
            }
        }
    }

    protected void updateSickness() {
        if (isSick) {
            sicknessDuration--;
            if (sicknessDuration <= 0) { // Recover after sickness duration ends
                isSick = false; // deactivate sickness efects
            }
        }
    }
    
    protected boolean canBreed() {
        return age >= breedingAge;
    }

    protected int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= animalsBreedingProbability) {
            births = rand.nextInt(litterSize) + 1;
        }
        return births;
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField() {
        return field;
    }
    
    /**
     * Changes the color of the animal
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * Returns the animal's color
     */
    public Color getTheColor() {
        return color;
    }   
    
    /**
     * Return the food value of animal.
     * By default, animals have 0 food value unless overridden.
     */
    public int getTheFoodValue() {
        return 0; // Default for predators 
    } 
}