package com.meet.now.apptsystem;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MapApptfriend {

    String friendID, friendNickname, userNickname, friendAddress;
    PointF point;

    public MapApptfriend(String friendID, String friendNickname, String userNickname, String friendAddress, PointF point) {
        this.friendID = friendID;
        this.friendNickname = friendNickname;
        this.userNickname = userNickname;
        this.friendAddress = friendAddress;
        this.point = point;
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

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

}