package us.cognice.knight;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class Controller {

    private int columns = 8;
    private int rows = 8;
    private ImageView knight;

    @FXML
    private GridPane grid ;

    public void initialize() throws IOException {
        URL url = getClass().getResource("/knight.png");
        knight = new ImageView(new Image(url.openStream()));
        knight.setFitHeight(60);
        knight.setFitWidth(60);
        for (int i = 0 ; i < columns ; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.NEVER);
            cc.setMinWidth(60);
            cc.setPrefWidth(60);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0 ; i < rows ; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.NEVER);
            rc.setMinHeight(60);
            rc.setPrefHeight(60);
            grid.getRowConstraints().add(rc);
        }

        for (int i = 0 ; i < columns ; i++) {
            for (int j = 0; j < rows; j++) {
                addPane(i, j);
            }
        }
    }

    private void addPane(int ci, int ri) {
        Field field = new Field();
        resetColor(field, ci, ri);
        field.setOnMouseClicked(e -> {
            //clearing all fields
            for (Node node : grid.getChildren()) {
                if (node instanceof Field) {
                    Field f = (Field) node;
                    f.getChildren().clear();
                    f.visited = false;
                    resetColor(f, GridPane.getColumnIndex(f), GridPane.getRowIndex(f));
                }
            }
            if (e.getTarget() instanceof Field) {
                Field root = (Field) e.getTarget();
                root.getChildren().add(knight);
                root.move = 0;
                System.out.printf("Mouse clicked cell [%d, %d]%n", ci, ri);
                setKnightMoves(root);
            }
        });
        grid.add(field, ci, ri);
    }

    private void resetColor(Field field, int c, int r) {
        boolean white = c % 2 == 0 ? r % 2 == 0 : r % 2 == 1;
        field.setStyle("-fx-background-color: " + (white ? "white" : "grey"));
    }

    private void setKnightMoves(Field root) {
        Queue<Field> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Field current = q.remove();
            if (!current.visited) {
                current.visited = true;
                Integer c = GridPane.getColumnIndex(current);
                Integer r = GridPane.getRowIndex(current);
                boolean white = c % 2 == 0 ? r % 2 == 0 : r % 2 == 1;
                current.setStyle("-fx-background-color: " + (white ? "#37DE79" : "#207A44"));
                if (current.move > 0) {
                    Color color = Color.web("#000000", 1.0 / current.move);
                    Text text = new Text(String.valueOf(current.move));
                    text.setFont(Font.font("Arial", 20));
                    text.setFill(color);
                    current.getChildren().add(text);
                    StackPane.setAlignment(text, Pos.CENTER);
                }
                for (Node node : grid.getChildren()) {
                    if (node instanceof Field) {
                        Field next = (Field) node;
                        Integer col = GridPane.getColumnIndex(next);
                        Integer row = GridPane.getRowIndex(next);
                        if (col == c - 2 && row == r - 1 ||
                                col == c - 1 && row == r - 2 ||
                                col == c + 2 && row == r - 1 ||
                                col == c + 1 && row == r - 2 ||
                                col == c - 1 && row == r + 2 ||
                                col == c - 2 && row == r + 1 ||
                                col == c + 1 && row == r + 2 ||
                                col == c + 2 && row == r + 1) {
                            next.move = current.move + 1;
                            q.add(next);
                        }
                    }
                }
            }
        }
    }

}
