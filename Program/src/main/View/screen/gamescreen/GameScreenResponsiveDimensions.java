package main.View.screen.gamescreen;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;

class GameScreenResponsiveDimensions {
    private final Scene scene;
    private final NumberBinding panelWidthBinding;
    private final NumberBinding panelPaddingBinding;
    private final NumberBinding buttonHeightBinding;
    private final NumberBinding minimapSizeBinding;
    private final NumberBinding statsFontSizeBinding;
    private final NumberBinding titleFontSizeBinding;
    private final NumberBinding messageFontSizeBinding;
    private final NumberBinding buttonFontSizeBinding;
    private final NumberBinding statsIconSizeBinding;
    private final NumberBinding portraitSizeBinding;

    // Configuration constants
    private static final double PANEL_PREF_WIDTH_RATIO = 0.14;
    private static final double PANEL_MIN_WIDTH_ABSOLUTE = 160.0;
    private static final double PANEL_MAX_WIDTH_ABSOLUTE = 220.0;
    private static final double PANEL_PADDING_RATIO = 0.008;
    private static final double BUTTON_HEIGHT_RATIO = 0.045;
    private static final double BUTTON_MIN_HEIGHT = 30.0;
    private static final double BUTTON_MAX_HEIGHT = 45.0;
    private static final double MINIMAP_SIZE_RATIO = 0.08;
    private static final double MINIMAP_MIN_SIZE = 70.0;
    private static final double MINIMAP_MAX_SIZE = 120.0;
    private static final double PORTRAIT_SIZE_RATIO = 0.1;
    private static final double PORTRAIT_MIN_SIZE = 80.0;
    private static final double PORTRAIT_MAX_SIZE = 150.0;
    private static final double STATS_FONT_SIZE_RATIO = 0.012;
    private static final double STATS_FONT_MIN_SIZE = 10.0;
    private static final double STATS_FONT_MAX_SIZE = 20.0;
    private static final double STATS_ICON_SIZE_RATIO = 0.009;
    private static final double STATS_ICON_MIN_SIZE = 12.0;
    private static final double STATS_ICON_MAX_SIZE = 20.0;
    private static final double TITLE_FONT_SIZE_RATIO = 0.020;
    private static final double MESSAGE_FONT_SIZE_RATIO = 0.007;
    private static final double BUTTON_FONT_SIZE_RATIO = 0.018;
    private static final double BUTTON_FONT_MIN_SIZE = 8.0;
    private static final double BUTTON_FONT_MAX_SIZE = 16.0;

    public GameScreenResponsiveDimensions(Scene scene) {
        this.scene = scene;

        panelWidthBinding = Bindings.createDoubleBinding(() -> {
            double sceneWidth = scene.getWidth();
            double calculated = sceneWidth * PANEL_PREF_WIDTH_RATIO;
            return Math.max(PANEL_MIN_WIDTH_ABSOLUTE, Math.min(PANEL_MAX_WIDTH_ABSOLUTE, calculated));
        }, scene.widthProperty());

        panelPaddingBinding = Bindings.createDoubleBinding(() -> {
            double basePadding = scene.getWidth() * PANEL_PADDING_RATIO;
            return Math.max(5.0, Math.min(15.0, basePadding));
        }, scene.widthProperty());

        buttonHeightBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * BUTTON_HEIGHT_RATIO;
            return Math.max(BUTTON_MIN_HEIGHT, Math.min(BUTTON_MAX_HEIGHT, calculated));
        }, scene.heightProperty());

        minimapSizeBinding = Bindings.createDoubleBinding(() -> {
            double smallerDimension = Math.min(scene.getWidth(), scene.getHeight());
            double calculated = smallerDimension * MINIMAP_SIZE_RATIO;
            return Math.max(MINIMAP_MIN_SIZE, Math.min(MINIMAP_MAX_SIZE, calculated));
        }, scene.widthProperty(), scene.heightProperty());

        portraitSizeBinding = Bindings.createDoubleBinding(() -> {
            double smallerDimension = Math.min(scene.getWidth(), scene.getHeight());
            double calculated = smallerDimension * PORTRAIT_SIZE_RATIO;
            return Math.max(PORTRAIT_MIN_SIZE, Math.min(PORTRAIT_MAX_SIZE, calculated));
        }, scene.widthProperty(), scene.heightProperty());

        statsFontSizeBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * STATS_FONT_SIZE_RATIO;
            return Math.max(STATS_FONT_MIN_SIZE, Math.min(STATS_FONT_MAX_SIZE, calculated));
        }, scene.heightProperty());

        statsIconSizeBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * STATS_ICON_SIZE_RATIO;
            return Math.max(STATS_ICON_MIN_SIZE, Math.min(STATS_ICON_MAX_SIZE, calculated));
        }, scene.heightProperty());

        titleFontSizeBinding = Bindings.createDoubleBinding(() ->
                Math.max(20.0, scene.getHeight() * TITLE_FONT_SIZE_RATIO), scene.heightProperty());

        messageFontSizeBinding = Bindings.createDoubleBinding(() ->
                Math.max(9.0, scene.getHeight() * MESSAGE_FONT_SIZE_RATIO), scene.heightProperty());

        buttonFontSizeBinding = Bindings.createDoubleBinding(() -> {
            double sceneHeight = scene.getHeight();
            double sceneWidth = scene.getWidth();
            double heightBasedSize = sceneHeight * BUTTON_FONT_SIZE_RATIO;
            double widthBasedSize = sceneWidth * (BUTTON_FONT_SIZE_RATIO * 0.5);
            double calculated = Math.min(heightBasedSize, widthBasedSize);
            return Math.max(BUTTON_FONT_MIN_SIZE, Math.min(BUTTON_FONT_MAX_SIZE, calculated));
        }, scene.heightProperty(), scene.widthProperty());
    }

    // Getters
    public Scene getScene() { return scene; }
    public NumberBinding getPanelWidthBinding() { return panelWidthBinding; }
    public NumberBinding getPanelPaddingBinding() { return panelPaddingBinding; }
    public NumberBinding getButtonHeightBinding() { return buttonHeightBinding; }
    public NumberBinding getMinimapSizeBinding() { return minimapSizeBinding; }
    public NumberBinding getPortraitSizeBinding() { return portraitSizeBinding; }
    public NumberBinding getStatsFontSizeBinding() { return statsFontSizeBinding; }
    public NumberBinding getStatsIconSizeBinding() { return statsIconSizeBinding; }
    public NumberBinding getTitleFontSizeBinding() { return titleFontSizeBinding; }
    public NumberBinding getMessageFontSizeBinding() { return messageFontSizeBinding; }
    public NumberBinding getButtonFontSizeBinding() { return buttonFontSizeBinding; }
}