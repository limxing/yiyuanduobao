package com.yuyou.yiyuanduobao.main;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.Course;

import java.util.List;


/**
 * Created by limxing on 2016/10/28.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ItemView> {

    private List<Course> list;

    public VideoAdapter() {
        if (ProjectApplication.user != null) {
        }

    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.fragment_video_item, null);
//        TypedValue typedValue = new TypedValue();
//        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
//        v.setBackgroundResource(typedValue.resourceId);
        return new ItemView(v);
    }

    @Override
    public void onBindViewHolder(ItemView holder, int i) {


        holder.name.setText(list.get(i).getName());

        holder.teacher.setText("讲师：" + list.get(i).getTeacher());

        holder.isBuy.setText("1 金币");
//        isBuyView.setVisibility(View.INVISIBLE);
        List<BuyData> buys = ProjectApplication.buyList;
        if (buys != null)
            for (BuyData buyData : buys) {
                if (buyData.getCourseid().equals(list.get(i).getId())) {
                    holder.isBuy.setText("已购买");
                    break;
                }
            }
        Glide.with(holder.pic.getContext())
                .load(list.get(i).getPic())
                .placeholder(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.pic);
    }

    public void setList(List<Course> list) {
        this.list = list;
    }

    public List<Course> getList() {
        return list;
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }


    public boolean isBuy(int position) {
        List<BuyData> buys = ProjectApplication.buyList;
        if (buys != null)
            for (BuyData buyData : buys) {
                if (buyData.getCourseid().equals(list.get(position).getId())) {
                    return true;
                }
            }

        return false;
    }

    public void notifyBuydata() {
        if (ProjectApplication.user != null)
//            buyList = Select.from(BuyData.class).where(Condition.prop("phone").eq(ProjectApplication.user.getPhone())).list();
            notifyDataSetChanged();
    }

    public String getCourseId(int position) {
        return list.get(position).getId();
    }

    public static class ItemView extends RecyclerView.ViewHolder {

       ImageView pic;
        TextView name;
        TextView teacher;
        TextView isBuy;

        public ItemView(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.video_item_name);
            teacher = (TextView) itemView.findViewById(R.id.video_item_teacher);
            isBuy = (TextView) itemView.findViewById(R.id.video_item_isbuy);
            pic = (ImageView) itemView.findViewById(R.id.video_item_pic);
        }
    }
}
