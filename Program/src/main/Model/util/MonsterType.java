package main.Model.util;

/**
 * Enum representing the different types of monsters in the game.
 * Each type can have predefined stats and abilities.
 */
public enum MonsterType {
    // Regular Monsters
    GOBLIN("Goblin", "A small, green, and mischievous creature often found in groups.", 40, 8, "Stab", 0.04, 2.0, 5, false, false),
    SKELETON("Skeleton", "An animated skeleton warrior, relentless and tireless.", 35, 10, "Bone Throw", 0.03, 1.8, 3, false, false),
    SLIME("Slime", "A gelatinous blob that can slowly engulf its prey.", 30, 5, "Acid Splash", 0.02, 1.5, 2, false, false),

    // Elite Monsters
    ORC("Orc", "A large, brutish humanoid known for its strength and ferocity.", 80, 15, "Cleave", 0.06, 2.0, 10, true, false),
    BIG_SLIME("Big Slime", "A massive slime that can split into smaller slimes upon taking enough damage.", 70, 12, "Engulf", 0.05, 1.7, 15, true, false),
    WIZARD("Wizard", "A rogue magic-user, wielding destructive spells.", 60, 20, "Lightning Bolt", 0.08, 2.2, 20, true, false),

    // Boss Monster
    GIANT("Giant", "A colossal humanoid creature, a true test of a hero's might.", 200, 25, "Ground Slam", 0.10, 2.0, 100, false, true);
    // Add more monster types as needed

    private final String myName;
    private final String myDescription;
    private final int myBaseHealth;
    private final int myBaseAttack;
    private final String mySpecialAttackName; // Could be an enum or ability object later
    private final double myCritChance;
    private final double myCritMultiplier;
    private final int myGoldReward;
    private final boolean myIsElite;
    private final boolean myIsBoss;


    MonsterType(String name, String description, int baseHealth, int baseAttack,
                String specialAttackName, double critChance, double critMultiplier,
                int goldReward, boolean isElite, boolean isBoss) {
        this.myName = name;
        this.myDescription = description;
        this.myBaseHealth = baseHealth;
        this.myBaseAttack = baseAttack;
        this.mySpecialAttackName = specialAttackName;
        this.myCritChance = critChance;
        this.myCritMultiplier = critMultiplier;
        this.myGoldReward = goldReward;
        this.myIsElite = isElite;
        this.myIsBoss = isBoss;
    }

    public String getName() {
        return myName;
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

    public double getCritChance() {
        return myCritChance;
    }

    public double getCritMultiplier() {
        return myCritMultiplier;
    }

    public int getGoldReward() {
        return myGoldReward;
    }

    public boolean isElite() {
        return myIsElite;
    }

    public boolean isBoss() {
        return myIsBoss;
    }

    @Override
    public String toString() {
        return myName;
    }
}
