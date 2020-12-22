package io.antmedia.android;

public class Message {
    private String userID, email, fullName, roomID, avartar, messageContent;

    public  Message()
    {}

    public Message(String userID, String email, String fullName, String roomID, String avartar, String messageContent) {
        this.userID = userID;
        this.email = email;
        this.fullName = fullName;
        this.roomID = roomID;
        this.avartar = avartar;
        this.messageContent = messageContent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}