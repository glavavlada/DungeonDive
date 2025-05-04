import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main class where the program is run.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/4/2025
 */
public class Main extends Application {
    /**
     * main method where the program is run.
     *
     * @param theArgs command line argument.
     */
    public static void main(String[] theArgs) {
        launch(theArgs);
    }

    @Override
    public void start(Stage thePrimaryStage) {
        thePrimaryStage.setTitle("DungeonDive");
        Button btn = new Button();
        btn.setText("Test Button");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent theEvent) {
                System.out.println("Hello World!");
            }

        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        thePrimaryStage.setScene(new Scene(root, 300, 250));
        thePrimaryStage.show();
    }
}