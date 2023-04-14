package com.example.fortbyte_conglomerate.domain;

import java.util.*;

public class User extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    private final String mail;
    private final List<Long> friends;

    public User(String firstName, String lastName, String mail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.friends = new Vector<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMail() {
        return mail;
    }

    public List<Long> getFriendsIds() {
        return friends;
    }

    public void addFriend(Long idUser){
        friends.add(idUser);
    }

    public void removeFriend(Long idUser){
        friends.remove(idUser);
    }

    public void setFriends(Long[] friends){
        this.friends.clear();
        this.friends.addAll(Arrays.asList(friends));
    }

    public void setFriends(Collection<Long> friends){
        this.friends.clear();
        this.friends.addAll(friends);
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends +
                ", mail=" + mail +
                '}' + "\t\t";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getMail(), getId());
    }
}