package com.example.fortbyte_conglomerate.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Friendship extends Entity<Long>{
    private final Long senderId;
    private final Long receiverId;
    private LocalDateTime friendsSince;

    private FriendshipStatus status;

    public Friendship(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        //by default, when created outside of database, the friendship is pending
        this.friendsSince = LocalDateTime.now();
        this.status = FriendshipStatus.PENDING;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public boolean hasId(Long id){
        return senderId.equals(id) || receiverId.equals(id);
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void changeStatus(FriendshipStatus status){
        this.setStatus(status);
        if(getStatus() == FriendshipStatus.ACCEPTED)
            setFriendsSince(LocalDateTime.now());
    }

    public void setFriendsSince(LocalDateTime friendsSince) {
        this.friendsSince = friendsSince;
    }

    public Iterable<Long> getUserIds() {
        List<Long> users = new ArrayList<>();
        users.add(senderId);
        users.add(receiverId);
        return users;
    }

    /***
     * return true if this friendship has the given user
     * @param id - ID of user to be tested for presence
     * @return bool
     */
    public boolean hasUser(Long id){
        return Objects.equals(senderId, id) || Objects.equals(receiverId, id);
    }
    public boolean isBetween(Long id1, Long id2){
        return hasUser(id1) && hasUser(id2);
    }
    public boolean hasSender(Long id){
        return Objects.equals(senderId, id);
    }
    public boolean hasReceiver(Long id){
        return Objects.equals(receiverId, id);
    }

    public Long getOtherUser(Long id){
        if(Objects.equals(senderId, id))
            return receiverId;
        if(Objects.equals(receiverId, id))
            return senderId;
        return null;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "userIds= " + senderId + ", " + receiverId +
                "\n\t\tfriendsSince: " + friendsSince +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(senderId, that.senderId) && Objects.equals(receiverId, that.receiverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderId, receiverId, status);
    }
}
