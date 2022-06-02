package com.zg.xqf.entity;

public class MsgRoomInfo extends Msg {
    public String roomName;
    public String masterId;
    public UserInfo[] connedUsers;


    public MsgRoomInfo(int msgType, String fromUserId, String roomName, String masterId, UserInfo[] connedUsers) {
        super(msgType, fromUserId);
        this.roomName = roomName;
        this.masterId = masterId;
        this.connedUsers = connedUsers;
    }
    @Override
    public String toString() {
        return gson.toJson(this);
    }
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public UserInfo[] getConnedUsers() {
        return connedUsers;
    }

    public void setConnedUsers(UserInfo[] connedUsers) {
        this.connedUsers = connedUsers;
    }
}
