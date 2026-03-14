package com.ed522.secant;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

public class PrimaryController {

    private int verticalScroll = 0;
    private int verticalSize = 16;
    private int horizontalScroll = 0;
    private int horizontalSize = 96;

    private static final Background LIGHT = Background.fill(Paint.valueOf("#EFEFEF"));
    private static final Background DARK = Background.fill(Paint.valueOf("#BFBFBF"));
    private static final Border BORDER = Border.stroke(Paint.valueOf("#000000"));

    @FXML
    private GridPane canvas;

    @FXML
    public void initialize() {
        Platform.runLater(this::updateGrid);
    }

    @FXML
    public void scrollPane(ScrollEvent event) {
        this.horizontalScroll += (int) event.getDeltaX();
        this.verticalScroll += (int) event.getDeltaY();
        this.horizontalScroll %= horizontalSize;
        this.verticalScroll %= verticalSize;
        this.updateGrid();

        System.out.println("scrolls");
    }

    private void updateGrid() {
        this.canvas.getColumnConstraints().clear();
        this.canvas.getRowConstraints().clear();

        double width = this.canvas.getWidth();
        double height = this.canvas.getHeight();

        int verticalCount = (int) Math.floor((height + verticalScroll) / verticalSize);
        int horizontalCount = (int) Math.floor((width + horizontalScroll) / horizontalSize);

        ColumnConstraints column = new ColumnConstraints(horizontalSize);
        for (int i = 0; i < horizontalCount; i++) {
            this.canvas.getColumnConstraints().add(column);
        }
        this.canvas.getColumnConstraints().add(new ColumnConstraints(width % horizontalSize));

        RowConstraints row = new RowConstraints(verticalSize);
        for (int i = 0; i < verticalCount; i++) {
            this.canvas.getRowConstraints().add(new RowConstraints(verticalSize));
        }
        this.canvas.getRowConstraints().add(row);

        for (int i = 0; i < horizontalCount + 1; i++) {
            for (int j = 0; j < verticalCount + 1; j++) {
                Pane pane = new Pane();
                if (j % 2 == 0) {
                    pane.setBackground(LIGHT);
                } else {
                    pane.setBackground(DARK);
                }
                pane.setBorder(BORDER);
                pane.setMaxSize(horizontalSize, verticalSize);
                this.canvas.add(pane, i, j);
            }
        }

    }

}
