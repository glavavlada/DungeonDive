package test.Model.Util;

import main.Model.util.MonsterType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MonsterType enum.
 * Tests all enum values and methods to ensure 100% coverage.
 */
class MonsterTypeTest {

    @Test
    void enumValues_containsAllExpectedMonsterTypes() {
        MonsterType[] monsterTypes = MonsterType.values();
        assertEquals(7, monsterTypes.length, "MonsterType enum should have exactly 7 values.");

        // Verify all expected monster types are present
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.GOBLIN),
                  "MonsterType enum should contain GOBLIN.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.SKELETON),
                  "MonsterType enum should contain SKELETON.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.SLIME),
                  "MonsterType enum should contain SLIME.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.ORC),
                  "MonsterType enum should contain ORC.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.BIG_SLIME),
                  "MonsterType enum should contain BIG_SLIME.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.WIZARD),
                  "MonsterType enum should contain WIZARD.");
        assertTrue(java.util.Arrays.asList(monsterTypes).contains(MonsterType.GIANT),
                  "MonsterType enum should contain GIANT.");
    }

    @Test
    void getName_returnsCorrectNames() {
        assertEquals("Goblin", MonsterType.GOBLIN.getName(),
                    "GOBLIN getName should return 'Goblin'.");
        assertEquals("Skeleton", MonsterType.SKELETON.getName(),
                    "SKELETON getName should return 'Skeleton'.");
        assertEquals("Slime", MonsterType.SLIME.getName(),
                    "SLIME getName should return 'Slime'.");
        assertEquals("Orc", MonsterType.ORC.getName(),
                    "ORC getName should return 'Orc'.");
        assertEquals("Big Slime", MonsterType.BIG_SLIME.getName(),
                    "BIG_SLIME getName should return 'Big Slime'.");
        assertEquals("Wizard", MonsterType.WIZARD.getName(),
                    "WIZARD getName should return 'Wizard'.");
        assertEquals("Giant", MonsterType.GIANT.getName(),
                    "GIANT getName should return 'Giant'.");
    }

    @Test
    void getDescription_returnsCorrectDescriptions() {
        assertEquals("A small, green, and mischievous creature often found in groups.",
                    MonsterType.GOBLIN.getDescription(),
                    "GOBLIN should have correct description.");
        assertEquals("An animated skeleton warrior, relentless and tireless.",
                    MonsterType.SKELETON.getDescription(),
                    "SKELETON should have correct description.");
        assertEquals("A gelatinous blob that can slowly engulf its prey.",
                    MonsterType.SLIME.getDescription(),
                    "SLIME should have correct description.");
        assertEquals("A large, brutish humanoid known for its strength and ferocity.",
                    MonsterType.ORC.getDescription(),
                    "ORC should have correct description.");
        assertEquals("A massive slime that can split into smaller slimes upon taking enough damage.",
                    MonsterType.BIG_SLIME.getDescription(),
                    "BIG_SLIME should have correct description.");
        assertEquals("A rogue magic-user, wielding destructive spells.",
                    MonsterType.WIZARD.getDescription(),
                    "WIZARD should have correct description.");
        assertEquals("A colossal humanoid creature, a true test of a hero's might.",
                    MonsterType.GIANT.getDescription(),
                    "GIANT should have correct description.");
    }

    @Test
    void getBaseHealth_returnsCorrectValues() {
        assertEquals(40, MonsterType.GOBLIN.getBaseHealth(),
                    "GOBLIN should have 40 base health.");
        assertEquals(35, MonsterType.SKELETON.getBaseHealth(),
                    "SKELETON should have 35 base health.");
        assertEquals(30, MonsterType.SLIME.getBaseHealth(),
                    "SLIME should have 30 base health.");
        assertEquals(80, MonsterType.ORC.getBaseHealth(),
                    "ORC should have 80 base health.");
        assertEquals(70, MonsterType.BIG_SLIME.getBaseHealth(),
                    "BIG_SLIME should have 70 base health.");
        assertEquals(60, MonsterType.WIZARD.getBaseHealth(),
                    "WIZARD should have 60 base health.");
        assertEquals(200, MonsterType.GIANT.getBaseHealth(),
                    "GIANT should have 200 base health.");
    }

    @Test
    void getBaseAttack_returnsCorrectValues() {
        assertEquals(8, MonsterType.GOBLIN.getBaseAttack(),
                    "GOBLIN should have 8 base attack.");
        assertEquals(10, MonsterType.SKELETON.getBaseAttack(),
                    "SKELETON should have 10 base attack.");
        assertEquals(5, MonsterType.SLIME.getBaseAttack(),
                    "SLIME should have 5 base attack.");
        assertEquals(15, MonsterType.ORC.getBaseAttack(),
                    "ORC should have 15 base attack.");
        assertEquals(12, MonsterType.BIG_SLIME.getBaseAttack(),
                    "BIG_SLIME should have 12 base attack.");
        assertEquals(20, MonsterType.WIZARD.getBaseAttack(),
                    "WIZARD should have 20 base attack.");
        assertEquals(25, MonsterType.GIANT.getBaseAttack(),
                    "GIANT should have 25 base attack.");
    }

    @Test
    void getSpecialAttackName_returnsCorrectNames() {
        assertEquals("Stab", MonsterType.GOBLIN.getSpecialAttackName(),
                    "GOBLIN should have 'Stab' special attack.");
        assertEquals("Bone Throw", MonsterType.SKELETON.getSpecialAttackName(),
                    "SKELETON should have 'Bone Throw' special attack.");
        assertEquals("Acid Splash", MonsterType.SLIME.getSpecialAttackName(),
                    "SLIME should have 'Acid Splash' special attack.");
        assertEquals("Cleave", MonsterType.ORC.getSpecialAttackName(),
                    "ORC should have 'Cleave' special attack.");
        assertEquals("Engulf", MonsterType.BIG_SLIME.getSpecialAttackName(),
                    "BIG_SLIME should have 'Engulf' special attack.");
        assertEquals("Lightning Bolt", MonsterType.WIZARD.getSpecialAttackName(),
                    "WIZARD should have 'Lightning Bolt' special attack.");
        assertEquals("Ground Slam", MonsterType.GIANT.getSpecialAttackName(),
                    "GIANT should have 'Ground Slam' special attack.");
    }

    @Test
    void getCritChance_returnsCorrectValues() {
        assertEquals(0.04, MonsterType.GOBLIN.getCritChance(), 0.001,
                    "GOBLIN should have 0.04 crit chance.");
        assertEquals(0.03, MonsterType.SKELETON.getCritChance(), 0.001,
                    "SKELETON should have 0.03 crit chance.");
        assertEquals(0.02, MonsterType.SLIME.getCritChance(), 0.001,
                    "SLIME should have 0.02 crit chance.");
        assertEquals(0.06, MonsterType.ORC.getCritChance(), 0.001,
                    "ORC should have 0.06 crit chance.");
        assertEquals(0.05, MonsterType.BIG_SLIME.getCritChance(), 0.001,
                    "BIG_SLIME should have 0.05 crit chance.");
        assertEquals(0.08, MonsterType.WIZARD.getCritChance(), 0.001,
                    "WIZARD should have 0.08 crit chance.");
        assertEquals(0.10, MonsterType.GIANT.getCritChance(), 0.001,
                    "GIANT should have 0.10 crit chance.");
    }

    @Test
    void getCritMultiplier_returnsCorrectValues() {
        assertEquals(2.0, MonsterType.GOBLIN.getCritMultiplier(), 0.001,
                    "GOBLIN should have 2.0 crit multiplier.");
        assertEquals(1.8, MonsterType.SKELETON.getCritMultiplier(), 0.001,
                    "SKELETON should have 1.8 crit multiplier.");
        assertEquals(1.5, MonsterType.SLIME.getCritMultiplier(), 0.001,
                    "SLIME should have 1.5 crit multiplier.");
        assertEquals(2.0, MonsterType.ORC.getCritMultiplier(), 0.001,
                    "ORC should have 2.0 crit multiplier.");
        assertEquals(1.7, MonsterType.BIG_SLIME.getCritMultiplier(), 0.001,
                    "BIG_SLIME should have 1.7 crit multiplier.");
        assertEquals(2.2, MonsterType.WIZARD.getCritMultiplier(), 0.001,
                    "WIZARD should have 2.2 crit multiplier.");
        assertEquals(2.0, MonsterType.GIANT.getCritMultiplier(), 0.001,
                    "GIANT should have 2.0 crit multiplier.");
    }

    @Test
    void getGoldReward_returnsCorrectValues() {
        assertEquals(5, MonsterType.GOBLIN.getGoldReward(),
                    "GOBLIN should give 5 gold reward.");
        assertEquals(3, MonsterType.SKELETON.getGoldReward(),
                    "SKELETON should give 3 gold reward.");
        assertEquals(2, MonsterType.SLIME.getGoldReward(),
                    "SLIME should give 2 gold reward.");
        assertEquals(10, MonsterType.ORC.getGoldReward(),
                    "ORC should give 10 gold reward.");
        assertEquals(15, MonsterType.BIG_SLIME.getGoldReward(),
                    "BIG_SLIME should give 15 gold reward.");
        assertEquals(20, MonsterType.WIZARD.getGoldReward(),
                    "WIZARD should give 20 gold reward.");
        assertEquals(100, MonsterType.GIANT.getGoldReward(),
                    "GIANT should give 100 gold reward.");
    }

    @Test
    void isElite_returnsCorrectValues() {
        // Regular monsters should not be elite
        assertFalse(MonsterType.GOBLIN.isElite(), "GOBLIN should not be elite.");
        assertFalse(MonsterType.SKELETON.isElite(), "SKELETON should not be elite.");
        assertFalse(MonsterType.SLIME.isElite(), "SLIME should not be elite.");

        // Elite monsters should be elite
        assertTrue(MonsterType.ORC.isElite(), "ORC should be elite.");
        assertTrue(MonsterType.BIG_SLIME.isElite(), "BIG_SLIME should be elite.");
        assertTrue(MonsterType.WIZARD.isElite(), "WIZARD should be elite.");

        // Boss should not be elite (different category)
        assertFalse(MonsterType.GIANT.isElite(), "GIANT should not be elite (it's a boss).");
    }

    @Test
    void isBoss_returnsCorrectValues() {
        // Regular and elite monsters should not be boss
        assertFalse(MonsterType.GOBLIN.isBoss(), "GOBLIN should not be boss.");
        assertFalse(MonsterType.SKELETON.isBoss(), "SKELETON should not be boss.");
        assertFalse(MonsterType.SLIME.isBoss(), "SLIME should not be boss.");
        assertFalse(MonsterType.ORC.isBoss(), "ORC should not be boss.");
        assertFalse(MonsterType.BIG_SLIME.isBoss(), "BIG_SLIME should not be boss.");
        assertFalse(MonsterType.WIZARD.isBoss(), "WIZARD should not be boss.");

        // Only GIANT should be boss
        assertTrue(MonsterType.GIANT.isBoss(), "GIANT should be boss.");
    }

    @Test
    void toString_returnsName() {
        assertEquals("Goblin", MonsterType.GOBLIN.toString(),
                    "GOBLIN toString should return name.");
        assertEquals("Skeleton", MonsterType.SKELETON.toString(),
                    "SKELETON toString should return name.");
        assertEquals("Slime", MonsterType.SLIME.toString(),
                    "SLIME toString should return name.");
        assertEquals("Orc", MonsterType.ORC.toString(),
                    "ORC toString should return name.");
        assertEquals("Big Slime", MonsterType.BIG_SLIME.toString(),
                    "BIG_SLIME toString should return name.");
        assertEquals("Wizard", MonsterType.WIZARD.toString(),
                    "WIZARD toString should return name.");
        assertEquals("Giant", MonsterType.GIANT.toString(),
                    "GIANT toString should return name.");
    }

    @Test
    void valueOf_validMonsterTypeNames_returnsCorrectEnum() {
        assertEquals(MonsterType.GOBLIN, MonsterType.valueOf("GOBLIN"),
                    "valueOf should return GOBLIN for 'GOBLIN' string.");
        assertEquals(MonsterType.SKELETON, MonsterType.valueOf("SKELETON"),
                    "valueOf should return SKELETON for 'SKELETON' string.");
        assertEquals(MonsterType.SLIME, MonsterType.valueOf("SLIME"),
                    "valueOf should return SLIME for 'SLIME' string.");
        assertEquals(MonsterType.ORC, MonsterType.valueOf("ORC"),
                    "valueOf should return ORC for 'ORC' string.");
        assertEquals(MonsterType.BIG_SLIME, MonsterType.valueOf("BIG_SLIME"),
                    "valueOf should return BIG_SLIME for 'BIG_SLIME' string.");
        assertEquals(MonsterType.WIZARD, MonsterType.valueOf("WIZARD"),
                    "valueOf should return WIZARD for 'WIZARD' string.");
        assertEquals(MonsterType.GIANT, MonsterType.valueOf("GIANT"),
                    "valueOf should return GIANT for 'GIANT' string.");
    }

    @Test
    void valueOf_invalidMonsterTypeName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> MonsterType.valueOf("INVALID"),
                "valueOf should throw IllegalArgumentException for invalid monster type name.");
        assertThrows(IllegalArgumentException.class, () -> MonsterType.valueOf("goblin"),
                "valueOf should throw IllegalArgumentException for lowercase monster type name.");
        assertThrows(IllegalArgumentException.class, () -> MonsterType.valueOf(""),
                "valueOf should throw IllegalArgumentException for empty string.");
        assertThrows(IllegalArgumentException.class, () -> MonsterType.valueOf("DRAGON"),
                "valueOf should throw IllegalArgumentException for non-existent monster type.");
    }

    @Test
    void valueOf_nullMonsterTypeName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MonsterType.valueOf(null),
                "valueOf should throw NullPointerException for null input.");
    }

    @Test
    void name_returnsCorrectName() {
        assertEquals("GOBLIN", MonsterType.GOBLIN.name(),
                "GOBLIN name should return 'GOBLIN'.");
        assertEquals("SKELETON", MonsterType.SKELETON.name(),
                "SKELETON name should return 'SKELETON'.");
        assertEquals("SLIME", MonsterType.SLIME.name(),
                "SLIME name should return 'SLIME'.");
        assertEquals("ORC", MonsterType.ORC.name(),
                "ORC name should return 'ORC'.");
        assertEquals("BIG_SLIME", MonsterType.BIG_SLIME.name(),
                "BIG_SLIME name should return 'BIG_SLIME'.");
        assertEquals("WIZARD", MonsterType.WIZARD.name(),
                "WIZARD name should return 'WIZARD'.");
        assertEquals("GIANT", MonsterType.GIANT.name(),
                "GIANT name should return 'GIANT'.");
    }

    @Test
    void ordinal_returnsValidOrdinalValues() {
        MonsterType[] types = MonsterType.values();
        for (int i = 0; i < types.length; i++) {
            assertEquals(i, types[i].ordinal(),
                    "Ordinal of " + types[i].name() + " should be " + i);
        }
    }

    @Test
    void compareTo_worksCorrectly() {
        MonsterType[] types = MonsterType.values();
        if (types.length > 1) {
            assertTrue(types[0].compareTo(types[1]) < 0,
                    "First enum should compare less than second enum.");
            assertTrue(types[1].compareTo(types[0]) > 0,
                    "Second enum should compare greater than first enum.");
        }

        // Test self comparison
        for (MonsterType type : types) {
            assertEquals(0, type.compareTo(type),
                    "Monster type should compare equal to itself.");
        }
    }

    @Test
    void equals_worksCorrectly() {
        for (MonsterType type : MonsterType.values()) {
            assertEquals(type, type, "Monster type should equal itself.");
            assertTrue(type.equals(type), "Monster type should equal itself using equals method.");
        }

        // Test inequality
        assertNotEquals(MonsterType.GOBLIN, MonsterType.SKELETON,
                "Different monster types should not be equal.");
        assertFalse(MonsterType.GOBLIN.equals(MonsterType.SKELETON),
                "Different monster types should not be equal using equals method.");
    }

    @Test
    void hashCode_isConsistent() {
        for (MonsterType type : MonsterType.values()) {
            int hashCode1 = type.hashCode();
            int hashCode2 = type.hashCode();
            assertEquals(hashCode1, hashCode2,
                    "Hash code should be consistent for " + type.name());
        }
    }

    @Test
    void enumConstantsAreImmutable() {
        // Verify that enum constants maintain their identity
        assertSame(MonsterType.GOBLIN, MonsterType.valueOf("GOBLIN"),
                "Enum constants should maintain singleton property.");

        for (MonsterType type : MonsterType.values()) {
            assertSame(type, MonsterType.valueOf(type.name()),
                    "Enum constant " + type.name() + " should maintain singleton property.");
        }
    }

    @Test
    void monsterTypeCategories_areCorrectlyClassified() {
        // Test regular monsters
        MonsterType[] regularMonsters = {MonsterType.GOBLIN, MonsterType.SKELETON, MonsterType.SLIME};
        for (MonsterType monster : regularMonsters) {
            assertFalse(monster.isElite(), monster.name() + " should not be elite.");
            assertFalse(monster.isBoss(), monster.name() + " should not be boss.");
        }

        // Test elite monsters
        MonsterType[] eliteMonsters = {MonsterType.ORC, MonsterType.BIG_SLIME, MonsterType.WIZARD};
        for (MonsterType monster : eliteMonsters) {
            assertTrue(monster.isElite(), monster.name() + " should be elite.");
            assertFalse(monster.isBoss(), monster.name() + " should not be boss.");
        }

        // Test boss monster
        assertTrue(MonsterType.GIANT.isBoss(), "GIANT should be boss.");
        assertFalse(MonsterType.GIANT.isElite(), "GIANT should not be elite.");
    }

    @Test
    void allGettersReturnNonNullValues() {
        for (MonsterType type : MonsterType.values()) {
            assertNotNull(type.getName(), type.name() + " getName should not return null.");
            assertNotNull(type.getDescription(), type.name() + " getDescription should not return null.");
            assertNotNull(type.getSpecialAttackName(), type.name() + " getSpecialAttackName should not return null.");

            assertFalse(type.getName().isEmpty(), type.name() + " getName should not return empty string.");
            assertFalse(type.getDescription().isEmpty(), type.name() + " getDescription should not return empty string.");
            assertFalse(type.getSpecialAttackName().isEmpty(), type.name() + " getSpecialAttackName should not return empty string.");
        }
    }

    @Test
    void statsArePositive() {
        for (MonsterType type : MonsterType.values()) {
            assertTrue(type.getBaseHealth() > 0, type.name() + " should have positive base health.");
            assertTrue(type.getBaseAttack() > 0, type.name() + " should have positive base attack.");
            assertTrue(type.getCritChance() >= 0, type.name() + " should have non-negative crit chance.");
            assertTrue(type.getCritMultiplier() > 0, type.name() + " should have positive crit multiplier.");
            assertTrue(type.getGoldReward() >= 0, type.name() + " should have non-negative gold reward.");
        }
    }

    @Test
    void critChanceIsReasonable() {
        for (MonsterType type : MonsterType.values()) {
            assertTrue(type.getCritChance() <= 1.0,
                    type.name() + " crit chance should not exceed 100%.");
            assertTrue(type.getCritChance() >= 0.0,
                    type.name() + " crit chance should not be negative.");
        }
    }
}