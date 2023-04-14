package com.example.fortbyte_conglomerate.controller;

import com.example.fortbyte_conglomerate.HelloApplication;
import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.observer.Observer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

public class UserController implements Initializable, Observer {
    ObservableList<String> friendsModel = FXCollections.observableArrayList();
    ObservableList<User> allUsersModel = FXCollections.observableArrayList();
    List<User> userFriendList;    //intermediary list
    private SocialNetworkService service;

    @FXML
    private JFXButton logoutButton;
    @FXML
    private JFXButton notificationsButton;

    @FXML
    private Text scene2firstname;
    @FXML
    private Text scene2lastname;
    @FXML
    private Text scene2mail;

    @FXML
    private Text mailBase;
    @FXML
    private Text firstNameBase;
    @FXML
    private Text lastNameBase;

    @FXML
    private TextField friendsSearch;
    @FXML
    private JFXListView<String> friendsList;
    @FXML
    private SearchableComboBox<User> searchableComboBox;

    private Stage stage;
    @FXML
    private AnchorPane subsceneAnchor;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setUtilizatorService(SocialNetworkService service) {
        this.service = service;

        friendsList.setItems(friendsModel);

        //listener to filter friend list view by current text in search field
        friendsSearch.textProperty().addListener((observable, oldValue, newValue) -> friendsModel.setAll(userFriendList.stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .filter(string -> string.contains(newValue))
                .collect(Collectors.toList())
        ));

        //listener to change whose profile to view when a friend is selected
        friendsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            String fullName = friendsList.getSelectionModel().getSelectedItem();

            User user = userFriendList.stream()
                    .filter(u -> (u.getFirstName() + " " + u.getLastName()).equals(fullName))
                    .findFirst()
                    .orElse(null);

            service.setIdProfileUser(user.getId());

        });

        //call handleProfile when any user in listView is clicked
        friendsList.setOnMouseClicked(event -> {
            try {
                handleProfile(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        service.addObserver(this);
        update();
        showCurrentUser();
        initializeComboBox();
    }

    private void initializeComboBox() {
        searchableComboBox.setPromptText("Search for a user");

        //change how selected item is displayed (User -> String)
        searchableComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User object) {
                if (object == null)
                    return null;
                return object.getFirstName() + " " + object.getLastName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        //show profile when clicked
        searchableComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            User user = searchableComboBox.getSelectionModel().getSelectedItem();
            service.setIdProfileUser(user.getId());

            try {
                handleProfile(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //change cellfactory to show only first and last name
        searchableComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getFirstName() + " " + item.getLastName());
                        }
                    }

                };
            }
        });

        searchableComboBox.setStyle("-fx-background-color: #2c0e4f; -fx-text-fill: #faa357; -fx-font-size: 16px;");

        allUsersModel.setAll(StreamSupport.stream(service.getAllUsers().spliterator(), false)
                        //filter out current user
                        .filter(user -> !user.getId().equals(service.getIdCurrentUser()))
                        //filter out friends
                        .filter(user -> !userFriendList.contains(user))
                        .collect(Collectors.toList()));
        searchableComboBox.setItems(allUsersModel);
    }

    private void showModel() {
        friendsModel.setAll(userFriendList.stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList())
        );
    }
    @Override
    public void update() {
        //display friends indepentend of sender and receiver
        userFriendList = StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                .filter(friendship -> friendship.hasUser(service.getIdCurrentUser()))
                .filter(friendship -> friendship.getStatus().name().equals("ACCEPTED"))
                .map(friendship -> service.getOneUser(friendship.getOtherUser(service.getIdCurrentUser()))).collect(Collectors.toList());

        //change combobox users
        allUsersModel.setAll(StreamSupport.stream(service.getAllUsers().spliterator(), false)
                //filter out current user
                .filter(user -> !user.getId().equals(service.getIdCurrentUser()))
                //filter out friends
                .filter(user -> !userFriendList.contains(user))
                .collect(Collectors.toList()));
        showModel();
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        service.logout();
        switchToScene1(event);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void showCurrentUser(){
        Long idUser = service.getIdCurrentUser();
        User user = service.getOneUser(idUser);
        scene2mail.setText(user.getMail());
        scene2mail.setStyle("-fx-text-fill: #c5561a;\n" +
                            "    -fx-font-size: 24px;\n" +
                            "    -fx-font-weight: bold;\n"
                );
        scene2firstname.setText(user.getFirstName());
        scene2firstname.setStyle("-fx-text-fill: #fd823b;\n" +
                                 "    -fx-font-size: 24px;\n" +
                                 "    -fx-font-weight: bold;\n"
                );
        scene2lastname.setText(user.getLastName());
        scene2lastname.setStyle("-fx-text-fill: #ff8136;\n" +
                                "    -fx-font-size: 24px;\n" +
                                "    -fx-font-weight: bold;\n"
                );
        for (Text text : Arrays.asList(mailBase, firstNameBase, lastNameBase)) {
            text.setStyle("-fx-text-fill: #ff752b;\n" +
                          "    -fx-font-size: 16px;\n" +
                          "    -fx-font-weight: bold;\n"
                    );
        }
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/loginView.fxml"));
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        LoginController loginController = loader.getController();
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        loginController.setStage(stage);
        loginController.setUtilizatorService(this.service);
        stage.show();
    }

    public FXMLLoader loadSubscene(String subsceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/" + subsceneName + ".fxml"));
        AnchorPane subscene = loader.load();

        subscene.setPrefWidth(subsceneAnchor.getPrefWidth());
        subscene.setPrefHeight(subsceneAnchor.getPrefHeight());
        subscene.setMaxHeight(subsceneAnchor.getMaxHeight());
        subscene.setMaxWidth(subsceneAnchor.getMaxWidth());
        subscene.setMinHeight(subsceneAnchor.getMinHeight());
        subscene.setMinWidth(subsceneAnchor.getMinWidth());

        subsceneAnchor.getChildren().setAll(subscene);

        return loader;
    }

    @FXML
    public void handleNotifications(ActionEvent event) throws IOException {
        NotificationsController notificationsController = loadSubscene("notifications").getController();
        notificationsController.setService(this.service);

    }

    @FXML
    public void handleProfile(MouseEvent event) throws IOException {
        if (service.getIdProfileUser() == null)
            return;
        ProfileController profileController = loadSubscene("userProfile").getController();
        profileController.setService(this.service, this);
    }

    void openMessenger(ActionEvent event) throws IOException {
        MessagerController messagerController = loadSubscene("messagingView").getController();
        messagerController.setService(this.service);
    }
}
