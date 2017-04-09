package sample;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

import static sample.Controller.*;

public class Main extends Application{
    static int glb = 4; // row x column
    static int slb = 1; // number of tile for each spawning
    static int plb = 5; // paddling between title
    static int crlb = 65; // tile size = crlb - plb
    boolean stopall;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller._main = this;

        //region Preparing Basic Interface
        group = new Group();
        group.prefHeight(crlb * glb + plb);
        group.prefWidth(crlb * glb + plb);

        // Prepare a dark background
        Rectangle background = new Rectangle(crlb * glb + plb, crlb * glb + plb, Paint.valueOf("#D9BF9C"));
        background.setArcHeight(7);
        background.setArcWidth(7);
        group.getChildren().add(background);

        // Prepare tile placeholders
        for (int i = 0; i < glb ; i++) {
            for (int j = 0; j < glb; j++) {
                Rectangle rect = new Rectangle(crlb-plb,crlb-plb,Paint.valueOf("#F2E7D8"));
                group.getChildren().add(rect);
                rect.setX(crlb * i + plb);
                rect.setY(crlb * j + plb);
                rect.setArcWidth(2*plb);
                rect.setArcHeight(2*plb);
            }
        }

        // PREPARE SOME RANDOM TILES
        // make column and row index become properties of the instance
        // so I can get them out and sort them whenever required
        while (iSet.size() < 3) {
            int i = new Random().nextInt(glb*glb);
            if (iSet.add(i)) {
                Tile tile = new Tile(i ,new Random().nextDouble() > 0.9 ? 4 : 2);
                group.getChildren().add(tile);
                tile.setLayoutCordinate();
                XTile.add(tile);
            }
        }

        // Attach all to the parent layout
        scene = new Scene(new BorderPane(group), crlb*glb+70, crlb*glb+70);
        primaryStage.setScene(scene);
        primaryStage.setTitle("2048 Bach Mai version");
        primaryStage.setResizable(false);
        primaryStage.show();
        //endregion

        // EVENT HANDLING
        scene.addEventHandler(KeyEvent.KEY_PRESSED, Controller::move_tile);

    }

    void gameover_annouce (boolean gameIsOver) {
        if (gameIsOver) {
            //region Game over handling
            // Cancel all d-key handler
            stopall = true;

            // Prepare the Vbox contain Message and New game button
            Label descrip = new Label("GAME OVER\nAnh Hiếu, anh NGU lắm!\nLàm game mà ko có Animation\nthì vứt cho dog ăn!\nN G U !!");
            descrip.setTextAlignment(TextAlignment.CENTER);

            Button newgame = new Button("Try again!");
            newgame.setPrefSize(150, 40);
            newgame.setTextFill(Color.WHITE);
            newgame.setFont(Font.font(14));
            newgame.setTextAlignment(TextAlignment.CENTER);
            newgame.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, new CornerRadii(5), null)));

            VBox vbox = new VBox(20);
            vbox.getChildren().addAll(descrip,newgame);
            vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            vbox.setOpacity(0);
            vbox.setAlignment(Pos.CENTER);
            group.getChildren().add(vbox);
            vbox.setPrefSize(crlb * glb + plb, crlb * glb + plb);

            // Animation for the Vbox to show
            FadeTransition ft = new FadeTransition(Duration.millis(100),vbox);
            ft.setDelay(Duration.millis(1000));
            ft.setFromValue(0);
            ft.setToValue(0.75);
            ft.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(100),vbox);
            st.setDelay(Duration.millis(1000));
            st.setFromY(0.8);
            st.setFromX(0.8);
            st.setToY(1);
            st.setToX(1);
            st.play();

            // Set default keystroke ENTER for the Try again button
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    newgame.fire();
                }
            });


            // Assign Action for the Try Again button
            newgame.setOnAction(event -> {
                FadeTransition ft2 = new FadeTransition(Duration.millis(500), vbox);
                ft2.setToValue(0);
                ft2.setOnFinished(event1 -> {
                    group.getChildren().remove(vbox);
                    Controller.clear_all();
                    stopall = false;
                });
                ft2.play();

            });
            //endregion
        }
    }


}
