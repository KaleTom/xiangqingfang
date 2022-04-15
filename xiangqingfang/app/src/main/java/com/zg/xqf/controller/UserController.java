package com.zg.xqf.controller;

import android.util.Log;

import com.zg.xqf.entity.Msg;
import com.zg.xqf.entity.MsgConn;
import com.zg.xqf.entity.MsgRoomInfo;
import com.zg.xqf.entity.MsgType;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.ShowUtils;
import com.zg.xqf.zego.IMyEventListener;
import com.zg.xqf.zego.Zego;

import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoUser;

public class UserController extends BaseController {
    private final static String TAG = "UserController";


    public UserController(ZegoExpressEngine engine, int maxUserCount, String roomId, String roomName, UserInfo userInfo) {
        super(engine, maxUserCount, roomId, roomName, userInfo, false);

    }


    public void reqConn() {
//        Msg msg = new ConnMsg(mUserInfo.uid, new String[]{"" + Msg.MSG_REQ_CONN, "-1", mUserInfo.mergeUserName()});
        Msg msg = new MsgConn(MsgType.MSG_REQ_CONN, mUserInfo.uid, -1, mUserInfo);
        sendMsgToMaster(msg);
        toast("已申请，请等待房主同意！");

    }


    public void reqDisconn() {
        ShowUtils.comfirm(mRoomFragment.getActivity(), "确认", "确认下麦吗？", "确认", new ShowUtils.OnClickOkListener() {
            @Override
            public void onOk() {
                Msg msg = new MsgConn(MsgType.MSG_REQ_DISCONN, mUserInfo.uid, -1, mUserInfo);
                sendMsgToMaster(msg);
                mEngine.stopPublishingStream();
            }
        });
    }

    //只有加入房间才会调用这个函数
    private void onUpdateRoomInfo(MsgRoomInfo msg) {
        mRoomName = msg.roomName;
        mMasterId = msg.masterId;
        mRoomFragment.setTitle(msg.roomName);
        mRoomFragment.updateConnedUsers(mEngine, msg.connedUsers);

    }

    @Override
    public void onDelUser(ArrayList<ZegoUser> userList) {
        for (ZegoUser zu : userList) {
            if (zu.userID.equals(mMasterId)) {
                //房主退出
                mRoomFragment.loginOut(2);
                break;
            }
        }


    }

    @Override
    public void onRcvMsg(Msg msg) {
        Log.e(TAG, "普通用户收到消息：" + msg.msgType);

        switch (msg.msgType) {
            case MsgType.MSG_CMD_ROOM_INFO: {
                MsgRoomInfo rimsg = (MsgRoomInfo) msg;
                Log.e(TAG, "普通用户收到消息：" + msg.toString());
                onUpdateRoomInfo(rimsg);
                break;
            }
        }
    }

    @Override
    public void onAddUser(ArrayList<ZegoUser> userList) {

    }


    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }
}
