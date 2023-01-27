package com.fxmlrubic.controller;

import com.fxmlrubic.game.Cube;
import com.fxmlrubic.game.RotateAnimation;
import com.fxmlrubic.game.RubicGroup;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController implements Initializable {
    public Group mainScene;
    Group rubicGroup;
    RubicGroup subScene;
    ArrayList<Cube> boxes;
    int size;
    int r_size;
    Cube[][][] rubic;
    static public AtomicBoolean isRotate = new AtomicBoolean();
    Cube lc, rc, uc, dc, fc, bc;
    String currentFormula;
    Timeline timeline;
    private final float moveSpeed = 0.05f;
    private final float mouseSensitivity = 0.5f;
    private float horizontalAngle = 0;
    private float verticalAngle = 0;
    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;

    //TODO UI
    // Click prime button and choose center of  mechanical rotation next move mouse with primary button down and rotate in right or left
    // Make fast animation of break pass in one more override of setRotate function rotation speed

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rubicGroup = new Group();
        subScene = new RubicGroup(rubicGroup);
        rubicGroup.setManaged(false);
        mainScene.getChildren().add(subScene);


        Translate pivot = new Translate();
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

        subScene.setOnMouseClicked(event -> {
            newMouseX = event.getSceneX();
            newMouseY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
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

        subScene.setCamera(camera);



        subScene.setFocusTraversable(true);
        subScene.requestFocus();

        isRotate.set(false);
        r_size = 3;
        size = 10;
        boxes = new ArrayList<>();
        rubic  = new Cube[r_size][r_size][r_size];
        for(int i = 0; i < r_size; i++)
            for(int j = 0; j < r_size; j++)
                for(int k = 0; k < r_size; k++) {
                    rubic[i][j][k] = createCube(i, j, k, size);
                    rubicGroup.getChildren().add(rubic[i][j][k]);
                    boxes.add(rubic[i][j][k]);

                    if(i == 0 && j == 1 && k == 1){
                        lc = rubic[i][j][k];
                    }else if(i == r_size-1 && j == 1 && k == 1){
                        rc = rubic[i][j][k];
                    }else if(i == 1 && j == 0 && k == 1){
                        uc = rubic[i][j][k];
                    }else if(i == 1 && j == r_size-1 && k == 1){
                        dc = rubic[i][j][k];
                    }else if(i == 1 && j == 1 && k == r_size-1){
                        fc = rubic[i][j][k];
                    }else if(i == 1 && j == 1 && k == 0){
                        bc = rubic[i][j][k];
                    }
                    if(i == 1 && j == 1 && k == 1){
                        System.out.println(rubic[i][j][k].localToScene(0,0,0).getZ());
                    }
                }
        timeline = new Timeline(new KeyFrame(Duration.millis(100), this::building));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void building(ActionEvent actionEvent) {
        if(!isRotate.get()) {
            isRotate.set(true);
            build(currentFormula.charAt(currentFormula.length()-1), false);
            currentFormula = currentFormula.substring(0, currentFormula.length()-1);
            if(currentFormula.length() == 0) {
                timeline.stop();
                currentFormula = null;
            }
        }
    }

    private Cube createCube(double i, double j, double k, double size){
        Image image;
        try {
            image = new Image(new File("src/main/resources/com/fxmlrubic/1.png").toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(image);

        float hw = (float)size/2f;
        float hh = (float)size/2f;
        float hd = (float)size/2f;

        float L = 400f;
        float H = 300f;

        float[] points =
                {
                        hw, hh, hd,
                        hw, hh, -hd,
                        hw, -hh, hd,
                        hw, -hh, -hd,
                        -hw, hh, hd,
                        -hw, hh, -hd,
                        -hw, -hh, hd,
                        -hw, -hh, -hd,
                };

        float tex[] = {
                100f / L,   0f / H,
                200f / L,   0f / H,
                0f / L, 100f / H,
                100f / L, 100f / H,
                200f / L, 100f / H,
                300f / L, 100f / H,
                400f / L, 100f / H,
                0f / L, 200f / H,
                100f / L, 200f / H,
                200f / L, 200f / H,
                300f / L, 200f / H,
                400f / L, 200f / H,
                100f / L, 300f / H,
                200f / L, 300f / H
        };

        int[] faces =
                {
                        0, 10, 2, 5, 1, 9,
                        2, 5, 3, 4, 1, 9,
                        4, 7, 5, 8, 6, 2,
                        6, 2, 5, 8, 7, 3,
                        0, 13, 1, 9, 4, 12,
                        4, 12, 1, 9, 5, 8,
                        2, 1, 6, 0, 3, 4,
                        3, 4, 6, 0, 7, 3,
                        0, 10, 4, 11, 2, 5,
                        2, 5, 4, 11, 6, 6,
                        1, 9, 3, 4, 5, 8,
                        5, 8, 3, 4, 7, 3
                };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(tex);
        mesh.getFaces().addAll(faces);

        int[] face = {
                0,0,1,1,2,2,3,3,4,4,5,5
        };
        mesh.getFaceSmoothingGroups().addAll(face);

        Cube box = new Cube(mesh);
        box.setMaterial(material);

        box.setTranslateX(i*size);
        box.setTranslateY(j*size);
        box.setTranslateZ(k*size);

        return box;
    }

    public String getFormula(){
        Random r = new Random();
        StringBuilder formula = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            int com = r.nextInt(12);
            switch (com) {
                case 0 -> formula.append('R');
                case 1 -> formula.append('L');
                case 2 -> formula.append('U');
                case 3 -> formula.append('D');
                case 4 -> formula.append('F');
                case 5 -> formula.append('B');
                case 6 -> formula.append('r');
                case 7 -> formula.append('l');
                case 8 -> formula.append('u');
                case 9 -> formula.append('d');
                case 10 -> formula.append('f');
                case 11 -> formula.append('b');
            }
        }
        return formula.toString();
    }

    public void disBuild(String formula){
        for (int u = 0; u < formula.length(); u++) {
            if(Character.isUpperCase(formula.charAt(u)))
                build(Character.toLowerCase(formula.charAt(u)), true);
            else if(Character.isLowerCase(formula.charAt(u)))
                build(Character.toUpperCase(formula.charAt(u)), true);
        }
    }

    public void build(char c, boolean permanent){
        if(!permanent)
            isRotate.set(true);
        switch (c) {
            case 'r' -> {
                for(var cube: boxes) {
                    var center = rc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(1, 0, 0), 1, rc);
                        else
                            setPermanentRotate(cube, new Point3D(1, 0, 0), 1, rc);
                    }
                }
            }
            case 'l' -> {
                for(var cube: boxes) {
                    var center = lc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(1, 0, 0), 1, lc);
                        else
                            setPermanentRotate(cube, new Point3D(1, 0, 0), 1, lc);
                    }
                }
            }
            case 'u' -> {
                for(var cube: boxes) {
                    var center = uc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 1, 0), 1, uc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 1, 0), 1, uc);
                    }
                }
            }
            case 'd' -> {
                for(var cube: boxes) {
                    var center = dc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 1, 0), 1, dc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 1, 0), 1, dc);
                    }
                }
            }
            case 'f' -> {
                for(var cube: boxes) {
                    var center = fc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 0, 1), 1, fc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 0, 1), 1, fc);
                    }
                }
            }
            case 'b' -> {
                for(var cube: boxes) {
                    var center = bc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 0, 1), 1, bc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 0, 1), 1, bc);
                    }
                }
            }
            case 'R' -> {
                for(var cube: boxes) {
                    var center = rc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(1, 0, 0), -1, rc);
                        else
                            setPermanentRotate(cube, new Point3D(1, 0, 0), -1, rc);
                    }
                }
            }
            case 'L' -> {
                for(var cube: boxes) {
                    var center = lc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(1, 0, 0), -1, lc);
                        else
                            setPermanentRotate(cube, new Point3D(1, 0, 0), -1, lc);
                    }
                }
            }
            case 'U' -> {
                for(var cube: boxes) {
                    var center = uc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 1, 0), -1, uc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 1, 0), -1, uc);
                    }
                }
            }
            case 'D' -> {
                for(var cube: boxes) {
                    var center = dc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 1, 0), -1, dc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 1, 0), -1, dc);
                    }
                }
            }
            case 'F' -> {
                for(var cube: boxes) {
                    var center = fc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 0, 1), -1, fc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 0, 1), -1, fc);
                    }
                }
            }
            case 'B' -> {
                for(var cube: boxes) {
                    var center = bc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, new Point3D(0, 0, 1), -1, bc);
                        else
                            setPermanentRotate(cube, new Point3D(0, 0, 1), -1, bc);
                    }
                }
            }
        }
    }

    public void setRotate(Cube box, Point3D axis, int dir, Cube center){
        Rotate rotate = new Rotate();

        var tr = box.getTransforms().toArray(new Transform[0]);
        box.getTransforms().clear();

        var x = center.localToScene(0,0,0).getX() - box.localToScene(0,0,0).getX();
        var y = center.localToScene(0,0,0).getY() - box.localToScene(0,0,0).getY();
        var z = center.localToScene(0,0,0).getZ() - box.localToScene(0,0,0).getZ();
        if(x != 0){
            x /= Math.abs(x);
        }
        if(y != 0){
            y /= Math.abs(y);
        }
        if(z != 0){
            z /= Math.abs(z);
        }
        rotate.setAxis(axis);
        rotate.setPivotX(x * size);
        rotate.setPivotY(y * size);
        rotate.setPivotZ(z * size);

        box.getTransforms().add(rotate);
        box.getTransforms().addAll(tr);

        box.rotateAnimation = new RotateAnimation(box, rotate, 0.5*dir);
        box.rotateAnimation.start();
    }

    public void setPermanentRotate(Cube box, Point3D axis, int dir, Cube center){
        Rotate rotate = new Rotate();

        var tr = box.getTransforms().toArray(new Transform[0]);
        box.getTransforms().clear();

        var x = center.localToScene(0,0,0).getX() - box.localToScene(0,0,0).getX();
        var y = center.localToScene(0,0,0).getY() - box.localToScene(0,0,0).getY();
        var z = center.localToScene(0,0,0).getZ() - box.localToScene(0,0,0).getZ();
        if(x != 0){
            x /= Math.abs(x);
        }
        if(y != 0){
            y /= Math.abs(y);
        }
        if(z != 0){
            z /= Math.abs(z);
        }
        rotate.setAxis(axis);
        rotate.setPivotX(x * size);
        rotate.setPivotY(y * size);
        rotate.setPivotZ(z * size);
        rotate.setAngle(90*dir);

        box.getTransforms().add(rotate);
        box.getTransforms().addAll(tr);
    }

    public void rotateR(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('R', false);
    }

    public void rotateL(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('L', false);
    }

    public void rotateU(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('U', false);
    }

    public void rotateD(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('D', false);
    }

    public void rotateF(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('F', false);
    }

    public void rotateB(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('B', false);
    }

    public void rotate_r(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('r', false);
    }

    public void rotate_l(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('l', false);
    }

    public void rotate_u(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('u', false);
    }

    public void rotate_d(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('d', false);
    }

    public void rotate_f(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('f', false);
    }

    public void rotate_b(ActionEvent actionEvent) {
        if(!isRotate.get())
            build('b', false);
    }

    public void disBuildOnAction(ActionEvent actionEvent) {
        if (!isRotate.get()) {
            isRotate.set(true);
            boxes.forEach(cube -> cube.getTransforms().clear());
            currentFormula = getFormula();
            disBuild(currentFormula);
        }
    }

    public void buildOnAction(ActionEvent actionEvent) {
        if (isRotate.get() && currentFormula != null && timeline.getStatus() != Animation.Status.RUNNING) {
            isRotate.set(false);
            timeline.play();
        }
    }
}