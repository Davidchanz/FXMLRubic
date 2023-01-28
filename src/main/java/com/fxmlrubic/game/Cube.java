package com.fxmlrubic.game;

import javafx.geometry.Point3D;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Cube extends MeshView {
    public RotateAnimation rotateAnimation;
    private Point3D axis;

    public Cube(TriangleMesh mesh){
        super(mesh);
    }

    public Point3D getAxis() {
        return axis;
    }

    public void setAxis(Point3D axis) {
        this.axis = axis;
    }
}
