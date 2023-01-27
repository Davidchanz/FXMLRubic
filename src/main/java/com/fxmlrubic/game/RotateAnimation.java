package com.fxmlrubic;

import javafx.animation.AnimationTimer;
import javafx.scene.transform.Rotate;

import java.util.concurrent.atomic.AtomicBoolean;

public class RotateAnimation extends AnimationTimer {
    Rotate rotate;
    double step;
    Cube cube;
    FunctionalInterface action;
    public RotateAnimation(Cube cube, Rotate rotate, double step){
        this.rotate = rotate;
        this.step = step;
        this.cube = cube;
    }

    public RotateAnimation(Cube cube, Rotate rotate, double step, FunctionalInterface action){
        this.rotate = rotate;
        this.step = step;
        this.cube = cube;
        this.action = action;
    }

    @Override
    public void handle(long l) {
        rotate.setAngle(rotate.getAngle() + step);
        if(rotate.getAngle() >= 90){
            stop();
            rotate.setAngle(90);
            HelloController.isRotate.set(false);
        } else if (rotate.getAngle() <= -90) {
            stop();
            rotate.setAngle(-90);
            HelloController.isRotate.set(false);
        }
    }
}
