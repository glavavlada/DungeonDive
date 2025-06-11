package main.Model.element;

import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;

public class VisionPotion extends Item {

    private final Dungeon myDungeon;

    public VisionPotion(final String theName, final String theDescription, final Dungeon theDungeon) {
        super(theName, theDescription);
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
        }
    }
}
