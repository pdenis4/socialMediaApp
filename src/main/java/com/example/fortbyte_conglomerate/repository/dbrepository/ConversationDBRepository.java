package com.example.fortbyte_conglomerate.repository.dbrepository;

import com.example.fortbyte_conglomerate.domain.Conversation;
import com.example.fortbyte_conglomerate.domain.Friendship;
import com.example.fortbyte_conglomerate.domain.Message;
import com.example.fortbyte_conglomerate.domain.validators.Validator;
import com.example.fortbyte_conglomerate.entityfactory.EntityFactory;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.repository.Repository;
import com.example.fortbyte_conglomerate.utils.Triplet;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConversationDBRepository implements Repository<Long, Conversation> {
    private final String url;
    private final String username;
    private final String password;

    public ConversationDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Conversation findOne(Long idUser) {
        return null;
    }

    public Conversation findConversation(Long idFirstUser, Long idSecondUser) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM conversations WHERE idfirstuser=? and idseconduser=?")
        ) {
            ps.setLong(1, idFirstUser);
            ps.setLong(2, idSecondUser);

            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next())
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();

            return entityFactory.createConversation(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Conversation> findAll() {
        Set<Conversation> conversations = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("select * from conversations");
             ResultSet resultSet = ps.executeQuery()
        ) {
            EntityFactory entityFactory = EntityFactory.getInstance();
            while (resultSet.next()) {
                Conversation conversation = entityFactory.createConversation(resultSet);
                conversations.add(conversation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }

    @Override
    public Conversation save(Conversation conversation) {
        String sql = "insert into conversations (idfirstuser, idseconduser) " +
                "values (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setLong(1, conversation.getFirstUserId());
            ps.setLong(2, conversation.getSecondUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversation;
    }

    @Override
    public Conversation delete(Long aLong) {
        return null;
    }

    public Conversation deleteConversation(Long idFirstUser, Long idSecondUser) {
        String sql = "delete from conversations where (idfirstuser=? and idseconduser=?) or (idfirstuser=? and idseconduser=?)";
        String sqlMessages = "delete from messages where (idsender=? and idreceiver=?) or (idsender=? and idreceiver=?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement psMessages = connection.prepareStatement(sqlMessages);
        ) {
            ps.setLong(1, idFirstUser);
            ps.setLong(2, idSecondUser);
            ps.setLong(3, idSecondUser);
            ps.setLong(4, idFirstUser);
            ps.executeUpdate();

            psMessages.setLong(1, idFirstUser);
            psMessages.setLong(2, idSecondUser);
            psMessages.setLong(3, idSecondUser);
            psMessages.setLong(4, idFirstUser);
            psMessages.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Conversation update(Conversation conversation) {
        return null;
    }

    @Override
    public int size() {
        String sql = "Select COUNT(*) from conversations";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            rs.next();
            return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addMessage(Long idSender, Long idReceiver, Message message) {
        String sql = "update conversations set messagelistsender = array_append(messagelistsender, ?)," +
                "messagelistreceiver = array_append(messagelistreceiver, ?)," +
                "messagelistposition = array_append(messagelistposition, ?) where (idfirstuser=? and idseconduser=?) or (idseconduser=? and idfirstuser=?)";

        String sqlMessage = "insert into messages (idsender, idreceiver, content, position) " +
                "values (?,?,?,?)";
        String sqlMaxPos = "SELECT MAX(position) FROM messages WHERE (idSender = ? AND idReceiver = ?) or (idreceiver = ? and idsender = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement psMessage = connection.prepareStatement(sqlMessage);
             PreparedStatement psMaxPos = connection.prepareStatement(sqlMaxPos);
        ) {
            //determine message position in the conversation
            psMaxPos.setLong(1, message.getSenderId());
            psMaxPos.setLong(2, message.getReceiverId());
            psMaxPos.setLong(3, message.getSenderId());
            psMaxPos.setLong(4, message.getReceiverId());
            ResultSet rs = psMaxPos.executeQuery();
            rs.next();
            int maxPos = rs.getInt("max");
            message.setPosition(maxPos + 1L);

            ps.setLong(1, message.getSenderId());
            ps.setLong(2, message.getReceiverId());
            ps.setLong(3, message.getPosition());
            ps.setLong(4, idSender);
            ps.setLong(5, idReceiver);
            ps.setLong(6, idSender);
            ps.setLong(7, idReceiver);
            ps.executeUpdate();

            psMessage.setLong(1, message.getSenderId());
            psMessage.setLong(2, message.getReceiverId());
            psMessage.setString(3, message.getContent());
            psMessage.setLong(4, message.getPosition());
            psMessage.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Iterable<Message> getMessages(Long idFirstUser, Long idSecondUser){
        String sql = "select * from messages where (idsender=? and idreceiver=?) or (idreceiver=? and idsender=?) " +
                "order by position";
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setLong(1, idFirstUser);
            ps.setLong(2, idSecondUser);
            ps.setLong(3, idFirstUser);
            ps.setLong(4, idSecondUser);
            ResultSet resultSet = ps.executeQuery();
            EntityFactory entityFactory = EntityFactory.getInstance();
            while (resultSet.next()) {
                Message message = entityFactory.createMessage(resultSet);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}