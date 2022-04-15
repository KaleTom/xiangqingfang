package com.zg.xqf.controller;

import android.util.Log;

import com.zg.xqf.entity.Msg;
import com.zg.xqf.entity.MsgConn;
import com.zg.xqf.entity.MsgRoomInfo;
import com.zg.xqf.entity.MsgSpeechState;
import com.zg.xqf.entity.MsgType;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.zego.Zego;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoUser;

public class MasterController extends BaseController {
    private final static String TAG = "MasterController";

    private Set<String> mRoomUserIds = new HashSet<>();
    private Set<UserInfo> mReqConnedUsers = new HashSet<>();
    private List<UserInfo> mConnedUsers = new ArrayList<>();

    //    public void muteMicrophone(boolean mute)
    public MasterController(ZegoExpressEngine engine, int maxUserCount, String roomId, String roomName, UserInfo userInfo) {
        super(engine, maxUserCount, roomId, roomName, userInfo, true);
        mMasterId = userInfo.uid;
    }

    public List<UserInfo> getReqConnedUser() {
        Log.e(TAG, "当前共有" + mReqConnedUsers.size() + "人请求连麦");
        return new ArrayList<>(mReqConnedUsers);
    }

    public List<UserInfo> getConnedUsers(boolean isIncMaster) {

        if (isIncMaster) return new ArrayList<>(mConnedUsers);
        else
            return new ArrayList<>(mConnedUsers.subList(1, mConnedUsers.size()));
    }

    private UserInfo[] broadcastRoomPos() {
        UserInfo[] users = new UserInfo[mConnedUsers.size()];
        for (int i = 0; i < users.length; ++i) {
            users[i] = mConnedUsers.get(i);
        }
        //管理员自己的视图也更新
        mRoomFragment.updateConnedUsers(mEngine, users);
        //广播通知其他用户更新视图
        Msg msg = new MsgRoomInfo(MsgType.MSG_CMD_ROOM_INFO, mUserInfo.uid, mRoomName, mUserInfo.uid, users);
        Zego.broadcastMsg(mEngine, mRoomId, msg);
        return users;
    }

    public void disConnOneUser(String uid) {
        boolean needUpdateConned = false;
        for (UserInfo user : mConnedUsers) {
            if (user.uid.equals(uid)) {
                needUpdateConned = true;
                mConnedUsers.remove(user);
            }
        }
        if (needUpdateConned) {
            broadcastRoomPos();
        }
    }

    public void disConnUser(List<String> uids) {
        boolean needUpdateConned = false;
        List<UserInfo> newUsers = new ArrayList<>();
        Set<String> disSet = new HashSet<>(uids);
        for (UserInfo user : mConnedUsers) {
            if (disSet.contains(user.uid)) {
                needUpdateConned = true;
            } else
                newUsers.add(user);
        }
        if (needUpdateConned) {
            mConnedUsers = newUsers;
            broadcastRoomPos();
        }
    }

    public void allowConn(UserInfo user) {//管理员允许xxx上麦

//        allowConn(cmsg.user);
        if (mConnedUsers.size() >= 3) {
            toast("上麦位置已满，请先下麦用户");
        } else {
            mConnedUsers.add(user);
            mReqConnedUsers.remove(user);
            UserInfo[] users = broadcastRoomPos();
        }
    }

    @Override
    public void onInit() {
        super.onInit();
        mRoomFragment.setTitle(mRoomName + "(" + mRoomId + ")");
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void onRcvMsg(Msg msg) {
        Log.e(TAG, "管理员收到消息：" + msg.msgType);
        switch (msg.msgType) {
            case MsgType.MSG_REQ_CONN: {
                Log.e(TAG, "管理员收到请求上线消息");
                MsgConn cmsg = (MsgConn) msg;
                mReqConnedUsers.add(cmsg.user);
                mRoomFragment.updateRedPoint(mReqConnedUsers.size());
                break;
            }
            case MsgType.MSG_REQ_DISCONN: {
                MsgConn cmsg = (MsgConn) msg;
                disConnOneUser(cmsg.user.uid);
                break;
            }
            case MsgType.MSG_CHG_SPEECH_STATE: {
                MsgSpeechState state = (MsgSpeechState) msg;
                boolean flag = false;
                for (UserInfo user : mConnedUsers) {
                    if (user.uid.equals(state.fromUserId)) {
                        user.isMuted = state.isMuted;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    broadcastRoomPos();
                }
                break;
            }
        }
    }

    @Override
    public void onAddUser(ArrayList<ZegoUser> userList) {
        //只有管理员做收发消息控制
        for (ZegoUser zu : userList) {
            mRoomUserIds.add(zu.userID);
        }
        UserInfo[] users = new UserInfo[mConnedUsers.size()];
        for (int i = 0; i < mConnedUsers.size(); ++i) {
            users[i] = mConnedUsers.get(i);
        }
        Log.e(TAG, "管理员收到消息，有用户加入：" + mUserInfo.uid);
        Msg msg = new MsgRoomInfo(MsgType.MSG_CMD_ROOM_INFO, mUserInfo.uid, mRoomName, mUserInfo.uid, users);
        Zego.sendMsg(mEngine, mRoomId, userList, msg);
    }

    @Override
    public void onDelUser(ArrayList<ZegoUser> userList) {
        //如果是上麦的用户，就将其停止拉流
        List<String> delIds = new ArrayList<>();
        for (ZegoUser zu : userList) {
            delIds.add(zu.userID);
            if (mConnedUsers.contains(zu.userID)) {
                mConnedUsers.remove(zu.userID);
            }
        }
        disConnUser(delIds);


    }

    @Override
    public void onConnected() {
//        mRoomFragment.playMasterPreview(mEngine, mUserInfo.uid);
        mUserInfo.name = "月老";
        mConnedUsers.add(mUserInfo);
        mRoomFragment.updateConnedUsers(mEngine, new UserInfo[]{mUserInfo});
    }

    @Override
    public void onDisconnected() {

    }
}
