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

    public Monster getMonster(final MonsterType theMonsterType, final Point theSpot) throws SQLException {
        Monster monster;
        if (theMonsterType == null || theSpot == null) {
            throw new NullPointerException("Null parameter caught");
        } else {
            try {
                Database database = new Database();
                ResultSet rs;
                switch (theMonsterType) {
                    case theMonsterType.GOBLIN:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 1");
                        monster = new Monster(rs.getString("name"),
                                              MonsterType.GOBLIN, rs.getBoolean("is_elite"),
                                              rs.getInt("health"), theSpot);
                        break;
                    case theMonsterType.SKELETON:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 2");
                        monster = new Monster(MonsterType.SKELETON.getName(),
                                              MonsterType.SKELETON, false,
                                              MonsterType.SKELETON.getBaseHealth(),
                                              theSpot);
                        break;
                    case theMonsterType.SLIME:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 3");
                        monster = new Monster(MonsterType.SLIME.getName(),
                                              MonsterType.SLIME, false,
                                              MonsterType.SLIME.getBaseHealth(),
                                              theSpot);
                        break;
                    case theMonsterType.ORC:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 4");
                        monster = new Monster(MonsterType.ORC.getName(),
                                              MonsterType.ORC, true,
                                              MonsterType.ORC.getBaseHealth(),
                                              theSpot);
                        break;
                    case theMonsterType.BIG_SLIME:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 5");
                        monster = new Monster(MonsterType.BIG_SLIME.getName(),
                                              MonsterType.BIG_SLIME, true,
                                              MonsterType.BIG_SLIME.getBaseHealth(),
                                              theSpot);
                        break;
                    case theMonsterType.WIZARD:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 6");
                        monster = new Monster(MonsterType.WIZARD.getName(),
                                              MonsterType.WIZARD, true,
                                              MonsterType.WIZARD.getBaseHealth(),
                                              theSpot);
                        break;
                    case theMonsterType.GIANT:
                        rs = database.executeQuery("SELECT * FROM monster_types WHERE id = 7");
                        monster = new Monster(MonsterType.GIANT.getName(),
                                              MonsterType.GIANT, false,
                                              MonsterType.GIANT.getBaseHealth(),
                                              theSpot);
                        break;
                    default:
                        throw new IllegalArgumentException("Not valid monster type");
                }
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                // This is horrific code but SQLException or just Exception
                // had a chain effect of making methods and classes have throws
                // from just this one method. This stops monster from sent out null.
                throw new NullPointerException("SQL Exception in Monster Factory");
            }
            return monster;
        }
    }

}
