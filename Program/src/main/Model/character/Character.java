package main.Model.character;

import main.Model.util.Point;
import main.Model.util.Direction;

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

    public abstract int attack(final Character theTarget);

    public void takeDamage(final int theDamage) {
        myHealth = Math.max(0, myHealth - theDamage);
    }

    public boolean isAlive() {
        return myHealth > 0;
    }

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

    public String getName() {
        return myName;
    }

    public int getHealth() {
        return myHealth;
    }

    public int getMaxHealth() {
        return myMaxHealth;
    }

    public Point getPosition() {
        return myPosition;
    }

    public void addMaxHealth(final int theHealth) {
        myMaxHealth += theHealth;
    }

    public void setHealth(final int theHealth) {
        myHealth = Math.min(theHealth, myMaxHealth);
    }

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

        // TODO: do null checks and other error checking for builder methods

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
