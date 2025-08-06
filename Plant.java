import javafx.scene.paint.Color;

/**
 * This is the plant class
 * The plant cells are green and they occupy empty cells while also having the chance of
 * being eaten by prey.
 * When a prey dies of starvation , old age or disease, they turn into plants
 */
public class Plant {
    private Color color;

    public Plant() {
        this.color = Color.LIGHTGREEN; // Plants are green
    }

    public Color getTheColor() {
        return color;
    }
}
