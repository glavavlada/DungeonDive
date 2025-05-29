package main.Model.character;

import main.Model.util.HeroType;
import main.Model.util.Point;

/**
 * Simple factory class for hero creation.
 *
 * @author Jacob Hilliker
 * @version 5/28/2025
 */
public class HeroFactory {

    public Hero getHero(final String theHeroName, final HeroType theHeroType,
                        final Point theStartingPoint) {
        Hero hero;
        if (theHeroName == null || theHeroType == null || theStartingPoint == null) {
            throw new IllegalArgumentException("Null parameter caught in getHero of " +
                                               "HeroFactory...");
        } else {
            hero = new Hero(theHeroName, theHeroType, theStartingPoint);
        }
        return hero;
    }

}
