package com.example.fortbyte_conglomerate.repository.dbrepository;

import com.example.fortbyte_conglomerate.domain.Account;
import com.example.fortbyte_conglomerate.domain.User;
import com.example.fortbyte_conglomerate.domain.validators.Validator;
import com.example.fortbyte_conglomerate.entityfactory.EntityFactory;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.exceptions.ValidationException;
import com.example.fortbyte_conglomerate.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AccountDBRepository {
    private final String url;
    private final String username;
    private final String password;

    private final Validator validator;

    public AccountDBRepository(String url, String username, String password, Validator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public Account findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE userid=?")
        ){
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException("Account with credentials not found");

            EntityFactory entityFactory = EntityFactory.getInstance();
            Account account = entityFactory.createAccount(resultSet);

            validator.validate(account);

            return account;
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Iterable<Account> findAll() {
        Set<Account> accounts = new HashSet<>();
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("select * from accounts");
             ResultSet resultSet = ps.executeQuery()
        ){
            while (resultSet.next()){
                EntityFactory entityFactory = EntityFactory.getInstance();
                Account account = entityFactory.createAccount(resultSet);
                accounts.add(account);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    public Account save(Account account) {
        String sql = "insert into accounts (userid, mail, password) values (?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)
        ){
            validator.validate(account);
            ps.setLong(1, account.getUserId());
            ps.setString(2, account.getMail());
            ps.setString(3, account.getPassword());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return account;
    }

    public Account delete(Long id) {
        String sqlDel = "DELETE FROM accounts WHERE userid=? RETURNING *";
        try( Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sqlDel)

        ){
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next())
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            return entityFactory.createAccount(resultSet);
        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Account update(Account account) {
        Long id = account.getUserId();
        String mail = account.getMail();
        String acc_password = account.getPassword();

        String sql = "insert into accounts (userid, mail, password) values (?,?,?) ON CONFLICT (userid)" +
                "DO UPDATE SET mail = ?, password = ? WHERE accounts.userid = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)
        ){
            ps.setLong(1, id);
            ps.setLong(6, id);
            ps.setString(2, mail);
            ps.setString(4, mail);
            ps.setString(3, acc_password);
            ps.setString(5, acc_password);

            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return account;
    }

    public int size(){
        String sql = "Select COUNT(*) from accounts";
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
