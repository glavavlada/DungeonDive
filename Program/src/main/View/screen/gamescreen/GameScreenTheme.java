package main.View.screen.gamescreen;

import javafx.scene.paint.Color;

class GameScreenTheme {
    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);
    private static final Color ORANGE_COLOR = Color.ORANGE;
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";
    private static final String PANEL_BORDER_COLOR = "#DAA520";

    public Color getTanColor() { return TAN_COLOR; }
    public Color getOrangeColor() { return ORANGE_COLOR; }
    public String getShadowStyle() { return SHADOW_STYLE; }
    public String getFontPath() { return FONT_PATH; }
    public String getBackgroundPath() { return BACKGROUND_PATH; }
    public String getPanelBorderColor() { return PANEL_BORDER_COLOR; }

    public String getBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    public String getFallbackBackgroundStyle() {
        return "-fx-background-color: #202020;";
    }

    public String getMainGameAreaStyle() {
        return "-fx-background-color: rgba(0,0,0,0.3);";
    }

    public String getStatsBoxStyle() {
        return "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 2px; -fx-padding: 8; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px;" +
                SHADOW_STYLE;
    }

    public String getPanelStyle() {
        return "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;" +
                SHADOW_STYLE;
    }

    public String getCenterAreaStyle() {
        return "-fx-background-color: rgba(0,0,0,0.2);";
    }

    public String getButtonBaseStyle() {
        return "-fx-background-color: #444444;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    }

    public String getButtonHoverStyle() {
        return "-fx-background-color: #666666;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    }

    public String getMessageContainerStyle() {
        return "-fx-background-color: rgba(42, 42, 42, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 3 0 0 0;" +
                SHADOW_STYLE;
    }

    public String getMessageScrollPaneStyle() {
        return "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;";
    }
}