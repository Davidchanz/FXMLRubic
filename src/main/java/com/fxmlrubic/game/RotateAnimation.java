package com.fxmlrubic.game;

import com.fxmlrubic.controller.GameController;
import com.fxmlrubic.game.Cube;
import com.fxmlrubic.utils.FunctionalInterface;
import javafx.animation.AnimationTimer;
import javafx.scene.transform.Rotate;

public class RotateAnimation extends AnimationTimer {
    Rotate rotate;
    double step;
    Cube cube;
    public RotateAnimation(Cube cube, Rotate rotate, double step){
        this.rotate = rotate;
        this.step = step;
        this.cube = cube;
    }

    @Override
    public void handle(long l) {
        rotate.setAngle(rotate.getAngle() + step);
        if(rotate.getAngle() >= 90){
            stop();
            rotate.setAngle(90);
            GameController.isRotate.set(false);
        } else if (rotate.getAngle() <= -90) {
            stop();
            rotate.setAngle(-90);
            GameController.isRotate.set(false);
        }
    }
}
