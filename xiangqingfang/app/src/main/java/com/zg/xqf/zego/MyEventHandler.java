package com.zg.xqf.zego;

import android.util.Log;

import com.zg.xqf.entity.Msg;
import com.zg.xqf.fragments.RoomFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;

public class MyEventHandler extends IZegoEventHandler {
    private final static String TAG = "MyEventHandler";
    private IMyEventListener mListener;

    public MyEventHandler() {
        mListener = null;
    }

    public void setMyEventListener(IMyEventListener listener) {
        mListener = listener;
    }


    @Override
    public void onCapturedSoundLevelUpdate(float soundLevel) {
        if (mListener != null)
            mListener.onMySoundLevelUpdate(soundLevel);
//        Log.e(TAG, "收到自己音浪消息...");
    }

    @Override
    public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
        if (mListener != null) {
            for (String streamId : soundLevels.keySet()) {
                float level = soundLevels.get(streamId);
                mListener.onRemoteSoundLevelUpdate(streamId, level);
//                Log.e(TAG, "收到远程音浪消息..." + level);
            }
        }
    }

    @Override
    public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
        super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
        if (mListener == null) return;
        if (state == ZegoRoomState.CONNECTED) {
            //房间连接成功
            mListener.onConnected();
        } else if (state == ZegoRoomState.CONNECTING) {
            //房间连接中
            Log.d("MainActivity", "connecting...");
        } else if (state == ZegoRoomState.DISCONNECTED) {
            //未连接或房间连接断开
            mListener.onDisconnected();
        }
    }

    @Override
    public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
        super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
        if (mListener == null) return;
        if (errorCode != 0) {
            Log.e("推流状态出错 errorCode: %d", errorCode + "");
        } else {
            switch (state) {
                case PUBLISHING:
                    //("正在推流");
                    break;
                case PUBLISH_REQUESTING:
                    //("正在请求推流");
                    break;
                case NO_PUBLISH:
                    //("没有推流");
                    Log.e(TAG, streamID + "停止推流...");
                    break;
            }
        }
    }


    @Override
    public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
        super.onPlayerStateUpdate(streamID, state, errorCode, extendedData);
        if (mListener == null) return;
        Log.e(TAG, "播放状态发生改变:" + state);
        if (errorCode != 0) {
            Log.e(TAG, String.format("拉流状态出错 streamID: %s, errorCode:%d", streamID, errorCode));
        } else {
            switch (state) {
                case PLAYING:
                    //("正在拉流中");
                    break;
                case PLAY_REQUESTING:
                    //("正在请求拉流中");
                    break;
                case NO_PLAY:
                    //("未进行拉流");
//                    mListener.onStopPushStream(streamID);
                    break;
            }
        }
    }

    @Override
    public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
        super.onRoomUserUpdate(roomID, updateType, userList);
        if (mListener == null) return;
//        Log.e(TAG, "房间信息更新..." + (updateType == ZegoUpdateType.ADD));
        // 您可以在回调中根据用户的进出/退出情况，处理对应的业务逻辑
//        mListener.onRoomUserUpdate(updateType == ZegoUpdateType.ADD, userList);
        if (updateType == ZegoUpdateType.ADD) {
            mListener.onAddUser(userList);

        } else if (updateType == ZegoUpdateType.DELETE) {
            mListener.onDelUser(userList);
        }

    }

    // 房间内其他用户推流/停止推流时，我们会在这里收到相应流增减的通知
    @Override
    public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
        super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
        if (mListener == null) return;

//        //当 updateType 为 ZegoUpdateType.ADD 时，代表有音视频流新增，此时我们可以调用 startPlayingStream 接口拉取播放该音视频流
//        if (updateType == ZegoUpdateType.ADD) {
//            // 开始拉流，设置远端拉流渲染视图，视图模式采用 SDK 默认的模式，等比缩放填充整个             View
//            // 如下 playView 为 UI 界面上 View.这里为了使示例代码更加简洁，我们只拉取新增的音视频流列表中第的第一条流，在实际的业务中，建议开发者循环遍历 streamList ，拉取每一条音视频流
//            ZegoStream stream = streamList.get(0);
////                playStreamID = stream.streamID;
////                ZegoCanvas playCanvas = new ZegoCanvas(playView);
////                engine.startPlayingStream(playStreamID, playCanvas);
//            for (ZegoStream zs : streamList) {
//                RoomFragment.this.mStreamIdList.add(zs.streamID);
//            }
//        } else if (updateType == ZegoUpdateType.DELETE) {
//
//            for (ZegoStream zs : streamList) {
//                RoomFragment.this.mStreamIdList.remove(zs.streamID);
//            }
//        }
    }

    @Override
    public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
        // 收到其他用户发送消息的处理
        if (mListener == null) return;
        try {
            Msg msg = Msg.parseMsg(fromUser.userID, command);
            mListener.onRcvMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收房间广播消息通知
     *
     * @param roomID      房间 ID
     * @param messageList 收到的消息列表
     */

    @Override
    public void onIMRecvBroadcastMessage(String roomID, ArrayList<ZegoBroadcastMessageInfo> messageList) {
        // 收到其他用户发送消息的处理
        Log.e(TAG, "收到广播消息");
        if (mListener == null) return;
        for (ZegoBroadcastMessageInfo info : messageList) {
            Msg msg = Msg.parseMsg(info.fromUser.userID, info.message);
            mListener.onRcvMsg(msg);
        }


    }

    public void setListener(IMyEventListener listener) {
        mListener = listener;

    }
}
