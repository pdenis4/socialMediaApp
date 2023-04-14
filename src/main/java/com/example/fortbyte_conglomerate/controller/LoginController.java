package com.example.fortbyte_conglomerate.controller;

import com.example.fortbyte_conglomerate.HelloApplication;
import com.example.fortbyte_conglomerate.domain.Account;
import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.exceptions.CredentialsException;
import com.example.fortbyte_conglomerate.exceptions.DuplicateException;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.exceptions.ValidationException;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.observer.Observer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController implements Initializable {

    ObservableList<Account> model = FXCollections.observableArrayList();

    SocialNetworkService service;

    private double x = 0, y = 0;
    private boolean flag_register_opened = false;
    @FXML
    AnchorPane sidebar;
    @FXML
    AnchorPane register;
    @FXML
    AnchorPane login;
    @FXML
    TextField mailRegister;
    @FXML
    TextField firstnameRegister;
    @FXML
    TextField lastnameRegister;
    @FXML
    TextField passwordRegister;
    @FXML
    TextField mailLogin;
    @FXML
    TextField passwordLogin;
    @FXML
    JFXCheckBox termsCheck;
    @FXML
    TextField errorsRegister;
    @FXML
    TextField errorsLogin;

    private Stage stage;

    @FXML
    AnchorPane rootPane;

    public void setUtilizatorService(SocialNetworkService service) {
        this.service = service;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sidebar.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        sidebar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });

        int duration = 700;
        handleScaleTransition(login, duration);
        handleFadeTransition(login, duration);
        handleTranslateTransition(login, duration, 600);

        register.setVisible(false);
        register.setDisable(true);
        errorsRegister.setVisible(false);
        errorsLogin.setVisible(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void closeProgram(ActionEvent event) {
        stage.close();
    }

    @FXML
    public void openRegisterMenu(ActionEvent event) {
        register.setDisable(flag_register_opened);
        register.setVisible(!flag_register_opened);
        flag_register_opened = !flag_register_opened;

        if(flag_register_opened) {
            int duration = 700;
            handleScaleTransition(register, duration);
            handleFadeTransition(register, duration);
            handleTranslateTransition(register, duration, -register.getLayoutX());
        }
    }

    private void handleFadeTransition(Node node, int duration){
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        fadeTransition.play();
    }

    private void handleTranslateTransition(Node node, int duration, double fromPosX){
        float scale = 0.6f;
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(duration), node);
        translateTransition.setFromX(fromPosX * scale);
        translateTransition.setToX(0);

        translateTransition.play();
    }

    private void handleScaleTransition(Node node, int duration){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), node);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);

        scaleTransition.play();
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String mail = mailRegister.getText();
        String firstname = firstnameRegister.getText();
        String lastname = lastnameRegister.getText();
        String password = passwordRegister.getText();
        boolean terms = termsCheck.isSelected();

        if(mail.equals("") || firstname.equals("") || lastname.equals("") || password.equals("") || !terms) {
            errorsRegister.setText("Please fill all the fields!");
            errorsRegister.setVisible(true);
            return;
        }

        try {
            service.addAccount(mail, password, firstname, lastname);

            mailRegister.setText("");
            firstnameRegister.setText("");
            lastnameRegister.setText("");
            passwordRegister.setText("");
            errorsRegister.setVisible(false);
            termsCheck.setSelected(false);
        }catch (ValidationException | CredentialsException | DuplicateException e){
            errorsRegister.setText(e.getMessage());
            errorsRegister.setVisible(true);
            //e.printStackTrace();
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        String mail = mailLogin.getText();
        String password = passwordLogin.getText();

        if(mail.equals("") || password.equals("")) {
            errorsLogin.setText("Please fill all the fields!");
            errorsLogin.setVisible(true);
            return;
        }

        try {
            service.login(mail, password);
            errorsLogin.setVisible(false);
            switchToScene2(event);
        }catch (ValidationException | CredentialsException | IdNotFoundException e){
            errorsLogin.setText(e.getMessage());
            errorsLogin.setVisible(true);
            //e.printStackTrace();
        }


        //System.out.println("Logged in!");
        //System.out.println(account);


    }

    public void switchToScene2(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/userView.fxml"));
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        UserController userController = loader.getController();
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        userController.setStage(stage);
        userController.setUtilizatorService(this.service);
        stage.show();
    }

}
