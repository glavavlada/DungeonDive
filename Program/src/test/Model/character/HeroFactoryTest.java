package test.Model.character;

import main.Model.character.Hero;
import main.Model.character.HeroFactory;
import main.Model.util.HeroType;
import main.Model.util.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroFactoryTest {

    private HeroFactory myHeroFactory;

    @BeforeEach
    void setUp() {
        myHeroFactory = new HeroFactory();
    }

    @Test
    void getHero_warrior() {
        Hero hero = myHeroFactory.getHero("BasicName", HeroType.WARRIOR, new Point(0, 0));
        assertEquals(hero.getMaxHealth(), 125);
        assertEquals(hero.getName(), "BasicName");
        assertEquals(0.05, hero.getCritChance());
        assertEquals(15, hero.getBaseAttackDamage());
    }

    @Test
    void getHero_priestess() {
        Hero hero = myHeroFactory.getHero("BasicName", HeroType.PRIESTESS, new Point(0, 0));
        assertEquals(hero.getMaxHealth(), 100);
        assertEquals(hero.getName(), "BasicName");
        assertEquals(0.05, hero.getCritChance());
        assertEquals(10, hero.getBaseAttackDamage());
    }

    @Test
    void getHero_theif() {
        Hero hero = myHeroFactory.getHero("BasicName", HeroType.THIEF, new Point(0, 0));
        assertEquals(hero.getMaxHealth(), 90);
        assertEquals(hero.getName(), "BasicName");
        assertEquals(0.20, hero.getCritChance());
        assertEquals(20, hero.getBaseAttackDamage());
    }

    // buildHero is indirectly tested through the above methods, and builder tests in Hero and Character tests.
}
