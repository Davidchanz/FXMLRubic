package com.fxmlrubic;

import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;

public class Cube extends MeshView {
    public int RL;
    public int UD;
    public int FB;
    public RotateAnimation rotateAnimation;

    public Cube(TriangleMesh mesh){
        super(mesh);
    }
}
