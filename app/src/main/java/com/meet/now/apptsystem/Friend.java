package com.meet.now.apptsystem;

public class Friend {

    String userID;
    String userPhoto;
    String friendNickname;
    String userNickname;
    private String userStatusmsg;
    String userAddress;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserStatusmsg() {
        return userStatusmsg;
    }

    public void setUserStatusmsg(String userStatusmsg) {
        this.userStatusmsg = userStatusmsg;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Friend(String userID, String userPhoto, String friendNickname, String userNickname, String userStatusmsg, String userAddress) {
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.friendNickname = friendNickname;
        this.userNickname = userNickname;
        this.userStatusmsg = userStatusmsg;
        this.userAddress = userAddress;
    }
}