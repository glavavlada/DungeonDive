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
    private final double myCritMultiplier;      // ex: 2.0 for double damage

    HeroType(String displayName, String description, int baseHealth, int baseAttack,
             String specialAttackName, int specialAttackDamage, double critChance, double critMultiplier) {
        this.myDisplayName = displayName;
        this.myDescription = description;
        this.myBaseHealth = baseHealth;
        this.myBaseAttack = baseAttack;
        this.mySpecialAttackName = specialAttackName;
        this.mySpecialAttackDamage = specialAttackDamage;
        this.myCritChance = critChance;
        this.myCritMultiplier = critMultiplier;
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
