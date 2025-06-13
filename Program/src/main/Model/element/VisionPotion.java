package main.Model.element;

import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;


/**
 * Class for the Vision Potion which reveals nearby tiles.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class VisionPotion extends Item {

    private final Dungeon myDungeon;

    /**
     * Constructor.
     *
     * @param theName item name.
     * @param theDescription item description.
     * @param theDungeon the dungeon that the potion will light up.
     */
    public VisionPotion(final String theName, final String theDescription, final Dungeon theDungeon) {
        super(theName, theDescription);
        if (theDungeon == null) {
            throw new IllegalArgumentException("Dungeon cannot be null for potion");
        }
        myDungeon = theDungeon;
    }

    /**
     * Lights up nearby tiles on minimap.
     *
     * @param theHero The hero using the potion.
     */
    @Override
    public void use(final Hero theHero) {
        if (theHero != null && theHero.isAlive()) {

            Point spot = theHero.getPosition();
            int x = spot.getX();
            int y = spot.getY();
            Room currRoom;

            int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dy = {-1,  0,  1, -1, 1, -1, 0, 1};

            for (int rooms = 0; rooms < 8; rooms++) {
                int currX = x + dx[rooms];
                int currY = y + dy[rooms];

                if (currX >= 0 && currX < myDungeon.getHeight() && currY >= 0 && currY < myDungeon.getWidth()) {
                    myDungeon.getRoom(currX, currY).setVisited(true);
                }
            }
        } else {
            if (theHero == null) {
                throw new NullPointerException("Hero is null for vision potion use method.");
            }
            if (!theHero.isAlive()) {
                throw new IllegalArgumentException("Hero must be alive to use potion.");
            }
        }
    }
}
