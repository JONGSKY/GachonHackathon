package com.meet.now.apptsystem;

import android.util.Log;

import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

class SetCenterPlace {
    private List<MapApptfriend> mapApptfriendList;

    SetCenterPlace(List<MapApptfriend> mapApptfriendList) {
        this.mapApptfriendList = mapApptfriendList;
    }

    public NGeoPoint CenterPlace(){
        NGeoPoint newMiddlePoint = new NGeoPoint();
        double MaxX = mapApptfriendList.get(0).getPoint().x;
        double MaxY = mapApptfriendList.get(0).getPoint().y;

        double MinX = mapApptfriendList.get(0).getPoint().x;
        double MinY = mapApptfriendList.get(0).getPoint().y;

        for(int i=0; i<mapApptfriendList.size(); i++) {
            MapApptfriend mapApptfriend = mapApptfriendList.get(i);

            double coordX = mapApptfriend.getPoint().x;
            double coordY = mapApptfriend.getPoint().y;

            if(MaxX+MaxY < coordX+coordY){
                MaxX = coordX; MaxY = coordY;
            }

            if(MinX+MinY > coordX+coordY){
                MinX = coordX; MinY = coordY;
            }
        }

        newMiddlePoint.latitude = (MaxX+MinX)/2.0;
        newMiddlePoint.longitude = (MaxY+MinY)/2.0;


        return newMiddlePoint;
    }
}
