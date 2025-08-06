import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It is flexible: it will create and maintain a counter
 * for any class of object that is found within the field.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */

public class FieldStats {
    
    private HashMap<Class, Counter> counters;
    private Map<Class<?>, Counter> infectedCounts; //counts the infected animals


    private boolean countsValid;

    /**
     * Construct a FieldStats object.  Set up a collection for counters for
     * each type of animal that we might find
     */
    public FieldStats() {
        counters = new HashMap<>();
        infectedCounts = new HashMap<>();
        countsValid = true;
        reset();
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field) {
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        if (!countsValid) {
            generateCounts(field);
        }
        for (Class key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer1.append(info.getName());
            buffer1.append(": ");
            buffer1.append(info.getCount());
            buffer1.append(' ');
        }
        return buffer1.toString();
        
    }

    /**
     * Invalidate the current set of statistics; reset all
     * counts to zero.
     */
    public void reset() {
        countsValid = false;
        for (Class key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
        resetInfected();
    }
    
    /*
     * find and update coutn of infcted animals 
     */
    public void incrementInfectedCount(Class animalClass) {
        Counter infectedAnimalCounts = infectedCounts.get(animalClass);
        
        if (infectedAnimalCounts == null) {
            infectedAnimalCounts = new Counter(animalClass.getName());
            infectedCounts.put(animalClass, infectedAnimalCounts);
        }
        infectedAnimalCounts.increment();
    }
    
    public void resetInfected() { //this is called every time the program ends 
        for (Class key : infectedCounts.keySet()) {
            Counter count = infectedCounts.get(key);
            count.reset();
        }
    }
    
    public String getTheInfectedPopulationDetails(Field field) {
        StringBuilder buffer = new StringBuilder();
        for (Class<?> key : infectedCounts.keySet()) {
            Counter info = infectedCounts.get(key);
            buffer.append(key.getSimpleName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(" ");
        }
        return buffer.toString();
    }

    /**
     * Increment the count for one class of animal
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Class animalClass) {
        Counter count = counters.get(animalClass);

        if (count == null) {

            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }

    /**
     * Indicate that an animal count has been completed.
     */
    public void countFinished() {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than 1 animal form alive
     */
    public boolean isViable(Field field) {
        int nonZero = 0;
        if (!countsValid) {
            generateCounts(field);
        }
        for (Class key : counters.keySet()) {
            Counter info = counters.get(key);
            if (info.getCount() > 0) {
                nonZero++;
            }
        }

        return nonZero >= 1;
    }

    /**
     * Generate counts of the number of animals.
     * These arent kept up to date.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field) {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);

                if (animal != null) {
                    incrementCount(animal.getClass());
                }
            }
        }
        countsValid = true;
    }
}