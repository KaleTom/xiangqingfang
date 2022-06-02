package com.zg.xqf.entity;

/**
 * 连麦消息
 */
public class MsgSpeechState extends Msg {
    public boolean isMuted;

    public MsgSpeechState(int msgType, String fromUserId, boolean isMuted) {
        super(msgType, fromUserId);
        this.isMuted = isMuted;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
}
