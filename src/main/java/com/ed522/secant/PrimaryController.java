package com.ed522.secant;

import com.ed522.secant.theory.Note;
import com.ed522.secant.theory.NoteCanvas;
import com.ed522.secant.theory.Pitch;
import com.ed522.secant.theory.Rhythm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class PrimaryController {

    // all values are exponential i.e. 1 means 2x, 0 is 1 and -1 is 1/2
    private static final double VERTICAL_SCALE_MAX = 2.0 / 3.0;
    private static final double HORIZONTAL_SCALE_MAX = 2.0 / 3.0;
    private static final double VERTICAL_SCALE_MIN = -2.0 / 3.0;
    private static final double HORIZONTAL_SCALE_MIN = -1.75;

    private static final double VSIZE = 16;
    private static final double HSIZE = 48;

    private static final Paint LIGHT = Paint.valueOf("#404040");
    private static final Paint DARK = Paint.valueOf("#2E2E2E");
    private static final Paint LINE_STROKE = Paint.valueOf("#000000");
    private static final Paint VERTICAL_LINE = Paint.valueOf("#707070");
    private static final Paint VERTICAL_LINE_ACCENT = Paint.valueOf("#A0A0A0");

    private static final boolean[] LINE_IS_LIGHT = new boolean[]
            {true, false, true, false, true, true, false, true, false, true, false, true};
    private static final double SCROLL_SCALE_FACTOR = 1.0 / 250.0;

    @FXML private AnchorPane canvas;
    @FXML private HBox chordBar;
    private final Scale canvasScale = new Scale(1, 1, 0, 0);

    private int verticalCount = 40;
    private int horizontalCount = 100;
    private int minOctave = 0;
    private int maxOctave = 7;
    private com.ed522.secant.theory.Scale scale ;

    private NoteCanvas noteCanvas;

    // Scales are logarithmic (ie. 2**scale = true scale factor)
    private double currentScaleX = 0;
    private double currentScaleY = 0;

    private Node makeNote() {
        Rectangle rect = new Rectangle(HSIZE,
                VSIZE);
        rect.setFill(Paint.valueOf("#4455CC"));
        return rect;
    }

    @FXML
    public void initialize() {
        this.canvas.setOnScroll(this::scrollBackground);
        this.canvas.setOnMouseClicked(e -> this.handleOnClick(e.getX(), e.getY()));
        this.canvas.getTransforms().add(canvasScale);
        Platform.runLater(this::renderBackground);
    }

    private void renderBackground() {

        this.verticalCount =
                this.scale.getChromaticNoteCount() * (this.maxOctave - this.minOctave);

        this.canvas.setPrefWidth(HSIZE * horizontalCount);
        this.canvas.setPrefHeight(VSIZE * verticalCount);

        this.canvas.setMaxWidth(HSIZE * horizontalCount);
        this.canvas.setMaxHeight(VSIZE * verticalCount);

        for (int i = 0; i < verticalCount; i++) {

            // Add horizontal note guides
            final Rectangle rectangle = new Rectangle(HSIZE * horizontalCount, VSIZE);
            rectangle.setStroke(LINE_STROKE);

            // alternates like a piano in a diatonic scale
            if (LINE_IS_LIGHT[i % LINE_IS_LIGHT.length]) {
                rectangle.setFill(LIGHT);
            } else {
                rectangle.setFill(DARK);
            }
            this.canvas.getChildren().add(rectangle);
            AnchorPane.setLeftAnchor(rectangle, 0.0);
            AnchorPane.setBottomAnchor(rectangle, (double) i * VSIZE);
        }

        for (int i = 1; i < horizontalCount; i++) {
            Line line = new Line(i * HSIZE, 0, i * HSIZE, VSIZE * verticalCount);
            if (i % 4 == 0) {
                line.setStroke(VERTICAL_LINE_ACCENT);
            } else {
                line.setStroke(VERTICAL_LINE);
            }
            this.canvas.getChildren().add(line);
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

    private void handleOnClick(double paneX, double paneY) {
        Node note = this.makeNote();
        this.canvas.getChildren().add(note);

        int xIndex = (int) paneY / (int) VSIZE;
        int yIndex = verticalCount - ((int) paneY / (int) VSIZE);
        Pitch pitch = this.scale.getChromatic(
                yIndex % this.scale.getChromaticNoteCount(),
                yIndex / this.scale.getChromaticNoteCount() + minOctave
        );
        Rhythm rhythm = new Rhythm(1, 4,
                xIndex % 4, xIndex / 4, 4);
        this.noteCanvas.addNote(new Note(rhythm, pitch));

        AnchorPane.setLeftAnchor(note, paneX - (paneX % HSIZE));
        AnchorPane.setTopAnchor(note, paneY - (paneY % VSIZE));
    }

}
