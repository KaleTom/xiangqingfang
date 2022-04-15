package com.zg.xqf.entity;


import android.util.Log;

public class Msg extends BaseEntity {
    private final static String TAG = "Msg";
    public String fromUserId;
    public int msgType;


    public Msg(int msgType, String fromUserId) {
        this.msgType = msgType;
        this.fromUserId = fromUserId;
    }


    public static Msg parseMsg(String fromUserId, String json) {
        Msg baseMsg = gson.fromJson(json, Msg.class);
        Msg msg = null;
        switch (baseMsg.msgType) {
            case MsgType.MSG_REQ_DISCONN:
            case MsgType.MSG_REQ_CONN: {
                msg = gson.fromJson(json, MsgConn.class);
                break;
            }

            case MsgType.MSG_CMD_ROOM_INFO: {
                msg = gson.fromJson(json, MsgRoomInfo.class);
                break;
            }
            case MsgType.MSG_CHG_SPEECH_STATE:{
                msg = gson.fromJson(json, MsgSpeechState.class);
                break;
            }
            default: {
                Log.e(TAG, "未知的消息类型：" + baseMsg.msgType);
                msg = baseMsg;
            }
        }
        if (msg != null) msg.fromUserId = fromUserId;
        return msg;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }


}
