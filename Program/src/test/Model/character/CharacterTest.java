package test.Model.character;

import main.Model.character.Character;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.util.Direction;
import main.Model.util.HeroType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the abstract Character class.
 * Note: The abstract attack() method is tested in concrete subclasses (HeroTest, MonsterTest).
 */
class CharacterTest {

    // A concrete implementation of Character for testing purposes
    private static class ConcreteCharacter extends main.Model.character.Character {
        public ConcreteCharacter(final CharacterBuilder theCharacterBuilder) {
            super(theCharacterBuilder);
        }

        @Override
        public int attack(Character target) {
            // Simple attack for testing, could be more complex
            int damage = 10;
            if (target != null) {
                target.takeDamage(damage);
            }
            return damage;
        }

        public static class ConcreteBuilder extends CharacterBuilder<ConcreteBuilder, ConcreteCharacter> {
            protected ConcreteBuilder self() {
                return this;
            }

            public ConcreteCharacter build() {
                return new ConcreteCharacter(this);
            }
        }
    }

    private ConcreteCharacter character;
    private ConcreteCharacter targetCharacter;


    @BeforeEach
    void setUp() {
        character = new ConcreteCharacter.ConcreteBuilder().
                                          setName("Name").
                                          setHealth(100).
                                          setMaxHealth(100).
                                          setBaseAttackDamage(10).
                                          setSpecialAttackDamage(20).
                                          setSpecialAttackName("Special").
                                          setCritChance(0.10).
                                          setCritMultiplier(2).
                                          setDescription("Description").
                                          setPosition(new Point(0, 0)).
                                          build();
        targetCharacter = new ConcreteCharacter.ConcreteBuilder().
                                                setHealth(50).
                                                setMaxHealth(50).
                                                setBaseAttackDamage(10).
                                                setSpecialAttackDamage(20).
                                                setSpecialAttackName("Special").
                                                setCritChance(0.10).
                                                setCritMultiplier(2).
                                                setDescription("Description").
                                                setPosition(new Point(1, 1)).
                                                build();
    }

    @Test
    void constructor_initializesHealthAndPosition() {
        assertEquals(100, character.getHealth(), "Initial health should be set by constructor.");
        assertEquals(new Point(0, 0), character.getPosition(), "Initial position should be set by constructor.");
    }

    @Test
    void takeDamage_reducesHealth() {
        character.takeDamage(30);
        assertEquals(70, character.getHealth(), "Health should be reduced by damage amount.");
    }

    @Test
    void takeDamage_healthDoesNotGoBelowZero() {
        character.takeDamage(150); // More damage than current health
        assertEquals(0, character.getHealth(), "Health should not go below zero.");
    }

    @Test
    void isAlive_returnsTrueWhenHealthAboveZero() {
        assertTrue(character.isAlive(), "Character should be alive when health > 0.");
    }

    @Test
    void isAlive_returnsFalseWhenHealthIsZero() {
        character.takeDamage(100);
        assertFalse(character.isAlive(), "Character should not be alive when health is 0.");
    }

    @Test
    void move_north_updatesPositionCorrectly() {
        character.move(Direction.NORTH);
        assertEquals(new Point(0, -1), character.getPosition(), "Position should update correctly for NORTH movement.");
    }

    @Test
    void move_south_updatesPositionCorrectly() {
        character.move(Direction.SOUTH);
        assertEquals(new Point(0, 1), character.getPosition(), "Position should update correctly for SOUTH movement.");
    }

    @Test
    void move_east_updatesPositionCorrectly() {
        character.move(Direction.EAST);
        assertEquals(new Point(1, 0), character.getPosition(), "Position should update correctly for EAST movement.");
    }

    @Test
    void move_west_updatesPositionCorrectly() {
        character.move(Direction.WEST);
        assertEquals(new Point(-1, 0), character.getPosition(), "Position should update correctly for WEST movement.");
    }

    @Test
    void setHealth_updatesHealth() {
        character.setHealth(80);
        assertEquals(80, character.getHealth(), "setHealth should update the health value.");
    }

    @Test
    void setPosition_updatesPosition() {
        Point newPosition = new Point(5, 5);
        character.setPosition(newPosition);
        assertEquals(newPosition, character.getPosition(), "setPosition should update the position value.");
    }

    @Test
    void concreteCharacterAttack_damagesTarget() {
        int initialTargetHealth = targetCharacter.getHealth();
        character.attack(targetCharacter);
        assertTrue(targetCharacter.getHealth() < initialTargetHealth, "Target character health should decrease after attack.");
        assertEquals(initialTargetHealth - 10, targetCharacter.getHealth(), "Target health should be reduced by attack damage.");
    }

    @Test
    void getName_properNames() {
        String name = "Name";
        assertTrue(name.equals(character.getName()));
    }

    @Test
    void addMaxHealth_properHealth() {
        character.addMaxHealth(15);
        assertEquals(115, character.getMaxHealth());
    }

    @Test
    void getBaseAttack_properAttack() {
        assertEquals(10, character.getBaseAttackDamage());
    }

    @Test
    void getSpecialAttackName_properName() {
        assertTrue("Special".equals(character.getSpecialAttackName()));
    }

    @Test
    void getCritChance_properCritChance() {
        assertEquals(0.10, character.getCritChance());
    }

    @Test
    void getCritMultiplier_properCritMultiplier() {
        assertEquals(2, character.getCritMultiplier());
    }

    @Test
    void addCritChance_properCritChance() {
        character.addCritChance(0.5);
        assertEquals(0.6, character.getCritChance());
    }

    @Test
    void setName_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setName("hello").build();
        assertEquals("hello", character.getName());
    }

    @Test
    void sethealth_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setHealth(234).build();
        assertEquals(234, character.getHealth());
    }

    @Test
    void sethealth_properThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            Character character = new ConcreteCharacter.ConcreteBuilder().setHealth(-23).build();
        });
    }

    @Test
    void setMaxHealth_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setMaxHealth(234).build();
        assertEquals(234, character.getMaxHealth());
    }

    @Test
    void setMaxHealth_properThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            Character character = new ConcreteCharacter.ConcreteBuilder().setMaxHealth(-23).build();
        });
    }

    @Test
    void setPosition_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setPosition(new Point(2,4)).build();
        assertEquals(2, character.getPosition().getX());
        assertEquals(4, character.getPosition().getY());
    }

    @Test
    void setBaseAttackDamage_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setBaseAttackDamage(27).build();
        assertEquals(27, character.getBaseAttackDamage());
    }

    @Test
    void setSpecialAttackDamage_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setSpecialAttackDamage(84).build();
        assertEquals(84, character.getSpecialAttackDamage());
    }

    @Test
    void setSpecialAttackName_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setSpecialAttackName("Goofy Name").build();
        assertEquals("Goofy Name", character.getSpecialAttackName());
    }

    @Test
    void setCritChance_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setCritChance(0.23423).build();
        assertEquals(0.23423, character.getCritChance());
    }

    @Test
    void setCritMultiplier_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setCritMultiplier(234.4236312).build();
        assertEquals(234.4236312, character.getCritMultiplier());
    }

    @Test
    void setDescription_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().setDescription("Hello World").build();
        assertEquals("Hello World", character.getDescription());
    }

    @Test
    void build_properBuild() {
        Character character = new ConcreteCharacter.ConcreteBuilder().
                setMaxHealth(4).
                setBaseAttackDamage(34).
                build();
        assertEquals(0, character.getHealth());
        assertNull(character.getPosition());
        assertEquals(4, character.getMaxHealth());
        assertEquals(34, character.getBaseAttackDamage());
    }

}
