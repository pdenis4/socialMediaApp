package com.example.fortbyte_conglomerate.entityfactory;

import com.example.fortbyte_conglomerate.domain.*;
import com.example.fortbyte_conglomerate.utils.Triplet;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EntityFactory {
    public static EntityFactory instance = null;
    private EntityFactory(){

    }

    public static EntityFactory getInstance(){
        if(instance == null)
            instance = new EntityFactory();
        return instance;
    }

    public User createUser(Long id, String firstName, String lastName, String mail){
        User user = new User(firstName, lastName, mail);
        //friends is empty by default
        user.setId(id);
        return user;
    }

    public User createUser(ResultSet resultSet){
        try {
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");
            String mail = resultSet.getString("mail");
            Long[] friends = (Long[]) resultSet.getArray("friends").getArray();

            User user = new User(firstName, lastName, mail);
            user.setId(id);
            user.setFriends(friends);

            return user;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Friendship createFriendShip(ResultSet resultSet){
        try {
            Long id = resultSet.getLong("id");
            Long id1 = resultSet.getLong("sender");
            Long id2 = resultSet.getLong("receiver");
            LocalDateTime date = resultSet.getTimestamp("friendssince").toLocalDateTime();
            String status = resultSet.getString("status");

            Friendship friendship = new Friendship(id1, id2);
            friendship.setId(id);
            friendship.setFriendsSince(date);
            friendship.setStatus(FriendshipStatus.valueOf(status));

            return friendship;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Friendship createFriendShip(Long id, Long id1, Long id2){
        Friendship friendship = new Friendship(id1, id2);
        friendship.setId(id);
        //friendsSince is now by default and status is pending
        return friendship;
    }

    public Account createAccount(ResultSet resultSet){
        try {
            Long userId = resultSet.getLong("userId");
            String mail = resultSet.getString("mail");
            String password = resultSet.getString("password");

            return new Account(userId, mail, password);

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Account createAccount(Long userId, String mail, String password){
        return new Account(userId, mail, password);
    }

    public Conversation createConversation(ResultSet resultSet){
        try {
            Long idFirstUser = resultSet.getLong("idfirstuser");
            Long idSecondUser = resultSet.getLong("idseconduser");
            Long[] listSender = (Long[]) resultSet.getArray("messagelistsender").getArray();
            Long[] listReceiver = (Long[]) resultSet.getArray("messagelistreceiver").getArray();
            Long[] listPosition = (Long[]) resultSet.getArray("messagelistposition").getArray();

            List<Triplet<Long, Long, Long>> messagesIds = new ArrayList<>();
            for(int i = 0; i < listSender.length; i++)
                messagesIds.add(new Triplet<>(listSender[i], listReceiver[i], listPosition[i]));

            Conversation conversation = new Conversation(idFirstUser, idSecondUser);
            conversation.setMessagesIds(messagesIds);

            return conversation;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Conversation createConversation(Long idFirstUser, Long idSecondUser){
        return new Conversation(idFirstUser, idSecondUser);
    }
    public Conversation createConversation(Long idFirstUser, Long idSecondUser, List<Triplet<Long, Long, Long>> messages){
        Conversation conversation = new Conversation(idFirstUser, idSecondUser);
        conversation.setMessagesIds(messages);
        return conversation;
    }

    public Message createMessage(Long idSender, Long idReceiver, String content){
        return new Message(idSender, idReceiver, content);
    }

    public Message createMessage(ResultSet resultSet){
        try {
            Long idSender = resultSet.getLong("idsender");
            Long idReceiver = resultSet.getLong("idreceiver");
            String content = resultSet.getString("content");
            Long position = resultSet.getLong("position");

            Message message = new Message(idSender, idReceiver, content);
            message.setPosition(position);

            return message;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}