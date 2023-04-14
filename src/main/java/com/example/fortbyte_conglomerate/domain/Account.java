package com.example.fortbyte_conglomerate.domain;

public class Account {
    Long userId;
    String mail;
    String password;

    public Account(Long userId, String mail, String password) {
        this.userId = userId;
        this.mail = mail;
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString(){
        return "Account{" +
                "userId=" + userId +
                ", mail='" + mail + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account that)) return false;
        return getUserId().equals(that.getUserId()) &&
                getMail().equals(that.getMail());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getUserId(), getMail());
    }
}
