package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import java.util.TreeMap;

import static sample.Main.glb;
import static sample.Main.plb;
import static sample.Main.crlb;

class Components {
    private static TreeMap<Integer, String> mydict;

    // Init the dictionary for color - value
    static {
        mydict = new TreeMap<>();
        mydict.put(2, "#FFF8ED");
        mydict.put(4, "#FFFF6E");
        mydict.put(8, "#FFE7BA");
        mydict.put(16, "#FFD77A");
        mydict.put(32, "#F77E7E");
        mydict.put(64, "#FFB0C8");
        mydict.put(128, "#F79CF6");
        mydict.put(256, "#6879E3");
        mydict.put(512, "#66C961");
        mydict.put(1024, "#CC00BB");
        mydict.put(2048, "#000000");
    }

    static String getColorForKey(int i) {
        if (mydict.containsKey(i)) {
            return mydict.get(i);
        } else {
            return "#1495B3";
        }
    }
}


class Tile extends Label {

    //region Necessary Instance properties and their Getters & Setters
    private int value;
    private int i_;
    private int j_;

    private boolean mergable;

    // CONSTRUCTOR
    Tile(int incremental_index, int value) {
        super();
        setPrefSize(crlb-plb, crlb-plb);
        setBackground(new Background(new BackgroundFill(Paint.valueOf(Components.getColorForKey(value)), new CornerRadii(plb), null)));
        setFont(Font.font("Candara", Math.round(crlb/2.5)));
        if (value < 15) {
            setTextFill(Color.BLACK);
        } else {
            setTextFill(Color.WHITE);
        }
        setAlignment(Pos.CENTER);
        setInc_index(incremental_index);
        setValue(value);
        setText(String.valueOf(value));
        setMergable(true);
    }

    boolean isMergable() {
        return mergable;
    }

    void setMergable(boolean mergable) {
        this.mergable = mergable;
    }

    int getValue() {
        return value;
    }

    void setValue(int i) {
        value = i;
    }

    int getI_() {
        return i_;
    }

    void setI_(int i_) {
        this.i_ = i_;
    }

    int getJ_() {
        return j_;
    }
    //endregion

    void setJ_(int j_) {
        this.j_ = j_;
    }

    // Special attributes for Tile: incremental index:
    int getInc_index() {
        return getI_()*glb+getJ_();
    }

    private void setInc_index(int inc_index) {
        setI_(Math.floorDiv(inc_index, glb));
        setJ_(inc_index - glb*getI_());
    }

    // Layout method for Tile depending on its Row and Column
    void setLayoutCordinate() {
        setLayoutX(crlb * getI_() + plb);
        setLayoutY(crlb * getJ_() + plb);
    }

    void setText() {
        super.setText(String.valueOf(getValue()));
        if (value < 15) {
            setTextFill(Color.BLACK);
        } else {
            setTextFill(Color.WHITE);
        }
        setBackground(new Background(new BackgroundFill(Paint.valueOf(Components.getColorForKey(getValue())), new CornerRadii(plb), null)));
    }
}