package com.example.fortbyte_conglomerate.repository.dbrepository;


import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.domain.validators.Validator;
import com.example.fortbyte_conglomerate.entityfactory.EntityFactory;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;

    private final Validator validator;

    public UserDBRepository(String url, String username, String password, Validator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long aLong) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id=?")
        ){
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException("User with id not found");

            EntityFactory entityFactory = EntityFactory.getInstance();
            User user = entityFactory.createUser(resultSet);

            validator.validate(user);

            return user;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("select * from users");
             ResultSet resultSet = ps.executeQuery()
        ){
            while (resultSet.next()){
                EntityFactory entityFactory = EntityFactory.getInstance();
                User user = entityFactory.createUser(resultSet);
                users.add(user);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User user) {
        String sql = "insert into users (id, firstname, lastname, mail, friends) values (?,?,?,?,array[]::bigint[])";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)
        ){
            validator.validate(user);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getMail());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User delete(Long aLong) {
        String sqlDel = "DELETE FROM users WHERE id=? RETURNING *";
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sqlDel)

        ){
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            return entityFactory.createUser(resultSet);
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User update(User entity) {
        Long id = entity.getId();
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String mail = entity.getMail();
        String sql = "insert into users (id, firstname, lastname, mail, friends) values (?,?,?,?,array[]::bigint[]) ON CONFLICT (id)" +
                "DO UPDATE SET firstname = ?, lastname = ?, mail = ? WHERE users.id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)
        ){
            ps.setLong(1, id);
            ps.setLong(8, id);
            ps.setString(2, firstName);
            ps.setString(5, firstName);
            ps.setString(3, lastName);
            ps.setString(6, lastName);
            ps.setString(4, mail);
            ps.setString(7, mail);

            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public int size(){
        String sql = "Select COUNT(*) from users";
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
