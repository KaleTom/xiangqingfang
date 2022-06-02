
实时音视频涉及到的技术栈、人力成本、硬件成本非常大，一般个人开发者基本无法独立完成一个功能健全并且稳定的实时音视频应用。本文介绍一天之内，无任何实时音视频低层技术的`android`开发者完成实时相亲房`APP`，效果如下：

笔者从搜索引擎上搜了一些第三方库，综合对比了一下，最终选择了[即构](https://www.zego.im/)。因为一方面他们提供了非常全的音视频能力，`SDK`使用起来也非常简单；另一方面他们每个月提供了`10000`分钟的免费额度，对于个人开发者来说足够用了。如果超过了免费额度，说明应用有一定的流量了，到那时候花点钱扩一下容量就好。


> 详细开发文档请参考[https://doc-zh.zego.im/article/7627](https://doc-zh.zego.im/article/7627)

# 1 需要用到的多人实时音视频能力

实现多人实时视频，可以简单地抽象成如下几个模块：
> 1. 获取本地视频流，并推送到服务器。
> 2. 拉取各个用户视频流，并在终端展示。
> 3. 房间管理，管理当前多人房间的用户连接状态、观众登录登出等。

接下来我们介绍基于即构`SDK`来实现上述模块功能。

## 1.1 准备工作
### 1.1.1 集成SDK

`SDK`集成方式请直接参考官方文档[https://doc-zh.zego.im/article/195](https://doc-zh.zego.im/article/195), 这里不过多描述。
### 1.1.2 初始化引擎
有了即构的实时音视频库后，接下来需要初始化库，得到一个`ZegoExpressEngine`引擎对象。这个对象比较重要，因为接下来我们的一切对音视频流的控制都是通过此对象实现。
```java
ZegoEngineProfile profile = new ZegoEngineProfile();
profile.appID = KeyCenter.APPID; 
profile.scenario = ZegoScenario.GENERAL;  // 通用场景接入
profile.application = app;
ZegoExpressEngine engine = = ZegoExpressEngine.createEngine(profile, null);

```
需要注意的是，这里第二行有`APPID`参数，这两个参数需要前往[https://console.zego.im](https://console.zego.im)创建一个项目，即可获取当前项目对应的`APPID`。

另外，`ZegoExpressEngine.createEngine`函数还有一个`handler`参数，这个参数对象用于监听当前房间的一些信息，如用户登录登出，用户发送实时消息等。这里我们先挖个坑，在后面详细描述这个对象。


## 1.2 房间登录登出
房间是用来承载多人音视频通话的空间，一切实时音视频都是发生在房间。因此，首先要登录房间：
```java
ZegoUser user = new ZegoUser(userID, userName);
ZegoRoomConfig config = new ZegoRoomConfig();
config.token = token; // 请求开发者服务端获取
config.isUserStatusNotify = true;
engine.loginRoom(roomId, user, config);
```
> 注意，需要保证`userID`唯一。`roomID`也要唯一，如果当前`roomID`已存在，那么加入此房间，如果不存在，则创建房间。

退出房间比较简单：执行`engine.logoutRoom();`即可退出。

注意到此函数需要传入`token`参数。`token`参数是采用**对称算法**生成。其大致原理如下：
> 1. 生成一个随机数，并将**有效时长**等其他相关参数，按照固定格式排列得到未加密版的`token`。
> 2. 使用密钥（在即构官方控制台中获取，每个APPID对应一个密钥）并使用对称加密算法加密，得到加密版的`token`，这个`token`是给客户端登录时使用的。

具体的代码实现操作请参考文末提供的源码，这里不再过多描述。

## 1.3 视频推流
如果希望别人能拉取到自己的实时视频画面，需要先将自己的视频流推送出去。
```java
engine.startPublishingStream(streamID);
```
> 注意，这里的`streamID`用于标识用户的视频流，因此需要保证唯一，因为其他用户在拉取视频流时，是根据这个标识来区分的。
 
## 1.4 视频显示
### 1.4.1 预览本地视频
```java
ZegoCanvas canvas = new ZegoCanvas(textureView);
//设置显示图像填充比例方式
canvas.viewMode = ASPECT_FILL; 
engine.startPreview(canvas); 
```
`textureView`是个`TextureView`对象，可以在布局文件中提前布局。
### 1.4.2 预览远程视频
```java
ZegoCanvas canvas = new ZegoCanvas(textureView);
//设置显示图像填充比例方式
canvas.viewMode = ASPECT_FILL;  
engine.startPlayingStream(streamID, canvas); 
```
预览远程用户与本地画面预览类似，需要差别是需要提供远程用户推流的`streamID`。


# 2 无后台开发
用户唯一性、视频流唯一性、房间号的唯一性等需要我们自己准备一台服务器来管理，如果读者有个人服务器，实现起来也非常简单。

考虑到大部分读者没有个人服务器，同时也为了方便本文提供的代码可以直接在任何网络环境下运行，接下来我们实现无后台的方案：
> 我们假设每个用户都是社会主义三好青年，不会去破解APP冒充房主，创建房间的房主就是管理员，管理员向房间内各个用户发送实时消息，房间内各个成员去执行管理员的指令，实现房间管理的能力。

这就需要用到即构`SDK`的房间内实时发送消息能力。

### 2.1 发送实时消息 
**广播消息：**
```java
public void sendBroadcastMessage(String roomID,
								 String msg,
								 IZegoIMSendBroadcastMessageCallback callback);
```
`roomID`和`message`就不需要再解释，`callback`对象是回调用户，用于获取消息发送是否成功。

**一对多发送消息：**
```java
public void sendCustomCommand(String roomID,
							  String msg,
							  ArrayList<ZegoUser> toUserList,
							  IZegoIMSendCustomCommandCallback callback);
```
`ZegoUser`类型在前面登录房间时我们已经介绍过，其他参数含义同上。

### 2.2 接收实时消息
前面在介绍创建`ZegoExpressEngine`引擎时，需要提供一个`handler`参数，这里派上用场了，它是抽象类`IZegoEventHandler`的子类对象，用于执行回调事件，例如接收广播消息：
```java
@Override
public void onIMRecvBroadcastMessage(String roomID, 
	ArrayList<ZegoBroadcastMessageInfo> messageList) {
        // 收到广播消息
        Log.d(TAG, "收到广播消息");
}

```
接收到一对多消息：
```java
@Override
public void onIMRecvCustomCommand(String roomID,
								  ZegoUser fromUser,
								  String command) {
        Log.d(TAG, "收到一对多消息");
}

```


### 2.3 IZegoEventHandler其他需要用到的回调函数
用户登录、登出回调，在用户登录时，可以将房间信息如房间名称，房主ID等发送给登录的用户：
```java
@Override
public void onRoomUserUpdate(String roomID,
					         ZegoUpdateType updateType,
							 ArrayList<ZegoUser> userList) {
     super.onRoomUserUpdate(roomID, updateType, userList); 
     if (updateType == ZegoUpdateType.ADD) {
          Log.d(TAG, "用户登录");
     } else if (updateType == ZegoUpdateType.DELETE) {
         Log.d(TAG, "用户登出");
     }

}
```
获取音量大小，用于显示每个连麦的用户的说话音量：
```java
@Override
// soundLevel取值为0~100
public void onCapturedSoundLevelUpdate(float soundLevel) {
    Log.d(TAG, "收到自己音浪消息...");
}
@Override
public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
    Log.d(TAG, "收到远程音浪消息...");
}
```
与房间连接状态回调，用于判断当前登录状态。
```java
@Override
public void onRoomStateUpdate(String roomID,
							ZegoRoomState state,
							int errorCode,
							JSONObject extendedData) {
   super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
   if (state == ZegoRoomState.CONNECTED) {
        Log.d(TAG, "房间连接成功...");
   } else if (state == ZegoRoomState.CONNECTING) {
        Log.d(TAG, "房间连接中...");
   } else if (state == ZegoRoomState.DISCONNECTED) {
        Log.d(TAG, "房间连接断开...");
   }
}
```

# 3 正式开发相亲房APP
有了前面的基础铺垫后，我们接下来进入正式的`相亲房APP`开发.

## 3.1 创建房间/进入房间界面功能与实现

![创建房间与进入房间.png](https://f.bitpy.cn/MTY0NzY4MzA1NzI3MzA=.png)
对于创建房间按钮，需要判断房间是否已存在， 可以通过获取指定的房间ID里面人数是否为0来判断房间是否已存在。同理，对于进入房间的用户来说，需要判断房间内用户数量是否大于0来判断房间是否存在。

例如，查询房间号为`123`和`456`房间的用户数量:
```python
https://rtc-api.zego.im/?Action=DescribeUserNum
&RoomId[]=123
&RoomId[]=456
&<公共请求参数>
```
> 注意，调用频率限制（同一个 AppID 下所有房间）：10 次/秒（测试环境：1 次/秒）

具体使用方法参考这里[https://doc-zh.zego.im/article/8780](https://doc-zh.zego.im/article/8780)

## 3.2 月老

我们把创建房间的房主称为月老。月老创建房间后：
1. 监听用户进入房间，当有用户进入房间时，向其发送当前房间的房间名称、月老的`UserID`、当前正在连麦的用户信息及其视频流`StreamID`。
2. 监听用户消息，接收房间内用户发送的消息，消息分为几类：请求上麦、下麦、用户已静音等连麦相关消息。
3. 当有连麦用户有变动，将连麦用户信息广播给各个用户。

## 3.3 用户

普通用户进入房间后：
1. 第一时间得到月老发送的房间信息，包括房间名称，月老`UserID`、正在连麦用户信息及其视频流`StreamID`
2. 得到连麦用户信息后，自动拉取连麦用户的`StreamID`对应的视频流， 并在界面展示。
3. 请求连麦，向月老发送请求连麦信息。
4. 每次收到月老发送的同步房间信息时，自动将拉取正在连麦的用户视频流，并取消下麦视频流的展示。


## 3.4 播放预览画面
在整个`相亲房APP`中，核心是拉取远程视频流和播放本地预览。前面提到，播放预览画面分为本地预览和拉取视频流预览。为了便于调用，将两种方式封装为一个函数`playStream`:
```java
private void playStream(ZegoExpressEngine engine, String streamId, TextureView tv) {
    if (streamId == null) return;
    if (streamId.equals(controller.mUserInfo.uid)) {
        Log.e(TAG, "预览自己");
        engine.startPublishingStream(streamId);//上传视频流
        Zego.playPreview(engine, tv, null);
    } else {
        Log.e(TAG, "拉取视频流");
        engine.stopPlayingStream(streamId);
        Zego.playPreview(engine, tv, streamId);
    }
}
```
`playStream`函数指定要拉取的`streamID`和用于显示的`TextureView`。在函数里面判断当前`streamID`是否属于自己，如果属于自己则直接预览本地即可，否则拉取远程视频流。其中`Zero.playPreview`如下：
```java
public static void playPreview(ZegoExpressEngine engine, TextureView tv, String streamId) {
    ZegoCanvas canvas = new ZegoCanvas(tv);
    canvas.viewMode = ASPECT_FILL;
    if (streamId == null) {//本地预览
        engine.startPreview(canvas);
    } else {//拉取视频流
        engine.startPlayingStream(streamId, canvas);
    }
}
```
根据传入的`streamID`是否为`null`来判断是拉取远程视频还是播放本地预览。

# 4 代码分享

无后台版`相亲房APP`源码下载：https://xxxxxxxxxx








