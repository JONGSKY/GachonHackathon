package com.meet.now.apptsystem;

public class MapApptfriend {

    String friendID, friendNickname, userNickname, friendAddress;

    public MapApptfriend(String friendID, String friendNickname, String userNickname, String friendAddress) {
        this.friendID = friendID;
        this.friendNickname = friendNickname;
        this.userNickname = userNickname;
        this.friendAddress = friendAddress;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
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

    public String getFriendAddress() {
        return friendAddress;
    }

    public void setFriendAddress(String friendAddress) {
        this.friendAddress = friendAddress;
    }
}