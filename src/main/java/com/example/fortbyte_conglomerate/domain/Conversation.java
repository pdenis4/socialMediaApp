package com.example.fortbyte_conglomerate.domain;

import com.example.fortbyte_conglomerate.utils.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Conversation extends Entity<Long> {
    private Long firstUserId;
    private Long secondUserId;
    private List<Triplet<Long, Long, Long>> messagesIds;

    public Conversation(Long firstUserId, Long secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.messagesIds = new ArrayList<>();
    }

    public Long getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(Long firstUserId) {
        this.firstUserId = firstUserId;
    }

    public Long getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(Long secondUserId) {
        this.secondUserId = secondUserId;
    }

    public List<Triplet<Long, Long, Long>> getMessagesIds() {
        return messagesIds;
    }

    public void setMessagesIds(List<Triplet<Long, Long, Long>> messagesIds) {
        this.messagesIds = messagesIds;
    }

    public void addMessage(Long senderId, Long receiverId, Long position){
        messagesIds.add(new Triplet<>(senderId, receiverId, position));
    }

    public void removeMessage(Long senderId, Long receiverId, Long position){
        messagesIds.remove(new Triplet<>(senderId, receiverId, position));
    }

    public boolean hasId(Long id){
        return firstUserId.equals(id) || secondUserId.equals(id);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "firstUserId=" + firstUserId +
                ", secondUserId=" + secondUserId +
                ", messagesIds=" + messagesIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conversation that)) return false;
        return getFirstUserId().equals(that.getFirstUserId()) && getSecondUserId().equals(that.getSecondUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstUserId(), getSecondUserId());
    }
}