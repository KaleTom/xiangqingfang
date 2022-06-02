package com.zg.xqf.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.codingending.popuplayout.PopupLayout;
import com.jaeger.library.StatusBarUtil;
import com.zg.xqf.MainActivity;
import com.zg.xqf.R;
import com.zg.xqf.controller.BaseController;
import com.zg.xqf.controller.MasterController;
import com.zg.xqf.controller.UserController;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.ShowUtils;
import com.zg.xqf.util.Utils;
import com.zg.xqf.view.PopupAdapter;
import com.zg.xqf.view.PreviewItem;
import com.zg.xqf.view.PreviewItemsGroup;
import com.zg.xqf.zego.Zego;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import im.zego.zegoexpress.ZegoExpressEngine;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class RoomFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "RoomFragment";
    private ImageView mCloseBtn;
    private TextView mRoomNameTV;
    private PreviewItemsGroup mPreviewIV;
    private Button mUpMicBtn;
    private Button mDownMicBtn;
    private ImageView mSpeechOpenBtn;
    private ImageView mSpeechClosedBtn;
    private ImageView mDelUserBtn;
    private ImageView mAddUserBtn;
    private boolean isConned;
    private BaseController controller;
    private List<String> mConnUserIds;
    private List<PreviewItem> mPreviewItems;
    private PopupLayout mOnlinePopup;
    private PopupLayout mReqConnPopup;
    private PopupAdapter mOnlineAdapter;
    private PopupAdapter mReqConnAdapter;

    private QBadgeView qBadgeView = null;
    private int mNavHeight;
    private boolean isMuted;
    private boolean hasReqConn = false;
    private boolean hasReqDisConn = false;
    private LoadingDailog mLoadingDialog = null;

    public RoomFragment() {
        super();
        mNavHeight = 0;
        isMuted = false;
    }

    @SuppressLint("ValidFragment")
    public RoomFragment(int navHeight) {
        super();
        mNavHeight = navHeight;
        isMuted = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_room, container, false);
        View bottomBtns = root.findViewById(R.id.bottomBtns);
        bottomBtns.setPadding(bottomBtns.getPaddingLeft(), bottomBtns.getPaddingTop(), bottomBtns.getPaddingRight(), bottomBtns.getPaddingBottom() + mNavHeight);

        mRoomNameTV = root.findViewById(R.id.roomName);
        mCloseBtn = root.findViewById(R.id.closeBtn);
        mPreviewIV = root.findViewById(R.id.previewItemView);
        mUpMicBtn = root.findViewById(R.id.upMicBtn);
        mDownMicBtn = root.findViewById(R.id.downMicBtn);
        mDelUserBtn = root.findViewById(R.id.delUserBtn);
        mAddUserBtn = root.findViewById(R.id.addUserBtn);
        mSpeechOpenBtn = root.findViewById(R.id.speechOpenBtn);
        mSpeechClosedBtn = root.findViewById(R.id.speechClosedBtn);
        mCloseBtn.setOnClickListener(this);
        mUpMicBtn.setOnClickListener(this);
        mDownMicBtn.setOnClickListener(this);
        mDelUserBtn.setOnClickListener(this);
        mAddUserBtn.setOnClickListener(this);
        mSpeechOpenBtn.setOnClickListener(this);
        mSpeechClosedBtn.setOnClickListener(this);
        isConned = false;
        hasReqConn = false;
        hasReqDisConn = false;
        isMuted = false;
        mLoadingDialog = null;
        mConnUserIds = new ArrayList<>();
        mPreviewItems = new ArrayList<>();
        StatusBarUtil.setTranslucentForImageView(getActivity(), 0, root.findViewById(R.id.content));
        initPopupLayout(inflater);
        initRedPoint(mAddUserBtn);
        loading();
        return root;
    }

    private void initPopupLayout(LayoutInflater inflater) {
        mOnlineAdapter = new PopupAdapter(getActivity(), true);
        mReqConnAdapter = new PopupAdapter(getActivity(), false);

        mOnlineAdapter.setOnClickItemBtnListener(new PopupAdapter.OnClickItemBtnListener() {
            @Override
            public void onClickItemBtn(int position) {
                if (position < 0 || position >= mOnlineAdapter.mUserList.size())
                    return;
                mOnlinePopup.hide();
                UserInfo user = mOnlineAdapter.mUserList.get(position);

                AlertView alert = new AlertView("确认", "是否踢" + user.name + "下麦？", "取消",
                        new String[]{"确认"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            mOnlineAdapter.mUserList.remove(position);
                            mOnlineAdapter.notifyDataSetChanged();
                            ((MasterController) controller).disConnOneUser(user.uid);
                        }
                    }
                });
                alert.show();

            }
        });
        mReqConnAdapter.setOnClickItemBtnListener(new PopupAdapter.OnClickItemBtnListener() {
            @Override
            public void onClickItemBtn(int position) {
                if (position < 0 || position >= mReqConnAdapter.mUserList.size())
                    return;
                mReqConnPopup.hide();
                ((MasterController) controller).allowConn(mReqConnAdapter.mUserList.get(position));
                mReqConnAdapter.mUserList.remove(position);
                updateRedPoint(mReqConnAdapter.mUserList.size());
                mReqConnAdapter.notifyDataSetChanged();
            }
        });
        View onlineRoot = PopupAdapter.initPopupView(getActivity(), inflater, mOnlineAdapter);
        ((TextView) onlineRoot.findViewById(R.id.popupTitle)).setText("在线用户");
        mOnlinePopup = PopupLayout.init(getActivity(), onlineRoot);


        View reqConnRoot = PopupAdapter.initPopupView(getActivity(), inflater, mReqConnAdapter);
        ((TextView) reqConnRoot.findViewById(R.id.popupTitle)).setText("请求连麦");
        mReqConnPopup = PopupLayout.init(getActivity(), reqConnRoot);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    public void setController(BaseController controller) {
        controller.setRoomFragment(this);
        this.controller = controller;
        isMuted = controller.mUserInfo.isMuted;
    }

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

    private void addPreviewItem(ZegoExpressEngine engine, UserInfo user, boolean isMaster) {
        PreviewItem item = mPreviewIV.addPreviewItem(getActivity(), user, isMaster);
        mPreviewItems.add(item);
        mConnUserIds.add(user.uid);
        Log.e(TAG, "add preview:" + (mConnUserIds.size() - 1) + "," + user.name);
        playStream(engine, user.uid, item.getTextureView());
    }

    private void updatePreviewItem(ZegoExpressEngine engine, UserInfo user, int idx, boolean isMaster) {
        if (idx >= mConnUserIds.size()) return;
        mPreviewIV.updateItem(idx, user, isMaster);
        if (mConnUserIds.get(idx).equals(user.uid)) return;
        playStream(engine, user.uid, getTexture(idx));
    }

    public void updateConnedUsers(ZegoExpressEngine engine, UserInfo[] users) {
        if (controller == null) return;

        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }

        boolean lastIsConned = isConned;
        isConned = false;
        int curCount = mConnUserIds.size();
        int i = 0;
        for (i = 0; i < users.length; ++i) {
            if (users[i].uid.equals(controller.mUserInfo.uid)) {
                isConned = true;
            }
            if (i < curCount) {
                updatePreviewItem(engine, users[i], i, i == 0);
            } else {
                addPreviewItem(engine, users[i], i == 0);
            }
            Log.e(TAG, "用户" + i + "的话筒状态" + users[i].isMuted);
        }
        if (!controller.isMaster && lastIsConned != isConned) {
            if (hasReqConn && isConned)
                Utils.toast(getActivity(), "上麦申请通过！");
            if (!hasReqDisConn && !isConned)
                Utils.toast(getActivity(), "房主已将你请下麦");
        }
        if (i < mConnUserIds.size()) {//删除下线用户
            for (int j = mConnUserIds.size() - 1; j >= i; j--) {
                mPreviewIV.delItem(j);
                mConnUserIds.remove(j);
            }
        }
        updateViewVisible();


    }

    private TextureView getTexture(int idx) {
        if (idx < 0 || idx >= mPreviewItems.size()) return null;
        return mPreviewItems.get(idx).getTextureView();
    }

    public void popBottom(boolean isShowOnline, List<UserInfo> users) {
        PopupAdapter adapter = isShowOnline ? mOnlineAdapter : mReqConnAdapter;
        PopupLayout popup = isShowOnline ? mOnlinePopup : mReqConnPopup;
        Log.e(TAG, isShowOnline + "更新用户数量：" + users.size());
        adapter.setData(users);
        adapter.notifyDataSetChanged();
        popup.show();
    }


    public void setTitle(String title) {
        mRoomNameTV.setText(title);
    }

    private void loading() {
        if (controller.isMaster) return;
        mLoadingDialog = ((MainActivity) getActivity()).showLoading("正在登录");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if (mLoadingDialog != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.cancel();
                                mLoadingDialog = null;
                                loginOut(3);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateViewVisible();
        if (controller != null) {
            controller.onInit();
        }
    }


    private void performLoginOut() {
        if (controller != null) {
            controller.onClose();
        }
        controller = null;
        ((MainActivity) getActivity()).closeRoom();
    }

    /**
     * level: 0 :no tips, 1: comfirm , 2:force, 3: login error
     */
    public void loginOut(int level) {
        if (level == 0 || null == controller) {
            performLoginOut();
        }
        switch (level) {
            case 1: {
                String msg = controller.isMaster ? "退出将解散房间，确定退出？" : "确定退出房间？";
                ShowUtils.comfirm(getActivity(), "退出房间", msg, "退出房间", new ShowUtils.OnClickOkListener() {
                    @Override
                    public void onOk() {
                        performLoginOut();
                    }
                });
                break;
            }
            case 2: {
                performLoginOut();
                ShowUtils.alert(getActivity(), "退出房间", "房间已解散！");
                break;
            }
            case 3: {
                performLoginOut();
                ShowUtils.alert(getActivity(), "退出房间", "月老已退出！");
                break;
            }
        }
    }

    /**
     * isAsk:是否弹出询问关闭对话框
     * isForce: 强制关闭，并且提示房间解散
     */
//    public void loginOut(boolean isAsk, boolean isForce) {
//        if (!isAsk || null == controller || isForce)
//            performLoginOut();
//        if (isForce) {
//            AlertView alert = new AlertView("退出房间", "房间已解散！", "确定",
//                    null, null, getActivity(), AlertView.Style.Alert, null);
//            alert.show();
//        } else if (isAsk) {
//            String msg = controller.isMaster ? "退出将解散房间，确定退出？" : "确定退出房间？";
//            AlertView alert = new AlertView("退出房间", msg, "取消",
//                    new String[]{"退出房间"}, null, getActivity(), AlertView.Style.Alert, new OnItemClickListener() {
//                @Override
//                public void onItemClick(Object o, int position) {
//                    if (position == 0) {
//                        performLoginOut();
//                    }
//                }
//            });
//            alert.show();
//        }
//    }

    /**
     * level:0~100
     */
    public void updateSpeechIcon(String userId, double level) {
        PreviewItem item = null;
        for (PreviewItem pi : mPreviewItems) {
            if (pi.getUserId().equals(userId)) {
                item = pi;
                break;
            }
        }
        if (item != null) {
            PreviewItem.SpeechState state = level > 10 ? PreviewItem.SpeechState.HIGH_SOUND : PreviewItem.SpeechState.LOW_SOUND;
            item.setSpeechState(state);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeBtn: {
                loginOut(1);
                break;
            }
            case R.id.upMicBtn: {

                if (!controller.isMaster) {
                    hasReqConn = true;
                    hasReqDisConn = false;
                    ((UserController) controller).reqConn();
                }
                break;
            }
            case R.id.downMicBtn: {
                if (!controller.isMaster) {
                    hasReqConn = false;
                    hasReqDisConn = true;
                    ((UserController) controller).reqDisconn();
                }
                break;
            }
            case R.id.speechOpenBtn:
            case R.id.speechClosedBtn: {
                isMuted = controller.switchAudio();
                updateViewVisible();
                break;
            }
            case R.id.delUserBtn: {
                if (controller.isMaster) {
                    MasterController ctrl = (MasterController) controller;
                    popBottom(true, ctrl.getConnedUsers(false));
                }
                break;
            }
            case R.id.addUserBtn: {
                if (controller.isMaster) {
                    List<UserInfo> users = ((MasterController) controller).getReqConnedUser();
                    popBottom(false, users);
                }
                break;
            }
        }

    }


    private void updateViewVisible() {

        mAddUserBtn.setVisibility(View.GONE);
        mSpeechOpenBtn.setVisibility(View.GONE);
        mSpeechClosedBtn.setVisibility(View.GONE);
        mDelUserBtn.setVisibility(View.GONE);
        mUpMicBtn.setVisibility(View.GONE);
        mDownMicBtn.setVisibility(View.GONE);

        if (controller.isMaster || isConned) {
            if (isMuted) mSpeechClosedBtn.setVisibility(View.VISIBLE);
            else mSpeechOpenBtn.setVisibility(View.VISIBLE);
        }
        if (controller.isMaster) {
            mAddUserBtn.setVisibility(View.VISIBLE);
            mDelUserBtn.setVisibility(View.VISIBLE);
        } else {
            if (isConned) {
                mDownMicBtn.setVisibility(View.VISIBLE);
            } else {
                mUpMicBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateRedPoint(int count) {
        if (count <= 0) {
            qBadgeView.setVisibility(View.GONE);
        } else if (count > 99) {
            qBadgeView.setBadgeText("99+");
            qBadgeView.setVisibility(View.VISIBLE);
        } else {
            qBadgeView.setBadgeText(String.valueOf(count));
            qBadgeView.setVisibility(View.VISIBLE);
        }
    }

    private void initRedPoint(View view) {
        if (!controller.isMaster) return;
        qBadgeView = new QBadgeView(getActivity());
        qBadgeView.setVisibility(View.GONE);
        qBadgeView.setBadgeBackgroundColor(Color.RED);
        qBadgeView.bindTarget(view);
        qBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
//        qBadgeView.setGravityOffset(0, 0,true);
        qBadgeView.setBadgeTextSize(8, true);
        qBadgeView.setBadgePadding(2, true);
        qBadgeView.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
            @Override
            public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                if (STATE_SUCCEED == dragState) {
                    badge.hide(true);
                }
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}