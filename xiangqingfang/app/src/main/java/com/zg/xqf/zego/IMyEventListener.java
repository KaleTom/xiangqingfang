package com.zg.xqf.zego;

import com.zg.xqf.entity.Msg;

import java.util.ArrayList;

import im.zego.zegoexpress.entity.ZegoUser;

public interface IMyEventListener {


    void onRcvMsg(Msg msg);

    void onAddUser(ArrayList<ZegoUser> userList);

    void onDelUser(ArrayList<ZegoUser> userList);

//    void onRoomUserUpdate(boolean isAdd, ArrayList<ZegoUser> userList);

    void onConnected();

    void onDisconnected();

    void onStopPushStream(String streamID);

    /**
     * soundLevel取值范围为 0.0 ~ 100.0
     */
    void onMySoundLevelUpdate(float soundLevel);

    void onRemoteSoundLevelUpdate(String userId, float soundLevel);

}
