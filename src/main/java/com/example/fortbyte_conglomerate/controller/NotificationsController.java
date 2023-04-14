package com.example.fortbyte_conglomerate.controller;

import com.example.fortbyte_conglomerate.domain.Friendship;
import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationsController implements Initializable, Observer {

    ObservableList<Friendship> notificationModel = FXCollections.observableArrayList();
    @FXML
    private ListView<Friendship> notificationList;
    private SocialNetworkService service;

    @Override
    public void update() {
        notificationModel.setAll(StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                        .filter(friendship -> friendship.getStatus().name().equals("PENDING"))
                        .filter(friendship -> Objects.equals(friendship.getReceiverId(), service.getIdCurrentUser()))
                        .collect(Collectors.toList()));
        notificationList.setItems(notificationModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setService(SocialNetworkService service) {
        this.service = service;
        service.addObserver(this);
        initListFactory();
        initModel();
    }

    private void initModel() {
        update();


    }

    private void initListFactory() {
        notificationList.setCellFactory(param -> {
            return new ListCell<Friendship>() {

                protected void updateItem(Friendship friendship, boolean empty) {
                    super.updateItem(friendship, empty);
                    if (empty || friendship == null) {
                        setText(null);
                    } else {
                        //Layout container
                        HBox root = new HBox(10);
                        root.setAlignment(Pos.CENTER_LEFT);
                        root.setPadding(new Insets(5, 10, 5, 10));

                        // Username left and two buttons right
                        Long idSender = friendship.getOtherUser(service.getIdCurrentUser());
                        User userSender = service.getOneUser(idSender);
                        String nameSender = userSender.getFirstName() + " " + userSender.getLastName();
                        Label label = new Label(nameSender + " requests friendship! " + friendship.getFriendsSince().toLocalDate());
                        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #fd835f;");
                        root.getChildren().add(label);

                        // Region to expand, pushing buttons to right
                        Region region = new Region();
                        HBox.setHgrow(region, Priority.ALWAYS);
                        root.getChildren().add(region);

                        //Images for buttons
                        ImageView acceptImage = new ImageView("com/example/fortbyte_conglomerate/images/tick.png");
                        acceptImage.setFitHeight(40);
                        acceptImage.setFitWidth(40);
                        ImageView declineImage = new ImageView("com/example/fortbyte_conglomerate/images/minus.png");
                        declineImage.setFitHeight(40);
                        declineImage.setFitWidth(40);

                        // Buttons
                        Button btnAddFriend = new Button("");
                        btnAddFriend.setOnAction(event -> {
                            service.acceptFriendship(friendship.getId());
                        });
                        Button btnRemove = new Button("");
                        btnRemove.setOnAction(event -> {
                            service.removeFriendship(friendship.getSenderId().toString(), friendship.getReceiverId().toString());
                        });

                        // add images to buttons
                        btnAddFriend.setGraphic(acceptImage);
                        btnRemove.setGraphic(declineImage);
                        btnAddFriend.setStyle("-fx-background-color: transparent;");
                        btnRemove.setStyle("-fx-background-color: transparent;");

                        //add buttons to layout
                        root.getChildren().addAll(btnAddFriend, btnRemove);

                        // Set cell to display the root HBox
                        setText(null);
                        setGraphic(root);

                    }
                    setStyle("-fx-background-color: transparent;");
                }
            };
        });
    }
}
