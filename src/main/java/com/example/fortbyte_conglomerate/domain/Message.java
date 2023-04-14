package com.example.fortbyte_conglomerate.domain;

import java.util.Objects;

public class Message extends Entity<Long> {
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long position;

    public Message(Long senderId, Long receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", position='" + position + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return getSenderId().equals(message.getSenderId()) && getReceiverId().equals(message.getReceiverId()) && getPosition().equals(message.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSenderId(), getReceiverId(), getPosition());
    }
}
