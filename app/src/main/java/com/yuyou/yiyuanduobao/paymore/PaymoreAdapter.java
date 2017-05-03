package com.yuyou.yiyuanduobao.paymore;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.PayType;

import java.util.List;

import me.leefeng.library.utils.LogUtils;

/**
 * Created by FengTing on 2017/4/26.
 */

public class PaymoreAdapter extends RecyclerView.Adapter<PaymoreAdapter.ItemView> {

    private List<PayType> list;
    private ItemClickListener listener;

    public PaymoreAdapter() {
        list = ProjectApplication.payTypes;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(View.inflate(parent.getContext(), R.layout.paymore_item, null));
    }

    @Override
    public void onBindViewHolder(ItemView holder, final int position) {
        PayType payType = list.get(position);
        holder.name.setText(payType.getName());
        holder.price.setText("¥ " + payType.getPrice()/100 + ".00元");
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("Adapter："+position);
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public static class ItemView extends RecyclerView.ViewHolder {


 View rootView;
        TextView name;
        TextView price;


        public ItemView(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.paymore_item_name);
            price = (TextView) itemView.findViewById(R.id.paymore_item_price);
            rootView =  itemView.findViewById(R.id.paymore_item_root);
        }
    }
}
