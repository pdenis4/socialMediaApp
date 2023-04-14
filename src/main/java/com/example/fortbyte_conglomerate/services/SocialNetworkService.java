package com.example.fortbyte_conglomerate.services;

import com.example.fortbyte_conglomerate.domain.*;
import com.example.fortbyte_conglomerate.entityfactory.EntityFactory;
import com.example.fortbyte_conglomerate.exceptions.CredentialsException;
import com.example.fortbyte_conglomerate.exceptions.DuplicateException;
import com.example.fortbyte_conglomerate.exceptions.EmptyRepositoryException;
import com.example.fortbyte_conglomerate.exceptions.IdNotFoundException;
import com.example.fortbyte_conglomerate.repository.Repository;
import com.example.fortbyte_conglomerate.repository.dbrepository.AccountDBRepository;
import com.example.fortbyte_conglomerate.repository.dbrepository.ConversationDBRepository;
import com.example.fortbyte_conglomerate.utils.observer.Observable;
import com.example.fortbyte_conglomerate.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SocialNetworkService implements Observable {
    private final Repository<Long, User> userRepository;
    private final Repository<Long, Friendship> friendshipRepository;
    private final AccountDBRepository accountRepository;

    private final ConversationDBRepository conversationRepository;
    private Long idCurrentUser;
    private Long idProfileUser;

    private Long lastUsedId = 0L;

    private final List<Observer> observers = new ArrayList<>();

    public SocialNetworkService(Repository<Long, User> userRepository, Repository<Long,
            Friendship> friendshipRepository, AccountDBRepository accountRepository,
                                ConversationDBRepository conversationRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.accountRepository = accountRepository;
        this.conversationRepository = conversationRepository;
        idCurrentUser = null;
    }

    public Long getIdProfileUser() {
        return idProfileUser;
    }
    public void setIdProfileUser(Long idProfileUser) {
        this.idProfileUser = idProfileUser;
    }
    public Long getIdCurrentUser(){
        return idCurrentUser;
    }
    public void setIdCurrentUser(Long id){
        idCurrentUser = id;
    }
    private Repository<Long, User> getUserRepository() {
        return userRepository;
    }

    private Repository<Long, Friendship> getFriendshipRepository() {
        return friendshipRepository;
    }

    private AccountDBRepository getAccountRepository() {
        return accountRepository;
    }
    public ConversationDBRepository getConversationRepository() {
        return conversationRepository;
    }

    public Iterable<User> getAllUsers(){
        return getUserRepository().findAll();
    }


    public Iterable<Friendship> getAllFriendships(){
        return getFriendshipRepository().findAll();
    }

    public Iterable<Account> getAllAccounts(){
        return getAccountRepository().findAll();
    }

    public Iterable<Conversation> getAllConversations(){
        return getConversationRepository().findAll();
    }

    public User getOneUser(Long id){
        return getUserRepository().findOne(id);
    }

    public Integer sizeOfUsers(){
        return getUserRepository().size();
    }

    public Integer sizeOfFriendships(){
        return getFriendshipRepository().size();
    }

    private Friendship getOneFriendship(Long id){
        return getFriendshipRepository().findOne(id);
    }

    public void acceptFriendship(Long id){
        Friendship friendship = getOneFriendship(id);
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        getFriendshipRepository().update(friendship);
        notifyObservers();
    }

    public void acceptFriendship(Long idSender, Long idReceiver){
        StreamSupport.stream(getAllFriendships().spliterator(), false)
                .filter(friendship -> friendship.getSenderId().equals(idSender) && friendship.getReceiverId().equals(idReceiver)
                    && friendship.getStatus().equals(FriendshipStatus.PENDING))
                .findAny()
                .ifPresent(friendship -> {
                    friendship.setStatus(FriendshipStatus.ACCEPTED);
                    getFriendshipRepository().update(friendship);
                    notifyObservers();
                });

    }

    /**
     * Creates a user with the given string and adds it to the user repository
     * @param firstName String - firstName field of user
     * @param lastName String - lastName field of user
     * @param id String - id field of entity
     */
    public void addUser(String firstName, String lastName, String mail, String id){
        try {
            Long idL = Long.parseLong(id);
            if(userIds().contains(idL) || friendshipIds().contains(idL))
                throw new DuplicateException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            User user = entityFactory.createUser(idL, firstName, lastName, mail);

            userRepository.save(user);

            notifyObservers();
        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch (DuplicateException e){
            System.out.println("ID is already used!\n");
        }


    }

    /**
     * Creates a friendship with the given string and adds it to the friendship repository
     * @param sender String - id of the user who sent the request
     * @param receiver String - id of the other user who received the request
     */
    public void addFriendship(String sender, String receiver){
        try {
            Long idL1 = Long.parseLong(sender);
            Long idL2 = Long.parseLong(receiver);
            Long idLfr = getNextFreeId();

            //duplicate friendship
            getFriendshipRepository().findAll().forEach(friendship ->{
                if((friendship.hasId(idL1) && friendship.hasId(idL2)))
                    throw new IllegalArgumentException();
            });

            User user1 = getOneUser(idL1);
            User user2 = getOneUser(idL2);
            user1.addFriend(idL2);
            user2.addFriend(idL1);

            EntityFactory entityFactory = EntityFactory.getInstance();
            Friendship friendship = entityFactory.createFriendShip(idLfr, idL1, idL2);

            getFriendshipRepository().save(friendship);

            notifyObservers();
        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch (EmptyRepositoryException e){
            System.out.println("Not enough entites!\n");
        }catch (DuplicateException e){
            System.out.println("Duplicate ID !\n");
        }catch (IllegalArgumentException e){
            System.out.println("Users already friends!\n");
        }catch (IdNotFoundException e){
            System.out.println("ID not found in users!");
        }
    }

    /**
     * Removes the user with the given id from the user repository
     * @param idStr String - id of the given user to be deleted
     * @return User - the user that has been deleted
     */
    public User removeUser(String idStr){
        try{
            Long id = Long.parseLong(idStr);
            User userToDel = getUserRepository().findOne(id);

            //stergere din prietenii
            //refactor?
            for(Friendship friendship : getAllFriendships())
                if(friendship.hasUser(userToDel.getId()))
                    getFriendshipRepository().delete(friendship.getId());

            User deletedUser = getUserRepository().delete(id);
            notifyObservers();
            return deletedUser;

        }catch(NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch(EmptyRepositoryException e){
            System.out.println("Not enough users\n\n");
        }catch (IdNotFoundException e){
            System.out.println("ID non existent!\n\n");
        }
        return null;
    }

    /**
     * Removes the friendship with the given id from the friendship repository
     * @param idSender String - id of the friendship to be deleted
     * @param idReceiver String - id of the friendship to be deleted
     * @return Friendship - the deleted friendship
     */
    public Friendship removeFriendship(String idSender, String idReceiver){
        try {
            Long idSenderL = Long.parseLong(idSender);
            Long idReceiverL = Long.parseLong(idReceiver);

            Friendship friendshipToDel = StreamSupport.stream(getFriendshipRepository().findAll().spliterator(), false)
                    .filter(friendship -> friendship.hasId(idSenderL) && friendship.hasId(idReceiverL))
                    .findFirst()
                    .orElseThrow(IdNotFoundException::new);

            Friendship removedFriendship = getFriendshipRepository().delete(friendshipToDel.getId());

            //remove conversation between the two users
            getConversationRepository().findAll().forEach(conversation -> {
                if(conversation.hasId(idSenderL) && conversation.hasId(idReceiverL))
                    getConversationRepository().deleteConversation(idSenderL, idReceiverL);
            });

            notifyObservers();
            return removedFriendship;

        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch (EmptyRepositoryException e) {
            System.out.println("Not enough friendships!\n\n");
        }catch (IdNotFoundException e){
            System.out.println("ID non existent!\n\n");
        }
        return null;
    }

    /**
     * Gives a vector with all the user ids
     * @return Vector/Long\
     */
    public List<Long> userIds(){
        return StreamSupport.stream(getUserRepository().findAll().spliterator(), false)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    /**
     * Gives a vector with all the friendships ids
     * @return Vector/Long\
     */
    public List<Long> friendshipIds(){
        return StreamSupport.stream(getFriendshipRepository().findAll().spliterator(), false)
                .map(Friendship::getId)
                .collect(Collectors.toList());
    }

    public void updateUser(String firstName, String lastName, String mail, String id){
        try {
            Long idL = Long.parseLong(id);
            if(friendshipIds().contains(idL))
                throw new DuplicateException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            User user = entityFactory.createUser(idL, firstName, lastName, mail);

            getUserRepository().update(user);

        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch (DuplicateException e){
            System.out.println("ID is already used in friendships!\n");
        }
    }

    private Long getNextFreeId(){
        while(userIds().contains(lastUsedId) || friendshipIds().contains(lastUsedId))
            lastUsedId += 47L;
        return lastUsedId;
    }

    public void addAccount(String mail, String password, String firstName, String lastName){
        try {
            Long idLUser = getNextFreeId();

            EntityFactory entityFactory = EntityFactory.getInstance();
            User user = entityFactory.createUser(idLUser, firstName, lastName, mail);
            Account account = entityFactory.createAccount(idLUser, mail, password);

            StreamSupport.stream(getAllAccounts().spliterator(), false)
                    .filter(acc -> acc.getMail().equals(mail))
                    .findAny()
                    .ifPresent(acc -> {
                        throw new DuplicateException("Mail already used!");
                    });
            getAccountRepository().save(account);
            getUserRepository().save(user);
        }catch (IdNotFoundException e){
            System.out.println("ID not found in users!");
        }

        notifyObservers();
    }

    public List<User> getFriendsOfUser(Long id) {
        return getOneUser(id).getFriendsIds().stream()
                .map(this::getOneUser)
                .collect(Collectors.toList());
    }

    public void addConversation(String id1, String id2){
        try {
            Long idL1 = Long.parseLong(id1);
            Long idL2 = Long.parseLong(id2);

            if(!userIds().contains(idL1) || !userIds().contains(idL2))
                throw new IdNotFoundException();

            EntityFactory entityFactory = EntityFactory.getInstance();
            Conversation conversation = entityFactory.createConversation(idL1, idL2);

            getConversationRepository().save(conversation);
        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }catch (DuplicateException e){
            System.out.println("ID already used!\n");
        }catch (IdNotFoundException e){
            System.out.println("ID not found in users!\n");
        }
    }

    public boolean isConversationBetween(Long id1, Long id2){
        return StreamSupport.stream(getConversationRepository().findAll().spliterator(), false)
                .anyMatch(conversation -> conversation.hasId(id1) && conversation.hasId(id2));
    }

    public boolean areFriends(Long id1, Long id2){
        return StreamSupport.stream(getFriendshipRepository().findAll().spliterator(), false)
                .anyMatch(friendship -> friendship.hasId(id1) && friendship.hasId(id2) && friendship.getStatus().name().equals("ACCEPTED"));
    }

    public Conversation getConversationBetween(Long id1, Long id2){
        return StreamSupport.stream(getConversationRepository().findAll().spliterator(), false)
                .filter(conversation -> conversation.hasId(id1) && conversation.hasId(id2))
                .findFirst()
                .orElseThrow(IdNotFoundException::new);
    }

    public void addMessage(String idSender, String idReceiver, String message){
        try {
            Long idSenderL = Long.parseLong(idSender);
            Long idReceiverL = Long.parseLong(idReceiver);

            EntityFactory entityFactory = EntityFactory.getInstance();
            Message message1 = entityFactory.createMessage(idSenderL, idReceiverL, message);

            getConversationRepository().addMessage(idSenderL, idReceiverL, message1);
        }catch (NumberFormatException e){
            System.out.println("[[ID] is not a number!]\n\n");
        }

        notifyObservers();
    }

    public List<Message> getMessagesFromUsers(Long idFirstUser, Long idSecondUser){
        return StreamSupport.stream(getConversationRepository().getMessages(idFirstUser, idSecondUser).spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean login(String mail, String password){
        User foundMailUser = StreamSupport.stream(getUserRepository().findAll().spliterator(), false)
                .filter(user -> user.getMail().equals(mail))
                .findFirst()
                .orElseThrow(() -> new CredentialsException("Mail not found!"));

        Long foundId = foundMailUser.getId();

        Account foundMailAccount = getAccountRepository().findOne(foundId);

        if(!Objects.equals(foundMailAccount.getPassword(), password))
            throw new CredentialsException("Wrong password!");

        //when no login errors
        setIdCurrentUser(foundId);
        return true;
    }

    public void logout(){
        setIdCurrentUser(null);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }
}
