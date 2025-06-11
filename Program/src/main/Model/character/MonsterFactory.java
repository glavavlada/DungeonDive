package main.Model.character;

import main.Model.Database;
import main.Model.util.MonsterType;
import main.Model.util.Point;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Factory class for creating Monsters.
 *
 * @author Jacob Hilliker
 * @version 5/28/2025
 */
public class MonsterFactory {

    public Monster getMonster(final MonsterType theMonsterType, final Point theSpot) {
        Monster monster;
        if (theMonsterType == null || theSpot == null) {
            throw new NullPointerException("Null parameter caught");
        } else {
            Database database = new Database();
            ResultSet rs;
            switch (theMonsterType) {
                case theMonsterType.GOBLIN:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 1");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.SKELETON:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 2");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.SLIME:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 3");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.ORC:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 4");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.BIG_SLIME:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 5");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.WIZARD:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 6");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                case theMonsterType.GIANT:
                    rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 7");
                    monster = buildMonster(rs, theMonsterType, theSpot);
                    break;
                default:
                    throw new IllegalArgumentException("Not valid monster type");
            }
            database.closeConnection();
            return monster;
        }
    }

    private Monster buildMonster(final ResultSet theRS, final MonsterType theMonsterType, final Point theSpot) {
        try {
            return new Monster.MonsterBuilder().setName(theRS.getString("name")).
                               setHealth(theRS.getInt("health")).
                               setMaxHealth(theRS.getInt("health")).
                               setPosition(theSpot).
                               setBaseAttackDamage(theRS.getInt("attack")).
                               setSpecialAttackName(theRS.getString("special_attack_name")).
                               setCritChance(theRS.getDouble("crit_chance")).
                               setCritMultiplier(theRS.getDouble("crit_multiplier")).
                               setGoldReward(theRS.getInt("gold_reward")).
                               setDescription(theRS.getString("description")).
                               setMonsterType(theMonsterType).
                               setIsElite(theRS.getBoolean("is_elite")).
                               build();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("SQL issues in buildMonster of MonsterFactory...");
        }
    }

}
