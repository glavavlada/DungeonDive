package main.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import main.Model.Model;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.element.VisionPotion;
import main.Model.util.Direction;
import main.View.GameUI;
import main.Controller.StateController.GameState;

import main.Model.util.Point;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls main game logic and coordinates between the game model and UI
 * This controller manages game state transitions, player actions, combat,
 * room interactions, and overall game flow during active gameplay.
 */
public class GameController {

    /**
     * The main game model containing all game state, dungeon data, and player information
     */
    private final Model myGameModel;

    /**
     * The game user interface responsible for displaying game state and handling visual updates
     */
    private final GameUI myGameUI;

    /**
     * The state controller that manages overall game state transitions
     */
    private final StateController myStateController;

    /**
     * The currently selected index in player's inventory
     */
    private int mySelectedInventoryIndex = 0;

    /**
     * Flag indicating whether player is currently entering combat
     */
    private boolean myEnteringCombat = false;

    /**
     * Timestamp of when last combat encounter ended
     */
    private long myLastCombatEndTime = 0;

    /**
     * Constructor for GameController
     *
     * @param theGameModel game model containing game data
     * @param theGameUI  game UI displaying game state
     * @param theStateController state controller tracking game state
     */
    public GameController(final Model theGameModel, final GameUI theGameUI,
                          final StateController theStateController) {
        if (theGameModel == null) {
            throw new IllegalArgumentException("Game model cannot be null for GameController.");
        }
        if (theGameUI == null) {
            throw new IllegalArgumentException("Game UI cannot be null for GameController.");
        }
        if (theStateController == null) {
            throw new IllegalArgumentException("State controller cannot be null for GameController.");
        }

        this.myGameModel = theGameModel;
        this.myGameUI = theGameUI;
        this.myStateController = theStateController;
        System.out.println("GameController initialized with model, UI, and state controller");

        Hero player = myGameModel.getPlayer();
        if (player != null) {
            Room startingRoom = myGameModel.getDungeon().getRoom(player.getPosition());
            if (startingRoom != null) {
                // Use existing enterRoom logic to handle all initial room effects consistently
                enterRoom(startingRoom);
            }
        }
    }

    /**
     * Determines if player can currently move based on game state
     *
     * @return true if player can move, false otherwise
     */
    private boolean canMovePlayer() {
        return myStateController.isInState(GameState.EXPLORING) && !myEnteringCombat;
    }


    /**
     * Initiates north movement for player character
     */
    public void startPlayerMovementNorth() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingNorth();
        }
    }

    /**
     * Initiates south movement for player character
     */
    public void startPlayerMovementSouth() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingSouth();
        }
    }

    /**
     * Initiates east movement for player character
     */
    public void startPlayerMovementEast() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingEast();
        }
    }

    /**
     * Initiates west movement for player character
     */
    public void startPlayerMovementWest() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingWest();
        }
    }

    /**
     * Stops north movement for player character
     */
    public void stopPlayerMovementNorth() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingNorth();
        }
    }

    /**
     * Stops south movement for player character
     */
    public void stopPlayerMovementSouth() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingSouth();
        }
    }

    /**
     * Stops east movement for player character
     */
    public void stopPlayerMovementEast() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingEast();
        }
    }

    /**
     * Stops west movement for player character
     */
    public void stopPlayerMovementWest() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingWest();
        }
    }

    /**
     * Moves player to room north of their current position
     */
    public void movePlayerNorth() {
        if (!canMovePlayer()) {
            System.out.println("Cannot move player - not in exploring state or entering combat");
            return;
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Point newPos = new Point(currentPos.getX(), currentPos.getY() - 1);

        // Move player to new room
        player.setPosition(newPos);

        // Enter new room and handle room effects
        Room newRoom = myGameModel.getDungeon().getRoom(newPos);
        enterRoom(newRoom);

        // Notify UI of room change
        notifyRoomChanged();

        System.out.println("Player moved north to " + newPos);
    }

    /**
     * Moves player to room south of their current position
     */
    public void movePlayerSouth() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Point newPos = new Point(currentPos.getX(), currentPos.getY() + 1);

        player.setPosition(newPos);
        Room newRoom = myGameModel.getDungeon().getRoom(newPos);
        enterRoom(newRoom);
        notifyRoomChanged();

        System.out.println("Player moved south to " + newPos);
    }

    /**
     * Moves player to room east of their current position
     */
    public void movePlayerEast() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Point newPos = new Point(currentPos.getX() + 1, currentPos.getY());

        player.setPosition(newPos);
        Room newRoom = myGameModel.getDungeon().getRoom(newPos);
        enterRoom(newRoom);
        notifyRoomChanged();

        System.out.println("Player moved east to " + newPos);
    }

    /**
     * Moves player to room west of their current position
     */
    public void movePlayerWest() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Point newPos = new Point(currentPos.getX() - 1, currentPos.getY());

        player.setPosition(newPos);
        Room newRoom = myGameModel.getDungeon().getRoom(newPos);
        enterRoom(newRoom);
        notifyRoomChanged();

        System.out.println("Player moved west to " + newPos);
    }

    /**
     * Stops all player movement
     */
    public void stopPlayerMovement() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingNorth();
            player.stopMovingSouth();
            player.stopMovingEast();
            player.stopMovingWest();
            System.out.println("Stopped all player movement");
        }
    }

    /**
     * Notifies UI that player has moved to new room
     */
    private void notifyRoomChanged() {
        if (myGameUI != null && myGameUI.getGameScreen() != null) {
            myGameUI.getGameScreen().onRoomChanged();
        }
    }


    /**
     * Handles room entry logic
     */
    private void enterRoom(final Room theRoom) {
        long timeSinceLastCombat = System.currentTimeMillis() - myLastCombatEndTime;
        if (timeSinceLastCombat < 200) {
            System.out.println("DEBUG: Too soon after last combat, skipping room entry");
            return;
        }

        System.out.println("DEBUG: Entering room at " + theRoom.getPosition());
        System.out.println("DEBUG: Room has pillar: " + theRoom.hasPillar());

        // Check for pillar FIRST before combat
        if (theRoom.hasPillar()) {
            Pillar pillar = theRoom.getPillar();
            System.out.println("DEBUG: Found pillar - Type: " + pillar.getType() + ", Activated: " + pillar.isActivated());

            if (!pillar.isActivated()) {
                System.out.println("DEBUG: Activating pillar...");
                activatePillar(theRoom);
            } else {
                System.out.println("DEBUG: Pillar already activated");
            }
        }

        // Prevent multiple rapid combat entries
        if (myEnteringCombat) {
            System.out.println("DEBUG: Already entering combat, skipping");
            return;
        }

        // Only enter combat if we're in exploring state
        if (!myStateController.isInState(GameState.EXPLORING)) {
            System.out.println("DEBUG: Not in exploring state, skipping combat entry");
            return;
        }

        // Mark room as visited
        theRoom.setVisited(true);

        // Check for monsters first
        if (!theRoom.getMonsters().isEmpty()) {
            System.out.println("DEBUG: Setting myEnteringCombat to true");
            myEnteringCombat = true;
            stopPlayerMovement();

            // Double-check monsters still exist before entering combat
            List<Monster> monsters = theRoom.getMonsters();
            if (monsters != null && !monsters.isEmpty()) {
                System.out.println("DEBUG: Changing state to COMBAT");
                myStateController.changeState(GameState.COMBAT);
                myGameUI.showCombatScreen(monsters);
                System.out.println("Entered combat with " + monsters.size() + " monsters");
                return;
            } else {
                System.out.println("DEBUG: Monsters disappeared, resetting flag");
                myEnteringCombat = false;
            }
        }

        //Reset flag if no combat
        myEnteringCombat = false;

        //Show room description with interaction hints
        String roomDesc = theRoom.getDescription();
        System.out.println(roomDesc);

        //check for chest
        if (theRoom.hasChest() && !theRoom.isChestOpened()) {
            myGameUI.showChestPrompt();
        }

        // Check for pillar
        if (theRoom.hasPillar()) {
            myGameUI.showPillarFound(theRoom.getPillar());
        }

        // Check for trap
        if (theRoom.hasTrap()) {
            activateTrap(theRoom);
        }

        // Update room description in UI
        myGameUI.updateRoomDescription(theRoom);
    }

    /**
     * Activates a cheat that reveals the entire dungeon map
     * Sets all rooms in the dungeon as visited, making them visible to the player
     */
    public void activateDungeonVisionCheat() {
        Dungeon dungeon = myGameModel.getDungeon();
        for (int row = 0; row < dungeon.getHeight(); row++) {
            for (int col = 0; col < dungeon.getWidth(); col++) {
                dungeon.getRoom(row, col).setVisited(true);
            }
        }
    }

    /**
     * Activates trap in room and applies its effects
     * Trap only triggers once per room
     *
     * @param theRoom room containing trap
     */
    private void activateTrap(final Room theRoom) {
        // Check if trap exists and hasn't been sprung yet
        if (theRoom.hasTrap() && !theRoom.getTrap().isSprung()) {
            Hero player = myGameModel.getPlayer();
            int damage = theRoom.getTrap().getDamage();

            // Apply trap damage to player
            player.takeDamage(damage);
            System.out.println("Player triggered a trap and took " + damage + " damage!");

            // Mark the trap as sprung so it won't trigger again
            theRoom.getTrap().trigger(player);

            // Trigger the damage flash effect
            triggerDamageFlash();

            // Update UI to show trap effect
            myGameUI.showTrapEffect(theRoom.getTrap());

            // Check if player died from trap
            checkPlayerStatus();
        } else if (theRoom.hasTrap() && theRoom.getTrap().isSprung()) {
            // Trap has already been triggered
            System.out.println("The trap here has already been sprung.");
        }
    }

    /**
     * Triggers damage flash effect on game screen
     */
    private void triggerDamageFlash() {
        if (myGameUI != null && myGameUI.getGameScreen() != null) {
            myGameUI.getGameScreen().flashDamageEffect();
            System.out.println("GameController triggered damage flash effect");
        } else {
            System.err.println("Cannot trigger damage flash: GameUI or GameScreen is null");
        }
    }
    /**
     *activates pillar in room
     *
     * @param theRoom room containing pillar
     */
    private void activatePillar(final Room theRoom) {
        if (theRoom.hasPillar() && !theRoom.getPillar().isActivated()) {
            Pillar pillar = theRoom.getPillar();
            Hero player = myGameModel.getPlayer();

            System.out.println("DEBUG: Before activation - Player pillars: " + player.getPillarsActivated());

            boolean activated = player.activatePillar(pillar);

            if (activated) {
                System.out.println("DEBUG: After activation - Player pillars: " + player.getPillarsActivated());

                // Record pillar activation in dungeon
                myGameModel.getDungeon().recordPillarActivation();

                // Update UI to show pillar activation and stat changes
                myGameUI.showPillarActivated(pillar);
                myGameUI.updatePlayerStats();

                System.out.println("Pillar of " + pillar.getType().getDisplayName() + " activated!");
                System.out.println("Player now has " + player.getPillarsActivated() + "/4 pillars");

                // Check if all pillars activated
                checkWinCondition();
            } else {
                System.out.println("DEBUG: Failed to activate pillar");
            }
        }
    }

    /**
     * Checks if player has met win condition
     */
    public void checkWinCondition() {
        Hero player = myGameModel.getPlayer();
        Point exitPoint = myGameModel.getDungeon().getExitPoint();
        Point playerPos = player.getPosition();

        System.out.println("=== CHECKING WIN CONDITION ===");
        System.out.println("Player position: " + playerPos);
        System.out.println("Exit position: " + exitPoint);
        System.out.println("Pillars activated: " + player.getPillarsActivated() + "/4");
        System.out.println("Boss slain: " + player.getBossSlain());
        System.out.println("All pillars activated: " + player.hasActivatedAllPillars());
        System.out.println("At exit: " + playerPos.equals(exitPoint));

        // Check all win conditions
        boolean allPillarsActivated = player.hasActivatedAllPillars();
        boolean atExit = playerPos.equals(exitPoint);
        boolean bossDefeated = player.getBossSlain();

        System.out.println("Win conditions:");
        System.out.println("- All pillars: " + allPillarsActivated);
        System.out.println("- At exit: " + atExit);
        System.out.println("- Boss defeated: " + bossDefeated);

        if (allPillarsActivated && atExit) {
            System.out.println("🎉 WIN CONDITION MET! 🎉");
            myStateController.changeState(GameState.VICTORY);
            myGameUI.showVictoryScreen();
        } else {
            System.out.println("Win condition not yet met:");
            if (!allPillarsActivated) System.out.println("- Need to activate all 4 pillars");
            if (!atExit) System.out.println("- Need to reach the exit");
            if (!bossDefeated) System.out.println("- Need to defeat the boss (if required)");
        }
        System.out.println("===============================");
    }

    /**
     * Checks player's health status and handles death if necessary
     */
    private void checkPlayerStatus() {
        if (myGameModel.getPlayer().getHealth() <= 0) {
            System.out.println("Player has died!");
            myStateController.changeState(GameState.GAME_OVER);
            myGameUI.showGameOverScreen();
        }
    }

    /**
     *handles player's attempt to interact with objects in current room
     */
    public void interact() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; //can only interact during exploration
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        System.out.println("DEBUG: Interacting in room at " + currentRoom.getPosition());

        // Check for pillar interaction fist
        if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            System.out.println("DEBUG: Manually activating pillar via interaction");
            activatePillar(currentRoom);
            return;
        }

        // Check for chest interaction
        if (currentRoom.hasChest()) {
            myStateController.changeState(GameState.CHEST);
            myGameUI.showChestInteraction();
            System.out.println("Interacting with chest");
            return;
        }

        // Check for items on the ground
        if (!currentRoom.getItems().isEmpty()) {
            collectItems(currentRoom);
            return;
        }

        System.out.println("Nothing to interact with in this room");
    }

    /**
     * Collects all items from current room
     *
     * @param theRoom room containing items
     */
    private void collectItems(final Room theRoom) {
        List<Item> items = new ArrayList<>(theRoom.getItems());
        if (!items.isEmpty()) {
            List<Item> collectedItems = new ArrayList<>();

            for (Item item : items) {
                boolean pickedUp = myGameModel.getPlayer().pickupItem(item);
                if (pickedUp) {
                    collectedItems.add(item);
                    System.out.println("Collected item: " + item.getName());
                } else {
                    System.out.println("Inventory full! Cannot pick up: " + item.getName());
                    break; // Stop collecting if inventory is full
                }
            }

            // Remove collected items from room
            for (Item collectedItem : collectedItems) {
                theRoom.removeItem(collectedItem);
            }

            // Update UI
            if (!collectedItems.isEmpty()) {
                myGameUI.updateInventory();
                myGameUI.showItemCollectionMessage(collectedItems);
            }
        }
    }

    /**
     * Opens the player's inventory
     */
    public void openInventory() {
        myStateController.changeState(GameState.INVENTORY);
        myGameUI.showInventoryScreen();
        System.out.println("Opened inventory");
    }

    /**
     * Closes the inventory and returns to previous state
     */
    public void closeInventory() {
        // Determine what state to return to
        Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());

        if (!currentRoom.getMonsters().isEmpty()) {
            // Return to combat if there are monsters
            myStateController.changeState(GameState.COMBAT);
            myGameUI.showCombatScreen(currentRoom.getMonsters());
        } else {
            // Return to exploration
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideInventoryScreen(); // This will switch back to game screen
        }

        System.out.println("Closed inventory");
    }

    /**
     * Scrolls the inventory selection up
     */
    public void scrollInventoryUp() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        mySelectedInventoryIndex--;
        if (mySelectedInventoryIndex < 0) {
            mySelectedInventoryIndex = inventory.size() - 1; // Wrap to bottom
        }

        myGameUI.updateInventorySelection(mySelectedInventoryIndex);
        System.out.println("Selected inventory item: " + inventory.get(mySelectedInventoryIndex).getName());
    }

    /**
     *scrolls inventory selection down
     */
    public void scrollInventoryDown() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        mySelectedInventoryIndex++;
        if (mySelectedInventoryIndex >= inventory.size()) {
            mySelectedInventoryIndex = 0; // Wrap to top
        }

        myGameUI.updateInventorySelection(mySelectedInventoryIndex);
        System.out.println("Selected inventory item: " + inventory.get(mySelectedInventoryIndex).getName());
    }

    /**
     *uses the currently selected item in the inventory
     */
    public void useSelectedItem() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty() || mySelectedInventoryIndex >= inventory.size()) {
            System.out.println("No item selected or inventory is empty");
            return;
        }

        Item selectedItem = inventory.get(mySelectedInventoryIndex);

        // Call the void useItem method
        myGameModel.getPlayer().useItem(selectedItem);

        System.out.println("Used item: " + selectedItem.getName());

        // Update UI
        myGameUI.updateInventory();
        myGameUI.updatePlayerStats();

        // If in combat, update monster display after using item
        if (myStateController.getCurrentState() == GameState.COMBAT) {
            Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
        }
    }

    /**
     * Performs regular attack against monsters in current room
     */
    public void playerAttack() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only attack during combat
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (monsters.isEmpty()) {
            endCombat();
            return;
        }

        // Get monster as target
        Monster target = monsters.get(0);

        // Call the attack method with target monster as argument
        int damage = player.attack(target);

        System.out.println("Player attacked " + target.getName() + " for " + damage + " damage!");

        // Check if monster is defeated
        if (target.getHealth() <= 0) {
            System.out.println("Defeated " + target.getName() + "!");
            currentRoom.removeMonster(target);

            // Check if all monsters are defeated
            if (currentRoom.getMonsters().isEmpty()) {
                System.out.println("All monsters defeated! Ending combat.");
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack
        if (myStateController.isInState(GameState.COMBAT)) {
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats(); // Update mana/energy display
        }
    }


    /**
     * Performs a special attack against monsters in the current room
     */
    public void playerSpecialAttack() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only attack during combat
        }

        Hero player = myGameModel.getPlayer();

        // Check if player has enough mana/energy for special attack
        if (!player.canUseSpecialAttack()) {
            System.out.println("Not enough mana/energy for special attack!");
            myGameUI.showNotEnoughManaMessage();
            return;
        }

        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (monsters.isEmpty()) {
            endCombat();
            return;
        }

        Monster target = monsters.get(0);

        // Perform special attack
        int damage = player.specialAttack();

        target.takeDamage(damage);
        System.out.println("Player used special attack on " + target.getName() + " for " + damage + " damage!");

        // Check if monster is defeated
        if (target.getHealth() <= 0) {
            System.out.println("Defeated " + target.getName() + "!");
            currentRoom.removeMonster(target);
            player.addGold(target.getGoldReward());
            if (target.getType().isBoss()) {
                player.setBossSlain(true);
            }

            // Check if all monsters are defeated
            if (currentRoom.getMonsters().isEmpty()) {
                System.out.println("All monsters defeated! Ending combat.");
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats(); // Update mana/energy display
        }
    }

    /**
     * Handles monster attacks against player
     */
    public void monsterAttacks() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        //create a copy to iterate over, as monsters might die and be removed
        List<Monster> attackers = new ArrayList<>(monsters);

        for (Monster monster : attackers) {
            if (monster.isAlive() && monster.getHealth() > 0) { // Double check monster is alive
                int damage = monster.attack(player);
                System.out.println(monster.getName() + " attacked player for " + damage + " damage!");
                myGameUI.showMonsterAttackEffect(monster, damage);
            }
        }

        myGameUI.updatePlayerStats();

        //only update combat screen if still in combat
        if (myStateController.isInState(GameState.COMBAT)) {
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats();
        }

        checkPlayerStatus();
    }


    /**
     *attempts to run from combat
     */
    public void playerRun() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only run during combat
        }

        // Chance to successfully run away
        double runChance = 0.6;

        if (Math.random() < runChance) {
            System.out.println("Successfully ran away from combat!");
            endCombat();
        } else {
            System.out.println("Failed to run away!");
            myGameUI.showRunFailedMessage();

            // Monsters get a free attack when run fails
            monsterAttacks();
        }
    }


    /**
     * Ends combat and returns to exploration mode
     */
    private void endCombat() {
        System.out.println("DEBUG: endCombat called - resetting flags");
        stopPlayerMovement();
        myEnteringCombat = false; // Reset the combat flag
        myLastCombatEndTime = System.currentTimeMillis();

        myStateController.changeState(GameState.EXPLORING);
        myGameUI.hideCombatScreen();
        System.out.println("Combat ended, returning to exploration");
    }

    /**
     * Opens a chest in the current room
     */
    public void openChest() {
        if (!myStateController.isInState(GameState.CHEST)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        if (currentRoom.hasChest()) {
            System.out.println("Opening chest...");
            System.out.println("Player has " + player.getGold() + " gold");

            // Get items from chest
            currentRoom.openChest(player);

            // Update UI
            myGameUI.showChestContents(currentRoom.getChest());
            myGameUI.updateInventory();

            // Return to exploration mode
            myStateController.changeState(GameState.EXPLORING);
        } else {
            System.out.println("No chest in this room!");
            myStateController.changeState(GameState.EXPLORING);
        }
    }

    /**
     * Cancels chest interaction
     */
    public void cancelChestInteraction() {
        if (myStateController.isInState(GameState.CHEST)) {
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideChestInteraction();
            System.out.println("Cancelled chest interaction");
        }
    }

    /**
     * pauses game
     */
    public void pauseGame() {
        myStateController.changeState(GameState.PAUSED);
        myGameUI.showPauseMenu(); // Changed from showPauseScreen() to showPauseMenu()
        System.out.println("Game paused");
    }


    /**
     * Inner class for serializing game state data to JSON
     */
    private static class GameStateData {
        /** current game state as string */
        public String currentState;
        /** Timestamp when game was saved */
        public long saveTimestamp;
    }

    /**
     *gets current room description
     *
     * @return A string description of the current room
     */
    public String getCurrentRoomDescription() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        StringBuilder description = new StringBuilder();
        description.append("You are in a ");
        description.append(currentRoom.getDescription());

        // Add information about doors
        description.append("\nExits: ");
        if (currentRoom.hasNorthDoor()) description.append("North ");
        if (currentRoom.hasEastDoor()) description.append("East ");
        if (currentRoom.hasSouthDoor()) description.append("South ");
        if (currentRoom.hasWestDoor()) description.append("West ");

        // Add information about contents
        if (currentRoom.hasChest()) {
            description.append("\nThere is a chest in this room.");
        }

        if (currentRoom.hasPillar()) {
            description.append("\nA Pillar of OO stands in the center of the room!");
        }

        if (!currentRoom.getItems().isEmpty()) {
            description.append("\nItems on the ground: ");
            for (Item item : currentRoom.getItems()) {
                description.append(item.getName()).append(" ");
            }
        }

        return description.toString();
    }

    /**
     * Sets currently selected inventory index
     *
     * @param theIndex index of inventory item to select
     */
    public void setInventoryIndex(final int theIndex) {
        mySelectedInventoryIndex = theIndex;
    }

    /**
     * Gets StateController associated with this GameController
     * @return StateController instance
     */
    public StateController getStateController() {
        return myStateController;
    }


    /**
     * Gets reference to GameUI for screen transitions
     * @return GameUI instance
     */
    public GameUI getGameUI() {
        return myGameUI;
    }

    /**
     * Loads game from database data
     */
    public boolean loadGameFromSaveData(String playerData, String dungeonData, String gameStateData) {
        try {
            // Restore player
            Hero loadedPlayer = Hero.fromJson(playerData);
            myGameModel.setPlayer(loadedPlayer);

            // Restore dungeon
            Dungeon loadedDungeon = Dungeon.fromJson(dungeonData);
            myGameModel.setDungeon(loadedDungeon);

            // Right here VisonPlaceholder potions are replaced with vision. Getting the dungeon needed for
            // vision potion parameters was hard in the reload hero stuff, so a placeholders were put into
            // the inventory for replacing here.
            int inventorySize = loadedPlayer.getInventory().size() - 1;
            while (inventorySize >= 0) {
                if (loadedPlayer.getInventory().get(inventorySize).getName().equals("VisionPlaceholder")) {
                    loadedPlayer.useItem(loadedPlayer.getInventory().get(inventorySize));
                    loadedPlayer.addItem(new VisionPotion("Vision Potion", "Reveals nearby tiles", loadedDungeon));
                }
                inventorySize--;
            }


            // DON'T restore the saved game state - always start in EXPLORING
            // The saved state might be PAUSED, COMBAT, etc. which we don't want
            // ObjectMapper mapper = new ObjectMapper();
            // GameStateData stateData = mapper.readValue(gameStateData, GameStateData.class);
            // myStateController.changeState(GameState.valueOf(stateData.currentState));

            // INSTEAD: Always set to EXPLORING when loading
            myStateController.changeState(GameState.EXPLORING);

            System.out.println("Game loaded successfully from save data - State set to EXPLORING");
            return true;
        } catch (Exception e) {
            System.err.println("Error loading game from save data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the game with a custom name
     */
    public void saveGameWithName(String saveName) {
        try {
            //serialize player data
            String playerData = myGameModel.getPlayer().toJson();

            //serialize dungeon data
            String dungeonData = myGameModel.getDungeon().toJson();

            //create game state info
            GameStateData stateData = new GameStateData();
            stateData.currentState = myStateController.getCurrentState().name();
            stateData.saveTimestamp = System.currentTimeMillis();

            ObjectMapper mapper = new ObjectMapper();
            String gameStateJson = mapper.writeValueAsString(stateData);

            //save to database with custom name
            boolean saved = myGameModel.getDatabase().saveGameData(
                    saveName,
                    playerData,
                    dungeonData,
                    gameStateJson
            );

            if (saved) {
                System.out.println("Game saved successfully as: " + saveName);
                myGameUI.showSaveSuccessMessage();
            } else {
                System.out.println("Failed to save game: " + saveName);
                myGameUI.showSaveFailureMessage();
            }
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            myGameUI.showSaveFailureMessage();
        }
    }

    /**
     * Outputs debug information about the player's current movement state
     */
    public void debugPlayerMovement() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            System.out.println("=== PLAYER MOVEMENT DEBUG ===");
            System.out.println("Room Position: " + player.getPosition());
            System.out.println("Pixel Position: (" + player.getPixelX() + "," + player.getPixelY() + ")");
            System.out.println("Is Moving: " + player.isMoving());
            System.out.println("Can Move: " + canMovePlayer());
            System.out.println("Game State: " + myStateController.getCurrentState());
            System.out.println("Entering Combat: " + myEnteringCombat);
            System.out.println("==============================");
        }
    }

}

