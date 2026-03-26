package com.ed522.secant;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import org.jetbrains.annotations.NotNull;

public class PrimaryController {

    private static final double VERTICAL_SCALE_MAX = 2.0 / 3.0;
    private static final double HORIZONTAL_SCALE_MAX = 2.0 / 3.0;
    private static final double VERTICAL_SCALE_MIN = -2.0 / 3.0;
    private static final double HORIZONTAL_SCALE_MIN = -1.75;

    private static final double INITIAL_VERTICAL_SIZE = 16;
    private static final double INITIAL_HORIZONTAL_SIZE = 48;
    private int verticalCount = 40;
    private int horizontalCount = 100;

    private static final Paint LIGHT = Paint.valueOf("#404040");
    private static final Paint DARK = Paint.valueOf("#2E2E2E");
    private static final Paint LINE_STROKE = Paint.valueOf("#000000");
    private static final Paint VERTICAL_LINE = Paint.valueOf("#707070");
    private static final Paint VERTICAL_LINE_ACCENT = Paint.valueOf("#A0A0A0");

    private static final boolean[] LINE_IS_LIGHT = new boolean[]
            {true, false, true, false, true, true, false, true, false, true, false, true};
    private static final double SCROLL_SCALE_FACTOR = 1.0 / 250.0;

    @FXML private AnchorPane canvasPane;
    @FXML private HBox chordBar;

    private NoteCanvas noteCanvas;

    // Scales are logarithmic (ie. 2**scale = true scale factor)
    private double currentScaleX = 0;
    private double currentScaleY = 0;

    private final Scale canvasScale = new Scale(1, 1, 0, 0);
    private double initialWidth;
    private double initialHeight;

    @FXML
    public void initialize() {
        this.initialWidth = canvasPane.getWidth();
        this.initialHeight = canvasPane.getHeight();
        this.canvasPane.setOnScroll(this::scrollBackground);
        this.canvasPane.getTransforms().add(canvasScale);
        Platform.runLater(this::renderBackground);
        Platform.runLater(this::renderNotes);
    }

    private void renderBackground() {

        this.canvasPane.setPrefWidth(INITIAL_HORIZONTAL_SIZE * horizontalCount);
        this.canvasPane.setPrefHeight(INITIAL_VERTICAL_SIZE * verticalCount);

        this.canvasPane.setMaxWidth(INITIAL_HORIZONTAL_SIZE * horizontalCount);
        this.canvasPane.setMaxHeight(INITIAL_VERTICAL_SIZE * verticalCount);

        for (int i = 0; i < verticalCount; i++) {

            // Add horizontal note guides
            final Rectangle rectangle = new Rectangle(INITIAL_HORIZONTAL_SIZE * horizontalCount, INITIAL_VERTICAL_SIZE);
            rectangle.setStroke(LINE_STROKE);

            // alternates like a piano in a diatonic scale
            if (LINE_IS_LIGHT[i % LINE_IS_LIGHT.length]) {
                rectangle.setFill(LIGHT);
            } else {
                rectangle.setFill(DARK);
            }
            this.canvasPane.getChildren().add(rectangle);
            AnchorPane.setLeftAnchor(rectangle, 0.0);
            AnchorPane.setBottomAnchor(rectangle, (double) i * INITIAL_VERTICAL_SIZE);
        }

        for (int i = 1; i < horizontalCount; i++) {
            Line line = new Line(i * INITIAL_HORIZONTAL_SIZE, 0, i * INITIAL_HORIZONTAL_SIZE, INITIAL_VERTICAL_SIZE * verticalCount);
            if (i % 4 == 0) {
                line.setStroke(VERTICAL_LINE_ACCENT);
            } else {
                line.setStroke(VERTICAL_LINE);
            }
            this.canvasPane.getChildren().add(line);
        }

    }

    private void scrollBackground(ScrollEvent event) {
        if (event.isControlDown() && event.getEventType() == ScrollEvent.SCROLL) {
            event.consume();
            if (event.isShiftDown()) {
                currentScaleY += event.getDeltaX() * SCROLL_SCALE_FACTOR;
                this.currentScaleY = Math.max(currentScaleY, VERTICAL_SCALE_MIN);
                this.currentScaleY = Math.min(currentScaleY, VERTICAL_SCALE_MAX);
                this.canvasScale.setY(Math.pow(2, currentScaleY));
            } else {
                currentScaleX += event.getDeltaY() * SCROLL_SCALE_FACTOR;
                this.currentScaleX = Math.max(currentScaleX, HORIZONTAL_SCALE_MIN);
                this.currentScaleX = Math.min(currentScaleX, HORIZONTAL_SCALE_MAX);
                this.canvasScale.setX(Math.pow(2, currentScaleX));
            }
        }
    }

    private void renderNotes() {

    }

    private void handleOnClick(@NotNull Pane pane) {
        if (!pane.getChildren().isEmpty()) {
            pane.getChildren().clear();
        } else {
            Button b = new Button("something");
            b.setOnMouseClicked(e -> pane.getChildren().clear());
            pane.getChildren().add(b);
        }
    }

}
