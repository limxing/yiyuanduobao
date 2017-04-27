package com.yuyou.yiyuanduobao.main;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.bean.Version;
import com.yuyou.yiyuanduobao.utils.HttpSend;
import com.yuyou.yiyuanduobao.utils.SecUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.leefeng.library.utils.IOUtils;
import me.leefeng.library.utils.LogUtils;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Func1;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainPresenter implements MainPreInterface {
    private VideoAdapter adapter;
    private MainView mainView;
    private String sOrderId;
    private String sVacCode;
    private File couseFile;
    private MainApi mainApi;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        adapter = new VideoAdapter();
        mainView.setAdapter(adapter);
//        updateCourse();
//        initUpdata();

    }

    @Override
    public void destory() {
        mainView = null;
    }

    @Override
    public void pay(Course course) {
        mainView.showLoading("正在提交订单");
        initPay(course);
    }

    @Override
    public void login(String phone) {

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null && list != null && list.size() > 0) {
                    ProjectApplication.user = list.get(0);

                    mainView.loginSuccess();
                } else {
                    e.printStackTrace();
                    mainView.loginFail();
                }
            }
        });
    }

    /**
     * 条目的点击事件
     *
     * @param position
     */
    @Override
    public void isBuy(final int position) {
        boolean isBuy = adapter.isBuy(position);
        if (isBuy) {
            mainView.openPlayer(position);
        } else {

//                BuyData buyData=  new BuyData(adapter.getList().get(position).getId(), ProjectApplication.user.getPhone());
//                buyData.save();
//                adapter.notifyBuydata();
            if (ProjectApplication.user.getAccount() != null && ProjectApplication.user.getAccount() >= 100) {
                mainView.showGoldDialog(adapter.getList().get(position), 100);
            } else {
                pay(adapter.getList().get(position));
            }


        }
    }


    /**
     * 登陆成功后，查询已购买列表
     */
    @Override
    public void getCourseList() {
        BmobQuery<BuyData> query = new BmobQuery<>();
        query.addWhereEqualTo("user", ProjectApplication.user.getObjectId());
        query.findObjects(new FindListener<BuyData>() {
            @Override
            public void done(List<BuyData> list, BmobException e) {
                if (e == null) {
                    LogUtils.i(list.toString());
                    ProjectApplication.buyList = list;
                    //缓存本地
                    //...
                    adapter.notifyBuydata();
                    mainView.svpDismiss();
                    mainView.stopFresh(true);
                } else {
                    mainView.showErrorWithStatus("获取购买信息失败，请刷新列表");
                    mainView.stopFresh(false);
                }

            }
        });
    }

    /**
     * 购买成功，添加数据库
     */
    @Override
    public void paySuccess(Course course) {
        BuyData buyData = new BuyData();
        buyData.setUser(ProjectApplication.user);
        buyData.setCourseid(course.getId());
        buyData.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mainView.buySuccess();

                } else {
                    e.printStackTrace();
                    mainView.showErrorWithStatus("购买失败");
                }
            }
        });
    }

    /**
     * 用金币支付课程
     *
     * @param course
     * @param money
     */
    @Override
    public void buyWithGold(final Course course, int money) {
        final User user = ProjectApplication.user;
        Long count = user.getAccount() - money;
        user.setAccount(count);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //添加购买了的列表
                    BuyData buyData = new BuyData();
                    buyData.setCourseid(course.getId());
                    buyData.setUser(user);
                    buyData.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                login(user.getPhone());
                            } else {
                                e.printStackTrace();
                                mainView.showErrorWithStatus("支付失败，请重试");
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                    mainView.showErrorWithStatus("支付失败，请重试");
                }
            }
        });
    }

    private void initPay(final Course course) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String ss = getCode();
                if (ss != null) {
                    JSONObject json = new JSONObject(ss);
                    sOrderId = json.getString("orderid");
                    sVacCode = json.getString("vacCode");
                }
                e.onNext(ss);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogUtils.i("onSubscribe:" + d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        LogUtils.i("onNext:" + s);
                        if (sOrderId == null || sVacCode == null) {
                            mainView.showToast("获取订单失败，请稍后再试");
                            mainView.svpDismiss();
                        } else {
                            mainView.payView(sOrderId, sVacCode, course);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.i("onError");
                        mainView.showToast("获取订单失败，请稍后再试");
                        mainView.svpDismiss();
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.i("onComplete:");
                    }
                });

    }


    public String getCode() throws JSONException, IOException {

        String url = "http://mobilepay.wocheng.tv:8090/wochengPay/wosdk/generateOrderId?channelCode=50021" +
                "&protocolVersion=1001";
        JSONObject json = new JSONObject();
        json.put("spcode", "1001");
        json.put("appid", "557950046");
        json.put("payid", "55795004601");
        json.put("mobile", ProjectApplication.user.getPhone());
        json.put("price", 100);
        json.put("appname", "一元夺宝");
        json.put("payname", "1001");
        json.put("imsi", "1234654641");
        json.put("cpparam", "1001");
        String key = "074dd3792d3ca1a94fb9763a60e7cec8";
        String body = SecUtil.AESEncrypt(json.toString(), key);


        String ans = HttpSend.post(url, body);
        System.out.println("getCode::" + ans);
        return ans;
    }

    private final String course = "course.json";

    public void initUpdata() {
        couseFile = new File(((Activity) mainView).getCacheDir(), course);
        mainApi = new Retrofit.Builder()
                .baseUrl("http://leefeng.me")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(MainApi.class);
        if (couseFile.exists()) {
            updateCourse();
            mainApi.getVersion()
                    .subscribeOn(rx.schedulers.Schedulers.io())
                    .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(new rx.Subscriber<Version>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Version version) {
//                            version.setCourse(2);
//                            version.setTitle("课程有更新");
//                            version.setValue("添加了毛概");
//                            version.setUrl("http://www.leefeng.me/download/beidacourse1.1.apk");
                            try {
                                if (version.getVersion() > ((Activity) mainView).getPackageManager().
                                        getPackageInfo(((Activity) mainView).getPackageName(), 0).versionCode) {
//                                    mainView.updateApp(version);
                                    return;
                                }
                                if (JSON.parseObject(IOUtils.streamToString(new FileInputStream(couseFile)))
                                        .getIntValue("courseVersion") != version.getCourse())
//                                    mainView.updateDialog(version.getTitle(), version.getValue());
                                {
                                    upDateCouse();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    });

        } else {
            upDateCouse();
        }

    }

    public void upDateCouse() {
        mainView.showLoading("正在更新课程");
        mainApi.getCourse()
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.schedulers.Schedulers.io())
                .map(new Func1<ResponseBody, Integer>() {
                    @Override
                    public Integer call(ResponseBody responseBody) {
                        int i = 0;
                        try {
                            FileOutputStream fout = new FileOutputStream(couseFile);
//                                openFileOutput(couseFile.toString(), MODE_PRIVATE);
                            byte[] bytes = responseBody.bytes();
                            fout.write(bytes);
                            fout.close();
                            i = 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new Integer(i);
                    }
                })
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mainView.showErrorWithStatus("更新失败，请稍后重试");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer.intValue() == 1) {
                            mainView.updateCourseSuccess();
                            updateCourse();
                        } else {
                            mainView.showErrorWithStatus("更新失败，请稍后重试");
                        }
                    }
                });

    }

    private void updateCourse() {
        rx.Observable.create(new rx.Observable.OnSubscribe<List<Course>>() {
            @Override
            public void call(rx.Subscriber<? super List<Course>> subscriber) {
                try {
                    subscriber.onNext(JSON.parseArray(JSON.parseObject(IOUtils.streamToString(new FileInputStream(
                            new File(((Activity) mainView).getCacheDir(), "course.json")))).getString("courses"), Course.class)
                    );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }).observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<List<Course>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Course> courses) {
                        ProjectApplication.cList = courses;
                        adapter.setList(courses);
                        adapter.notifyDataSetChanged();
                    }
                });

    }


}
