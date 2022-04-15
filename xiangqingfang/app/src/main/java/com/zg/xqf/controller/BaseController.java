package com.zg.xqf.controller;

import android.util.Log;

import com.zg.xqf.entity.Msg;
import com.zg.xqf.entity.MsgSpeechState;
import com.zg.xqf.entity.MsgType;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.fragments.RoomFragment;
import com.zg.xqf.util.Utils;
import com.zg.xqf.zego.IMyEventListener;
import com.zg.xqf.zego.Zego;

import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoUser;


public class BaseController implements IMyEventListener {
    private final static String TAG = "BaseController";
    public boolean isMaster;
    public String mMasterId;
    protected ZegoExpressEngine mEngine;
    protected RoomFragment mRoomFragment;
    public String mRoomId;
    public String mRoomName;
    public UserInfo mUserInfo;

    public BaseController(ZegoExpressEngine engine, int maxUserCount, String roomId, String roomName, UserInfo userInfo, boolean isMaster) {
        mEngine = engine;
        mRoomId = roomId;
        mRoomName = roomName;
        mUserInfo = userInfo;
        this.isMaster = isMaster;
    }

    protected void toast(String msg) {
        Utils.toast(mRoomFragment.getActivity(), msg);
    }

    public void setRoomFragment(RoomFragment roomFragment) {
        mRoomFragment = roomFragment;
    }

    protected void sendMsg(String toUserId, Msg msg) {
        ArrayList<ZegoUser> users = new ArrayList<>();
        users.add(new ZegoUser(toUserId));
        Zego.sendMsg(mEngine, mRoomId, users, msg);
    }

    public void onInit() {
        Zego.loginRoom(mEngine, mUserInfo, mRoomId);

        mEngine.startSoundLevelMonitor(); //启动声浪监听
    }

    public boolean switchAudio() {
        boolean isMuted = mEngine.isMicrophoneMuted();
        mEngine.muteMicrophone(!isMuted);
        MsgSpeechState msg = new MsgSpeechState(MsgType.MSG_CHG_SPEECH_STATE, mUserInfo.uid, !isMuted);
        sendMsgToMaster(msg);
        Log.e(TAG, "当前状态:" + isMuted);
        return !isMuted;
    }

    protected void sendMsgToMaster(Msg msg) {
        Log.e(TAG, "向管理员发消息：" + msg.toString());

        if (isMaster) {//有些指令需要让管理员广播，在一些场景可能会出现管理员自己给自己发消息
            onRcvMsg(msg);
        } else {
            ArrayList<ZegoUser> master = new ArrayList<>();
            master.add(new ZegoUser(mMasterId));
            Zego.sendMsg(mEngine, mRoomId, master, msg);
        }
    }

    public void onClose() {

        // mSDKEnging 为 ZegoExpressEngine 的实例
        mEngine.stopSoundLevelMonitor();
        mEngine.stopPublishingStream();
        mEngine.logoutRoom();
    }

    @Override
    public void onMySoundLevelUpdate(float soundLevel) {
        mRoomFragment.updateSpeechIcon(mUserInfo.uid, soundLevel);
//        Log.e(TAG, "my level:" + soundLevel);
    }

    @Override
    public void onRemoteSoundLevelUpdate(String userId, float soundLevel) {
        mRoomFragment.updateSpeechIcon(userId, soundLevel);
//        Log.e(TAG, "remote level:" + soundLevel);
    }

    @Override
    public void onStopPushStream(String streamID) {

//        if (streamID.equals(mMasterId)) {
//            Log.e(TAG, "月老停止推流...");
//            mRoomFragment.loginOut(true, true);
//        }
    }

    @Override
    public void onRcvMsg(Msg msg) {

    }

    @Override
    public void onAddUser(ArrayList<ZegoUser> userList) {

    }

    @Override
    public void onDelUser(ArrayList<ZegoUser> userList) {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }
}
