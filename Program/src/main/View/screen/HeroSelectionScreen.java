package main.View.screen;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.View.GameUI;
import main.Model.util.HeroType;

import java.io.InputStream;

/**
 * Hero selection screen with responsive scaling and sprite-based hero selection.
 */
public class HeroSelectionScreen extends Screen {

    private static final double BASE_WIDTH = 800.0;
    private static final double BASE_HEIGHT = 600.0;
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 2.0;

    private static final int BASE_TITLE_FONT_SIZE = 45;
    private static final int BASE_LABEL_FONT_SIZE = 18;
    private static final int BASE_HERO_NAME_FONT_SIZE = 16;
    private static final int BASE_BUTTON_FONT_SIZE = 22;
    private static final int BASE_TEXT_FIELD_FONT_SIZE = 14;

    private static final int BASE_TEXT_FIELD_WIDTH = 400;
    private static final int BASE_TEXT_FIELD_HEIGHT = 30;
    private static final int BASE_BUTTON_WIDTH = 280;
    private static final int BASE_BUTTON_HEIGHT = 45;
    private static final int BASE_SPRITE_SIZE = 64;
    private static final int BASE_DESCRIPTION_WIDTH = 600;
    private static final int BASE_DESCRIPTION_HEIGHT = 40;

    private static final int BASE_CONTENT_SPACING = 20;
    private static final int BASE_TOP_PADDING = 30;
    private static final int BASE_HERO_SPACING = 100;
    private static final int BASE_TITLE_SPACING = 10;
    private static final int BASE_NAME_SECTION_SPACING = 10;
    private static final int BASE_HERO_SECTION_SPACING = 15;
    private static final int BASE_HERO_BOX_SPACING = 5;

    private static final double GLOW_LEVEL = 0.8;
    private static final int SHADOW_RADIUS = 15;
    private static final int SELECTED_SHADOW_RADIUS = 20;
    private static final int FLASH_DURATION_MS = 150;

    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);
    private static final Color HOVER_GLOW_COLOR = Color.GOLD;
    private static final Color SELECTION_GLOW_COLOR = Color.ORANGE;

    private static final String WARRIOR_SPRITE_PATH = "/sprites/heroes/warrior.png";
    private static final String PRIESTESS_SPRITE_PATH = "/sprites/heroes/priestess.png";
    private static final String THIEF_SPRITE_PATH = "/sprites/heroes/thief.png";
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";

    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    private static final String TEXT_FIELD_BASE_STYLE =
            "-fx-background-color: #2C2C2C; -fx-text-fill: #E0E0E0; -fx-border-color: #DAA520; -fx-border-width: 3px; -fx-prompt-text-fill: #888888;";
    private static final String BUTTON_BASE_STYLE =
            "-fx-text-fill: #E0E0E0; -fx-border-width: 4px; -fx-padding: 15px 40px; -fx-background-radius: 0; -fx-border-radius: 0;";
    private static final String VALID_BUTTON_STYLE =
            BUTTON_BASE_STYLE + "-fx-background-color: #2C2C2C; -fx-border-color: #DAA520; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";
    private static final String INVALID_BUTTON_STYLE =
            BUTTON_BASE_STYLE + "-fx-background-color: linear-gradient(to bottom, #1A1A1A, #0D0D0D); -fx-border-color: #333333; -fx-text-fill: #666666;";
    private static final String FLASH_BUTTON_STYLE =
            BUTTON_BASE_STYLE + "-fx-background-color: linear-gradient(to bottom, #2A2A2A, #000000); -fx-text-fill: #888888; -fx-border-color: #444444;";

    private HeroType selectedHeroType;
    private ImageView selectedHeroView;
    private Label descriptionLabel;
    private Button startGameBtn;
    private TextField nameBox;
    private Scene scene;
    private double currentScale = 1.0;

    public HeroSelectionScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        VBox root = createRootLayout();
        Stage currentStage = getStage();

        double width = currentStage.getScene() != null ?
                currentStage.getScene().getWidth() : BASE_WIDTH;
        double height = currentStage.getScene() != null ?
                currentStage.getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(root, width, height);

        setupBackground(root);
        setupComponents(root, theUI);
        setupScaling(root);
        setupResponsiveBindings();

        boolean wasFullScreen = currentStage.isFullScreen();

        currentStage.setScene(scene);
        currentStage.setTitle("Select Your Hero - Dungeon Dive");

        if (wasFullScreen && !currentStage.isFullScreen()) {
            currentStage.setFullScreen(true);
        }

        if (!currentStage.isShowing()) {
            currentStage.show();
        }
    }

    private VBox createRootLayout() {
        VBox root = new VBox(BASE_CONTENT_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(BASE_TOP_PADDING));
        return root;
    }

    private void setupBackground(VBox root) {
        try (InputStream bgStream = getResourceStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                root.setStyle(buildBackgroundStyle());
            } else {
                root.setStyle("-fx-background-color: #202020;");
                logError("Background image not found: " + BACKGROUND_PATH + ". Using fallback color.");
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #202020;");
            logError("Error loading background: " + e.getMessage());
        }
    }

    private String buildBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    private void setupScaling(VBox root) {
        var scaleBinding = Bindings.createDoubleBinding(() -> {
            double widthScale = scene.getWidth() / BASE_WIDTH;
            double heightScale = scene.getHeight() / BASE_HEIGHT;
            double scale = Math.min(widthScale, heightScale);
            return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
        }, scene.widthProperty(), scene.heightProperty());

        root.scaleXProperty().bind(scaleBinding);
        root.scaleYProperty().bind(scaleBinding);
    }

    private void setupResponsiveBindings() {
        if (nameBox != null) {
            nameBox.setMaxWidth(BASE_TEXT_FIELD_WIDTH);
        }
        if (descriptionLabel != null) {
            descriptionLabel.setMaxWidth(BASE_DESCRIPTION_WIDTH);
            descriptionLabel.setWrapText(true);
        }
    }

    private void setupComponents(VBox root, GameUI theUI) {
        FontBundle fonts = loadFonts();

        root.getChildren().addAll(
                createTitle(fonts.title),
                createNameSection(fonts.label, fonts.textField),
                createHeroSpriteSection(fonts.label),
                createDescriptionLabel(fonts.label),
                createStartButton(fonts.button, theUI)
        );

        validateForm();
    }

    private static class FontBundle {
        final Font title, label, button, textField;

        FontBundle(Font title, Font label, Font button, Font textField) {
            this.title = title;
            this.label = label;
            this.button = button;
            this.textField = textField;
        }
    }

    private FontBundle loadFonts() {
        return new FontBundle(
                loadFont(BASE_TITLE_FONT_SIZE, "Impact"),
                loadFont(BASE_LABEL_FONT_SIZE, "Arial"),
                loadFont(BASE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(BASE_TEXT_FIELD_FONT_SIZE, "Courier New")
        );
    }

    private Font loadFont(int baseSize, String fallbackFamily) {
        try (InputStream fontStream = getResourceStream(FONT_PATH)) {
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, baseSize);
                if (customFont != null) return customFont;
            }
        } catch (Exception e) {
            logError("Failed to load custom font " + FONT_PATH + ". Using fallback. Error: " + e.getMessage());
        }
        return Font.font(fallbackFamily, baseSize);
    }

    private VBox createTitle(Font titleFont) {
        VBox titleBox = new VBox(BASE_TITLE_SPACING);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(
                createStyledText("DUNGEON", titleFont),
                createStyledText("DIVE", titleFont)
        );
        return titleBox;
    }

    private Text createStyledText(String text, Font font) {
        Text textElement = new Text(text);
        textElement.setFont(font);
        textElement.setFill(Color.ORANGE);
        textElement.setStyle(SHADOW_STYLE);
        return textElement;
    }

    private VBox createNameSection(Font labelFont, Font textFieldFont) {
        VBox nameSection = new VBox(BASE_NAME_SECTION_SPACING);
        nameSection.setAlignment(Pos.CENTER);

        Label nameLabel = createStyledLabel("ENTER YOUR HERO'S NAME:", labelFont, TAN_COLOR);
        nameBox = createNameTextField(textFieldFont);
        setupNameFieldEventHandlers();

        nameSection.getChildren().addAll(nameLabel, nameBox);
        return nameSection;
    }

    private TextField createNameTextField(Font textFieldFont) {
        TextField textField = new TextField();
        textField.setPromptText("Hero Name");
        textField.setFont(textFieldFont);
        textField.setStyle(TEXT_FIELD_BASE_STYLE);
        textField.setPrefWidth(BASE_TEXT_FIELD_WIDTH);
        textField.setPrefHeight(BASE_TEXT_FIELD_HEIGHT);
        return textField;
    }

    private void setupNameFieldEventHandlers() {
        nameBox.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        nameBox.setOnAction(event -> {
            updateControllerWithName();
            validateForm();
            if (isFormValid()) {
                startGameBtn.fire();
            }
        });
        nameBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateControllerWithName();
                validateForm();
            }
        });
    }

    private boolean isFormValid() {
        return !nameBox.getText().trim().isEmpty() && selectedHeroType != null;
    }

    private void updateControllerWithName() {
        if (getController() != null) {
            getController().setHeroName(nameBox.getText());
        }
    }

    private VBox createHeroSpriteSection(Font labelFont) {
        VBox heroSection = new VBox(BASE_HERO_SECTION_SPACING);
        heroSection.setAlignment(Pos.CENTER);

        Label heroSelectionLabel = createStyledLabel("SELECT YOUR HERO:", labelFont, TAN_COLOR);
        HBox heroSpritesBox = createHeroSpritesBox(labelFont);

        heroSection.getChildren().addAll(heroSelectionLabel, heroSpritesBox);
        return heroSection;
    }

    private HBox createHeroSpritesBox(Font labelFont) {
        HBox heroSpritesBox = new HBox(BASE_HERO_SPACING);
        heroSpritesBox.setAlignment(Pos.CENTER);

        heroSpritesBox.getChildren().addAll(
                createHeroBox("WARRIOR", WARRIOR_SPRITE_PATH, HeroType.WARRIOR, labelFont),
                createHeroBox("PRIESTESS", PRIESTESS_SPRITE_PATH, HeroType.PRIESTESS, labelFont),
                createHeroBox("THIEF", THIEF_SPRITE_PATH, HeroType.THIEF, labelFont)
        );

        return heroSpritesBox;
    }

    private VBox createHeroBox(String heroName, String spritePath, HeroType heroType, Font labelFont) {
        VBox heroBox = new VBox(BASE_HERO_BOX_SPACING);
        heroBox.setAlignment(Pos.CENTER);

        ImageView heroSprite = createHeroSprite(spritePath, heroType);
        Font heroNameFont = loadFont(BASE_HERO_NAME_FONT_SIZE, labelFont.getFamily());
        Label nameLabel = createStyledLabel(heroName, heroNameFont, TAN_COLOR);

        heroBox.getChildren().addAll(heroSprite, nameLabel);
        return heroBox;
    }

    private ImageView createHeroSprite(String spritePath, HeroType heroType) {
        ImageView heroSprite = new ImageView();
        setupSpriteSize(heroSprite);
        loadSpriteImage(heroSprite, spritePath, heroType);
        setupSpriteEventHandlers(heroSprite, heroType);
        return heroSprite;
    }

    private void setupSpriteSize(ImageView sprite) {
        sprite.setPreserveRatio(true);
        sprite.setFitWidth(BASE_SPRITE_SIZE);
        sprite.setFitHeight(BASE_SPRITE_SIZE);
    }

    private void loadSpriteImage(ImageView sprite, String spritePath, HeroType heroType) {
        try (InputStream spriteStream = getResourceStream(spritePath)) {
            if (spriteStream != null) {
                sprite.setImage(new Image(spriteStream));
            } else {
                createFallbackSprite(sprite, heroType);
                logError("Sprite image not found: " + spritePath);
            }
        } catch (Exception e) {
            createFallbackSprite(sprite, heroType);
            logError("Error loading sprite: " + spritePath + " - " + e.getMessage());
        }
    }

    private void createFallbackSprite(ImageView sprite, HeroType heroType) {
        String color = getHeroFallbackColor(heroType);
        sprite.setImage(null);
        sprite.setStyle("-fx-background-color: " + color + "; -fx-border-color: #DAA520; -fx-border-width: 2px;");
        sprite.setFitWidth(BASE_SPRITE_SIZE);
        sprite.setFitHeight(BASE_SPRITE_SIZE);
    }

    private void setupSpriteEventHandlers(ImageView sprite, HeroType heroType) {
        sprite.setOnMouseEntered(e -> applySpriteHoverEffect(sprite));
        sprite.setOnMouseExited(e -> removeSpriteHoverEffect(sprite));
        sprite.setOnMouseClicked(e -> handleSpriteSelection(sprite, heroType));
    }

    private void applySpriteHoverEffect(ImageView sprite) {
        sprite.setScaleX(1.25);
        sprite.setScaleY(1.25);
        sprite.setEffect(createGlowEffect(HOVER_GLOW_COLOR, SHADOW_RADIUS));
    }

    private void removeSpriteHoverEffect(ImageView sprite) {
        if (sprite == selectedHeroView) {
            applySelectedState(sprite);
        } else {
            applyNormalState(sprite);
        }
    }

    private void applySelectedState(ImageView heroSprite) {
        heroSprite.setScaleX(1.0);
        heroSprite.setScaleY(1.0);
        heroSprite.setEffect(createGlowEffect(SELECTION_GLOW_COLOR, SELECTED_SHADOW_RADIUS));
    }

    private void applyNormalState(ImageView heroSprite) {
        heroSprite.setScaleX(1.0);
        heroSprite.setScaleY(1.0);
        heroSprite.setEffect(null);
    }

    private void resetPreviousSelection() {
        if (selectedHeroView != null) {
            applyNormalState(selectedHeroView);
        }
    }

    private void handleSpriteSelection(ImageView sprite, HeroType heroType) {
        if (selectedHeroView != sprite) {
            resetPreviousSelection();
            selectedHeroView = sprite;
            selectedHeroType = heroType;
        }
        applySelectedState(sprite);
        updateHeroDescription(heroType);
        validateForm();
    }

    private Glow createGlowEffect(Color glowColor, int radius) {
        Glow glow = new Glow(GLOW_LEVEL);
        DropShadow shadow = new DropShadow();
        shadow.setColor(glowColor);
        shadow.setRadius(radius);
        glow.setInput(shadow);
        return glow;
    }

    private void updateHeroDescription(HeroType heroType) {
        if (getController() != null && descriptionLabel != null) {
            descriptionLabel.setTextFill(TAN_COLOR);
            int heroTypeIndex = getHeroTypeIndex(heroType);
            if (heroTypeIndex != 0) {
                getController().heroDescription(heroTypeIndex, descriptionLabel);
            } else {
                descriptionLabel.setText("SELECT A HERO TO SEE THEIR DESCRIPTION.");
            }
        }
    }

    private int getHeroTypeIndex(HeroType heroType) {
        if (heroType == null) return 0;
        return switch (heroType) {
            case WARRIOR -> 1;
            case PRIESTESS -> 2;
            case THIEF -> 3;
        };
    }

    private String getHeroFallbackColor(HeroType heroType) {
        if (heroType == null) return "#CCCCCC";
        return switch (heroType) {
            case WARRIOR -> "#8B4513";
            case PRIESTESS -> "#DAA520";
            case THIEF -> "#696969";
        };
    }

    private Label createStyledLabel(String text, Font font, Color textColor) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(textColor);
        return label;
    }

    private Label createDescriptionLabel(Font labelFont) {
        descriptionLabel = createStyledLabel("SELECT A HERO TO SEE THEIR DESCRIPTION.", labelFont, TAN_COLOR);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setStyle("-fx-text-alignment: center;");
        descriptionLabel.setPrefHeight(BASE_DESCRIPTION_HEIGHT);
        descriptionLabel.setMaxWidth(BASE_DESCRIPTION_WIDTH);
        return descriptionLabel;
    }

    private Button createStartButton(Font buttonFont, GameUI theUI) {
        startGameBtn = new Button("START");
        startGameBtn.setFont(buttonFont);
        startGameBtn.setOnAction(event -> handleStartGameAction(theUI));
        startGameBtn.setPrefWidth(BASE_BUTTON_WIDTH);
        startGameBtn.setPrefHeight(BASE_BUTTON_HEIGHT);
        return startGameBtn;
    }

    private void handleStartGameAction(GameUI theUI) {
        String heroName = nameBox.getText().trim();

        if (heroName.isEmpty() || selectedHeroType == null) {
            flashButton();
            handleInvalidForm(heroName);
            return;
        }

        if (getController() != null) {
            updateControllerWithName();
            startGame(theUI);
        }
    }

    private void handleInvalidForm(String heroName) {
        if (heroName.isEmpty()) {
            nameBox.requestFocus();
        } else if (selectedHeroType == null && descriptionLabel != null) {
            descriptionLabel.setText("PLEASE SELECT A HERO TYPE.");
            descriptionLabel.setTextFill(Color.RED);
        }
    }

    private void startGame(GameUI theUI) {
        ToggleGroup mockGroup = createMockToggleGroup();
        getController().startGame(null, theUI, mockGroup);
    }

    private ToggleGroup createMockToggleGroup() {
        ToggleGroup mockGroup = new ToggleGroup();
        if (selectedHeroType != null) {
            RadioButton mockButton = new RadioButton();
            mockButton.setUserData(selectedHeroType);
            mockButton.setSelected(true);
            mockButton.setToggleGroup(mockGroup);
        }
        return mockGroup;
    }

    private void validateForm() {
        if (startGameBtn == null || nameBox == null) return;

        boolean isValid = isFormValid();
        updateButtonStyle(isValid);
        updateDescriptionIfNeeded(isValid);
    }

    private void updateDescriptionIfNeeded(boolean isValid) {
        if (descriptionLabel != null && descriptionLabel.getTextFill().equals(Color.RED)) {
            if (isValid || (nameBox.getText().trim().isEmpty() && selectedHeroType != null)) {
                if (selectedHeroType != null) {
                    updateHeroDescription(selectedHeroType);
                } else {
                    descriptionLabel.setText("SELECT A HERO TO SEE THEIR DESCRIPTION.");
                    descriptionLabel.setTextFill(TAN_COLOR);
                }
            }
        }
    }

    private void updateButtonStyle(boolean isValid) {
        if (isValid) {
            applyValidButtonStyle();
        } else {
            applyInvalidButtonStyle();
        }
    }

    private void applyValidButtonStyle() {
        startGameBtn.setStyle(VALID_BUTTON_STYLE);
        startGameBtn.setOnMouseEntered(e ->
                startGameBtn.setStyle(VALID_BUTTON_STYLE + "-fx-background-color: #3C3C3C;"));
        startGameBtn.setOnMouseExited(e ->
                startGameBtn.setStyle(VALID_BUTTON_STYLE));
    }

    private void applyInvalidButtonStyle() {
        startGameBtn.setStyle(INVALID_BUTTON_STYLE);
        startGameBtn.setOnMouseEntered(e -> {});
        startGameBtn.setOnMouseExited(e -> {});
    }

    private void flashButton() {
        startGameBtn.setStyle(FLASH_BUTTON_STYLE);
        Timeline timeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.millis(FLASH_DURATION_MS), e -> validateForm())
        );
        timeline.play();
    }

    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    private void logError(String message) {
        System.err.println(message);
    }
}