package com.ed522.secant;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

public class PrimaryController {

    private double verticalSize = 16;
    private double horizontalSize = 96;
    private int verticalCount = 40;
    private int horizontalCount = 100;

    private static final Paint LIGHT = Paint.valueOf("#404040");
    private static final Paint DARK = Paint.valueOf("#2E2E2E");
    private static final Paint LINE_STROKE = Paint.valueOf("#000000");
    private static final Paint VERTICAL_LINE = Paint.valueOf("#707070");
    private static final Paint VERTICAL_LINE_ACCENT = Paint.valueOf("#A0A0A0");

    private static final boolean[] LINE_IS_LIGHT = new boolean[]
            {true, false, true, false, true, true, false, true, false, true, false, true};

    @FXML private AnchorPane canvas;
    @FXML private HBox chordBar;

    @FXML
    public void initialize() {
        this.canvas.setOnScroll(this::scrollBackground);
        Platform.runLater(this::renderBackground);
        Platform.runLater(this::renderNotes);
    }

    private void renderBackground() {

        this.canvas.setPrefWidth(horizontalSize * horizontalCount);
        this.canvas.setPrefHeight(verticalSize * verticalCount);

        this.canvas.setMaxWidth(horizontalSize * horizontalCount);
        this.canvas.setMaxHeight(verticalSize * verticalCount);

        for (int i = 0; i < verticalCount; i++) {

            // Add horizontal note guides
            final Rectangle rectangle = new Rectangle(horizontalSize * horizontalCount, verticalSize);
            rectangle.setStroke(LINE_STROKE);

            // alternates like a piano in a diatonic scale
            if (LINE_IS_LIGHT[i % LINE_IS_LIGHT.length]) {
                rectangle.setFill(LIGHT);
            } else {
                rectangle.setFill(DARK);
            }
            this.canvas.getChildren().add(rectangle);
            AnchorPane.setLeftAnchor(rectangle, 0.0);
            AnchorPane.setBottomAnchor(rectangle, (double) i * verticalSize);
        }

        for (int i = 1; i < horizontalCount; i++) {
            Line line = new Line(i * horizontalSize, 0, i * horizontalSize, verticalSize * verticalCount);
            if (i % 4 == 0) {
                line.setStroke(VERTICAL_LINE_ACCENT);
            } else {
                line.setStroke(VERTICAL_LINE);
            }
            this.canvas.getChildren().add(line);
        }

    }

    private void scrollBackground(ScrollEvent event) {
        if (event.isControlDown() && event.getEventType()) {
            event.consume();
            if (event.isShiftDown()) {
                this.verticalSize += event.getDeltaY();
            } else {
                this.horizontalSize += event.getDeltaY();
            }
            Platform.runLater(this::renderBackground);
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
