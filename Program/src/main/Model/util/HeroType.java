package main.Model.util;

/**
 * Enum representing the different types of heroes available in the game.
 * Each hero type can have distinct base stats and abilities.
 */
public enum HeroType {
    WARRIOR("Warrior", "A strong and resilient fighter, excelling in melee combat.", 125, 15, "Mighty Swing", 25, 0.05, 2.0),
    PRIESTESS("Priestess", "A divine spellcaster focused on healing and support, with moderate combat skills.", 100, 10, "Holy Smite", 30, 0.05, 1.5), // Example stats
    THIEF("Thief", "A nimble and cunning rogue, adept at dealing critical damage and evading foes.", 90, 20, "Backstab", 35, 0.20, 2.5);

    private final String myDisplayName;
    private final String myDescription;
    private final int myBaseHealth;
    private final int myBaseAttack;
    private final String mySpecialAttackName;
    private final int mySpecialAttackDamage;
    private final double myCritChance;          // ex: 0.05 for 5%
    private final double myCritMultiplier;

    HeroType(final String theDisplayName, final String theDescription, final int theBaseHealth,
             final int theBaseAttack, final String theSpecialAttackName, final int theSpecialAttackDamage,
             final double theCritChance, final double theCritMultiplier) {
        this.myDisplayName = theDisplayName;
        this.myDescription = theDescription;
        this.myBaseHealth = theBaseHealth;
        this.myBaseAttack = theBaseAttack;
        this.mySpecialAttackName = theSpecialAttackName;
        this.mySpecialAttackDamage = theSpecialAttackDamage;
        this.myCritChance = theCritChance;
        this.myCritMultiplier = theCritMultiplier;
    }

    public String getDisplayName() {
        return myDisplayName;
    }

    public String getDescription() {
        return myDescription;
    }

    public int getBaseHealth() {
        return myBaseHealth;
    }

    public int getBaseAttack() {
        return myBaseAttack;
    }

    public String getSpecialAttackName() {
        return mySpecialAttackName;
    }

    public int getSpecialAttackDamage() {
        return mySpecialAttackDamage;
    }

    public double getCritChance() {
        return myCritChance;
    }

    public double getCritMultiplier() {
        return myCritMultiplier;
    }

    @Override
    public String toString() {
        return myDisplayName;
    }
}
