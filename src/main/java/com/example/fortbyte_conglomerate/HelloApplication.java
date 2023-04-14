package com.example.fortbyte_conglomerate;

import com.example.fortbyte_conglomerate.controller.LoginController;
import com.example.fortbyte_conglomerate.controller.UserController;
import com.example.fortbyte_conglomerate.domain.Conversation;
import com.example.fortbyte_conglomerate.domain.validators.Validator;
import com.example.fortbyte_conglomerate.domain.validators.strategies.ValidateAccount;
import com.example.fortbyte_conglomerate.domain.validators.strategies.ValidateFriendship;
import com.example.fortbyte_conglomerate.domain.validators.strategies.ValidateUser;
import com.example.fortbyte_conglomerate.repository.dbrepository.AccountDBRepository;
import com.example.fortbyte_conglomerate.repository.dbrepository.ConversationDBRepository;
import com.example.fortbyte_conglomerate.repository.dbrepository.FriendshipDBRepository;
import com.example.fortbyte_conglomerate.repository.dbrepository.UserDBRepository;
import com.example.fortbyte_conglomerate.services.SocialNetworkService;
import com.example.fortbyte_conglomerate.utils.Triplet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {
    private SocialNetworkService service;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        System.out.println("Reading data from file");
        String url = "jdbc:postgresql://localhost:5432/academic";
        UserDBRepository userDBRepository = new UserDBRepository(url, "postgres", "postgres",
                new Validator(new ValidateUser()));
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(url, "postgres",
                "postgres", new Validator(new ValidateFriendship()));
        AccountDBRepository accountRepository = new AccountDBRepository(url, "postgres", "postgres",
                new Validator(new ValidateAccount()));
        ConversationDBRepository conversationDBRepository = new ConversationDBRepository(url, "postgres",
                "postgres");

        service = new SocialNetworkService(userDBRepository, friendshipDBRepository,
                accountRepository, conversationDBRepository);

        initView(primaryStage);
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/loginView.fxml"));

        Parent root = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        Scene scene = new Scene(root);

        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);

        loginController.setStage(primaryStage);

        loginController.setUtilizatorService(service);
        primaryStage.show();

    }
}