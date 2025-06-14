package main.Controller;

import main.Model.Model;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.util.HeroType;
import main.View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Controls how user input is passed from view to model
 * This class serves as the main controller in the MVC architecture,
 * managing game initialization, hero selection, and coordinating
 * between the UI and game model
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class Controller {

    /**
     * Flag indicating whether the hero's name has been properly set
     */
    private boolean myNameSet = false;

    /**
     * The name of the hero character chosen by player
     * Defaults to "Player" if no custom name is provided
     */
    private String myHeroName = "Player";

    /**
     * The main game model that contains all game state and logic
     * This model manages dungeon, player character, game progression,
     * and all core game mechanics
     */
    private final Model myGameModel;

    /**
     * Reference to text field UI component where player enters their hero's name
     */
    private TextField myHeroNameField;

    /**
     * Controller responsible for managing game logic and operations
     * Handles game state transitions, win/lose conditions, and coordinates
     * between model and view during active gameplay
     */
    private GameController myGameController;

    /**
     * Controller responsible for handling user input during gameplay
     * Processes keyboard input, movement commands, and other player actions
     */
    private InputController myInputController;

    /**
     * Controller responsible for managing overall state of game
     * Tracks current game phase, menu states, and coordinates state transitions
     * between different parts of the application
     */
    private StateController myStateController;

    /**
     * Constructs new Controller instance
     */
    public Controller() {
        this.myGameModel = new Model();
    }

    /**
     * Gets main game model instance
     *
     * @return Model instance containing all game state and logic
     */
    public Model getGameModel() {
        return myGameModel;
    }

    /**
     * Initiates process of starting a new game
     * Transitions UI to hero selection screen where player
     * can choose their character type and name
     *
     * @param theEvent ActionEvent that triggered this method call
     * @param theUI GameUI instance used to control screen transitions
     */
    public void newGame(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showHeroSelection();
    }

    /**
     * Displays saved games screen
     * Allows player to view and load previously saved game sessions
     *
     * @param theEvent ActionEvent that triggered this method call
     * @param theUI GameUI instance used to control screen transitions
     */
    public void savedGames(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showSavesScreen();
    }

    /**
     * Exits application completely
     * Terminates program with exit code 0
     *
     * @param theEvent ActionEvent that triggered this method call
     */
    public void exitProgram(final ActionEvent theEvent) {
        System.exit(0);
    }

    /**
     * Sets hero's name based on user input
     * Validates input and sets name flag appropriately
     * If the input is null or empty, defaults to "Player"
     *
     * @param theHeroNameInput name input from user, may be null or empty
     */
    public void setHeroName(final String theHeroNameInput) {
        if (theHeroNameInput != null && !theHeroNameInput.trim().isEmpty()) {
            this.myHeroName = theHeroNameInput.trim();
            this.myNameSet = true;
            System.out.println("Hero name set to: " + this.myHeroName);
        } else {
            this.myHeroName = "Player";
            this.myNameSet = false; // Or true if you allow default name
            System.out.println("Hero name is empty or invalid; will use default or prompt.");
        }
    }

    /**
     * Updates label with description of selected hero type
     * Maps integer hero type codes to HeroType enums and displays their descriptions
     *
     * @param theHeroType integer code representing hero type (1=WARRIOR, 2=PRIESTESS, 3=THIEF)
     * @param theDescription Label component to update with hero description
     */
    public void heroDescription(final int theHeroType, final Label theDescription) {
        HeroType selectedHeroType = null;
        switch (theHeroType) {
            case 1: selectedHeroType = HeroType.WARRIOR; break;
            case 2: selectedHeroType = HeroType.PRIESTESS; break;
            case 3: selectedHeroType = HeroType.THIEF; break;
            default:
                theDescription.setText("Select a hero to see their description.");
                return;
        }
        // Use the description from the enum
        theDescription.setText(selectedHeroType.getDescription());
    }


    /**
     * Starts new game with selected hero type and name
     * Validates that both hero type is selected and name is provided,
     * then initializes game model and controllers before transitioning to gameplay
     *
     * @param theEvent ActionEvent that triggered this method call
     * @param theUI GameUI instance used for screen transitions and controller setup
     * @param theHeroes ToggleGroup containing hero type selection radio buttons
     */
    public void startGame(final ActionEvent theEvent, final GameUI theUI,
                          final ToggleGroup theHeroes) {

        if (myHeroNameField != null) {
            setHeroName(myHeroNameField.getText());
        }

        if (theHeroes.getSelectedToggle() == null) {
            System.err.println("Start Game Error: No hero type selected.");
            return;
        }
        if (!myNameSet || myHeroName.trim().isEmpty()) {
            if (myHeroNameField != null && !myHeroNameField.getText().trim().isEmpty()) {
                setHeroName(myHeroNameField.getText());
            } else {
                System.err.println("Start Game Error: Hero name not set or is empty.");
                return;
            }
        }

        HeroType selectedType = getHeroTypeFromToggle(theHeroes);
        if (selectedType == null) {
            System.err.println("Start Game Error: Could not determine selected hero type.");
            return;
        }

        myGameModel.initializeNewGame(selectedType, myHeroName);
        initializeGameControllers(theUI); // Initialize controllers AFTER model is ready
        theUI.showGameScreen(); // Show game screen AFTER controllers are set up
    }

    /**
     * Displays help menu screen
     * Shows game instructions and controls player
     *
     * @param theEvent the ActionEvent that triggered this method call
     * @param theUI the GameUI instance used to show the help screen
     */
    public void helpMenu(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showHelpMenu();
    }

    /**
     * Pauses current game and shows pause menu
     * Allows player to access game options without losing progress
     *
     * @param theEvent ActionEvent that triggered this method call
     * @param theUI GameUI instance used to show the pause menu
     */
    public void pauseGame(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showPauseMenu();
    }


    /**
     * Resumes current game session
     * Checks if game is in win state and handles accordingly,
     * otherwise returns to main game screen
     *
     * @param theUI GameUI instance used to transition back to game screen
     */
    public void resumeCurrentGame(final GameUI theUI) {
        if (myGameModel.getPlayer().getBossSlain()) {
            myGameController.checkWinCondition();
        } else {
            //debug player movement when resuming
            if (myGameController != null) {
                myGameController.debugPlayerMovement();
            }
            theUI.showGameScreen();
        }
    }

    /**
     * Quits current game and returns to main menu
     * Resets model, clears all game controllers, and transitions
     * to intro screen
     * All game progress will be lost unless previously saved
     *
     * @param theUI GameUI instance used to transition to intro screen
     */
    public void quitToMenu(final GameUI theUI) {
        myGameModel.resetGame();
        myGameController = null;
        myInputController = null;
        myStateController = null;
        theUI.showIntroScreen();
    }


    /**
     * Extracts selected HeroType from ToggleGroup of radio buttons
     *
     * @param theHeroes ToggleGroup containing hero selection radio buttons
     * @return selected HeroType, or null if no selection is made
     */
    private HeroType getHeroTypeFromToggle(final ToggleGroup theHeroes) {
        if (theHeroes.getSelectedToggle() == null) return null;

        RadioButton selectedButton = (RadioButton) theHeroes.getSelectedToggle();
        if (selectedButton.getUserData() instanceof HeroType) {
            return (HeroType) selectedButton.getUserData();
        }

        String heroNameString = selectedButton.getText().trim();
        try {
            return HeroType.valueOf(heroNameString.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Could not map radio button text '" + heroNameString + "' to HeroType. Check UserData or button text. Defaulting to WARRIOR.");
        }
        return HeroType.WARRIOR; // Fallback
    }

    /**
     * Initializes game-specific controllers
     * This should be called when a new game starts or a game is loaded
     * @param theUI GameUI reference.
     */
    private void initializeGameControllers(final GameUI theUI) {
        try {
            //initialize StateController first
            myStateController = new StateController();

            //then initialize GameController with Model, UI, and StateController
            myGameController = new GameController(myGameModel, theUI, myStateController);

            //initialize InputController with GameController and StateController
            myInputController = new InputController(myGameController, myStateController);

            System.out.println("Game controllers initialized.");

            //set input controller in the UI
            if (theUI != null) {
                theUI.setInputController(myInputController);
            } else {
                System.err.println("Warning: GameUI is null when setting InputController");
            }

            //verify the setup
            if (myGameController != null && myInputController != null && myStateController != null) {
                System.out.println("All controllers properly initialized");
            } else {
                System.err.println("Error: One or more controllers failed to initialize");
            }

        } catch (Exception e) {
            System.err.println("Error initializing game controllers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Provides access to player's Hero object
     *
     * @return Hero object representing player character
     */
    public Hero getPlayer() {
        return myGameModel.getPlayer();
    }


    /**
     * Provides access to the game's Dungeon object
     *
     * @return Dungeon object containing game world
     */
    public Dungeon getDungeon() {
        return myGameModel.getDungeon();
    }


    /**
     * Provides access to the GameController instance
     *
     * @return GameController instance, or null if not initialized
     */
    public GameController getGameController() {
        return myGameController;
    }


    /**
     * Initializes game controllers for a loaded game session
     * specifically designed for restoring a previously saved game state
     *
     * @param theUI GameUI instance to connect with initialized controllers
     */
    public void initializeGameControllersForLoadedGame(final GameUI theUI) {
        System.out.println("Initializing controllers for loaded game...");

        //initialize controllers
        initializeGameControllers(theUI);

        //verify everything is connected
        if (myInputController != null && theUI != null) {
            theUI.setInputController(myInputController);
            System.out.println("InputController connected to UI");
        } else {
            System.err.println("ERROR: Failed to connect InputController to UI");
        }
    }
}
