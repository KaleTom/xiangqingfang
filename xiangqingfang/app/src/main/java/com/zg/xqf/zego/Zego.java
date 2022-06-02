package com.zg.xqf.zego;

import android.app.Application;
import android.util.Log;
import android.view.TextureView;

import com.zg.xqf.entity.Msg;
import com.zg.xqf.entity.TokenEntity;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.TokenUtils;


import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendBroadcastMessageCallback;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoUser;

import static im.zego.zegoexpress.constants.ZegoViewMode.ASPECT_FILL;

public class Zego {
    private final static String TAG = "Zego";

    public static int randInt(int min, int max) {
        int idx = (int) (min + Math.random() * (max - min + 1));
        if (idx >= max) idx = max - 1;
        return idx;
    }

    public static String getPublicArgs() {

        //生成16进制随机字符串(16位)
        byte[] bytes = new byte[8];
        //使用SecureRandom获取高强度安全随机数生成器
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(bytes);
        String signatureNonce = bytesToHex(bytes);
        long timestamp = System.currentTimeMillis() / 1000L;
        String signature = GenerateSignature(KeyCenter.APPID, signatureNonce, KeyCenter.SERVER_SECRET, timestamp);
        return "AppId=" + KeyCenter.APPID +
                "&SignatureNonce=" + signatureNonce +
                "&Timestamp=" + timestamp +
                "&Signature=" + signature + "&SignatureVersion=2.0&IsTest=false";
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }


    // Signature=md5(AppId + SignatureNonce + ServerSecret + Timestamp)
    public static String GenerateSignature(long appId, String signatureNonce, String serverSecret, long timestamp) {
        String str = String.valueOf(appId) + signatureNonce + serverSecret + String.valueOf(timestamp);
        String signature = "";
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算后获得字节数组
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            //把数组每一字节换成16进制连成md5字符串
            signature = bytesToHex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static ZegoExpressEngine createEngine(Application app, IZegoEventHandler handler) {

        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = KeyCenter.APPID;
        profile.scenario = ZegoScenario.GENERAL;  // 通用场景接入
        profile.application = app;
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, handler);
        return engine;
    }

    public static String getToken(String userId, String roomId) {
        TokenEntity tokenEntity = new TokenEntity(KeyCenter.APPID, userId, roomId, 60 * 60, 1, 1);

        String token = TokenUtils.generateToken04(tokenEntity);
        return token;

    }

    public static boolean loginRoom(ZegoExpressEngine engine, String userId, String userName, String roomId, String token) {

        ZegoUser user = new ZegoUser(userId, userName);
        ZegoRoomConfig config = new ZegoRoomConfig();
        config.token = token; // 请求开发者服务端获取
        config.isUserStatusNotify = true;
        engine.loginRoom(roomId, user, config);
        Log.e(TAG, "登录房间：" + roomId);
        return true;
    }


    public static void playPreview(ZegoExpressEngine engine, TextureView tv, String streamId) {
        ZegoCanvas canvas = new ZegoCanvas(tv);
        canvas.viewMode = ASPECT_FILL;
        if (streamId == null) {//本地预览
            engine.startPreview(canvas);
        } else {//拉取视频流
            engine.startPlayingStream(streamId, canvas);
        }
    }

    public static void sendMsg(ZegoExpressEngine engine, String roomId, ArrayList<ZegoUser> userList, Msg msg) {

        String msgPack = msg.toString();
        // 发送自定义信令，`toUserList` 中指定的用户才可以通过 onIMSendCustomCommandResult 收到此信令
        // 若 `toUserList` 参数传 `null` 则 SDK 将发送该信令给房间内所有用户
        engine.sendCustomCommand(roomId, msgPack, userList, new IZegoIMSendCustomCommandCallback() {
            /** 发送用户自定义消息结果回调处理 */
            @Override
            public void onIMSendCustomCommandResult(int errorCode) {
                //发送消息结果成功或失败的处理
                Log.e(TAG, "消息发送结束，回调：" + errorCode);
            }
        });

    }


    public static void broadcastMsg(ZegoExpressEngine engine, String roomId, Msg msg) {

        try {
            //限制QPS=2
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msgPack = msg.toString();
        // 发送广播消息，每个登录房间的用户都会通过 onIMRecvBroadcastMessage 回调收到此消息【发送方不会收到该回调】
        engine.sendBroadcastMessage(roomId, msgPack, new IZegoIMSendBroadcastMessageCallback() {
            /** 发送广播消息结果回调处理 */
            @Override
            public void onIMSendBroadcastMessageResult(int errorCode, long messageID) { //发送消息结果成功或失败的处理
                Log.e(TAG, "广播消息发送结束，回调：" + errorCode);
                //发送消息结果成功或失败的处理
            }
        });

    }

}
