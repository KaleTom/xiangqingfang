package com.zg.xqf.entity;

/**
 * 连麦消息
 */
public class MsgConn extends Msg {
    public int pos;
    public UserInfo user;

    public MsgConn(int msgType, String fromUserId, int pos, UserInfo user) {
        super(msgType, fromUserId);
        this.pos = pos;
        this.user = user;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
