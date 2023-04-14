package com.example.fortbyte_conglomerate.controller;

import com.example.fortbyte_conglomerate.domain.Message;
import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.observer.Observer;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class MessagerController implements Initializable, Observer {

    ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    @FXML
    private TextField messageField;

    @FXML
    private JFXListView<Message> messageListView;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;

    private SocialNetworkService service;

    @Override
    public void update() {
        messagesModel.setAll(service.getMessagesFromUsers(service.getIdCurrentUser(), service.getIdProfileUser()));
        messageListView.scrollTo(messagesModel.size() - 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setService(SocialNetworkService service) {
        this.service = service;
        service.addObserver(this);

        initializeListStyle();
        initTextField();

        firstNameLabel.setText(service.getOneUser(service.getIdProfileUser()).getFirstName());
        lastNameLabel.setText(service.getOneUser(service.getIdProfileUser()).getLastName());

        firstNameLabel.setStyle("-fx-text-fill: #ffa65d" +
                ";-fx-font-size: 20px" +
                ";-fx-font-weight: bold");

        lastNameLabel.setStyle("-fx-text-fill: #ffa65d" +
                ";-fx-font-size: 20px" +
                ";-fx-font-weight: bold");

        update();
        messageListView.setItems(messagesModel);
    }

    private void initializeListStyle(){
        messageListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Message message, boolean empty) {
                        super.updateItem(message, empty);
                        if (message == null || empty) {
                            setText(null);
                            setStyle("-fx-background-color: transparent");
                        } else {
                            setText(message.getContent());

                            if(message.getSenderId().equals(service.getIdCurrentUser())){
                                setStyle("-fx-text-fill: #9563ff;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 20px;" +
                                        "-fx-background-color: transparent");
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setStyle("-fx-text-fill: #ff8b4d" +
                                        ";-fx-font-weight: bold;" +
                                                "-fx-font-size: 20;" +
                                        "-fx-background-color: transparent");
                                setAlignment(Pos.CENTER_LEFT);
                            }
                        }
                    }

                };
            }
        });
    }

    private void initTextField(){
        messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    handleSendMessage();
                }
            }
        });
    }
    @FXML
    public void handleSendMessage(){
        if(messageField.getText().equals(""))
            return;
        service.addMessage(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString(), messageField.getText());
        messageField.setText("");
    }
}
