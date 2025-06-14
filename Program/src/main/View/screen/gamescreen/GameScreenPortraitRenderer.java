package main.View.screen.gamescreen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;

class GameScreenPortraitRenderer {
    private final Controller controller;
    private Canvas portraitCanvas;
    private GraphicsContext portraitGraphics;

    public GameScreenPortraitRenderer(Controller controller) {
        this.controller = controller;
    }

    public void initialize(Canvas portraitCanvas) {
        this.portraitCanvas = portraitCanvas;
        this.portraitGraphics = portraitCanvas.getGraphicsContext2D();

        portraitCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());
        portraitCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());
    }

    public void updatePortraitCanvas() {
        if (portraitGraphics == null) return;

        Hero player = controller.getPlayer();
        if (player == null) return;

        double w = portraitCanvas.getWidth();
        double h = portraitCanvas.getHeight();

        portraitGraphics.setFill(Color.rgb(40, 40, 45));
        portraitGraphics.fillRect(0, 0, w, h);

        double size = Math.min(w, h) * 0.8;
        double x = (w - size) / 2;
        double y = (h - size) / 2;

        Image spriteSheet = player.getSpriteSheet();
        if (spriteSheet != null) {
            portraitGraphics.drawImage(spriteSheet,
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    x, y, size, size);
        }
    }

    private void updatePortraitBackground() {
        if (portraitGraphics != null) {
            portraitGraphics.setFill(Color.rgb(40, 40, 45));
            portraitGraphics.fillRect(0, 0, portraitCanvas.getWidth(), portraitCanvas.getHeight());
        }
    }
}
