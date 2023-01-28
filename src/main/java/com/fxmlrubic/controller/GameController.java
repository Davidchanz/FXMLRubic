package com.fxmlrubic.controller;

import com.fxmlrubic.game.Cube;
import com.fxmlrubic.game.RotateAnimation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
    public Group rubicGroup;
    public SubScene subScene;
    public ArrayList<Cube> boxes;
    public int size;
    public int r_size;
    public Cube[][][] rubic;
    public static AtomicBoolean isRotate = new AtomicBoolean();
    private Cube lc, rc, uc, dc, fc, bc;
    private Cube rubicCenter;
    private String currentFormula = "";
    private Timeline buildTimeLine;
    private Timeline disBuildTimeLine;
    private final float mouseSensitivity = 0.5f;
    private float horizontalAngle = 0;
    private float verticalAngle = 0;
    private double newMouseX, newMouseY, mouseOnCenterX;
    private final AtomicBoolean isManualControl = new AtomicBoolean();
    private final AtomicBoolean isBuilding = new AtomicBoolean();
    private final ArrayList<Cube> activeManualCube = new ArrayList<>();
    private int lastDir;
    private String disBuildingFormula;
    //TODO UI

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rubicGroup = new Group();
        subScene = new SubScene(rubicGroup, 600,600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.ALICEBLUE);
        rubicGroup.setManaged(false);
        mainScene.getChildren().add(subScene);

        isBuilding.set(false);
        isManualControl.set(false);
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
                        lc.setAxis(new Point3D(1,0,0));
                    }else if(i == r_size-1 && j == 1 && k == 1){
                        rc = rubic[i][j][k];
                        rc.setAxis(new Point3D(1,0,0));
                    }else if(i == 1 && j == 0 && k == 1){
                        uc = rubic[i][j][k];
                        uc.setAxis(new Point3D(0,1,0));
                    }else if(i == 1 && j == r_size-1 && k == 1){
                        dc = rubic[i][j][k];
                        dc.setAxis(new Point3D(0,1,0));
                    }else if(i == 1 && j == 1 && k == r_size-1){
                        fc = rubic[i][j][k];
                        fc.setAxis(new Point3D(0,0,1));
                    }else if(i == 1 && j == 1 && k == 0){
                        bc = rubic[i][j][k];
                        bc.setAxis(new Point3D(0,0,1));
                    }else if(i == 1 && j == 1 && k == 1){
                        rubicCenter = rubic[i][j][k];
                    }
                }

        Cube[] centers = {lc,rc,uc,dc,fc,bc};
        for(var it: centers) {
            it.setOnMousePressed((event) -> onCenterClicked(event, it));
            it.setOnMouseDragged((event) -> onCenterDragged(event, it));
            it.setOnMouseReleased((event) -> onCenterReleased());
        }

        subScene.setCamera(createCamera());
        subScene.setFocusTraversable(true);
        subScene.requestFocus();

        buildTimeLine = new Timeline(new KeyFrame(Duration.millis(100), this::building));
        buildTimeLine.setCycleCount(Timeline.INDEFINITE);

        disBuildTimeLine = new Timeline(new KeyFrame(Duration.millis(10), this::disBuilding));
        disBuildTimeLine.setCycleCount(Timeline.INDEFINITE);
    }

    private void disBuilding(ActionEvent event) {
        if(!isRotate.get()) {
            isRotate.set(true);
            build(disBuildingFormula.charAt(0), true);
            disBuildingFormula = disBuildingFormula.substring(1);
            if(disBuildingFormula.length() == 0) {
                disBuildTimeLine.stop();
                disBuildingFormula = "";
                isBuilding.set(false);
            }
        }
    }

    private void onCenterReleased() {
        if (isManualControl.get()) {
            isRotate.set(true);
            for (var cube: activeManualCube){
                setRotateToEnd(cube, lastDir);
            }

            activeManualCube.clear();
            isManualControl.set(false);
        }
    }

    private void onCenterClicked(MouseEvent event, Cube center){
        if(!isManualControl.get() && event.isPrimaryButtonDown() && !isRotate.get() && activeManualCube.isEmpty() && !isBuilding.get()){
            isManualControl.set(true);

            mouseOnCenterX = event.getSceneX();
            for(var cube: boxes) {
                long x = -1;
                if(center == rc){
                    x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                }else if(center == lc){
                    x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                }else if(center == uc){
                    x = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                }else if(center == dc){
                    x = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                }else if(center == fc){
                    x = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                }else if(center == bc){
                    x = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                }
                if (x == 0) {
                    activeManualCube.add(cube);
                    cube.getTransforms().add(0, new Rotate());
                }
            }
        }
    }

    private void onCenterDragged(MouseEvent event, Cube center){
        if(isManualControl.get() && event.isPrimaryButtonDown() && !activeManualCube.isEmpty()){
            if(event.getSceneX() > mouseOnCenterX)
                lastDir = 1;
            else
                lastDir = -1;

            for(var cube: activeManualCube){
                setPermanentRotate(cube, lastDir, 1, center);
            }
            mouseOnCenterX = event.getSceneX();
        }
    }

    private Camera createCamera(){
        Translate pivot = new Translate();
        pivot.setX(rubicCenter.localToScene(0,0,0).getX());
        pivot.setY(rubicCenter.localToScene(0,0,0).getY());
        pivot.setZ(rubicCenter.localToScene(0,0,0).getZ());

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
                new Translate(0,0,-120)
        );

        subScene.setOnMousePressed(event -> {
            if(event.isSecondaryButtonDown()) {
                newMouseX = event.getSceneX();
                newMouseY = event.getSceneY();
            }
        });
        subScene.setOnMouseDragged(event -> {
            float dx = (float) (newMouseX - event.getSceneX());
            float dy = (float) (newMouseY - event.getSceneY());

            if (event.isSecondaryButtonDown()) {
                verticalAngle += dy * mouseSensitivity;
                horizontalAngle += dx * mouseSensitivity;
            }

            xRotate.setAngle(+verticalAngle);
            yRotate.setAngle(-horizontalAngle);

            newMouseX = event.getSceneX();
            newMouseY = event.getSceneY();
        });

        camera.setNearClip(1);
        camera.setFarClip(10000);

        return camera;
    }

    private void building(ActionEvent actionEvent) {
        if(!isRotate.get()) {
            isRotate.set(true);
            build(currentFormula.charAt(currentFormula.length()-1), false);
            currentFormula = currentFormula.substring(0, currentFormula.length()-1);
            if(currentFormula.length() == 0) {
                buildTimeLine.stop();
                currentFormula = "";
                isBuilding.set(false);
            }
        }
    }

    private Cube createCube(double i, double j, double k, double size){
        Image image;
        try {
            image = new Image(new File("src/main/resources/com/fxmlrubic/cube.png").toURI().toURL().toExternalForm());
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

        float[] tex = {
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
        StringBuilder disBuildingFormulaBuffer = new StringBuilder();
        for (int u = 0; u < formula.length(); u++) {
            if(Character.isUpperCase(formula.charAt(u)))
                disBuildingFormulaBuffer.append(Character.toLowerCase(formula.charAt(u)));
            else if(Character.isLowerCase(formula.charAt(u)))
                disBuildingFormulaBuffer.append(Character.toUpperCase(formula.charAt(u)));
        }
        disBuildingFormula = disBuildingFormulaBuffer.toString();
        isBuilding.set(true);
        disBuildTimeLine.play();
    }

    public void build(char c, boolean permanent){
        if(isManualControl.get())
            return;
        isRotate.set(true);
        switch (c) {
            case 'r' -> {
                for(var cube: boxes) {
                    var center = rc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, 1, rc);
                        else
                            setFastRotate(cube, 1, rc);
                    }
                }
            }
            case 'l' -> {
                for(var cube: boxes) {
                    var center = lc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, 1, lc);
                        else
                            setFastRotate(cube, 1, lc);
                    }
                }
            }
            case 'u' -> {
                for(var cube: boxes) {
                    var center = uc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, 1, uc);
                        else
                            setFastRotate(cube, 1, uc);
                    }
                }
            }
            case 'd' -> {
                for(var cube: boxes) {
                    var center = dc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, 1, dc);
                        else
                            setFastRotate(cube, 1, dc);
                    }
                }
            }
            case 'f' -> {
                for(var cube: boxes) {
                    var center = fc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, 1, fc);
                        else
                            setFastRotate(cube, 1, fc);
                    }
                }
            }
            case 'b' -> {
                for(var cube: boxes) {
                    var center = bc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, 1, bc);
                        else
                            setFastRotate(cube, 1, bc);
                    }
                }
            }
            case 'R' -> {
                for(var cube: boxes) {
                    var center = rc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, -1, rc);
                        else
                            setFastRotate(cube, -1, rc);
                    }
                }
            }
            case 'L' -> {
                for(var cube: boxes) {
                    var center = lc;
                    var x = Math.round(center.localToScene(0,0,0).getX() - cube.localToScene(0,0,0).getX());
                    if (x == 0) {
                        if(!permanent)
                            setRotate(cube, -1, lc);
                        else
                            setFastRotate(cube, -1, lc);
                    }
                }
            }
            case 'U' -> {
                for(var cube: boxes) {
                    var center = uc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, -1, uc);
                        else
                            setFastRotate(cube, -1, uc);
                    }
                }
            }
            case 'D' -> {
                for(var cube: boxes) {
                    var center = dc;
                    var y = Math.round(center.localToScene(0,0,0).getY() - cube.localToScene(0,0,0).getY());
                    if (y == 0) {
                        if(!permanent)
                            setRotate(cube, -1, dc);
                        else
                            setFastRotate(cube, -1, dc);
                    }
                }
            }
            case 'F' -> {
                for(var cube: boxes) {
                    var center = fc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, -1, fc);
                        else
                            setFastRotate(cube, -1, fc);
                    }
                }
            }
            case 'B' -> {
                for(var cube: boxes) {
                    var center = bc;
                    var z = Math.round(center.localToScene(0,0,0).getZ() - cube.localToScene(0,0,0).getZ());
                    if (z == 0) {
                        if(!permanent)
                            setRotate(cube, -1, bc);
                        else
                            setFastRotate(cube, -1, bc);
                    }
                }
            }
        }
    }

    public void setRotate(Cube box, int dir, Cube center){
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
        rotate.setAxis(center.getAxis());
        rotate.setPivotX(x * size);
        rotate.setPivotY(y * size);
        rotate.setPivotZ(z * size);

        box.getTransforms().add(rotate);
        box.getTransforms().addAll(tr);

        box.rotateAnimation = new RotateAnimation(box, rotate, 0.5*dir);
        box.rotateAnimation.start();
    }

    public void setFastRotate(Cube box, int dir, Cube center){
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
        rotate.setAxis(center.getAxis());
        rotate.setPivotX(x * size);
        rotate.setPivotY(y * size);
        rotate.setPivotZ(z * size);

        box.getTransforms().add(rotate);
        box.getTransforms().addAll(tr);

        box.rotateAnimation = new RotateAnimation(box, rotate, 2*dir);
        box.rotateAnimation.start();
    }

    public void setPermanentRotate(Cube box, int dir, double speed, Cube center){
        Rotate rotate = (Rotate) box.getTransforms().get(0);

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
        rotate.setAxis(center.getAxis());
        rotate.setPivotX(x * size);
        rotate.setPivotY(y * size);
        rotate.setPivotZ(z * size);
        rotate.setAngle(rotate.getAngle()+speed*dir);

        box.getTransforms().addAll(tr);
    }

    public void setRotateToEnd(Cube box, int dir){
        Rotate rotate = (Rotate) box.getTransforms().get(0);

        box.getTransforms().remove(rotate);
        var tr = box.getTransforms().toArray(new Transform[0]);
        box.getTransforms().clear();

        box.getTransforms().add(rotate);
        box.getTransforms().addAll(tr);

        box.rotateAnimation = new RotateAnimation(box, rotate, 0.5*dir);
        box.rotateAnimation.start();
    }

    public void onRotateButtonAction(ActionEvent event){
        if(!isRotate.get() && !isBuilding.get())
            build(((Button)event.getSource()).getText().charAt(0), false);
    }

    public void disBuildOnAction() {
        if (!isRotate.get()) {
            boxes.forEach(cube -> cube.getTransforms().clear());
            currentFormula = getFormula();
            disBuild(currentFormula);
        }
    }

    public void buildOnAction() {
        if (!isRotate.get() && !currentFormula.isEmpty() && buildTimeLine.getStatus() == Animation.Status.STOPPED) {
            isBuilding.set(true);
            buildTimeLine.play();
        }
    }
}