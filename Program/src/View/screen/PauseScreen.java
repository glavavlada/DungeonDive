package View.screen;

import View.GameUI;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PauseScreen extends Screen {

    public PauseScreen(Stage thePrimaryStage) {
        super(thePrimaryStage);
    }

    @Override
    public void showScreen(GameUI theUI) {
        Pane root = new Pane();
        Scene gameScreen = new Scene(root, 600, 500);

        Button resumeBtn = new Button();
        resumeBtn.setText("Resume Game");
        Button quitToMenuBtn = new Button();
        quitToMenuBtn.setText("Quit to Menu");

        setButtonSize(resumeBtn);
        setButtonSize(quitToMenuBtn);

        root.getChildren().addAll(resumeBtn, quitToMenuBtn);

        resumeBtn.setOnAction(event -> CONTROLLER.resumeGame(theUI));
        quitToMenuBtn.setOnAction(event -> CONTROLLER.quitToMenu(theUI));

        PRIMARY_STAGE.setScene(gameScreen);
        PRIMARY_STAGE.show();

        resumeBtn.setLayoutX((600 - resumeBtn.getWidth()) / 2);
        resumeBtn.setLayoutY((500 - resumeBtn.getHeight()) / 2);
        quitToMenuBtn.setLayoutX((600 - quitToMenuBtn.getWidth()) / 2);
        quitToMenuBtn.setLayoutY((440 - quitToMenuBtn.getHeight()) / 2);
    }
}
