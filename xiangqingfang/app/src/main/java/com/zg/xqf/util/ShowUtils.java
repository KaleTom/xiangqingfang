package com.zg.xqf.util;

import android.content.Context;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

public class ShowUtils {
    public interface OnClickOkListener {
        void onOk();
    }

    public static void alert(Context ctx, String title, String msg) {
        AlertView alert = new AlertView(title, msg, "确定",
                null, null, ctx, AlertView.Style.Alert, null);
        alert.show();
    }

    public static void comfirm(Context ctx, String title, String msg, String ok, OnClickOkListener clickListener) {
        AlertView alert = new AlertView(title, msg, "取消",
                new String[]{ok}, null, ctx, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0 && clickListener != null) {
                    clickListener.onOk();
                }
            }
        });
        alert.show();
    }
}
