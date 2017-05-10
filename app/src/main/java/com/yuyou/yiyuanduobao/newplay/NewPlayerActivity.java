package com.yuyou.yiyuanduobao.newplay;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.superplayer.library.SuperPlayer;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.yuyou.yiyuanduobao.BaseActivity;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.R;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.download.DownLoadManager;
import com.yuyou.yiyuanduobao.download.DownLoadService;
import com.yuyou.yiyuanduobao.download.TaskInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.leefeng.library.utils.SharedPreferencesUtil;
import me.leefeng.library.utils.StringUtils;
import me.leefeng.library.utils.ToastUtils;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by limxing on 2016/11/2.
 */

public class NewPlayerActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView newplayer_recycleview;
    private PlayerListAdapter mAdapter;
    private SuperPlayer player;
    private String url;
    private ArrayList<PlayerItemBean> playerItemBeanList;
    private int current;
    private Course course;
    private View fab;
    private boolean isShow;
    private View newplay_title;
    private RelativeLayout.LayoutParams recycleParams;
    private boolean isDownloadController;
    private int titleHeight;
    private View newplayer_down_bottom;
    private int down_bottom_height;
    private TextView newplayer_all;
    private DownLoadManager downLoadManager;
    private File file;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        downLoadManager = DownLoadService.getDownLoadManager();
        newplay_title = findViewById(R.id.newplay_title);
        newplay_title.measure(0, 0);
        titleHeight = newplay_title.getMeasuredHeight();
        newplay_title.setY(-titleHeight);
        ((TextView) findViewById(R.id.title_name)).setText("选择下载");
        ((ImageView) findViewById(R.id.title_back)).setImageDrawable(getResources().getDrawable(R.drawable.close));
        findViewById(R.id.title_back).setOnClickListener(this);


        int courseIndex = getIntent().getIntExtra("course", 0);
        course = ProjectApplication.cList.get(courseIndex);


        if (course.getCatelogue().size() == 0) {
            ToastUtils.showShort(this, "本单元暂时没视频");
            return;
        }
        playerItemBeanList = new ArrayList<PlayerItemBean>();
        for (int i = 0; i < course.getCatelogue().size(); i++) {
            String caper = course.getCatelogue().get(i);
            List<String> list = course.getVideos().get(i).getValues();

            for (String s : list) {
                playerItemBeanList.add(new PlayerItemBean(s, formatUrl(s), course.getId(), caper));
            }
        }
        current = SharedPreferencesUtil.getIntData(this, course.getId(), 0);

        playerItemBeanList.get(current).setPlaying(true);

        player = (SuperPlayer) findViewById(R.id.newplay_player);
        newplayer_recycleview = (RecyclerView) findViewById(R.id.newplayer_recycleview);
        player.measure(0, 0);

        recycleParams = (RelativeLayout.LayoutParams) newplayer_recycleview.getLayoutParams();
        recycleParams.setMargins(0, player.getMeasuredHeight(), 0, 0);
        newplayer_recycleview.setLayoutParams(recycleParams);

        newplayer_down_bottom = findViewById(R.id.newplayer_down_bottom);
        newplayer_down_bottom.measure(0, 0);
        down_bottom_height = newplayer_down_bottom.getMeasuredHeight();
        newplayer_down_bottom.setTranslationY(down_bottom_height);

        findViewById(R.id.newplayer_down).setOnClickListener(this);
        newplayer_all = (TextView) findViewById(R.id.newplayer_all);
        newplayer_all.setOnClickListener(this);

        mAdapter = new PlayerListAdapter(course.getCatelogue(), playerItemBeanList);
        newplayer_recycleview.setAdapter(mAdapter);
        newplayer_recycleview.setLayoutManager(new LinearLayoutManager(this));
        newplayer_recycleview.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));

        initPlayer();
        mAdapter.setOnItemClickListener(new PlayerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isDownloadController) {
                    playerItemBeanList.get(position).setChecked(!playerItemBeanList.get(position).isChecked());
                    mAdapter.notifyItemChanged(position);
                    return;
                }
                if (current == position) {
                    ToastUtils.showLong(view.getContext(), "正在播放");
                    return;
                }
                url = course.getVideos().get((int) mAdapter.getHeaderId(position)).getUrl();
                playerItemBeanList.get(current).setPlaying(false);
                current = position;
                playerItemBeanList.get(position).setPlaying(true);
                mAdapter.notifyDataSetChanged();

                player.stop();

                player.setTitle(playerItemBeanList.get(current).getName());
                file = new File(playerItemBeanList.get(current).getPath());
                if (file.exists()) {
                    player.setUrl(file.toString()).play(0);
                    ;
                } else {
                    player.play(playerItemBeanList.get(current).getUrl());
                }
//                        .play(playerItemBeanList.get(current).getUrl());
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setVisibility(View.GONE);

        isShow = true;
//        newplayer_recycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
//
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (isDownloadController) return;
//                if (dy > 2 && isShow) {
//                    isShow = false;
//                    translationY(fab, 0, 300);
//                } else if (dy < -1 && !isShow) {
//                    isShow = true;
//                    translationY(fab, 300, 0);
//                }
//            }
//        });

    }

    private void initPlayer() {
        url = course.getVideos().get((int) mAdapter.getHeaderId(current)).getUrl();
        int position = SharedPreferencesUtil.getIntData(this, course.getId() + "play", 0);
        player.setNetChangeListener(true)//设置监听手机网络的变化
//                .setOnNetChangeListener(this)//实现网络变化的回调
                .setShowNavIcon(true)
                .showCenterControl(true)
                .setCompleteToSmall(false)
                .setOrientationChangeListener(false)
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
                playerItemBeanList.get(current).setPlaying(false);
                current++;
                if (current < playerItemBeanList.size()) {
                    url = course.getVideos().get((int) mAdapter.getHeaderId(current)).getUrl();
                    playerItemBeanList.get(current).setPlaying(true);
                    player.setTitle(playerItemBeanList.get(current).getName());
                    file = new File(playerItemBeanList.get(current).getPath());
                    if (file.exists()) {
                        player.setUrl(file.toString()).play(0);
                    } else {
                        player.play(playerItemBeanList.get(current).getUrl());
                    }
//                            .play(playerItemBeanList.get(current).getUrl());
                } else {
                    player.toggleFullScreen();
                }
                mAdapter.notifyDataSetChanged();
            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */
            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */
                ToastUtils.showLong(mContext, "播放失败");

            }
        }).setTitle(playerItemBeanList.get(current).getName())//设置视频的titleName
                .setCoverImage(R.drawable.default_player);
        file = new File(playerItemBeanList.get(current).getPath());
        if (file.exists()) {
            player.setUrl(file.toString());
        } else {
            player.setUrl(playerItemBeanList.get(current).getUrl());
        }
        player.seekTo(position, true)
                .play(500);

        player.setScaleType(SuperPlayer.SCALETYPE_FITPARENT);
        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置

        newplayer_recycleview.scrollToPosition(current - 1);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_newplay;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && !isDownloadController) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.saveIntData(this, course.getId() + "", current);
        SharedPreferencesUtil.saveIntData(this, course.getId() + "play", player.getCurrentPosition());
        if (player != null) {
            player.onDestroy();

        }
    }

    @Override
    protected void doReceive(Intent action) {

    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(true);
            fab.setVisibility(View.GONE);
            newplayer_recycleview.setVisibility(View.VISIBLE);
        } else {
//            SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
            fab.setVisibility(View.GONE);
            newplayer_recycleview.setVisibility(View.GONE);
        }
    }

    /**
     * 控件的移动动画
     *
     * @param view
     * @param fromY
     * @param toY
     */

    public void translationY(final View view, int fromY, int toY) {
        ValueAnimator animator = null;
        animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setTarget(view);
        animator.setDuration(500).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(f);

            }

        });
    }

    /**
     * 控件的移动动画
     *
     * @param view
     * @param fromY
     * @param toY
     */

    public void padingY(final View view, int fromY, int toY) {
        ValueAnimator animator = null;
        animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setTarget(view);
        animator.setDuration(500).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setPadding(0, 0, 0, (int) f);

            }

        });
    }

    /**
     * 控件的Y值的动画
     *
     * @param view
     * @param fromY
     * @param toY
     * @param
     */

    public void setAnimY(final View view, int fromY, int toY) {
        ValueAnimator animator = null;
        animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setTarget(view);
        animator.setDuration(500).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                recycleParams.setMargins(0, (int) f, 0, 0);
                view.setLayoutParams(recycleParams);
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                translationY(view, 0, 300);
                setAnimY(newplayer_recycleview, player.getMeasuredHeight(), titleHeight);
                padingY(newplayer_recycleview, 0, down_bottom_height);
                translationY(newplay_title, -titleHeight, 0);
                translationY(newplayer_down_bottom, down_bottom_height, 0);
                isDownloadController = true;
                player.onPause();
                mAdapter.setDownloadController(true);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.title_back:
                setAnimY(newplayer_recycleview, titleHeight, player.getMeasuredHeight());
                padingY(newplayer_recycleview, down_bottom_height, 0);
                translationY(newplay_title, 0, -titleHeight);
                translationY(newplayer_down_bottom, 0, down_bottom_height);
                translationY(fab, 300, 0);
                view.bringToFront();
                isDownloadController = false;
                player.onResume();
                mAdapter.setDownloadController(false);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.newplayer_down:
                findViewById(R.id.title_back).callOnClick();
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int i = 0;
                        for (PlayerItemBean itemBean : playerItemBeanList) {
                            if (itemBean.isChecked()) {
                                //没有下载
                                TaskInfo info = new TaskInfo();
                                info.setFileName(itemBean.getName());
                            /*服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况*/
                                info.setTaskID(itemBean.getUrl());
                                info.setOnDownloading(true);
                                downLoadManager.addTask(info.getTaskID(), info.getTaskID(),
                                        info.getFileName(), itemBean.getPath());
                                i++;
                            }
                        }
                        subscriber.onNext(i);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Integer integer) {
                                if (integer > 0) {
                                    ToastUtils.showLong(mContext, "新增" + integer + "个下载任务");
                                } else {
                                    ToastUtils.showLong(mContext, "没有新增下载任务");
                                }
                            }
                        });
                break;
            case R.id.newplayer_all:

                for (PlayerItemBean bean : playerItemBeanList) {
                    bean.setChecked(newplayer_all.getText().toString().equals("全选"));
                }
                mAdapter.notifyDataSetChanged();
                if (newplayer_all.getText().toString().equals("全选")) {
                    newplayer_all.setText("全不选");
                } else {
                    newplayer_all.setText("全选");
                }
                break;
        }
    }


    /**
     * 格式化url
     *
     * @param name
     * @return
     */
    private String formatUrl(String name) {
        String u = "C01S00P00";
        if (name.indexOf(" ") > 0) {
            String[] s = name.substring(0, name.indexOf(" ")).split("\\.");
            if (s.length > 0) {
                u = "C" + String.format("%02d", Integer.parseInt(s[0])) + "S" + String.format("%02d", Integer.parseInt(s[1]));
                if (s.length > 2) {
                    u = u + "P" + String.format("%02d", Integer.parseInt(s[2]));
                } else {
                    u = u + "P00";
                }
            }
        }
        String s = name.substring(0, 2);
        if (s.contains(".")) {
            s = String.valueOf(s.charAt(0));
        }
        int i = Integer.parseInt(s) - 1;
        String endString = course.getVideos().get(i).getUrlend();
        if (StringUtils.isEmpty(endString)) {
            endString = "-300K.mp4";
        }
        u = course.getVideos().get(i).getUrl() + u + endString;
        return u;
    }
}
