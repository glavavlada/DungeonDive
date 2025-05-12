package Model;

import Model.character.Hero;
import Model.dungeon.Dungeon;
import Model.util.HeroType;
import Model.util.Point;

/**
 * Where the logic and data of DungeonDive is
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 4/26/2025
 */
public class Model {
    private Hero player;
    private Dungeon dungeon;
    private Database database;

    /**
     * constructor Model.
     */
    public Model() {
        // Initialize database connection
        this.database = new Database();
    }

    /**
     * initialize new game with selected hero type
     *
     * @param heroType type of hero selected by player
     */
    public void initializeGame(HeroType heroType) {
        //create hero at starting position (0,0 for now)
        String heroName = "Player"; //this should come from UI input
        Point startPosition = new Point(0, 0);

        //get base stats based on hero type
        int initialHealth = switch (heroType) {
            case WARRIOR -> 125;
            case PRIESTESS -> 90;
            case THIEF -> 75;
            default -> 100;
        };

        //create hero
        Hero player = new Hero(heroName, heroType, initialHealth, startPosition);

        //generate dungeon
        Dungeon dungeon = new Dungeon();

        //store references
        this.player = player;
        this.dungeon = dungeon;
    }

    /**
     * create hero with specified parameters
     *
     * @param name hero's name
     * @param type hero's type
     * @param startPosition starting position
     * @return A new Hero object
     */
    private Hero createHero(String name, HeroType type, Point startPosition) {
        // get base stats from database based on hero type
        int initialHealth = 100; // temp default value

        //create and return the hero
        return new Hero(name, type, initialHealth, startPosition);
    }

    /**
     * Generate new dungeon
     *
     * @return new dungeon object
     */
    private Dungeon generateDungeon() {
        // create new dungeon (placeholder for now)
        Dungeon newDungeon = new Dungeon();

        // generate rooms, place pillars, entrance, exit, traps, monsters, etc
        // this would be where the algorithm goes

        return newDungeon;
    }

    /**
     * get player hero
     *
     * @return the player's hero
     */
    public Hero getPlayer() {
        return player;
    }

    /**
     * get current dungeon
     *
     * @return The current dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }
}