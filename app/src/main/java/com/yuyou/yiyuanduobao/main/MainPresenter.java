package com.yuyou.yiyuanduobao.main;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.BuySuccess;
import com.yuyou.yiyuanduobao.bean.Course;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.bean.Version;
import com.yuyou.yiyuanduobao.dbmodel.DBBuy;
import com.yuyou.yiyuanduobao.utils.HttpSend;
import com.yuyou.yiyuanduobao.utils.SecUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
import rx.Subscriber;
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

    @Override
    public boolean isBuy(int position) {
        return adapter.isBuy(position);
    }

    /**
     * 条目的点击事件
     *
     * @param position
     */

    public void toBuy(int position) {
//                BuyData buyData=  new BuyData(adapter.getList().get(position).getId(), ProjectApplication.user.getPhone());
//                buyData.save();
//                adapter.notifyBuydata();
        if (ProjectApplication.user.getAccount() != null && ProjectApplication.user.getAccount() >= 1) {
            mainView.showGoldDialog(adapter.getList().get(position), 1);
        } else {
            pay(adapter.getList().get(position));
//            mainView.payView("","",adapter.getList().get(position));
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
                    ProjectApplication.liteOrm.deleteAll(DBBuy.class);
                    for (BuyData buyData : list) {
                        ProjectApplication.liteOrm.save(new DBBuy(ProjectApplication.user.getPhone(), buyData.getCourseid()));
                    }

                    //...
                    adapter.notifyBuydata();
                    mainView.svpDismiss();
                    mainView.stopFresh(true);
                } else {
                    e.printStackTrace();
                    switch (e.getErrorCode()) {
                        case 9016:
                            mainView.showErrorWithStatus("无法连接网络");
                            break;
                        default:
                            mainView.showErrorWithStatus("获取购买信息失败，请刷新列表");

                            break;
                    }
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

    /**
     * 访问网络是否购买成功
     * @param orderId
     * @param currentCurse
     */
    @Override
    public void getPayifSuccess(final String orderId, final Course currentCurse) {
        rx.Observable.create(new rx.Observable.OnSubscribe<BuySuccess>() {
            @Override
            public void call(Subscriber<? super BuySuccess> subscriber) {

                String post = "phone="+ProjectApplication.user.getPhone()+"&orderid="+orderId+"&price=100";

                String result = postDownloadJson("http://yuyou.meiquant.com/api/worldplay/account/addcoin",post);
                LogUtils.i("请求支付结果查询:"+result);
                if (result==null){
                   subscriber.onError(new Throwable("请求失败，请重试"));
                }else{
                  subscriber.onNext(JSON.parseObject(result,BuySuccess.class));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BuySuccess>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mainView.showErrorWithStatus(throwable.getMessage());

                    }

                    @Override
                    public void onNext(BuySuccess buySuccess) {
                        if (buySuccess.isStatus()){
                           paySuccess(currentCurse);
                        }else{
                            mainView.showErrorWithStatus(buySuccess.getMsg());
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
        json.put("mobile", "18686549472");
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
mainView.svpDismiss();
                        }

                        @Override
                        public void onNext(Version version) {
//                            version.setCourse(2);
//                            version.setTitle("课程有更新");
//                            version.setValue("添加了毛概");
//                            version.setUrl("http://www.leefeng.me/download/beidacourse1.1.apk");
                            LogUtils.i(version.toString());
                            try {
//                                if (version.getVersion() > ((Activity) mainView).getPackageManager().
//                                        getPackageInfo(((Activity) mainView).getPackageName(), 0).versionCode) {
////                                    mainView.updateApp(version);
//                                    return;
//                                }
                                if (JSON.parseObject(IOUtils.streamToString(new FileInputStream(couseFile)))
                                        .getIntValue("courseVersion") != version.getCourse())
//                                    mainView.updateDialog(version.getTitle(), version.getValue());
                                {
                                    LogUtils.i("版本不一致更新课程");
                                    upDateCouse();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    });

        } else {
            upDateCouse();
        }

    }

    /**
     * 联网获取课程json
     */
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

    /**
     * w为界面更新课程
     */
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



    /**
     * 从网络获取json数据,(String byte[})

     * @param path
     * @return
     */
    public static String getJsonByInternet(String path){
        try {
            URL url = new URL(path.trim());
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }






//获取其他页面的数据
    /**
     * POST请求获取数据
     */
    public  String postDownloadJson(String path,String post){
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(post);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while((len=bis.read(arr))!= -1){
                bos.write(arr,0,len);
                bos.flush();
            }
            bos.close();
            return bos.toString("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
