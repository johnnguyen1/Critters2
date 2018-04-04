package assignment5;
/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * John Nguyen
 * jhn595
 * Slip days used: <0>
 * Spring 2018
 */


import javafx.application.*;

import javafx.scene.shape.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.layout.*;

import javafx.scene.text.*;

import javafx.scene.control.*;

import javax.naming.InvalidNameException;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Critters 2");
        int stage_width = 1000;
        int stage_height = 800;
        GridPane grid = new GridPane();
        Group root = new Group();
        Scene scene = new Scene(root, stage_width, stage_height);


        Text t = new Text(5, 20, "Menu:");
        root.getChildren().add(t);
        grid.setVgap(20);
        //number of steps textbox
        final TextField stepNum = new TextField();
        stepNum.setPromptText("Enter # of steps.");
        stepNum.setPrefColumnCount(5);
        stepNum.getText();
        GridPane.setConstraints(stepNum, 0, 1);
        grid.getChildren().add(stepNum);

        //button to activate steps
        Button step = new Button("Step");
        GridPane.setConstraints(step, 1, 1);
        step.setOnAction((event) -> {
            if ((step.getText() != null && !step.getText().isEmpty())) {
                try{
                    int num = Integer.parseInt(step.getText());
                    for(int i = 0; i<num; i++){
                        Critter.worldTimeStep();
                    }
                }catch (NumberFormatException nfe){
                    Critter.worldTimeStep(); // figure out if string
                }
            }
            else if (step.getText().isEmpty()) {
                Critter.worldTimeStep();
            }
        });
        grid.getChildren().add(step);

        //drop down to select critter to make
        final ComboBox critterBox = new ComboBox();
        critterBox.setPromptText("Select Critter");
        critterBox.getItems().addAll(
                "Algae",
                "AlgaephobicCritter",
                "Craig",
                "TragicCritter"
        );
        GridPane.setConstraints(critterBox, 0, 3);
        grid.getChildren().add(critterBox);

        //num of critters to make texbox
        final TextField makeNum = new TextField();
        makeNum.setPromptText("Enter # of critters.");
        makeNum.setPrefColumnCount(5);
        makeNum.getText();
        GridPane.setConstraints(makeNum, 1, 3);
        grid.getChildren().add(makeNum);

        //button to make critters
        Button makeCritter = new Button("Make Critter");
        GridPane.setConstraints(makeCritter, 2, 3);
        makeCritter.setOnAction((event) -> {
            if ((step.getText() != null && !step.getText().isEmpty())) {
                try{
                    int num = Integer.parseInt(makeNum.getText());
                    for(int i = 0; i<num; i++){
                        Critter.makeCritter((String)critterBox.getValue());
                    }
                }catch (InvalidCritterException ice){
                    System.out.println("error processing: " + critterBox.getValue());
                }
            }
        });
        grid.getChildren().add(makeCritter);

        final Canvas canvas = new Canvas(stage_width, stage_height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        makeGrid(gc, stage_width, stage_height);

        root.getChildren().add(canvas);
        root.getChildren().add(grid);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void makeGrid(GraphicsContext gc, double sW, double sH){
        int h = Params.world_height;
        int w = Params.world_width;
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        double x1 = sW/2;
        double x2 = sW - sW/50;
        double sideLen = x2-x1;
        double y1 = (sH - sideLen)/2;
        double y2 = y1 + sideLen;

        for(double x = x1; x <= x1+sideLen; x+=sideLen/w){
            for(double y = y1; y <= y2; y+=sideLen/h){
                gc.strokeLine(x, y, x2, y);
                gc.strokeLine(x, y, x, y2);
            }
        }


    }
	public static void main(String[] args) {
        launch(args);
	}
}
