package com.zg.xqf.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zg.xqf.R;
import com.zg.xqf.entity.UserInfo;
import com.zg.xqf.util.TextureRoundOutlineProvider;
import com.zg.xqf.util.Utils;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

public class PreviewItemsGroup extends ViewGroup {
    private int mSlot;
    private List<PreviewItem> mChilds = new LinkedList<>();

    private void init(Context ctx, int slot) {
        mSlot = Utils.dip2px(ctx, slot);
    }


    public PreviewItemsGroup(Context context) {
        super(context);
        init(context, 12);
    }

    public PreviewItemsGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, 12);
    }

    public PreviewItemsGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, 12);
    }


    public void updateItem(int idx, UserInfo user, boolean isMaster) {
        mChilds.get(idx).updateItem(user, isMaster);
    }

    public PreviewItem addPreviewItem(Context ctx, UserInfo user, boolean isMaster) {
        PreviewItem item = new PreviewItem(ctx, user);
        mChilds.add(item);
        addView(item.getRootView());
        updateItem(getChildCount() - 1, user, isMaster);
        return item;
//        return new View[]{root.findViewById(R.id.texture), root.findViewById(R.id.mic_btn)};
    }

    public void delItem(int idx) {
        View view = mChilds.get(idx).getRootView();
        removeView(view);
        mChilds.remove(idx);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int iw = (w - mSlot) / 2;
        int ih = (h - mSlot) / 2;
        int wms = MeasureSpec.makeMeasureSpec(iw, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(ih, MeasureSpec.EXACTLY);
        measureChildren(wms, hms);

    }

    private int getOX(int i, int w, int iw, int count) {
        int ox = 0;

        if (count % 2 == 0) {//针对偶数
            if (i % 2 == 0) ox = 0;
            else ox = iw + mSlot;
        } else {//奇数
            if (i == 0)
                ox = (w - iw) / 2;
            else {
                if ((i - 1) % 2 == 0) ox = 0;
                else ox = iw + mSlot;
            }
        }
        return ox;
    }

    private int getOY(int i, int h, int ih, int count) {
        int oy = 0;
        if (count <= 2) {
            oy = (h - ih) / 2;
        } else {
            if (count % 2 == 0) {//偶数

                oy = i / 2 * (mSlot + ih);
            } else {//奇数

                oy = (i + 1) / 2 * (mSlot + ih);
            }
        }
        return oy;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        int w = r - l;
        int h = b - t;
        int slot = Utils.dip2px(getContext(), 12);
        int iw = (w - slot) / 2;
        int ih = (h - slot) / 2;
        int count = getChildCount();

//        Log.e("TAG", "w=" + w + ", h=" + h);
        int ox = 0, oy = 0;
        for (int i = 0; i < count; i++) {

            ox = getOX(i, w, iw, count);
            oy = getOY(i, h, ih, count);
//            Log.e("TAG", i + ":" + ox + "," + oy);
            View child = getChildAt(i);

            child.layout(ox, oy, ox + iw, oy + ih);
        }
    }


}
