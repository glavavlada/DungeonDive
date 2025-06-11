package main.View.screen;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Controller.Controller;
import main.Model.element.Item;
import main.View.GameUI;

import java.util.List;

public class InventoryScreen extends Screen {

    // ====== RESPONSIVE CONFIGURATION CONSTANTS ======
    private static final class Config {
        // Window size
        static final double WINDOW_WIDTH_RATIO = 0.35;
        static final double WINDOW_HEIGHT_RATIO = 0.75;
        static final double WINDOW_MIN_WIDTH = 400.0;
        static final double WINDOW_MAX_WIDTH = 600.0;
        static final double WINDOW_MIN_HEIGHT = 500.0;
        static final double WINDOW_MAX_HEIGHT = 800.0;

        // Font size ratios relative to window height (further reduced for buttons)
        static final double TITLE_FONT_RATIO = 0.035;
        static final double SECTION_TITLE_FONT_RATIO = 0.025;
        static final double ITEM_FONT_RATIO = 0.022;
        static final double DESC_FONT_RATIO = 0.020;
        static final double BUTTON_FONT_RATIO = 0.020;

        // Spacing and padding ratios
        static final double PADDING_RATIO = 0.025;
        static final double SPACING_RATIO = 0.02;
        static final double ITEM_SPACING_RATIO = 0.015;

        // Colors (Medieval/Fantasy Theme)
        static final String BACKGROUND_COLOR = "#2a2a2a";
        static final String PANEL_COLOR = "#3a3a3a";
        static final String BORDER_COLOR = "#8B4513"; // SaddleBrown
        static final String TEXT_COLOR = "#E0E0E0";
        static final String TITLE_COLOR = "#D4AF37"; // Gold
        static final String SECTION_TITLE_COLOR = "#DAA520"; // GoldenRod
        static final String SELECTION_COLOR = "#8B4513";
        static final String EMPTY_TEXT_COLOR = "#AAAAAA";

        // Button styles (matching HeroSelectionScreen)
        static final String BUTTON_BASE_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #4A4A4A, #2A2A2A); " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 2, 2);";

        static final String BUTTON_HOVER_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #5A5A5A, #3A3A3A); " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-color: #FFD700; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(218,165,32,0.8), 15, 0.7, 3, 3);";

        static final String BUTTON_DISABLED_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #1A1A1A, #0D0D0D); " +
                        "-fx-text-fill: #666666; " +
                        "-fx-border-color: #333333; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0.3, 1, 1);";

        // Pixel font path
        static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    }

    // ====== UI AND LOGIC MEMBERS ======
    private final Stage myInventoryStage;
    private VBox myItemListVBox;
    private Label myItemDescriptionLabel;
    private Label mySelectedItemTitleLabel;
    private Button myUseButton;
    private Button myCloseButton;
    private int mySelectedIndex = 0;
    private List<Item> myCurrentInventory;
    private boolean isUIInitialized = false;

    public InventoryScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        myInventoryStage = new Stage();
        myInventoryStage.initStyle(StageStyle.TRANSPARENT);
        myInventoryStage.initOwner(getStage());
    }

    @Override
    public void showScreen(final GameUI theUI) {
        // Calculate initial size based on the main window
        double primaryWidth = getStage().getWidth();
        double primaryHeight = getStage().getHeight();
        double windowWidth = Math.max(Config.WINDOW_MIN_WIDTH,
                Math.min(Config.WINDOW_MAX_WIDTH, primaryWidth * Config.WINDOW_WIDTH_RATIO));
        double windowHeight = Math.max(Config.WINDOW_MIN_HEIGHT,
                Math.min(Config.WINDOW_MAX_HEIGHT, primaryHeight * Config.WINDOW_HEIGHT_RATIO));

        BorderPane root = createInventoryLayout();
        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.setFill(null); // Make scene background transparent

        setupEventHandlers(scene);

        myInventoryStage.setScene(scene);
        myInventoryStage.setTitle("DungeonDive - Inventory");

        // Center the inventory on the main game window
        myInventoryStage.setX(getStage().getX() + (getStage().getWidth() - windowWidth) / 2);
        myInventoryStage.setY(getStage().getY() + (getStage().getHeight() - windowHeight) / 2);

        myInventoryStage.show();

        // Mark UI as initialized and then activate
        isUIInitialized = true;
        onScreenActivated();
    }

    private BorderPane createInventoryLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + "; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 3px; -fx-background-radius: 6px; -fx-border-radius: 6px;");

        // Responsive Bindings
        NumberBinding padding = root.heightProperty().multiply(Config.PADDING_RATIO);
        NumberBinding spacing = root.heightProperty().multiply(Config.SPACING_RATIO);
        root.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(padding.doubleValue()), padding));

        // ====== TOP: TITLE ======
        Label titleLabel = createTitleLabel(root);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // ====== CENTER: ITEM LIST AND DESCRIPTION ======
        HBox centerContainer = new HBox();
        centerContainer.spacingProperty().bind(spacing);

        // Left side: Item List
        VBox leftPanel = createItemListPanel(root, spacing);

        // Right side: Selected Item Description
        VBox rightPanel = createDescriptionPanel(root, spacing);

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        centerContainer.getChildren().addAll(leftPanel, rightPanel);

        // ====== BOTTOM: BUTTONS ======
        HBox buttonBox = createButtonBox(root, spacing);

        root.setTop(titleLabel);
        root.setCenter(centerContainer);
        root.setBottom(buttonBox);

        return root;
    }

    private Label createTitleLabel(BorderPane root) {
        Label titleLabel = new Label("INVENTORY");
        titleLabel.setStyle("-fx-text-fill: " + Config.TITLE_COLOR + ";");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.TITLE_FONT_RATIO, 40);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Impact");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));
        return titleLabel;
    }

    private VBox createItemListPanel(BorderPane root, NumberBinding spacing) {
        Label listTitle = new Label("Your Items");
        listTitle.setStyle("-fx-text-fill: " + Config.SECTION_TITLE_COLOR + ";");
        listTitle.setWrapText(true);
        listTitle.setAlignment(Pos.CENTER);
        listTitle.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.SECTION_TITLE_FONT_RATIO, 18);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        myItemListVBox = new VBox();
        NumberBinding itemSpacing = root.heightProperty().multiply(Config.ITEM_SPACING_RATIO);
        myItemListVBox.spacingProperty().bind(itemSpacing);

        ScrollPane scrollPane = new ScrollPane(myItemListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + Config.PANEL_COLOR + "; " +
                "-fx-background-color: transparent; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox listContainer = new VBox();
        listContainer.spacingProperty().bind(spacing.divide(2));
        listContainer.getChildren().addAll(listTitle, scrollPane);

        NumberBinding containerPadding = root.heightProperty().multiply(Config.PADDING_RATIO / 3);
        listContainer.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(containerPadding.doubleValue()), containerPadding));

        listContainer.setStyle("-fx-background-color: " + Config.PANEL_COLOR + "; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return listContainer;
    }

    private VBox createDescriptionPanel(BorderPane root, NumberBinding spacing) {
        mySelectedItemTitleLabel = new Label("No Item Selected");
        mySelectedItemTitleLabel.setStyle("-fx-text-fill: " + Config.SECTION_TITLE_COLOR + ";");
        mySelectedItemTitleLabel.setWrapText(true);
        mySelectedItemTitleLabel.setAlignment(Pos.CENTER);
        mySelectedItemTitleLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.SECTION_TITLE_FONT_RATIO, 18);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        myItemDescriptionLabel = new Label("Select an item to see its description.");
        myItemDescriptionLabel.setStyle("-fx-text-fill: " + Config.TEXT_COLOR + ";");
        myItemDescriptionLabel.setWrapText(true);
        myItemDescriptionLabel.setAlignment(Pos.TOP_LEFT);
        myItemDescriptionLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.DESC_FONT_RATIO, 14);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), fontSize);
        }, root.heightProperty()));

        // Bind the max width to ensure proper wrapping
        myItemDescriptionLabel.maxWidthProperty().bind(root.widthProperty().multiply(0.4));

        VBox descriptionContainer = new VBox();
        descriptionContainer.spacingProperty().bind(spacing.divide(2));
        descriptionContainer.getChildren().addAll(mySelectedItemTitleLabel, myItemDescriptionLabel);

        NumberBinding containerPadding = root.heightProperty().multiply(Config.PADDING_RATIO / 3);
        descriptionContainer.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(containerPadding.doubleValue()), containerPadding));

        descriptionContainer.setStyle("-fx-background-color: " + Config.PANEL_COLOR + "; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px;");

        VBox.setVgrow(myItemDescriptionLabel, Priority.ALWAYS);

        return descriptionContainer;
    }

    private HBox createButtonBox(BorderPane root, NumberBinding spacing) {
        myUseButton = createStyledButton("Use", root);
        myUseButton.setOnAction(e -> useSelectedItem());

        myCloseButton = createStyledButton("Close", root);
        myCloseButton.setOnAction(e -> closeInventory());

        HBox buttonBox = new HBox();
        buttonBox.spacingProperty().bind(spacing);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(myUseButton, myCloseButton);

        // Make buttons expand to fill available space
        HBox.setHgrow(myUseButton, Priority.ALWAYS);
        HBox.setHgrow(myCloseButton, Priority.ALWAYS);

        return buttonBox;
    }

    private Button createStyledButton(String text, BorderPane root) {
        Button button = new Button(text);

        // Apply initial style
        button.setStyle(Config.BUTTON_BASE_STYLE);

        // Set up font binding with maximum size constraints
        button.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.BUTTON_FONT_RATIO, 14);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Courier New");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        // Set up size bindings with better constraints for smaller buttons
        button.prefWidthProperty().bind(root.widthProperty().multiply(0.28));
        button.minWidthProperty().bind(root.widthProperty().multiply(0.25));
        button.maxWidthProperty().bind(root.widthProperty().multiply(0.35));
        button.minHeightProperty().bind(root.heightProperty().multiply(0.055));
        button.maxHeightProperty().bind(root.heightProperty().multiply(0.08));

        // Enable text wrapping for buttons
        button.setWrapText(true);
        button.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Set up hover effects
        setupButtonHoverEffects(button);

        return button;
    }

    private void setupButtonHoverEffects(Button button) {
        button.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                button.setStyle(Config.BUTTON_HOVER_STYLE);
            }
        });

        button.setOnMouseExited(e -> {
            if (button.isDisabled()) {
                button.setStyle(Config.BUTTON_DISABLED_STYLE);
            } else {
                button.setStyle(Config.BUTTON_BASE_STYLE);
            }
        });

        // Update style when disabled state changes
        button.disabledProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                button.setStyle(Config.BUTTON_DISABLED_STYLE);
            } else {
                button.setStyle(Config.BUTTON_BASE_STYLE);
            }
        });
    }

    // ====== LOGIC METHODS ======

    public void onScreenActivated() {
        if (!isUIInitialized) {
            return;
        }
        updateInventory();
    }

    public void updateInventory() {
        if (MY_CONTROLLER.getPlayer() == null) return;

        // Safety check for UI components
        if (myItemListVBox == null || mySelectedItemTitleLabel == null || myItemDescriptionLabel == null) {
            return;
        }

        myCurrentInventory = MY_CONTROLLER.getPlayer().getInventory();
        myItemListVBox.getChildren().clear();

        if (myCurrentInventory.isEmpty()) {
            Label emptyLabel = new Label("Inventory is empty.");
            emptyLabel.setStyle("-fx-text-fill: " + Config.EMPTY_TEXT_COLOR + "; -fx-font-style: italic;");
            emptyLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
                double fontSize = Math.min(myInventoryStage.getScene().getHeight() * Config.DESC_FONT_RATIO, 14);
                Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
                return Font.font(pixelFont.getFamily(), fontSize);
            }, myInventoryStage.getScene().heightProperty()));
            emptyLabel.setWrapText(true);
            emptyLabel.setAlignment(Pos.CENTER);
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setPadding(new Insets(20));

            myItemListVBox.getChildren().add(emptyLabel);
            myUseButton.setDisable(true);
            mySelectedIndex = -1;
            updateSelectedItemInfo();
        } else {
            // Ensure index is valid
            mySelectedIndex = Math.max(0, Math.min(mySelectedIndex, myCurrentInventory.size() - 1));

            for (int i = 0; i < myCurrentInventory.size(); i++) {
                final int itemIndex = i;
                Item item = myCurrentInventory.get(i);
                Label itemLabel = createItemLabel(item.getName(), itemIndex);
                myItemListVBox.getChildren().add(itemLabel);
            }
            myUseButton.setDisable(false);
            updateSelectedItemInfo();
        }
    }

    private Label createItemLabel(String itemName, int itemIndex) {
        Label itemLabel = new Label(itemName);

        // Set up font binding with size constraints
        itemLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double baseFontSize = Math.min(myInventoryStage.getScene().getHeight() * Config.ITEM_FONT_RATIO, 14);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)baseFontSize, "Arial");
            return Font.font(pixelFont.getFamily(), baseFontSize);
        }, myInventoryStage.getScene().heightProperty()));

        // Enable text wrapping and set proper alignment
        itemLabel.setWrapText(true);
        itemLabel.setAlignment(Pos.CENTER_LEFT);

        NumberBinding padding = myInventoryStage.getScene().heightProperty().multiply(0.008);
        itemLabel.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(padding.doubleValue()), padding));

        itemLabel.setMaxWidth(Double.MAX_VALUE);

        // Bind max width to ensure proper wrapping within the panel
        itemLabel.maxWidthProperty().bind(myInventoryStage.getScene().widthProperty().multiply(0.15));

        // Add click handler to select item
        itemLabel.setOnMouseClicked(e -> updateSelection(itemIndex));

        // Add hover effects
        itemLabel.setOnMouseEntered(e -> {
            if (itemIndex != mySelectedIndex) {
                itemLabel.setStyle("-fx-background-color: rgba(139, 69, 19, 0.3); " +
                        "-fx-text-fill: " + Config.TEXT_COLOR + "; " +
                        "-fx-background-radius: 3px; " +
                        "-fx-cursor: hand;");
            }
        });

        itemLabel.setOnMouseExited(e -> {
            if (itemIndex != mySelectedIndex) {
                itemLabel.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: " + Config.TEXT_COLOR + ";");
            }
        });

        // Style the selected item
        if (itemIndex == mySelectedIndex) {
            itemLabel.setStyle("-fx-background-color: " + Config.SELECTION_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 3px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(218,165,32,0.6), 8, 0.5, 2, 2);");
        } else {
            itemLabel.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: " + Config.TEXT_COLOR + ";");
        }

        return itemLabel;
    }

    private void updateSelectedItemInfo() {
        if (mySelectedIndex >= 0 && mySelectedIndex < myCurrentInventory.size()) {
            Item selectedItem = myCurrentInventory.get(mySelectedIndex);
            mySelectedItemTitleLabel.setText(selectedItem.getName());
            myItemDescriptionLabel.setText(selectedItem.getDescription());
        } else {
            mySelectedItemTitleLabel.setText("No Item Selected");
            if (myCurrentInventory != null && myCurrentInventory.isEmpty()) {
                myItemDescriptionLabel.setText("Your pack is empty. Find some loot!");
            } else {
                myItemDescriptionLabel.setText("Select an item to see its description.");
            }
        }
    }

    public void updateSelection(int newIndex) {
        if (myCurrentInventory != null && !myCurrentInventory.isEmpty()) {
            mySelectedIndex = Math.max(0, Math.min(newIndex, myCurrentInventory.size() - 1));
            updateInventory(); // Refresh the whole display to update selection style
        }
    }

    private void useSelectedItem() {
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().useSelectedItem();
            updateInventory();
        }
    }

    private void closeInventory() {
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().closeInventory();
        }
    }

    public void closeScreen() {
        myInventoryStage.close();
    }

    private void setupEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) {
                updateSelection(mySelectedIndex - 1);
            } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                updateSelection(mySelectedIndex + 1);
            } else if (code == KeyCode.E || code == KeyCode.ENTER) {
                useSelectedItem();
            } else if (code == KeyCode.I || code == KeyCode.ESCAPE) {
                closeInventory();
            }
        });

        // Handler to close window via controller
        myInventoryStage.setOnCloseRequest(event -> {
            event.consume();
            closeInventory();
        });
    }
}