package com.fxmlrubic;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class RubicGroup extends SubScene {
    public RubicGroup(Group root){
        super(root, 600, 600, true, SceneAntialiasing.BALANCED);
        setFill(Color.ALICEBLUE);
    }
}
