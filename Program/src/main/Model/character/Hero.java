package main.Model.character;

import java.util.*;

import main.Model.dungeon.Room;
import main.Model.element.HealthPotion;
import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.element.VisionPotion;
import main.Model.util.HeroType;
import javafx.scene.image.Image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import main.Model.util.Point;

/**
 * Represents the player character.
 * This class extends Character and includes hero-specific attributes like inventory,
 * hero type, and interaction with game elements.
 */
public class Hero extends Character {

    // Constants
    private static final int MAX_INVENTORY_SIZE = 10;
    private static final int TOTAL_PILLARS = 4;
    private static final double BASE_CANVAS_SIZE = 480.0;
    private static final double BASE_MOVEMENT_SPEED = 2.0;
    public static final int SPRITE_FRAME_WIDTH = 232;
    public static final int SPRITE_FRAME_HEIGHT = 212;

    // Core hero attributes
    private final HeroType myHeroType;
    private final ArrayList<Item> myInventory;
    private int myPillarsActivated;
    private int myGold;
    private int mySpecialMana = 2;
    private boolean myBossSlain = false;

    private boolean myManaBuff = false;
    private int myAttackBuff = 0;

    // Movement and positioning
    private double myPixelX;
    private double myPixelY;
    private final MovementState myMovementState;
    private double myMovementSpeed = BASE_MOVEMENT_SPEED;

    // Animation
    private transient Image mySpriteSheet;
    private final AnimationState myAnimationState;
    private transient Image mySprite;

    public Hero(final HeroBuilder theHeroBuilder) {
        super(theHeroBuilder);
        this.myHeroType = theHeroBuilder.myHeroType;
        this.myInventory = new ArrayList<>();
        this.myPillarsActivated = 0;
        this.myGold = 0;
        this.myMovementState = new MovementState();
        this.myAnimationState = new AnimationState();
        loadSpriteSheet();
    }



    private void loadSpriteSheet() {
        try {
            String spritePath = getSpritePathForHeroType();
            mySpriteSheet = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(spritePath)));
        } catch (Exception e) {
            System.err.println("Error loading hero sprite sheet: " + e.getMessage());
            mySpriteSheet = null;
        }
    }

    private String getSpritePathForHeroType() {
        return switch (myHeroType) {
            case THIEF -> "/sprites/heroes/thief_walk_spritesheet.png";
            case WARRIOR -> "/sprites/heroes/warrior_walk_spritesheet.png";
            case PRIESTESS -> "/sprites/heroes/priestess_walk_spritesheet.png";
        };
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
        if(theTarget == null || !theTarget.isAlive()) {
            return 0;//Cannot attack null or dead target
        }

        //Base damage from HeroType
        int damageDealt = myHeroType.getBaseAttack() + myAttackBuff;

        Random rand = new Random();
        if(rand.nextDouble(1) < getCritChance()){
            damageDealt *= getCritMultiplier();
            System.out.println("Crit landed");
        }

        System.out.println(getName()+ "attacks" + ((theTarget instanceof Monster) ?
                ((Monster)theTarget).getName():"target") + "for" + damageDealt + "damage.");

        theTarget.takeDamage(damageDealt);
        if(!theTarget.isAlive() && theTarget instanceof Monster) {
            addGold(((Monster)theTarget).getGoldReward());
            addMana();
            if (((Monster) theTarget).getType().isBoss()) {
                myBossSlain = true;
            }
        }
        return damageDealt;
    }

    private String getTargetName(Character target) {
        return (target instanceof Monster monster) ? monster.getName() : "target";
    }

    public void useItem(final Item theItem) {
        if (theItem == null || !myInventory.contains(theItem)) {
            if (theItem != null) {
                System.out.println(getName() + " tried to use " + theItem.getName() +
                        " but it's not in inventory.");
            }
            return;
        }

        System.out.println(getName() + " uses " + theItem.getName() + ".");
        theItem.use(this);
        myInventory.remove(theItem);
    }

    public boolean pickupItem(final Item theItem) {
        if (theItem == null) {
            return false;
        }

        if (myInventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println(getName() + "'s inventory is full. Cannot pick up " +
                    theItem.getName() + ".");
            return false;
        }

        myInventory.add(theItem);

        System.out.println(getName() + " picked up " + theItem.getName() + ".");
        return true;
    }

    public boolean activatePillar(final Pillar thePillar) {
        if (thePillar == null || thePillar.isActivated()) {
            return false;
        }

        thePillar.activate(this);

        // Increment pillars activated count
        myPillarsActivated++;
        System.out.println("Pillar activated! Total: " + myPillarsActivated + "/4");

        return true;
    }

    public boolean hasActivatedAllPillars() {
        boolean hasAll = myPillarsActivated >= TOTAL_PILLARS;
        System.out.println("Checking all pillars: " + myPillarsActivated + "/" + TOTAL_PILLARS + " = " + hasAll);
        return hasAll;
    }

    public int specialAttack() {
        int baseDamage = myHeroType.getSpecialAttackDamage();
        // Stat bonuses from pillars
        int totalDamage = baseDamage + myAttackBuff;

        Random rand = new Random();
        if(rand.nextDouble(1) < getCritChance()){
            totalDamage *= getCritMultiplier();
            System.out.println("Critlanded");
        }

        System.out.println(getName() + " performs a special attack!");
        if (myManaBuff) {
            mySpecialMana--;
            System.out.println("Mana at: " + mySpecialMana + "/4");
        } else {
            mySpecialMana -= 2;
            System.out.println("Mana at: " + mySpecialMana + "/4");
        }
        return totalDamage;
    }

    public boolean canUseSpecialAttack() {
        boolean canUse = false;
        if (myManaBuff) {
            canUse = mySpecialMana >= 1;
        } else {
            canUse = mySpecialMana >= 2;
        }
        return canUse;
    }

    public void addMana() {
        if (mySpecialMana < 4){
            mySpecialMana++;
            System.out.println("Mana gained: " + mySpecialMana + "/4");
        } else {
            System.out.println("Mana at max: " + mySpecialMana + "/4");
        }
    }

    @Override
    public void takeDamage(final int theDamageAmount) {
        super.takeDamage(theDamageAmount);
        System.out.println(getName() + " takes " + theDamageAmount +
                " damage. Current health: " + getHealth() + "/" + getMaxHealth());

        if (!isAlive()) {
            System.out.println(getName() + " has been defeated!");
        }
    }

    public void addGold(final int theAmount) {
        if (theAmount > 0) {
            this.myGold += theAmount;
            System.out.println(getName() + " gained " + theAmount +
                    " gold. Total: " + myGold);
        }
    }

    public boolean spendGold(final int theAmount) {
        if (theAmount <= 0 || this.myGold < theAmount) {
            System.out.println(getName() + " does not have enough gold to spend " +
                    theAmount + ".");
            return false;
        }

        this.myGold -= theAmount;
        System.out.println(getName() + " spent " + theAmount +
                " gold. Remaining: " + myGold);
        return true;
    }

    // Movement methods
    public void setPixelPosition(double x, double y) {
        myPixelX = x;
        myPixelY = y;
    }

    public void startMovingNorth() { myMovementState.setMovingNorth(true); }
    public void stopMovingNorth() { myMovementState.setMovingNorth(false); }
    public void startMovingSouth() { myMovementState.setMovingSouth(true); }
    public void stopMovingSouth() { myMovementState.setMovingSouth(false); }
    public void startMovingEast() { myMovementState.setMovingEast(true); }
    public void stopMovingEast() { myMovementState.setMovingEast(false); }
    public void startMovingWest() { myMovementState.setMovingWest(true); }
    public void stopMovingWest() { myMovementState.setMovingWest(false); }

    public void setMovementSpeedForCanvasSize(double canvasSize) {
        double speedScale = canvasSize / BASE_CANVAS_SIZE;
        myMovementSpeed = BASE_MOVEMENT_SPEED * speedScale;
        myMovementSpeed = Math.max(1.0, Math.min(myMovementSpeed, 8.0));
    }

    public void updatePixelPosition() {
        if (myMovementState.isMovingNorth()) myPixelY -= myMovementSpeed;
        if (myMovementState.isMovingSouth()) myPixelY += myMovementSpeed;
        if (myMovementState.isMovingEast()) myPixelX += myMovementSpeed;
        if (myMovementState.isMovingWest()) myPixelX -= myMovementSpeed;
    }

    public boolean isMoving() {
        return myMovementState.isMoving();
    }

    public void updateAnimation(long currentTimeNanos) {
        myAnimationState.update(currentTimeNanos, isMoving(), myMovementState);
    }

    // Animation getters
    public Image getSpriteSheet() { return mySpriteSheet; }
    public Image getSprite() { return mySprite; }
    public int getCurrentFrameX() { return myAnimationState.getCurrentFrameX(); }
    public int getAnimationRow() { return myAnimationState.getAnimationRow(); }

    // Health display methods
    public double getHealthPercentage() {
        return Math.max(0.0, Math.min(1.0, (double) getHealth() / getMaxHealth()));
    }

    public String getHealthDisplay() {
        return getHealth() + "/" + getMaxHealth();
    }

    public int getSpecialMana() {
        return mySpecialMana;
    }

    // Getters
    public HeroType getType() { return myHeroType; }
    public List<Item> getInventory() { return Collections.unmodifiableList(myInventory); }
    public int getPillarsActivated() { return myPillarsActivated; }
    public int getGold() { return myGold; }
    public double getPixelX() { return myPixelX; }
    public double getPixelY() { return myPixelY; }

    // Setters
    public void setPillarsActivated(final int thePillarsActivated) {
        this.myPillarsActivated = Math.max(0, thePillarsActivated);
    }

    public void setManaBuff(final boolean theManaBuff) {
        myManaBuff = theManaBuff;
    }

    public void addAttackBuff(final int theAttackBuff) {
        myAttackBuff += theAttackBuff;
    }

    public void addItem(final Item theItem) {
        pickupItem(theItem);
    }

    public boolean getBossSlain() {
        return myBossSlain;
    }

    public void setBossSlain(final boolean theBossSlain) {
        myBossSlain = theBossSlain;
    }

    // Inner classes for better organization
    private static class MovementState {
        private boolean isMovingNorth = false;
        private boolean isMovingSouth = false;
        private boolean isMovingEast = false;
        private boolean isMovingWest = false;

        public void setMovingNorth(boolean moving) { isMovingNorth = moving; }
        public void setMovingSouth(boolean moving) { isMovingSouth = moving; }
        public void setMovingEast(boolean moving) { isMovingEast = moving; }
        public void setMovingWest(boolean moving) { isMovingWest = moving; }

        public boolean isMovingNorth() { return isMovingNorth; }
        public boolean isMovingSouth() { return isMovingSouth; }
        public boolean isMovingEast() { return isMovingEast; }
        public boolean isMovingWest() { return isMovingWest; }

        public boolean isMoving() {
            return isMovingNorth || isMovingSouth || isMovingEast || isMovingWest;
        }
    }

    private static class AnimationState {
        private static final int TOTAL_FRAMES_PER_DIRECTION = 3;
        private static final long FRAME_DURATION_MILLIS = 150;

        private int currentFrameX = 0;
        private int animationRow = 0;
        private long lastFrameTime = 0;

        public void update(long currentTimeNanos, boolean isMoving, MovementState movementState) {
            if (lastFrameTime == 0) {
                lastFrameTime = currentTimeNanos;
            }

            if (!isMoving) {
                currentFrameX = 1; // Idle frame
                return;
            }

            long elapsedTimeMillis = (currentTimeNanos - lastFrameTime) / 1_000_000;
            if (elapsedTimeMillis > FRAME_DURATION_MILLIS) {
                lastFrameTime = currentTimeNanos;
                currentFrameX = (currentFrameX + 1) % TOTAL_FRAMES_PER_DIRECTION;
            }

            updateAnimationRow(movementState);
        }

        private void updateAnimationRow(MovementState movementState) {
            if (movementState.isMovingNorth()) {
                animationRow = 2;
            } else if (movementState.isMovingSouth()) {
                animationRow = 0;
            } else if (movementState.isMovingWest()) {
                animationRow = 3;
            } else if (movementState.isMovingEast()) {
                animationRow = 1;
            }
        }

        public int getCurrentFrameX() { return currentFrameX; }
        public int getAnimationRow() { return animationRow; }
    }

    public static class HeroBuilder extends CharacterBuilder<HeroBuilder, Hero> {
        private HeroType myHeroType;

        public HeroBuilder setHeroType(final HeroType theHeroType) {
            myHeroType = theHeroType;
            return self();
        }

        @Override
        protected HeroBuilder self() {
            return this;
        }

        @Override
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
            saveData.attackBuff = myAttackBuff; // You were going to fix up factory for hero to add these things
            saveData.specialMana = mySpecialMana;
            saveData.manaBuff = myManaBuff;
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
                    .setMaxHealth(saveData.maxHealth)
                    .setPosition(new Point(saveData.positionX, saveData.positionY))
                    .build();

            // Restore all the saved data
            hero.myGold = saveData.gold;
            hero.myPillarsActivated = saveData.pillarsActivated;

            // CRITICAL: Restore pixel position
            hero.myPixelX = saveData.pixelX;
            hero.myPixelY = saveData.pixelY;
            hero.myAttackBuff = saveData.attackBuff;
            hero.mySpecialMana = saveData.specialMana;
            hero.myManaBuff = saveData.manaBuff;

            // CRITICAL: Reset movement state to ensure clean state
            hero.myMovementState.setMovingNorth(false);
            hero.myMovementState.setMovingSouth(false);
            hero.myMovementState.setMovingEast(false);
            hero.myMovementState.setMovingWest(false);

            // Restore inventory
            for (String itemName : saveData.inventoryItems) {
                if (itemName.equals("Health Potion")) {
                    hero.addItem(new HealthPotion("Health Potion", "Heals 50", 50));
                } else {
                    // Because a dungeon object is reloaded after Hero, and a dungeon is needed for the vision potion,
                    // a placeholder is put here then replaced at GameController loadGameFromSave.
                    hero.addItem(new HealthPotion("VisionPlaceholder", "Description", 0));
                }
            }

            // CRITICAL: Reload sprite sheet since it's transient
            hero.loadSpriteSheet();

            System.out.println("Hero loaded - Position: " + hero.getPosition() +
                              ", Pixel: (" + hero.myPixelX + "," + hero.myPixelY + ")");

            return hero;
        } catch (Exception e) {
            System.err.println("Error deserializing hero: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // inner class for save data
    private static class HeroSaveData {
        public String name;
        public String heroType;
        public int health;
        public int maxHealth;
        public int attackBuff;
        public int specialMana;
        public boolean manaBuff;
        public int gold;
        public int pillarsActivated;
        public int positionX;
        public int positionY;
        public double pixelX;
        public double pixelY;
        public List<String> inventoryItems;
    }

    /**
     * synchronizes pixel position with room position
     * Call this after loading to ensure pixel and room positions match
     */
    public void synchronizePositions(double roomPixelWidth, double roomPixelHeight) {
        Point roomPos = getPosition();
        myPixelX = roomPos.getX() * roomPixelWidth + (roomPixelWidth / 2);
        myPixelY = roomPos.getY() * roomPixelHeight + (roomPixelHeight / 2);

        System.out.println("Synchronized positions - Room: " + roomPos +
                          ", Pixel: (" + myPixelX + "," + myPixelY + ")");
    }

    /**
     *reset all movement flags to ensure clean state
     */
    public void resetMovementState() {
        myMovementState.setMovingNorth(false);
        myMovementState.setMovingSouth(false);
        myMovementState.setMovingEast(false);
        myMovementState.setMovingWest(false);
        System.out.println("Movement state reset");
    }
}
