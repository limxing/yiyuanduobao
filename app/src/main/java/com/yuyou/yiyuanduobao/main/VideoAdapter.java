package com.yuyou.yiyuanduobao.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.Course;

import java.util.List;


/**
 * Created by limxing on 2016/10/28.
 */

public class VideoAdapter extends BaseAdapter {

    private List<Course> list;

    public VideoAdapter() {
        if (ProjectApplication.user != null){}
    }

    public void setList(List<Course> list) {
        this.list = list;
    }

    public List<Course> getList() {
        return list;
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(viewGroup.getContext(), R.layout.fragment_video_item, null);
        TextView name = (TextView) v.findViewById(R.id.video_item_name);
        name.setText(list.get(i).getName());
        TextView teacher = (TextView) v.findViewById(R.id.video_item_teacher);
        teacher.setText("讲师：" + list.get(i).getTeacher());
        View isBuyView = v.findViewById(R.id.video_item_isbuy);
        isBuyView.setVisibility(View.INVISIBLE);
//        if (buyList != null)
//            for (BuyData buyData : buyList) {
//                if (buyData.getCourseId().equals(list.get(i).getId())) {
//                    isBuyView.setVisibility(View.VISIBLE);
//                    break;
//                }
//            }

        ImageView imageView = (ImageView) v.findViewById(R.id.video_item_pic);
        Glide.with(viewGroup.getContext())
                .load(list.get(i).getPic())
                .placeholder(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
        return v;
    }

    public boolean isBuy(int position) {
//        if (buyList != null)
//            for (BuyData buyData : buyList) {
//                if (buyData.getCourseId().equals(list.get(position).getId())) {
//                    return true;
//                }
//            }

        return false;
    }

    public void notifyBuydata() {
        if (ProjectApplication.user != null)
//            buyList = Select.from(BuyData.class).where(Condition.prop("phone").eq(ProjectApplication.user.getPhone())).list();
        notifyDataSetChanged();
    }
}
