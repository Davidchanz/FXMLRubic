package com.fxmlrubic.game;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Cube extends MeshView {
    public int RL;
    public int UD;
    public int FB;
    public RotateAnimation rotateAnimation;

    public Cube(TriangleMesh mesh){
        super(mesh);
    }
}
