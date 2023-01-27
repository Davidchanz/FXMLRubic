package com.fxmlrubic;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;

public class HelloApplication extends Application {
    private float moveSpeed = 0.05f, mouseSensitivity = 0.5f, distance = 2.0f, horizontalAngle = 0, verticalAngle = 0;
    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240, true);

        /*Translate pivot = new Translate();
        pivot.setX(10);
        pivot.setY(10);
        pivot.setZ(10);

        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll (
                pivot,
                yRotate,
                xRotate,
                zRotate,
                new Translate(0,0,-100)
        );

        scene.setOnMouseClicked(event -> {
            newMouseX = event.getSceneX();
            newMouseY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            newMouseX = event.getSceneX();
            newMouseY = event.getSceneY();

            float dx = (float) (event.getSceneX() - oldMouseX);
            float dy = (float) (event.getSceneY() - oldMouseY);

            if (event.isPrimaryButtonDown()) {
                verticalAngle -= dy * mouseSensitivity;
                horizontalAngle += dx * mouseSensitivity;
            }

            xRotate.setAngle(+verticalAngle);
            yRotate.setAngle(-horizontalAngle);

            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
        });

        //camera.setTranslateZ(-000);
        camera.setNearClip(1);
        camera.setFarClip(10000);

        scene.setCamera(camera);*/

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}