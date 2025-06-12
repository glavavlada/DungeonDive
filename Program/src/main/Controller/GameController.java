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
 * control main game logic
 */
public class GameController {
    private final Model myGameModel;
    private final GameUI myGameUI;
    private final StateController myStateController;

    private int mySelectedInventoryIndex = 0;
    private boolean myEnteringCombat = false;
    private long myLastCombatEndTime = 0;

    /**
     * Constructor for GameController.
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
                // Use the existing enterRoom logic to handle all initial room effects consistently.
                enterRoom(startingRoom);
            }
        }
    }
    // Add this method to prevent room transitions during combat
    private boolean canMovePlayer() {
        return myStateController.isInState(GameState.EXPLORING) && !myEnteringCombat;
    }

    // Update all movement methods to use this check:
    public void startPlayerMovementNorth() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingNorth();
        }
    }

    public void startPlayerMovementSouth() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingSouth();
        }
    }

    public void startPlayerMovementEast() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingEast();
        }
    }

    public void startPlayerMovementWest() {
        if (canMovePlayer()) {
            myGameModel.getPlayer().startMovingWest();
        }
    }

    public void stopPlayerMovementNorth() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingNorth();
        }
    }

    public void stopPlayerMovementSouth() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingSouth();
        }
    }

    public void stopPlayerMovementEast() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingEast();
        }
    }

    public void stopPlayerMovementWest() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMovingWest();
        }
    }

    /**
      * Updates existing room transition methods to only handle room changes,
      * not pixel movement (that's handled in Hero.updatePixelPosition()).
      */
    public void movePlayerNorth() {
        if (!canMovePlayer()) {
            System.out.println("Cannot move player - not in exploring state or entering combat");
            return;
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Point newPos = new Point(currentPos.getX(), currentPos.getY() - 1);

        // Move player to the new room
        player.setPosition(newPos);

        // Enter the new room and handle room effects
        Room newRoom = myGameModel.getDungeon().getRoom(newPos);
        enterRoom(newRoom);

        // Notify UI of room change
        notifyRoomChanged();

        System.out.println("Player moved north to " + newPos);
    }

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

//    /**
//     * Update the legacy startPlayerMovement method to work with new system.
//     */
//    public void startPlayerMovement(final Direction theDirection) {
//        switch (theDirection) {
//            case NORTH:
//                startPlayerMovementNorth();
//                break;
//            case SOUTH:
//                startPlayerMovementSouth();
//                break;
//            case EAST:
//                startPlayerMovementEast();
//                break;
//            case WEST:
//                startPlayerMovementWest();
//                break;
//        }
//    }
//
//    /**
    /**
     * Stops all player movement - useful when transitioning between states
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
     * Notifies the UI that the player has moved to a new room.
     */
    private void notifyRoomChanged() {
        if (myGameUI != null && myGameUI.getGameScreen() != null) {
            myGameUI.getGameScreen().onRoomChanged();
        }
    }


    /**
     * Handles room entry logic.
     */
    private void enterRoom(final Room theRoom) {
        // Add cooldown check to prevent immediate re-entry into combat
        long timeSinceLastCombat = System.currentTimeMillis() - myLastCombatEndTime;
        if (timeSinceLastCombat < 200) { // 200ms cooldown
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

    public void activateDungeonVisionCheat() {
        Dungeon dungeon = myGameModel.getDungeon();
        for (int row = 0; row < dungeon.getHeight(); row++) {
            for (int col = 0; col < dungeon.getWidth(); col++) {
                dungeon.getRoom(row, col).setVisited(true);
            }
        }
    }

    /**
     * Activates a trap in the room and applies its effects.
     *
     * @param theRoom The room containing the trap
     */
    private void activateTrap(final Room theRoom) {
        Hero player = myGameModel.getPlayer();
        int damage = theRoom.getTrap().getDamage();

        // Apply trap damage to player
        player.takeDamage(damage);
        System.out.println("Player triggered a trap and took " + damage + " damage!");

        // Update UI to show trap effect
        myGameUI.showTrapEffect(theRoom.getTrap());

        // Check if player died from trap
        checkPlayerStatus();
    }

    /**
     *activates pillar in room
     *
     * @param theRoom The room containing the pillar
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

                // Check if all pillars activated (win condition)
                checkWinCondition();
            } else {
                System.out.println("DEBUG: Failed to activate pillar");
            }
        }
    }

    /**
     * Checks if the player has met the win condition (activated all pillars and in exit).
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

        // For now, let's make it so you only need pillars and exit (not boss)
        // You can add boss requirement later
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
     * Checks player's health status and handles death if necessary.
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

        // Check for pillar interaction FIRST
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
     * Collects all items from the current room.
     *
     * @param theRoom The room containing items
     */
    private void collectItems(final Room theRoom) {
        List<Item> items = new ArrayList<>(theRoom.getItems()); // Create copy to avoid modification issues
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
     * Opens the player's inventory.
     */
    public void openInventory() {
        myStateController.changeState(GameState.INVENTORY);
        myGameUI.showInventoryScreen();
        System.out.println("Opened inventory");
    }

    /**
     * Closes the inventory and returns to previous state.
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
     * Scrolls the inventory selection up.
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

        // Since we can't check the return value, we'll assume the item was used successfully
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

        // Check if monster is defeated - SAME FIX HERE
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

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats(); // Update mana/energy display
        }
    }


    /**
     * Performs a special attack against monsters in the current room.
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

        // Check if monster is defeated - THIS IS THE KEY FIX
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
     *attempts to run from combat.
     */
    public void playerRun() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only run during combat
        }

        // Chance to successfully run away (could be based on player stats)
        double runChance = 0.6; // 60% chance to run

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
     * Ends combat and returns to exploration mode.
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
     * Opens a chest in the current room.
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
     * Cancels chest interaction.
     */
    public void cancelChestInteraction() {
        if (myStateController.isInState(GameState.CHEST)) {
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideChestInteraction();
            System.out.println("Cancelled chest interaction");
        }
    }

    /**
     *pauses game
     */
    public void pauseGame() {
        myStateController.changeState(GameState.PAUSED);
        myGameUI.showPauseMenu(); // Changed from showPauseScreen() to showPauseMenu()
        System.out.println("Game paused");
    }

    /**
     *resumes game from pause
     */
    public void resumeGame() {
        // Return to previous state
        Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());

        if (!currentRoom.getMonsters().isEmpty()) {
            myStateController.changeState(GameState.COMBAT);
            myGameUI.showCombatScreen(currentRoom.getMonsters());
        } else {
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideAllOverlays();
        }

        System.out.println("Game resumed");
    }

    private static class GameStateData {
        public String currentState;
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

    public void setInventoryIndex(final int theIndex) {
        mySelectedInventoryIndex = theIndex;
    }

    /**
     *gets current combat status description
     *
     * @return string description of current combat
     */
    public String getCombatDescription() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (monsters.isEmpty()) {
            return "No monsters present.";
        }

        StringBuilder description = new StringBuilder();
        description.append("Combat with: ");

        for (Monster monster : monsters) {
            description.append("\n").append(monster.getName())
                    .append(" (HP: ").append(monster.getHealth()).append(")");
        }

        return description.toString();
    }

    /**
     *gets the player's inventory as a formatted string.
     *
     * @return string representation of player's inventory
     */
    public String getInventoryDescription() {
        List<Item> inventory = myGameModel.getPlayer().getInventory();

        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder description = new StringBuilder();
        description.append("Inventory:\n");

        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            description.append(i == mySelectedInventoryIndex ? "→ " : "  ");
            description.append(item.getName()).append(" - ").append(item.getDescription()).append("\n");
        }

        return description.toString();
    }

    /**
     * Gets the StateController associated with this GameController.
     * @return The StateController instance.
     */
    public StateController getStateController() {
        return myStateController;
    }

    /**
     * temporary method to demonstrate GameController is working.
     */
    public void printStatus() {
        System.out.println("Game is working");
        System.out.println("Current state: " + myStateController.getCurrentState());
        System.out.println("Player: " + myGameModel.getPlayer().getName() +
                " (HP: " + myGameModel.getPlayer().getHealth() + "/" +
                myGameModel.getPlayer().getMaxHealth() + ")");
    }

    /**
     * Gets reference to the GameUI for screen transitions
     * @return The GameUI instance
     */
    public GameUI getGameUI() {
        return myGameUI;
    }

    /**
     * Loads a game from database data (called from SavesScreen)
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
     * Call this method after loading a game to ensure everything is properly initialized
     */
    public void initializeLoadedGame() {
        System.out.println("Initializing loaded game...");

        // Reset combat flags
        myEnteringCombat = false;
        myLastCombatEndTime = 0;

        // Reset inventory selection
        mySelectedInventoryIndex = 0;

        // Ensure player movement state is clean
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.resetMovementState();

            // Synchronize positions (you may need to adjust these values based on your room size)
            player.synchronizePositions(480.0, 480.0); // Adjust based on your actual room pixel size

            System.out.println("Player initialized - Position: " + player.getPosition() +
                              ", Pixel: (" + player.getPixelX() + "," + player.getPixelY() + ")");
        }

        // Ensure we're in the correct state
        myStateController.changeState(GameState.EXPLORING);

        // Update UI
        if (myGameUI != null) {
            myGameUI.updatePlayerStats();
            notifyRoomChanged();
        }

        System.out.println("Loaded game initialization complete");
    }

    // Add debug method to check movement
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

