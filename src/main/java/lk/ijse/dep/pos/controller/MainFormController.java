package lk.ijse.dep.pos.controller;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep.pos.AppInitializer;

import lk.ijse.deppo.crypto.DEPCrypt;
import org.springframework.core.env.Environment;


/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class MainFormController implements Initializable {

    public ProgressIndicator pgb;
    @FXML
    private AnchorPane root;
    @FXML
    private Label lblMenu;
    @FXML
    private Label lblDescription;

    /**
     * Initializes the lk.ijse.dep.pos.controller class.
     */

    private Environment env;

    private   String getUsername() {
       return DEPCrypt.decode(env.getProperty("javax.persistence.jdbc.user"),"dep4") ;
    }


    private   String getPassword() {
        return DEPCrypt.decode(env.getProperty("javax.persistence.jdbc.password"),"dep4");
    }

    private   String getDb() {
        return env.getRequiredProperty("ijse.dep.db");
    }

    private  String getPort() {
       return env.getRequiredProperty("ijse.dep.port");
    }

    private   String getHost() {
        return env.getRequiredProperty("ijse.dep.host");

    }
    public void initialize(URL url, ResourceBundle rb) {
        env= AppInitializer.ctx.getBean(Environment.class);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        pgb.setVisible(false);
    }

    @FXML
    private void playMouseExitAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();

            icon.setEffect(null);
            lblMenu.setText("Welcome");
            lblDescription.setText("Please select one of above main operations to proceed");
        }
    }

    @FXML
    private void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            switch (icon.getId()) {
                case "imgCustomer":
                    lblMenu.setText("Manage Customers");
                    lblDescription.setText("Click to add, edit, delete, search or lk.ijse.dep.pos.view customers");
                    break;
                case "imgItem":
                    lblMenu.setText("Manage Items");
                    lblDescription.setText("Click to add, edit, delete, search or lk.ijse.dep.pos.view items");
                    break;
                case "imgOrder":
                    lblMenu.setText("Place Orders");
                    lblDescription.setText("Click here if you want to place a new order");
                    break;
                case "imgViewOrders":
                    lblMenu.setText("Search Orders");
                    lblDescription.setText("Click if you want to search orders");
                    break;
            }

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.CORNFLOWERBLUE);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }


    @FXML
    private void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            Parent root = null;

            FXMLLoader fxmlLoader = null;
            switch (icon.getId()) {
                case "imgCustomer":
                    root = FXMLLoader.load(this.getClass().getResource("/view/ManageCustomerForm.fxml"));
                    break;
                case "imgItem":
                    root = FXMLLoader.load(this.getClass().getResource("/view/ManageItemForm.fxml"));
                    break;
                case "imgOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/view/PlaceOrderForm.fxml"));
                    break;
                case "imgViewOrders":
                    fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/SearchOrdersForm.fxml"));
                    root = fxmlLoader.load();
                    break;
            }

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }

    public void btnRestore_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Let's restore the backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File file = fileChooser.showOpenDialog(this.root.getScene().getWindow());
        if (file != null) {
            String[] commands;
            if(getPassword().length()>0) {
                commands = new String[]{"mysql", "-h", getHost(),"--port", getPort(),"-u", getUsername(), "-p" + getPassword(), getDb(),
                        "-e", "source " + file.getAbsolutePath()}; // need a space after "source "}
            }else{
                commands = new String[]{"mysql", "-h", getHost(),"--port", getPort(), "-u", getUsername(), getDb(),
                        "-e", "source " + file.getAbsolutePath()}; // need a space after "source "}
            } this.root.getScene().setCursor(Cursor.WAIT);
           pgb.setVisible(true);

           // ong running task  == Restore

        Task task =  new Task<Void>(){

                @Override
                protected Void call() throws Exception {
                    Process process = Runtime.getRuntime().exec(commands);
                    int exitCode = process.waitFor();

                    if(exitCode!=0){
                     /*   InputStream errStream = process.getErrorStream();
                        InputStreamReader isr = new InputStreamReader(errStream);
                        BufferedReader br = new BufferedReader(isr);*/

                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));

               /*         String out = "";
                        String line = null;

                        while ((line = br.readLine()) != null) {
                            out += line + "\n";
                        }*/
//                        System.out.println(out);

//                        br.lines().forEach(new Consumer<String>() {
//                            @Override
//                            public void accept(String s) {
//                                System.out.println(s)
//                            }
//                        });

                        br.lines().forEach(System.out::println);
                        br.close();
                        throw new RuntimeException("Wade Kachal");
                    }else{
                        return null;
                    }



                }
            };

        task.setOnSucceeded(event -> {
            this.root.setCursor(Cursor.DEFAULT);
            pgb.setVisible(false);
            new Alert(Alert.AlertType.INFORMATION,"Restore Process has been success");
        });
        task.setOnFailed(event -> {
            pgb.setVisible(false);
            this.root.setCursor(Cursor.DEFAULT);
            new Alert(Alert.AlertType.ERROR,"Failed to restore the backup");
        });

        new Thread(task).start();
        }
    }

    public void btnBackup_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the DB Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File file = fileChooser.showSaveDialog(this.root.getScene().getWindow());

        if (file != null) {
            Task task =  new Task<Void>(){

                @Override
                protected Void call() throws Exception {
                    Process process = Runtime.getRuntime().exec("mysqldump -h" + getHost() +" --port "+getPort()+ " -u" + getUsername() + " -p" + getPassword() + " " + getDb() + " --result-file " + file.getAbsolutePath() + ((file.getAbsolutePath().endsWith(".sql")) ? "" : ".sql"));
                    int exitCode = process.waitFor();

                    if(exitCode!=0){
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        br.lines().forEach(System.out::println);
                        br.close();
                        throw new RuntimeException("Wade Kachal");
                    }else{
                        return null;
                    }



                }
            };
            task.setOnSucceeded(event -> {
                this.root.setCursor(Cursor.DEFAULT);
                pgb.setVisible(false);
                new Alert(Alert.AlertType.INFORMATION,"Backup Process has been success");
            });
            task.setOnFailed(event -> {
                pgb.setVisible(false);
                this.root.setCursor(Cursor.DEFAULT);
                new Alert(Alert.AlertType.ERROR,"Failed to Backup the backup");
            });

            new Thread(task).start();

        }
    }
}
