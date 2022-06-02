package com.zg.xqf.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zg.xqf.R;
import com.zg.xqf.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.UserViewHolder> implements View.OnClickListener {
    private static final String TAG = "MyAdapter";

    private OnClickItemBtnListener onClickItemBtnListener;
    public List<UserInfo> mUserList;
    private Context context;
    private boolean isShowOnline;

    public PopupAdapter(Context context, boolean isShowOnline) {
        this.context = context;
        this.mUserList = new ArrayList<>();
        this.isShowOnline = isShowOnline;
    }

    public void setData(List<UserInfo> users) {
        mUserList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserViewHolder viewHolder;
        View inflate = LayoutInflater.from(context).inflate(R.layout.popup_user_item, parent, false);
        viewHolder = new UserViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserInfo person = mUserList.get(position);
        holder.rightBtn.setTag(position);
        holder.rightBtn.setText(isShowOnline ? "下麦" : "上麦");
        holder.nameTV.setText(person.getName());
        // 关于图片加载，建议使用Glide，
        holder.headerIV.setImageResource(R.drawable.header);
        holder.rightBtn.setOnClickListener(this);
    }


    public static View initPopupView(Context ctx, LayoutInflater inflater, PopupAdapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
        View root = inflater.inflate(R.layout.popup_user, null);
        RecyclerView recyclerView = root.findViewById(R.id.rclView);
//        BottomPopAdapter adapter = new BottomPopAdapter(ctx, mList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //添加Android自带的分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // RecyclerView比ListView相比优点在于可定制强，
        // 也正是由于RecyclerView的可定制性太强，好多功能实现都需要自己来写，
        // RecyclerView不像ListView给开发者提供了setOnItemClickListener()方法，但是要实现监听也不难实现，
        return root;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "点击按钮" + v.getTag());
        if (this.onClickItemBtnListener == null) return;
        int pos = (int) v.getTag();
        this.onClickItemBtnListener.onClickItemBtn(pos);

    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView headerIV;
        TextView nameTV;
        Button rightBtn;

        public UserViewHolder(View itemView) {
            super(itemView);
            headerIV = itemView.findViewById(R.id.headerIV);
            nameTV = itemView.findViewById(R.id.name);
            rightBtn = itemView.findViewById(R.id.itemRightBtn);
        }
    }


    public void setOnClickItemBtnListener(PopupAdapter.OnClickItemBtnListener onClickItemBtnListener) {
        this.onClickItemBtnListener = onClickItemBtnListener;
    }

    public interface OnClickItemBtnListener {
        void onClickItemBtn(int position);
    }
}
