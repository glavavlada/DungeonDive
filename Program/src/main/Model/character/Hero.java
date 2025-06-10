package main.Model.character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.util.Direction;
import main.Model.util.HeroType;
import main.Model.util.Point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Represents the player character.
 * This class extends Character and includes hero-specific attributes like inventory,
 * hero type, and interaction with game elements.
 */
public class Hero extends Character { // Make sure Character is in main.Model.character
    private final HeroType myHeroType;
    private final ArrayList<Item> myInventory;
    private int myPillarsActivated;
    private int myGold;
    private int mySpecialMana = 2;
    // Replace the pillar collection counter with stat tracking
    private int myStrengthBonus = 0;
    private int myDefenseBonus = 0;
    private int myAgilityBonus = 0;
    private int myHealthBonus = 0;
    // Add fields for other stats derived from HeroType if they can be modified by game events (buffs, items)
    // example:
    // private int myCurrentAttackPower;
    // private double myCurrentCritChance;
    // private double myCurrentCritMultiplier;

    private double myPixelX; // Actual pixel position
    private double myPixelY;
    private double myMoveSpeed = 2.0; // Pixels per frame
    private boolean myIsMoving = false;
    private Direction myFacingDirection = Direction.SOUTH;
    private Direction myMoveDirection = null;

    /**
     * Constructor for Hero.
     * Initializes the hero with a name, type, and starting position.
     * Base stats like health, attack, crit chance, etc., are derived from the HeroType enum.
     */
    public Hero(final HeroBuilder theHeroBuilder) {

        super(theHeroBuilder);
        myHeroType = theHeroBuilder.myHeroType;

        this.myInventory = new ArrayList<>();
        this.myPillarsActivated = 0;
        this.myGold = 0;

        // Initialize other modifiable stats from HeroType's base values
        // this.myCurrentAttackPower = theType.getBaseAttack();
        // this.myCurrentCritChance = theType.getCritChance();
        // this.myCurrentCritMultiplier = theType.getCritMultiplier();
        // etc.
    }

    public void startMoving(final Direction theDirection) {
        myMoveDirection = theDirection;
        myFacingDirection = theDirection;
        myIsMoving = true;
    }

    public void stopMoving() {
        myIsMoving = false;
        myMoveDirection = null;
    }

    public void updatePosition() {
        if (myIsMoving && myMoveDirection != null) {
            switch (myMoveDirection) {
                case NORTH:
                    myPixelY -= myMoveSpeed;
                    break;
                case SOUTH:
                    myPixelY += myMoveSpeed;
                    break;
                case EAST:
                    myPixelX += myMoveSpeed;
                    break;
                case WEST:
                    myPixelX -= myMoveSpeed;
                    break;
            }
        }
    }

    /**
     * Performs an attack on a target character.
     * Damage calculation can be based on hero's type, equipped items, and other factors.
     *
     * @param theTarget The Character to attack.
     * @return The amount of damage dealt.
     */
    @Override
    public int attack(final Character theTarget) {
        if (theTarget == null || !theTarget.isAlive()) {
            return 0; // Cannot attack null or dead target
        }

        // Base damage from HeroType
        int damageDealt = myHeroType.getBaseAttack();

        // TODO: Implement more complex damage calculation:
        // 1. Add weapon damage if applicable.


        Random rand = new Random();
        if (rand.nextDouble(1) < getCritChance()) {
            damageDealt *= getCritMultiplier();
            System.out.println("Crit landed");
        }


        System.out.println(getName() + " attacks " + ((theTarget instanceof Monster) ? ((Monster)theTarget).getName() : "target") + " for " + damageDealt + " damage.");
        theTarget.takeDamage(damageDealt);
        if (!theTarget.isAlive() && theTarget instanceof Monster) {
            addGold(((Monster) theTarget).getGoldReward());
            addMana();
        }
        return damageDealt;
    }

    /**
     * Uses an item from the hero's inventory.
     * The item's effect is applied to the hero, and the item is removed from inventory.
     *
     * @param theItem The Item to use.
     */
    public void useItem(final Item theItem) {
        if (theItem != null && myInventory.contains(theItem)) {
            System.out.println(getName() + " uses " + theItem.getName() + ".");
            theItem.use(this); // Item's use method will affect the hero
            myInventory.remove(theItem);
        } else if (theItem != null) {
            System.out.println(getName() + " tried to use " + theItem.getName() + " but it's not in inventory.");
        }
    }

    /**
     * Picks up an item and adds it to the hero's inventory if there's space.
     *
     * @param theItem The Item to pick up.
     * @return true if the item was successfully picked up, false otherwise.
     */
    public boolean pickupItem(final Item theItem) {
        if (theItem == null) return false;

        // Example: Limit inventory size
        final int maxInventorySize = 10; // Make this a constant or configurable
        if (myInventory.size() < maxInventorySize) {
            myInventory.add(theItem);
            System.out.println(getName() + " picked up " + theItem.getName() + ".");
            return true;
        } else {
            System.out.println(getName() + "'s inventory is full. Cannot pick up " + theItem.getName() + ".");
            return false;
        }
    }

    public HeroType getType() {
        return myHeroType;
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(myInventory);
    }

    public int getPillarsActivated() {
        return myPillarsActivated;
    }

    public int getGold() {
        return myGold;
    }


    public void setPillarsActivated(final int thePillarsActivated) {
        this.myPillarsActivated = Math.max(0, thePillarsActivated);
    }

    public void addGold(final int theAmount) {
        if (theAmount > 0) {
            this.myGold += theAmount;
            System.out.println(getName() + " gained " + theAmount + " gold. Total: " + myGold);
        }
    }

    public boolean spendGold(final int theAmount) {
        if (theAmount > 0 && this.myGold >= theAmount) {
            this.myGold -= theAmount;
            System.out.println(getName() + " spent " + theAmount + " gold. Remaining: " + myGold);
            return true;
        }
        System.out.println(getName() + " does not have enough gold to spend " + theAmount + ".");
        return false;
    }

    /**
     * Overrides Character's takeDamage to provide hero-specific feedback or effects.
     * @param theDamageAmount The amount of damage to take.
     */
    @Override
    public void takeDamage(final int theDamageAmount) {
        super.takeDamage(theDamageAmount); // Calls Character's takeDamage logic
        System.out.println(getName() + " takes " + theDamageAmount + " damage. Current health: " + getHealth() + "/" + getMaxHealth());
        if (!isAlive()) {
            System.out.println(getName() + " has been defeated!");
            // Game over logic would typically be handled by a GameController observing this state.
        }
    }

    /**
     *add item to hero's inventory
     *
     * @param theItem item to add
     * @return true if item was added successfully, false otherwise
     */
    public boolean addItem(final Item theItem) {
        return pickupItem(theItem); // Use existing pickupItem method
    }

    /**
     * Activates a pillar and receives its stat bonus.
     *
     * @param thePillar The pillar to activate
     * @return true if the pillar was successfully activated
     */
    public boolean activatePillar(final Pillar thePillar) {
        if (thePillar == null || thePillar.isActivated()) {
            return false;
        }

        // Mark the pillar as activated - pass this hero as the parameter
        thePillar.activate(this);

        return true;
    }

    /**
     * Checks if the hero has activated all pillars.
     *
     * @return true if all pillars have been activated, false otherwise
     */
    public boolean hasActivatedAllPillars() {
        // Assuming there are 4 pillars in total
        final int TOTAL_PILLARS = 4;
        return myPillarsActivated >= TOTAL_PILLARS;
    }



    /**
     * Performs a special attack, now affected by strength and agility bonuses.
     *
     * @return The amount of damage dealt
     */
    public int specialAttack() {

        int baseDamage = myHeroType.getSpecialAttackDamage();
        int bonusDamage = myStrengthBonus + myAgilityBonus;
        int totalDamage = baseDamage + bonusDamage;

        Random rand = new Random();
        if (rand.nextDouble(1) < getCritChance()) {
            totalDamage *= getCritMultiplier();
            System.out.println("Crit landed");
        }

        System.out.println(getName() + " performs a special attack!");
        mySpecialMana -= 2;
        return totalDamage;
    }

    /**
     *checks if hero can use special attack
     * This is simple implementation that always returns true
     *modify this later to add limitations as needed
     *
     * @return true if hero can use special attack
     */
    public boolean canUseSpecialAttack() {
        boolean canUse = mySpecialMana >= 2;
        return canUse;
    }

    public void addMana() {
        if (mySpecialMana < 4) {
            mySpecialMana++;
            System.out.println("Mana gained: " + mySpecialMana + "/4");
        } else {
            System.out.println("Mana at max: " + mySpecialMana + "/4");
        }
    }


    public double getPixelX() {
        return myPixelX;
    }

    public double getPixelY() {
        return myPixelY;
    }

    public void setPixelX(int i) {
        myPixelX = i;
    }

    public void setPixelY(int i) {
        myPixelY = i;
    }

    /**
     * Gets the current health percentage for display purposes
     * @return Health as a percentage (0.0 to 1.0)
     */
    public double getHealthPercentage() {
        return Math.max(0.0, Math.min(1.0, (double) getHealth() / getMaxHealth()));
    }

    /**
     * Gets formatted health display string
     * @return String in format "current/max"
     */
    public String getHealthDisplay() {
        return getHealth() + "/" + getMaxHealth();
    }

    public static class HeroBuilder extends CharacterBuilder<HeroBuilder, Hero> {

        private HeroType myHeroType;

        public HeroBuilder setHeroType(final HeroType theHeroType) {
            myHeroType = theHeroType;
            return self();
        }

        protected HeroBuilder self() {
            return this;
        }

        public Hero build() {
            return new Hero(this);
        }
    }

    // SERIALIZATION  METHODS //

    /**
     * Serializes hero to JSON format for saving.
     * Converts hero's state including personal data, stats, inventory,
     * position, and progress into JSON string that can be stored in database
     * inventory items are serialized by name and will need to be recreated during loading
     *
     * @return JSON string representation of hero's state, or null if serialization fails
     */
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HeroSaveData saveData = new HeroSaveData();
            saveData.name = getName();
            saveData.heroType = myHeroType.name();
            saveData.health = getHealth();
            saveData.maxHealth = getMaxHealth();
            saveData.gold = myGold;
            saveData.pillarsActivated = myPillarsActivated;
            saveData.positionX = getPosition().getX();
            saveData.positionY = getPosition().getY();
            saveData.pixelX = myPixelX;
            saveData.pixelY = myPixelY;

            //serialize inventory
            saveData.inventoryItems = new ArrayList<>();
            for (Item item : myInventory) {
                saveData.inventoryItems.add(item.getName());
            }

            return mapper.writeValueAsString(saveData);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing hero: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deserializes a hero from JSON format
     * Creates a new Hero instance from saved JSON data, restoring all hero state
     * including name, type, health, position, gold, pillar progress, and inventory
     *
     * @param json JSON string containing serialized hero data
     * @return new Hero instance restored from JSON data, or null if deserialization fails
     */
    public static Hero fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HeroSaveData saveData = mapper.readValue(json, HeroSaveData.class);

            HeroType heroType = HeroType.valueOf(saveData.heroType);
            Hero hero = new HeroBuilder()
                    .setName(saveData.name)
                    .setHeroType(heroType)
                    .setHealth(saveData.health)
                    .setPosition(new Point(saveData.positionX, saveData.positionY))
                    .build();

            hero.myGold = saveData.gold;
            hero.myPillarsActivated = saveData.pillarsActivated;
            hero.myPixelX = saveData.pixelX;
            hero.myPixelY = saveData.pixelY;

            // Restore inventory (you'll need to implement item recreation)
            for (String itemName : saveData.inventoryItems) {
                // hero.addItem(ItemFactory.createItem(itemName));
            }

            return hero;
        } catch (Exception e) {
            System.err.println("Error deserializing hero: " + e.getMessage());
            return null;
        }
    }

    // inner class for save data
    private static class HeroSaveData {
        public String name;
        public String heroType;
        public int health;
        public int maxHealth;
        public int gold;
        public int pillarsActivated;
        public int positionX;
        public int positionY;
        public double pixelX;
        public double pixelY;
        public List<String> inventoryItems;
    }

}
