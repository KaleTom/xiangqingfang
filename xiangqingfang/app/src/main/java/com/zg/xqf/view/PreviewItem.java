package com.zg.xqf.view;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zg.xqf.R;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.TextureRoundOutlineProvider;

public class PreviewItem {
    private View mRoot;
    private TextureView mTextureView;
    private TextView mNameTV;
    private TextView mAgeTV;
    private TextView mProvinceTV;
    private String mUserId;
    private ImageView mMutedState;
    private ImageView mLowSound;
    private ImageView mHighSound;
    private LinearLayout mAgeProvince;
    private boolean isMuted;

    public enum SpeechState {
        MUTED_STATE,
        LOW_SOUND,
        HIGH_SOUND
    }

    public PreviewItem(Context ctx, UserInfo user) {
        mUserId = user.uid;
        mRoot = LayoutInflater.from(ctx).inflate(R.layout.preview_item, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRoot.setOutlineProvider(new TextureRoundOutlineProvider(ctx, 6));
            mRoot.setClipToOutline(true);
        }
        mTextureView = mRoot.findViewById(R.id.texture);
        mNameTV = mRoot.findViewById(R.id.name);
        mAgeTV = mRoot.findViewById(R.id.age);
        mProvinceTV = mRoot.findViewById(R.id.addr);
        mMutedState = mRoot.findViewById(R.id.mic_no_speech);
        mLowSound = mRoot.findViewById(R.id.mic_low_sound);
        mHighSound = mRoot.findViewById(R.id.mic_high_sound);
        mAgeProvince = mRoot.findViewById(R.id.age_province);
        setSpeechState(SpeechState.LOW_SOUND);
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    public void updateItem(UserInfo user, boolean isMaster) {
        String name = "", age = "", province = "", uid = "";
        if (user != null ) {
            name = user.name;
            age = user.age + "岁 ";
            uid = user.uid;
            province = " " + user.province;
        }
        isMuted = user.isMuted;
        mUserId = uid;
        mNameTV.setText(name);
        mAgeTV.setText(age);
        mProvinceTV.setText(province);
        if (isMaster) mAgeProvince.setVisibility(View.GONE);
        else mAgeProvince.setVisibility(View.VISIBLE);
        if (isMuted) setSpeechState(SpeechState.MUTED_STATE);
        if (isMuted) {
            Log.e("ITEM", "用户" + name + "麦克风关闭");
        } else {
            Log.e("ITEM", "用户" + name + "麦克风开启");
        }
    }


    public String getUserId() {
        return mUserId;
    }

    public View getRootView() {
        return mRoot;
    }

    /**
     * state: 0: lowerSpeech, 1:highSpeech, 2:noSpeech
     */
    public void setSpeechState(SpeechState state) {
        mMutedState.setVisibility(View.GONE);
        mLowSound.setVisibility(View.GONE);
        mHighSound.setVisibility(View.GONE);
        if (isMuted) {
            mMutedState.setVisibility(View.VISIBLE);
            return;
        }
        switch (state) {
            case MUTED_STATE: {
                mMutedState.setVisibility(View.VISIBLE);
                break;
            }
            case LOW_SOUND: {
                mLowSound.setVisibility(View.VISIBLE);
                break;
            }
            case HIGH_SOUND: {
                mHighSound.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}
