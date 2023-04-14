package com.example.fortbyte_conglomerate.repository.dbrepository;

import com.example.fortbyte_conglomerate.domain.Friendship;
import com.example.fortbyte_conglomerate.domain.validators.Validator;
import com.example.fortbyte_conglomerate.entityfactory.EntityFactory;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Long, Friendship> {
    private final String url;
    private final String username;
    private final String password;

    private final Validator validator;

    public FriendshipDBRepository(String url, String username, String password, Validator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Long aLong) {

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM friendships WHERE id=?")
        ){
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();

            return entityFactory.createFriendShip(resultSet);
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = ps.executeQuery()
        ){
            while (resultSet.next()){
                EntityFactory entityFactory = EntityFactory.getInstance();
                Friendship friendship = entityFactory.createFriendShip(resultSet);

                friendships.add(friendship);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship friendship) {
        String sql = "insert into friendships (id, sender, receiver, friendssince, status) values (?,?,?,?,?)";
        String sqlFriends = "UPDATE users SET friends = array_append(friends, ?) WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement psUser1 = connection.prepareStatement(sqlFriends);
            PreparedStatement psUser2 = connection.prepareStatement(sqlFriends)
        ){
            validator.validate(friendship);
            ps.setLong(1, friendship.getId());
            ps.setLong(2, friendship.getSenderId());
            ps.setLong(3, friendship.getReceiverId());
            ps.setTimestamp(4, Timestamp.valueOf(friendship.getFriendsSince()));
            ps.setString(5, friendship.getStatus().name());
            ps.executeUpdate();


            psUser1.setLong(1, friendship.getReceiverId());
            psUser1.setLong(2, friendship.getSenderId());
            psUser2.setLong(1, friendship.getSenderId());
            psUser2.setLong(2, friendship.getReceiverId());

            psUser1.executeUpdate();
            psUser2.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Friendship delete(Long aLong) {
        String sqlDel = "DELETE FROM friendships WHERE id=? RETURNING *";
        String sqlFriends = "UPDATE users SET friends = array_remove(friends, ?) WHERE id = ?";
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sqlDel);
             PreparedStatement psUser1 = connection.prepareStatement(sqlFriends);
             PreparedStatement psUser2 = connection.prepareStatement(sqlFriends)
        ){
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            Friendship friendship = entityFactory.createFriendShip(resultSet);

            psUser1.setLong(1, friendship.getReceiverId());
            psUser1.setLong(2, friendship.getSenderId());
            psUser2.setLong(1, friendship.getSenderId());
            psUser2.setLong(2, friendship.getReceiverId());

            psUser1.executeUpdate();
            psUser2.executeUpdate();

            return friendship;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Friendship update(Friendship friendship) {
        Long id = friendship.getId();
        Long id1 = friendship.getSenderId();
        Long id2 = friendship.getReceiverId();
        Timestamp timestamp = Timestamp.valueOf(friendship.getFriendsSince()) ;
        String status = friendship.getStatus().name();

        String sql = "insert into friendships (id, sender, receiver, friendssince, status) values (?,?,?,?,?) ON CONFLICT (id)" +
                "DO UPDATE SET friendssince = ?, status = ? WHERE friendships.id = ?";
        String sqlFriends = "UPDATE users SET friends = (select array_agg(distinct e) from unnest(friends || ?) e) WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement psUser1 = connection.prepareStatement(sqlFriends);
            PreparedStatement psUser2 = connection.prepareStatement(sqlFriends)
        ){
            ps.setLong(1, id);
            ps.setLong(8, id);
            ps.setLong(2, id1);
            ps.setLong(3, id2);
            ps.setTimestamp(4, timestamp);
            ps.setTimestamp(6, timestamp);
            ps.setString(7, status);
            ps.setString(5, friendship.getStatus().toString());

            ps.executeUpdate();

            psUser1.setLong(1, friendship.getReceiverId());
            psUser1.setLong(2, friendship.getSenderId());
            psUser2.setLong(1, friendship.getSenderId());
            psUser2.setLong(2, friendship.getReceiverId());

            psUser1.executeUpdate();
            psUser2.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return friendship;
    }

    @Override
    public int size(){
        String sql = "Select COUNT(*) from friendships";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ){
            rs.next();
            return rs.getInt("count");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
}
