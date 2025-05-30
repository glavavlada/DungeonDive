package main.Model.character;

import main.Model.Database;
import main.Model.util.HeroType;
import main.Model.util.Point;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Factory class for hero creation.
 *
 * @author Jacob Hilliker
 * @version 5/29/2025
 */
public class HeroFactory {

    public Hero getHero(final String theHeroName, final HeroType theHeroType,
                        final Point theStartingPoint) {
        Hero hero;
        if (theHeroName == null || theHeroName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero name cannot be null or empty...");
        } else if (theHeroType == null) {
            throw new IllegalArgumentException("Hero Type cannot be null...");
        } else if (theStartingPoint == null) {
            throw new IllegalArgumentException("Hero Starting Point cannot be null...");
        } else {
            Database database = new Database();
            ResultSet rs;
            switch (theHeroType) {
                case theHeroType.WARRIOR:
                    rs = database.executeQuery("SELECT * FROM character_types WHERE id = 1");
                    hero = buildHero(rs, theHeroName, theHeroType, theStartingPoint);
                    break;
                case  theHeroType.PRIESTESS:
                    rs = database.executeQuery("SELECT * FROM character_types WHERE id = 2");
                    hero = buildHero(rs, theHeroName, theHeroType, theStartingPoint);
                    break;
                case theHeroType.THIEF:
                     rs = database.executeQuery("SELECT * FROM character_types WHERE id = 3");
                     hero = buildHero(rs, theHeroName, theHeroType, theStartingPoint);
                     break;
                default:
                     throw new IllegalArgumentException("Not valid HeroType");
            }
            database.closeConnection();
        }
        return hero;
    }

    private Hero buildHero(final ResultSet theRS, final String theHeroName,
                           final HeroType theHeroType, final Point theStartingPoint) {
        try {
            return new Hero.HeroBuilder().setName(theHeroName).
                    setPosition(theStartingPoint).
                    setHealth(theRS.getInt("base_health")).
                    setBaseAttackDamage(theRS.getInt("base_attack")).
                    setSpecialAttackDamage(theRS.getInt("special_attack_damage")).
                    setSpecialAttackName(theRS.getString("special_attack_name")).
                    setCritChance(theRS.getDouble("crit_chance")).
                    setCritMultiplier(theRS.getDouble("crit_multiplier")).
                    setDescription(theRS.getString("description")).
                    setHeroType(theHeroType).
                    build();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("SQL issues in buildHero of HeroFactory");
        }
    }

}
