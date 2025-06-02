package main.Model;

import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.dungeon.Dungeon;
import main.Model.util.HeroType;
import main.Model.util.Point;

/**
 * Manages the core game logic and data for DungeonDive.
 * This class acts as a facade for the model components,
 * handling game initialization, and providing access to the player and dungeon.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class Model {
    private Hero myPlayer;
    private Dungeon myDungeon;
    private final Database myDatabase;
    private final HeroFactory myHeroFactory;

    private static final int DEFAULT_DUNGEON_WIDTH = 10;
    private static final int DEFAULT_DUNGEON_HEIGHT = 10;
    private static final String DEFAULT_DIFFICULTY = "Normal";

    /**
     * Constructor for Model.
     * Initializes the database connection.
     */
    public Model() {
        this.myDatabase = new Database();
        this.myHeroFactory = new HeroFactory();
    }

    /**
     * Initializes a new game with the selected hero type and default dungeon settings.
     *
     * @param theHeroType The type of hero selected by the player.
     * @param theHeroName The name for the hero, provided by the player.
     */
    public void initializeNewGame(final HeroType theHeroType, final String theHeroName) {
        Point startPosition = new Point(0, 0); // Default, will be overridden by dungeon spawn
        this.myPlayer = myHeroFactory.getHero(theHeroName, theHeroType, startPosition);

        this.myDungeon = new Dungeon(DEFAULT_DUNGEON_WIDTH, DEFAULT_DUNGEON_HEIGHT, DEFAULT_DIFFICULTY);

        if (this.myDungeon.getHeroSpawnPoint() != null) {
            this.myPlayer.setPosition(this.myDungeon.getHeroSpawnPoint());
        } else {
            System.err.println("Warning: Hero spawn point not set in dungeon, using default (0,0).");
            // Player position is already (0,0) from constructor if spawn point is null
        }

        System.out.println("Game initialized with " + theHeroType.getDisplayName() + " '" + theHeroName + "'.");
        System.out.println("Dungeon generated: " + myDungeon.getWidth() + "x" + myDungeon.getHeight() + ", Difficulty: " + myDungeon.getDifficulty());
    }

    /**
     * Resets the current game state by clearing the player and dungeon.
     * This is typically used when quitting to the main menu.
     */
    public void resetGame() {
        this.myPlayer = null;
        this.myDungeon = null;
        // Reset any other game-specific state variables here if necessary
        System.out.println("Game model has been reset.");
    }


    // --- Getters ---

    public Hero getPlayer() {
        return myPlayer;
    }

    public Dungeon getDungeon() {
        return myDungeon;
    }

    public Database getDatabase() {
        return myDatabase;
    }

    // --- Save and Load Game (Placeholders) ---

    public boolean saveGame(final String theSaveSlotName) {
        if (myPlayer == null || myDungeon == null) {
            System.err.println("Cannot save game: Player or Dungeon not initialized.");
            return false;
        }
        // TODO: Implement saving logic using myDatabase
        System.out.println("Saving game to slot: " + theSaveSlotName + " (Not yet implemented)");
        return false;
    }

    public boolean loadGame(final String theSaveSlotName) {
        // TODO: Implement loading logic using myDatabase
        System.out.println("Loading game from slot: " + theSaveSlotName + " (Not yet implemented)");
        // This method should reconstruct myPlayer and myDungeon from database data
        // and then potentially call a method to update the UI.
        return false;
    }

    // --- Setters ---
    public void setPlayer(final Hero thePlayer) {
        this.myPlayer = thePlayer;
        System.out.println("Player set to: " + (thePlayer != null ? thePlayer.getName() : "null"));
    }

    public void setDungeon(final Dungeon theDungeon) {
        this.myDungeon = theDungeon;
        System.out.println("Dungeon set: " + (theDungeon != null ? "loaded" : "null"));
    }

}
