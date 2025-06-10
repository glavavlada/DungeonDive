package main.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.Model.Model;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.element.Item;
import main.Model.element.Pillar;
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
    }

//    public void startPlayerMovement(final Direction theDirection) {
//        if (!myStateController.isInState(GameState.EXPLORING)) {
//            return;
//        }
//
//        Hero player = myGameModel.getPlayer();
//        player.startMoving(theDirection);
//    }
//
//    public void stopPlayerMovement() {
//        Hero player = myGameModel.getPlayer();
//        if (player != null) {
//            player.stopMoving();
//        }
//    }

    /**
     * Starts continuous movement north.
     */
    public void startPlayerMovementNorth() {
        if (myStateController.isInState(GameState.EXPLORING)) {
            myGameModel.getPlayer().startMovingNorth();
        }
    }

    public void startPlayerMovementSouth() {
        if (myStateController.isInState(GameState.EXPLORING)) {
            myGameModel.getPlayer().startMovingSouth();
        }
    }

    public void startPlayerMovementEast() {
        if (myStateController.isInState(GameState.EXPLORING)) {
            myGameModel.getPlayer().startMovingEast();
        }
    }

    public void startPlayerMovementWest() {
        if (myStateController.isInState(GameState.EXPLORING)) {
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
        if (!myStateController.isInState(GameState.EXPLORING)) {
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
//     * Update the legacy stopPlayerMovement method.
//     */
//    public void stopPlayerMovement() {
//        Hero player = myGameModel.getPlayer();
//        if (player != null) {
//            player.stopAllMovement();
//        }
//    }

    /**
     * Notifies the UI that the player has moved to a new room.
     */
    private void notifyRoomChanged() {
        if (myGameUI != null && myGameUI.getGameScreen() != null) {
            myGameUI.getGameScreen().onRoomChanged();
        }
    }

    /**
     * Adds a message to the game UI.
     */
    private void addGameMessage(String message) {
        if (myGameUI != null && myGameUI.getGameScreen() != null) {
            myGameUI.getGameScreen().addGameMessage(message);
        }
    }

    /**
     * Handles room entry logic.
     *
     * @param theRoom The room being entered
     */
    private void enterRoom(final Room theRoom) {
        // Mark room as visited
        theRoom.setVisited(true);

        // Check for monsters
        if (!theRoom.getMonsters().isEmpty()) {
            // Enter combat mode
            myStateController.changeState(GameState.COMBAT);
            myGameUI.showCombatScreen(theRoom.getMonsters());
            System.out.println("Entered combat with " + theRoom.getMonsters().size() + " monsters");
            return;
        }

        // Check for chest
        if (theRoom.hasChest()) {
            System.out.println("Room contains a chest that can be opened with 'E'");
            myGameUI.showChestPrompt();
        }

        // Check for pillar
        if (theRoom.hasPillar()) {
            System.out.println("Room contains a pillar of OO!");
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
            boolean activated = myGameModel.getPlayer().activatePillar(pillar);

            if (activated) {
                myGameModel.getPlayer().setPillarsActivated(myGameModel.getPlayer().getPillarsActivated() + 1);
                //update UI to show pillar activation and stat changes
                myGameUI.showPillarActivated(pillar);
                myGameUI.updatePlayerStats();

                //check if all pillars activated (win condition)
                checkWinCondition();
            }
        }
    }

    /**
     * Checks if the player has met the win condition (activated all pillars).
     */
    private void checkWinCondition() {
        if (myGameModel.getPlayer().hasActivatedAllPillars()) {
            System.out.println("Player has activated all pillars! Victory!");
            myStateController.changeState(GameState.VICTORY);
            myGameUI.showVictoryScreen();
        }
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

        // Check for chest interaction
        if (currentRoom.hasChest()) {
            myStateController.changeState(GameState.CHEST);
            myGameUI.showChestInteraction();
            System.out.println("Interacting with chest");
        }
        // Check for pillar interaction
        else if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            activatePillar(currentRoom);
            System.out.println("Activated pillar in room");
        }
        // Check for items on the ground
        else if (!currentRoom.getItems().isEmpty()) {
            collectItems(currentRoom);
        }
        else {
            System.out.println("Nothing to interact with in this room");
        }
    }

    /**
     * Collects all items from the current room.
     *
     * @param theRoom The room containing items
     */
    private void collectItems(final Room theRoom) {
        List<Item> items = theRoom.getItems();
        if (!items.isEmpty()) {
            for (Item item : items) {
                myGameModel.getPlayer().addItem(item);
                System.out.println("Collected item: " + item.getName());
            }

            // Clear items from room
            theRoom.clearItems();

            // Update UI
            myGameUI.updateInventory();
            myGameUI.showItemCollectionMessage(items);
        }
    }

    /**
     * Opens the player's inventory.
     */
    public void openInventory() {
        mySelectedInventoryIndex = 0; // Reset selection to first item
        myStateController.changeState(GameState.INVENTORY);
        myGameUI.showInventoryScreen();
        System.out.println("Opened inventory");
    }

    /**
     * Closes the inventory and returns to previous state.
     */
    public void closeInventory() {
        // Return to previous state (usually EXPLORING or COMBAT)
        if (myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition()).getMonsters().isEmpty()) {
            myStateController.changeState(GameState.EXPLORING);
        } else {
            myStateController.changeState(GameState.COMBAT);
        }

        myGameUI.hideInventoryScreen();
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
     *performs regular attack against monsters in current room
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

        // takeDamage() is already called in attack(), it was causing double hits.
        // I'll just keep it as comment for now.
        //target.takeDamage(damage);
        System.out.println("Player attacked " + target.getName() + " for " + damage + " damage!");

        //check if monster is defeated
        if (target.getHealth() <= 0) {
            System.out.println("Defeated " + target.getName() + "!");
            currentRoom.removeMonster(target);

            // Check if all monsters are defeated
            if (monsters.isEmpty()) {
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            monsterAttacks();
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
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

        if (currentRoom.getMonsters().isEmpty()) {
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

            // Check if all monsters are defeated
            if (currentRoom.getMonsters().isEmpty()) {
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            monsterAttacks();
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats(); // Update mana/energy display
        }
    }

    /**
     *handles monster attacks against player
     */
    public void monsterAttacks() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        // Create a copy to iterate over, as monsters might die and be removed
        List<Monster> attackers = new ArrayList<>(monsters);

        for (Monster monster : attackers) {
            if (monster.isAlive()) { // Only living monsters attack
                int damage = monster.attack(player); // attack should call takeDamage
                System.out.println(monster.getName() + " attacked player for " + damage + " damage!");
                myGameUI.showMonsterAttackEffect(monster, damage);
            }
        }

        myGameUI.updatePlayerStats();
        myGameUI.updateCombatScreen(currentRoom.getMonsters());
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
            // Get items from chest
            List<Item> chestItems = currentRoom.openChest();

            // Add items to player inventory
            for (Item item : chestItems) {
                player.addItem(item);
                System.out.println("Found item in chest: " + item.getName());
            }

            // Update UI
            myGameUI.showChestContents(chestItems);
            myGameUI.updateInventory();

            // Return to exploration mode
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

    /**
     *saves the current game state
     */
    public void saveGame() {
        try {
            // Serialize player data
            String playerData = myGameModel.getPlayer().toJson();

            // Serialize dungeon data (you'll need to implement this in Dungeon class)
            String dungeonData = myGameModel.getDungeon().toJson();

            // Create game state info
            GameStateData stateData = new GameStateData();
            stateData.currentState = myStateController.getCurrentState().name();
            stateData.saveTimestamp = System.currentTimeMillis();

            ObjectMapper mapper = new ObjectMapper();
            String gameStateJson = mapper.writeValueAsString(stateData);

            // Save to database
            boolean saved = myGameModel.getDatabase().saveGameData(
                    "save1", // You can make this dynamic
                    playerData,
                    dungeonData,
                    gameStateJson
            );

            if (saved) {
                System.out.println("Game saved successfully");
                myGameUI.showSaveSuccessMessage();
            } else {
                System.out.println("Failed to save game");
                myGameUI.showSaveFailureMessage();
            }
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            myGameUI.showSaveFailureMessage();
        }
    }


    /**
     * Loads saved game state
     *
     * @param theSaveId ID of save to load
     * @return True if game was loaded successfully
     */
    public boolean loadGame(final String theSaveId) {
        try {
            ResultSet rs = myGameModel.getDatabase().loadGameData(theSaveId);

            if (rs != null && rs.next()) {
                String playerData = rs.getString("player_data");
                String dungeonData = rs.getString("dungeon_data");
                String gameStateData = rs.getString("game_state");

                // Restore player
                Hero loadedPlayer = Hero.fromJson(playerData);
                myGameModel.setPlayer(loadedPlayer);

                // Restore dungeon (implement Dungeon.fromJson())
                // Dungeon loadedDungeon = Dungeon.fromJson(dungeonData);
                // myGameModel.setDungeon(loadedDungeon);

                // Restore game state
                ObjectMapper mapper = new ObjectMapper();
                GameStateData stateData = mapper.readValue(gameStateData, GameStateData.class);
                myStateController.changeState(GameState.valueOf(stateData.currentState));

                // Update UI
                myGameUI.updatePlayerStats();
                myGameUI.updateInventory();

                System.out.println("Game loaded successfully");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
        }

        return false;
    }

    private static class GameStateData {
        public String currentState;
        public long saveTimestamp;
    }


    /**
     * Gets reference to the game model
     * @return The Model instance
     */
    public Model getGameModel() {
        return myGameModel;
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

            // Restore game state
            ObjectMapper mapper = new ObjectMapper();
            GameStateData stateData = mapper.readValue(gameStateData, GameStateData.class);
            myStateController.changeState(GameState.valueOf(stateData.currentState));

            System.out.println("Game loaded successfully from save data");
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

}


