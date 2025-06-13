package main.Model.character;

import main.Model.util.Point;
import main.Model.util.Direction;

/**
 * Abstract class for Character, representing Heros and Monsters.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public abstract class Character {

    private final String myName;
    private int myMaxHealth;
    private int myHealth;
    private Point myPosition;
    private final int myBaseAttackDamage;
    private final int mySpecialAttackDamage;
    private final String mySpecialAttackName;
    private double myCritChance;
    private final double myCritMultiplier;
    private final String myDescription;

    /**
     * Character Constructor.
     *
     * @param theCharacterBuilder builder object for character.
     */
    public Character(final CharacterBuilder theCharacterBuilder) {
        myName = theCharacterBuilder.myName;
        myMaxHealth = theCharacterBuilder.myMaxHealth;
        myHealth = theCharacterBuilder.myHealth;
        myPosition = theCharacterBuilder.myPosition;
        myBaseAttackDamage = theCharacterBuilder.myBaseAttackDamage;
        mySpecialAttackDamage = theCharacterBuilder.mySpecialAttackDamage;
        mySpecialAttackName = theCharacterBuilder.mySpecialAttackName;
        myCritChance = theCharacterBuilder.myCritChance;
        myCritMultiplier = theCharacterBuilder.myCritMultiplier;
        myDescription = theCharacterBuilder.myDescription;
    }

    /**
     * Abstract attack method.
     *
     * @param theTarget target to be attacked.
     * @return integer of damage.
     */
    public abstract int attack(final Character theTarget);

    /**
     * Takes damage.
     *
     * @param theDamage the damage to apply.
     */
    public void takeDamage(final int theDamage) {
        myHealth = Math.max(0, myHealth - theDamage);
    }

    /**
     * Checks if character is alive.
     *
     * @return boolean of alive status.
     */
    public boolean isAlive() {
        return myHealth > 0;
    }

    /**
     * Moves character.
     *
     * @param theDirection direction to be moved in.
     */
    public void move(final Direction theDirection) {
        switch (theDirection) {
            case NORTH:
                myPosition = new Point(myPosition.getX(), myPosition.getY() - 1);
                break;
            case SOUTH:
                myPosition = new Point(myPosition.getX(), myPosition.getY() + 1);
                break;
            case EAST:
                myPosition = new Point(myPosition.getX() + 1, myPosition.getY());
                break;
            case WEST:
                myPosition = new Point(myPosition.getX() - 1, myPosition.getY());
                break;
        }
    }

    /**
     * Gets character name.
     *
     * @return a name.
     */
    public String getName() {
        return myName;
    }

    /**
     * Gets character health.
     *
     * @return health as an integer.
     */
    public int getHealth() {
        return myHealth;
    }

    /**
     * Gets character health.
     *
     * @return health as an integer.
     */
    public int getMaxHealth() {
        return myMaxHealth;
    }

    /**
     * Gets character position
     *
     * @return position as a Point
     */
    public Point getPosition() {
        return myPosition;
    }

    /**
     * Adds character max health.
     */
    public void addMaxHealth(final int theHealth) {
        myMaxHealth += theHealth;
    }

    public void setHealth(final int theHealth) {
        myHealth = Math.min(theHealth, myMaxHealth);
    }

    /**
     * Sets position.
     */
    public void setPosition(final Point thePosition) {
        myPosition = thePosition;
    }

    public int getBaseAttackDamage() {
        return myBaseAttackDamage;
    }

    public String getSpecialAttackName() {
        return mySpecialAttackName;
    }

    public int getSpecialAttackDamage() {return mySpecialAttackDamage; }

    public double getCritChance() {
        return myCritChance;
    }

    public double getCritMultiplier() {
        return myCritMultiplier;
    }

    public String getDescription() {
        return myDescription;
    }

    public void addCritChance(final double theCritChance) {
        myCritChance += theCritChance;
    }

    /**
     * Character builder class.
     *
     * @param <BuilderType> Builder type.
     * @param <Type> class type.
     */
    public static abstract class CharacterBuilder<BuilderType, Type> {

        private String myName;
        private int myMaxHealth;
        private int myHealth;
        private Point myPosition;
        private int myBaseAttackDamage;
        private int mySpecialAttackDamage;
        private String mySpecialAttackName;
        private double myCritChance;
        private double myCritMultiplier;
        private String myDescription;

        public BuilderType setName(final String theName) {
            myName = theName;
            return self();
        }

        public BuilderType setHealth(final int theHealth) {
            if (theHealth > 0) {
                myHealth = theHealth;
                return self();
            } else {
                throw new IllegalArgumentException("Health cannot be zero or less" +
                                                   " Character Builder setHealth()");
            }
        }

        public BuilderType setMaxHealth(final int theMaxHealth) {
            if (theMaxHealth > 0) {
                myMaxHealth = theMaxHealth;
                return self();
            } else {
                throw new IllegalArgumentException("Health cannot be zero or less" +
                        " Character Builder setHealth()");
            }
        }

        public BuilderType setPosition(final Point thePosition) {
            myPosition = thePosition;
            return self();
        }

        public BuilderType setBaseAttackDamage(final int theBaseAttackDamage) {
            myBaseAttackDamage = theBaseAttackDamage;
            return self();
        }

        public BuilderType setSpecialAttackDamage(final int theSpecialAttackDamage) {
            mySpecialAttackDamage = theSpecialAttackDamage;
            return self();
        }

        public BuilderType setSpecialAttackName(final String theSpecialAttackName) {
            mySpecialAttackName = theSpecialAttackName;
            return self();
        }

        public BuilderType setCritChance(final double theCritChance) {
            myCritChance = theCritChance;
            return self();
        }

        public BuilderType setCritMultiplier(final double theCritMultiplier) {
            myCritMultiplier = theCritMultiplier;
            return self();
        }

        public BuilderType setDescription(final String theDescription) {
            myDescription = theDescription;
            return self();
        }

        protected abstract BuilderType self();

        public abstract Type build();

    }



}
