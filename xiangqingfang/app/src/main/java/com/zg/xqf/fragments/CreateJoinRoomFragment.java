package com.zg.xqf.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Fragment;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;

import com.jaeger.library.StatusBarUtil;
import com.zg.xqf.MainActivity;
import com.zg.xqf.R;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.HttpsRequest;
import com.zg.xqf.util.Utils;
import com.zg.xqf.zego.Zego;

import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class CreateJoinRoomFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "CreateJoinRoomFragment";
    private MainActivity mActivity;
    private EditText mRoomIDEt;
    private EditText mRoomNameEt;
    private Button mCreateRoomBtn;
    private Button mJoinRoomBtn;
    private String mRoomName;
    private String mRoomId;
    private boolean isCreate;
    private TextView mRoomNameTip;
    private TextView mRoomIdTip;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_create_join_room, container, false);

        mRoomIDEt = (EditText) root.findViewById(R.id.roomIdEt);
        mRoomNameEt = (EditText) root.findViewById(R.id.roomNameEt);
        mCreateRoomBtn = (Button) root.findViewById(R.id.createRoomBtn);
        mJoinRoomBtn = (Button) root.findViewById(R.id.joinRoomBtn);
        mRoomNameTip = root.findViewById(R.id.roomNameTip);
        mRoomIdTip = root.findViewById(R.id.roomIdTip);
        mRoomIdTip.setText("");
        mRoomNameTip.setText("");
        mCreateRoomBtn.setOnClickListener(this);
        mJoinRoomBtn.setOnClickListener(this);
        StatusBarUtil.setTranslucentForImageView(getActivity(), 0, null);

        return root;
    }

    public String getEditRoomName() {

        return mRoomNameEt.getText().toString();
    }

    public String getEditRoomId() {

        return mRoomIDEt.getText().toString();
    }


    private void checkRoomId(String roomId) {
        LoadingDailog dialog = mActivity.showLoading("??????????????????ID...");
        String url = "https://rtc-api.zego.im/?Action=DescribeUserNum&RoomId[]=" + roomId +
                "&" + Zego.getPublicArgs();
        HttpsRequest.getJSON(mActivity, url, new HttpsRequest.OnJSONCallback() {
            @Override
            public void onJSON(JSONObject json) {
                dialog.cancel();
                int code = Utils.getInt(json, "Code", -1);
                String msg = Utils.getStr(json, "Message", "??????????????????");
                Log.e(TAG, json.toString());
                if (code == 0) {
                    int userCount = Utils.getInt(json, "Data/UserCountList[RoomId=" + roomId + "]/UserCount", -1);
                    Log.e(TAG, "userCount" + userCount);
                    if (userCount >= 0) {
                        CreateJoinRoomFragment.this.onCheckedRoomUserNum(userCount);
                    } else {
                        mActivity.toast("??????????????????");
                    }
                } else {
//                    mActivity.toast(msg);
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.cancel();
                mActivity.toast("????????????");

            }
        });


    }

    public void onCheckedRoomUserNum(int userNum) {
        if (isCreate) {
            if (userNum <= 0)
                mActivity.createRoom(UserInfo.random(), mRoomName, mRoomId);
            else
                mRoomIdTip.setText("??????ID?????????");
        } else {
            if (userNum <= 0)
                mRoomIdTip.setText("??????ID?????????");
            else
                mActivity.joinRoom(UserInfo.random(), mRoomId);
        }
    }

    @Override
    public void onClick(View view) {
        mRoomIdTip.setText("");
        mRoomNameTip.setText("");
        mRoomName = getEditRoomName();
        mRoomId = getEditRoomId();
        if (mRoomId.length() <= 0) {
            mRoomIdTip.setText("???????????????ID");
            return;
        }
        if (view == mCreateRoomBtn) {
            if (mRoomName.length() <= 0) {
                mRoomNameTip.setText("?????????????????????");
                return;
            }
            if(mRoomName.length()>8){
                mRoomNameTip.setText("??????8?????????");
                return;

            }
            isCreate = true;
        } else {
            isCreate = false;
        }
        checkRoomId(mRoomId);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}