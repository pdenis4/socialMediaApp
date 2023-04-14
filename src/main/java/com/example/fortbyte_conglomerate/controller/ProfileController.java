package com.example.fortbyte_conglomerate.controller;

import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ProfileController implements Initializable, Observer {

    SocialNetworkService service;
    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    Button addFriendButton;
    @FXML
    Button removeFriendButton;
    @FXML
    Button cancelFriendRequestButton;
    @FXML
    Button acceptFriendRequestButton;
    @FXML
    Button declineFriendRequestButton;
    @FXML
    Button messengerButton;

    UserController userController;

    @Override
    public void update() {
        handleActiveButtons();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setService(SocialNetworkService service, UserController userController) {
        this.service = service;
        service.addObserver(this);
        this.userController = userController;

        User profileUser = service.getOneUser(service.getIdProfileUser());

        firstNameLabel.setText(profileUser.getFirstName());
        lastNameLabel.setText(profileUser.getLastName());

        handleActiveButtons();
    }

    private void handleActiveButtons() {
        User currentUser = service.getOneUser(service.getIdCurrentUser());
        User profileUser = service.getOneUser(service.getIdProfileUser());

        //they are friends (friendship status is ACCEPTED)
        StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                .filter(friendship -> friendship.isBetween(currentUser.getId(), profileUser.getId())
                        && friendship.getStatus().name().equals("ACCEPTED"))
                .findFirst()
                .ifPresent(friendship -> {
                    addFriendButton.setDisable(true);
                    addFriendButton.setVisible(false);
                    removeFriendButton.setDisable(false);
                    removeFriendButton.setVisible(true);
                    cancelFriendRequestButton.setDisable(true);
                    cancelFriendRequestButton.setVisible(false);
                    acceptFriendRequestButton.setDisable(true);
                    acceptFriendRequestButton.setVisible(false);
                    declineFriendRequestButton.setDisable(true);
                    declineFriendRequestButton.setVisible(false);
                });

        //they are not friends, check for if they have a pending request
        StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                //pending friendship between the two users
                .filter(friendship -> friendship.isBetween(currentUser.getId(), profileUser.getId()))
                .filter(friendship -> friendship.getStatus().name().equals("PENDING"))
                .findFirst()
                .ifPresent(friendship -> {
                    //current user was sender, he can cancel the request
                    if(friendship.hasSender(currentUser.getId())){
                        addFriendButton.setDisable(true);
                        addFriendButton.setVisible(false);
                        removeFriendButton.setDisable(true);
                        removeFriendButton.setVisible(false);
                        cancelFriendRequestButton.setDisable(false);
                        cancelFriendRequestButton.setVisible(true);
                        acceptFriendRequestButton.setDisable(true);
                        acceptFriendRequestButton.setVisible(false);
                        declineFriendRequestButton.setDisable(true);
                        declineFriendRequestButton.setVisible(false);
                    }
                    //current user was receiver, he can accept or decline the request
                    else {
                        addFriendButton.setDisable(true);
                        addFriendButton.setVisible(false);
                        removeFriendButton.setDisable(true);
                        removeFriendButton.setVisible(false);
                        cancelFriendRequestButton.setDisable(true);
                        cancelFriendRequestButton.setVisible(false);
                        acceptFriendRequestButton.setDisable(false);
                        acceptFriendRequestButton.setVisible(true);
                        declineFriendRequestButton.setDisable(false);
                        declineFriendRequestButton.setVisible(true);
                    }

                });

        //if they are not friends nor have a pending request, current user can send a friend request
        StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                        .filter(friendship -> friendship.isBetween(currentUser.getId(), profileUser.getId()))
                        .findAny()
                        .ifPresentOrElse(friendship -> {}, () -> {
                            addFriendButton.setDisable(false);
                            addFriendButton.setVisible(true);
                            removeFriendButton.setDisable(true);
                            removeFriendButton.setVisible(false);
                            cancelFriendRequestButton.setDisable(true);
                            cancelFriendRequestButton.setVisible(false);
                            acceptFriendRequestButton.setDisable(true);
                            acceptFriendRequestButton.setVisible(false);
                            declineFriendRequestButton.setDisable(true);
                            declineFriendRequestButton.setVisible(false);
                        });
    }

    @FXML
    public void handleAddFriendButton(){
        service.addFriendship(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString());
    }
    @FXML
    public void handleRemoveFriendButton(){
        //cancel friendship (ACCEPTED)
        service.removeFriendship(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString());
    }
    @FXML
    public void handleCancelFriendRequestButton(){
        //cancel pending friendship request
        service.removeFriendship(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString());
    }
    @FXML
    public void handleAcceptFriendRequestButton(){
        //accept pending friendship request
        service.acceptFriendship(service.getIdProfileUser(), service.getIdCurrentUser());
    }
    @FXML
    public void handleDeclineFriendRequestButton(){
        //decline pending friendship request
        service.removeFriendship(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString());
    }
    @FXML
    public void handleMessenger(ActionEvent event) throws IOException {
        if(!service.areFriends(service.getIdCurrentUser(), service.getIdProfileUser()))
            return;

        if (!service.isConversationBetween(service.getIdCurrentUser(), service.getIdProfileUser()))
            service.addConversation(service.getIdCurrentUser().toString(), service.getIdProfileUser().toString());

        userController.openMessenger(event);
    }
}
