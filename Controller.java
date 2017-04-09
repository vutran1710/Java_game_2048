package sample;

import com.sun.istack.internal.NotNull;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

import static sample.Main.glb;
import static sample.Main.slb;
import static sample.Main.crlb;

public class Controller {
    // Static attributes and collections
    static HashSet<Integer> iSet = new HashSet<>(); // this set will check index number (from 1 to glb) to make sure no duplicate tiles are created
    static ArrayList<Tile> XTile = new ArrayList<>(); // this dynamic array shall hold reference to tiles being shown
    static Group group; // Reference to Group of nodes in main
    static boolean _moved; // Boolean value to detect any movement of tile within keystroke handling process
    static Main _main; // Ref,. to Main.
    private static boolean anim_bar = false; // Prevent keystroke handling while animation of previous process has not finished


    // Method to analyse D-key pressed and execute proper actions
    static void move_tile (KeyEvent ke) {
        // If the game is already over, this method is canceled!
        if (_main.stopall) return;

        // If animation of tile is playing and not finished yet, this method is also canceled
        if (anim_bar) return;

        // If conditions are satisfied, execute the below code to move tiles properly
        // Reset the tile-movement detection
        _moved = false;
        // Reset merge condition of tiles
        for (Tile tile : XTile) {
            tile.setMergable(true);
        }

        // Get the key code
        KeyCode kc = ke.getCode();

        // Handle different directions
        switch (kc) {
            //region Prepare Data per Swipe direction
            case UP:
                for (int i = 0; i < glb; i++) { // Browse columns
                    int finalI = i;
                    ArrayList<Tile> focus = XTile.stream().filter(tile -> tile.getI_() == finalI)
                            .sorted(((o1, o2) -> o1.getJ_() - o2.getJ_()))
                            .collect(Collectors.toCollection(ArrayList<Tile>::new));
                    if (focus.size() == 0) continue;
                    Logic_move.move_to_bound(kc, focus.get(0));
                    for (int j = 1; j < focus.size(); j++) {
                        Logic_move.move_to_nearest(kc, focus.get(j), focus.get(j-1));
                    }
                }
                break;
            
            case DOWN:
                for (int i = 0; i < glb; i++) { // Browse columns
                    int finalI = i;
                    ArrayList<Tile> focus = XTile.stream().filter(tile -> tile.getI_() == finalI)
                            .sorted(((o1, o2) -> o2.getJ_() - o1.getJ_()))
                            .collect(Collectors.toCollection(ArrayList<Tile>::new));
                    if (focus.size() == 0) continue;
                    Logic_move.move_to_bound(kc, focus.get(0));
                    for (int j = 1; j < focus.size(); j++) {
                        Logic_move.move_to_nearest(kc, focus.get(j), focus.get(j-1));
                    }
                }
                break;
            
            case RIGHT:
                for (int j = 0; j < glb; j++) { // Browse rows
                    int finalJ = j;
                    ArrayList<Tile> focus = XTile.stream().filter(tile -> tile.getJ_() == finalJ)
                            .sorted(((o1, o2) -> o2.getI_() - o1.getI_()))
                            .collect(Collectors.toCollection(ArrayList<Tile>::new));
                    if (focus.size() == 0) continue;
                    Logic_move.move_to_bound(kc, focus.get(0));
                    for (int i = 1; i < focus.size(); i++) {
                        Logic_move.move_to_nearest(kc, focus.get(i), focus.get(i-1));
                    }
                }
                break;
            
            case LEFT:
                for (int j = 0; j < glb; j++) { // Browse columns
                    int finalJ = j;
                    ArrayList<Tile> focus = XTile.stream().filter(tile -> tile.getJ_() == finalJ)
                            .sorted(((o1, o2) -> o1.getI_() - o2.getI_()))
                            .collect(Collectors.toCollection(ArrayList<Tile>::new));
                    if (focus.size() == 0) continue;
                    Logic_move.move_to_bound(kc, focus.get(0));
                    for (int i = 1; i < focus.size(); i++) {
                        Logic_move.move_to_nearest(kc, focus.get(i), focus.get(i-1));
                    }
                }
                break;
            //endregion

            //region Supplemental controls for Delete & Spawning Tiles manually
            case Q:
                if (XTile.size() > 0) {
                    int i = XTile.size();
                    Tile t = XTile.get(new Random().nextInt(i));
                    group.getChildren().remove(t);
                    XTile.remove(t);
                } else {
                    System.out.println("NO MORE TILE TO DELETE");
                }
                break;

            case E:
                spawning();
            //endregion
        }

        // Check game_over and spawning condition
        // if tile movement did take place, spawning is allowed
        if (_moved) {
            spawning();
        } else {
            // If no tiles were moved while there are still empty tile-placeholders...
            // then something did happen, check the game-over condition
            _main.gameover_annouce(Controller.isOver());
        }
    }

    static void clear_all () {
        group.getChildren().removeAll(XTile);
        XTile.clear();
        iSet.clear();

        while (iSet.size() < 3) {
            int i = new Random().nextInt(glb*glb);
            if (iSet.add(i)) {
                Tile tile = new Tile(i ,new Random().nextDouble() > 0.9 ? 4 : 2);
                group.getChildren().add(tile);
                tile.setLayoutCordinate();
                XTile.add(tile);
            }
        }
    }

    private static void spawning () {
        anim_bar = true;
        int count = 0;

        iSet.clear();
        iSet.addAll(XTile.stream().map(Tile::getInc_index).collect(Collectors.toList()));
        int spawnz = Math.min(glb*glb-XTile.size(), slb);

        while (count < spawnz) {
            int i = new Random().nextInt(glb*glb);
            if (!iSet.contains(i)) {
                iSet.add(i);
                count++;
                Tile tile = new Tile(i, 2);
                tile.setOpacity(0);
                group.getChildren().add(tile);
                tile.setLayoutCordinate();
                XTile.add(tile);

                //region Animation in & out for new tile
                ScaleTransition st = new ScaleTransition(Duration.millis(150), tile);
                st.setFromY(0);
                st.setFromX(0);
                st.setToX(1);
                st.setToY(1);
                st.setDelay(Duration.millis(150));

                FadeTransition ft = new FadeTransition(Duration.millis(150), tile);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(170));

                st.play();
                ft.play();

                st.setOnFinished(event -> anim_bar = false);
            }
                //endregion
            }

    }

    private static boolean isOver () {
        if (XTile.size() < glb*glb) {
            return false;
        } else {
            boolean check_v = false;
            boolean check_h = false;
            ArrayList<Tile> xtc = XTile.stream()
                    .sorted((o1, o2) -> o1.getInc_index() - o2.getInc_index())
                    .collect(Collectors.toCollection(ArrayList::new));

            int i = 0;
            while (i < glb & !check_h & !check_v) {
                for (int j = 0; j < (glb-1); j++) {
                    check_v = xtc.get(j+i*glb).getValue() == xtc.get(i*glb+j+1).getValue();
                    check_h = xtc.get(i+j*glb).getValue() == xtc.get(i+(j+1)*glb).getValue();
                    if (check_h|check_v) break;
                }
                i++;
            }
            return !check_h && !check_v;
        }
    }
}


class Logic_move {

    private static void reset_cord(Tile tile) {
        tile.setLayoutX(tile.getLayoutX() + tile.getTranslateX());
        tile.setLayoutY(tile.getLayoutY() + tile.getTranslateY());
        tile.setTranslateY(0);
        tile.setTranslateX(0);
    }

    static void move_to_bound (KeyCode kc, Tile tile) {
        Logic_move.reset_cord(tile);
        TranslateTransition tt = new TranslateTransition(Duration.millis(150), tile);
        double delta;

        if (tile.getTranslateY() != 0 | tile.getTranslateY() != 0) return;

        //region Direction handler
        switch (kc) {
            case UP:
                if (tile.getJ_() != 0) {
                    delta = tile.getJ_()*-crlb;
                    tt.setToY(delta);
                    tile.setJ_(0);
                    tt.play();
                    Controller._moved = true;
                }
                break;

            case DOWN:
                if (tile.getJ_() != (glb-1)) {
                    delta = ((glb-1)-tile.getJ_())*crlb;
                    tt.setToY(delta);
                    tile.setJ_((glb-1));
                    tt.play();
                    Controller._moved = true;
                }
                break;

            case RIGHT:
                if (tile.getI_() != (glb-1)) {
                    delta = ((glb-1)-tile.getI_())*crlb;
                    tt.setToX(delta);
                    tile.setI_((glb-1));
                    tt.play();
                    Controller._moved = true;
                }
                break;

            case LEFT:
                if (tile.getI_() != 0) {
                    delta = tile.getI_()*-crlb;
                    tt.setToX(delta);
                    tile.setI_(0);
                    tt.play();
                    Controller._moved = true;
                }
                break;
        }
        //endregion

    }

    static void move_to_nearest (@NotNull KeyCode kc,@NotNull Tile t_m, @NotNull Tile t_dc) {
        reset_cord(t_m);
        reset_cord(t_dc);

        TranslateTransition tt = new TranslateTransition(Duration.millis(150), t_m);
        double delta = 0;
        boolean check_merge_condition = (t_m.getValue() == t_dc.getValue() && t_dc.isMergable());

        //region Direction handler
        switch (kc) {
            case UP:
                delta = check_merge_condition? (t_dc.getJ_()-t_m.getJ_())*crlb : (t_dc.getJ_()-t_m.getJ_() + 1)*crlb;
                tt.setToY(delta);
                t_m.setJ_(check_merge_condition? t_dc.getJ_() : t_dc.getJ_() + 1);
                tt.play();
                break;

            case DOWN:
                delta = check_merge_condition? (t_dc.getJ_()-t_m.getJ_())*crlb : (t_dc.getJ_()-t_m.getJ_() - 1)*crlb;
                tt.setToY(delta);
                t_m.setJ_(check_merge_condition? t_dc.getJ_() : t_dc.getJ_() - 1);
                tt.play();
                break;

            case RIGHT:
                delta = check_merge_condition? (t_dc.getI_()-t_m.getI_())*crlb : (t_dc.getI_()-t_m.getI_() - 1)*crlb;
                tt.setToX(delta);
                t_m.setI_(check_merge_condition? t_dc.getI_() : t_dc.getI_() - 1);
                tt.play();
                break;

            case LEFT:
                delta = check_merge_condition? (t_dc.getI_()-t_m.getI_())*crlb : (t_dc.getI_()-t_m.getI_() + 1)*crlb;
                tt.setToX(delta);
                t_m.setI_(check_merge_condition? t_dc.getI_() : t_dc.getI_() + 1);
                tt.play();
                break;
        }
        //endregion
        if (delta != 0) {
            Controller._moved = true;
        }

        if (check_merge_condition) {
            t_m.setValue(t_m.getValue()*2);
            t_m.setMergable(false);
            Controller.XTile.remove(t_dc);
            tt.setOnFinished(event -> {
                Controller.group.getChildren().remove(t_dc);

                ScaleTransition st = new ScaleTransition(Duration.millis(75));
                st.setToY(1.1);
                st.setToX(1.1);

                st.setOnFinished(event1 -> t_m.setText());

                ScaleTransition st2 = new ScaleTransition(Duration.millis(100));
                st2.setToX(1);
                st2.setToY(1);

                SequentialTransition seq = new SequentialTransition(st, st2);
                seq.setNode(t_m);
                seq.play();

                //t_m.setText();
            });
        }
    }

}