import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.Group; 
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.HBox; 
import javafx.scene.paint.Color; 
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;


/**
 * A graphical view of the simulation grid. The view displays a rectangle for
 * each location.
 *
 * @author David J. Barnes, Michael Kölling & Jeffery Raphael
 * @version 2024.02.03
 */

public class SimulatorView extends Application {

    public static final int GRID_WIDTH = 100;
    public static final int GRID_HEIGHT = 80;    
    public static final int WIN_WIDTH = 800;
    public static final int WIN_HEIGHT = 800;  
    
    private static final Color EMPTY_COLOR = Color.WHITE;

    private final String GENERATION_PREFIX = "Generation: ";
    private final String POPULATION_PREFIX = "Population: "; //label for population

    private Label genLabel, population,infectedPopulation, infoLabel;

    private FieldCanvas fieldCanvas;
    private FieldStats stats;
    private Simulator simulator;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    @Override
    public void start(Stage stage) {

        stats = new FieldStats();
        fieldCanvas = new FieldCanvas(WIN_WIDTH - 50, WIN_HEIGHT - 50);
        fieldCanvas.setScale(GRID_HEIGHT, GRID_WIDTH);
        simulator = new Simulator(GRID_HEIGHT, GRID_WIDTH);

        Group root = new Group();

        genLabel = new Label(GENERATION_PREFIX);
        infoLabel = new Label("  ");
        population = new Label(POPULATION_PREFIX);
        infectedPopulation = new Label("Infected Population: ");


        BorderPane bPane = new BorderPane();
        HBox infoPane = new HBox();
        HBox bottomPane = new HBox();  // Combine population and legend into one pane
        HBox topPane = new HBox();  


        infoPane.setSpacing(10);
        infoPane.getChildren().addAll(genLabel, infoLabel);

        
        bottomPane.setSpacing(20);
        bottomPane.getChildren().addAll(population);
        addColorLegend(bottomPane);  // addingthe legend
        
        topPane.setSpacing(20);
        topPane.getChildren().addAll(genLabel, infoLabel, infectedPopulation);

        bPane.setTop(infoPane);
        bPane.setCenter(fieldCanvas);
        bPane.setBottom(bottomPane);  // Single bottom pane with population + legend
        bPane.setTop(topPane);  // Single top pane with INFECTED population + generation count
        
    

        BorderPane.setAlignment(bottomPane, javafx.geometry.Pos.BOTTOM_RIGHT);
        BorderPane.setAlignment(topPane, javafx.geometry.Pos.TOP_RIGHT);


        root.getChildren().add(bPane);
        Scene scene = new Scene(root, WIN_WIDTH, WIN_HEIGHT);

        stage.setScene(scene);
        stage.setTitle("Predator/Prey Simulation");
        updateCanvas(simulator.getStep(), simulator.getField());
        

        stage.show();
        
    }


    /**
     * Add a color legend to the simulation interface.
     */
    private void addColorLegend(HBox bottomPane) {
        
        //Bear = red
        //Wolf = pink
        //Wild boar = grey
        //Deer = blue
        //Squirrel = purple 
        
        Label legendLabel = new Label("Legend:");
    
        Label bearLabel = new Label("Bear");
        bearLabel.setTextFill(Color.RED);
    
        Label wildBoarLabel = new Label("Wild boar");
        wildBoarLabel.setTextFill(Color.GRAY); 
    
        Label wolfLabel = new Label("Wolf");
        wolfLabel.setTextFill(Color.PINK);
    
        Label deerLabel = new Label("Deer");
        deerLabel.setTextFill(Color.BLUE); 
    
        Label squirrelLabel = new Label("Squirrel");
        squirrelLabel.setTextFill(Color.PURPLE);
        
        Label plantLabel = new Label("Plant");
        plantLabel.setTextFill(Color.LIGHTGREEN);
    
        // Add all labels to the bottom pane
        bottomPane.getChildren().addAll(legendLabel, bearLabel, wildBoarLabel, 
        wolfLabel, deerLabel, squirrelLabel, plantLabel);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text) {
        infoLabel.setText(text);
        }

    /**
     * Show the current status of the field.
     * @param generation The current generation.
     * @param field The field whose status is to be displayed.
     */
    public void updateCanvas(int generation, Field field) {
        genLabel.setText(GENERATION_PREFIX + generation);
        stats.reset(); //reset normal population
        stats.resetInfected(); //also reset infected population


        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) { //iterate through grid
                Animal animal = field.getObjectAt(row, col);
                Plant plant = field.getPlantAt(new Location(row, col));

                if (animal != null && animal.isAlive()) {
                    stats.incrementCount(animal.getClass()); //adds specific animal to count 
                
                // Track infected animals and adds specific infected animal to count 
                if (animal.isSick()) {
                    stats.incrementInfectedCount(animal.getClass());
                }
                    // Darken the color if the animal sick
                    Color colorOfAnimal = animal.isSick() ? animal.getTheColor().darker() : animal.getTheColor();
                    fieldCanvas.drawMark(col, row, colorOfAnimal);
                } else if (plant != null) {
                    fieldCanvas.drawMark(col, row, plant.getTheColor());
                } else {
                    fieldCanvas.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();
        infectedPopulation.setText("Infected Population: " + stats.getTheInfectedPopulationDetails(field));
        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
    }


    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Run the simulation from its current state for the given number of
     * generations.  Stop before the given number of generations if the
     * simulation ceases to be viable.
     * @param numGenerations The number of generations to run for.
     */
    public void simulate(int numStep) {
        new Thread(() -> {
           
            for (int gen = 1; gen <= numStep; gen++) {
                simulator.simulateOneStep();    
                simulator.delay(500);
                Platform.runLater(() -> {
                    updateCanvas(simulator.getStep(), simulator.getField());
                });
            }
            
        }).start();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        simulator.reset();
        updateCanvas(simulator.getStep(), simulator.getField());
    }
    
    /**
     * Application main that loads and initializes the specified Application class 
     * on the JavaFX Application Thread.
     */
    public static void main(String args[]){           
        launch(args);      
   } 
}