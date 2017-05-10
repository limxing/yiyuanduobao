package com.yuyou.yiyuanduobao.paymore;

import com.yuyou.yiyuanduobao.ProjectApplication;
import com.yuyou.yiyuanduobao.bean.BuyData;
import com.yuyou.yiyuanduobao.bean.PayType;
import com.yuyou.yiyuanduobao.bean.User;
import com.yuyou.yiyuanduobao.utils.HttpSend;
import com.yuyou.yiyuanduobao.utils.SecUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
import me.leefeng.library.utils.LogUtils;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public class PaymorePresenter implements PaymorePreInterface {
    private PaymoreView paymoreView;
    private String sOrderId = null;
    private String sVacCode = null;

    public PaymorePresenter(PaymoreView paiView) {
        this.paymoreView = paiView;

        if (ProjectApplication.payTypes == null) {
            paymoreView.showLoading("正在初始化");
            BmobQuery<PayType> query = new BmobQuery();
            query.findObjects(new FindListener<PayType>() {
                @Override
                public void done(List<PayType> list, BmobException e) {
                    if (e == null) {
                        ProjectApplication.payTypes = list;
                        LogUtils.i(list.toString());
                        paymoreView.getListSuccess();
                        initList();
                    } else {
                        e.printStackTrace();
                        paymoreView.getListFail();
                    }
                }
            });
        } else {
            initList();
        }

    }

    /**
     * 初始化Adapter
     */
    private void initList() {

        PaymoreAdapter adapter = new PaymoreAdapter();
        adapter.setListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtils.i("PayMore:" + position);
                PayType payType = ProjectApplication.payTypes.get(position);
                paymoreView.showLoading("正在提交订单..");
                initPay(payType);

            }
        });
        paymoreView.setAdapter(adapter);
    }

    @Override
    public void destory() {
        paymoreView = null;
    }

    @Override
    public void paySuccess(final PayType payType) {
        paymoreView.showLoading("正在支付");
        final User user = ProjectApplication.user;
        Long count = 0l;
        if (user.getAccount() != null) {
            count = user.getAccount();
        }
        final Long account = count;

        user.setAccount(account + payType.getPrice() / 100);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    paymoreView.paySuccess();
                } else {
                    e.printStackTrace();

                    user.setAccount(account);
                    paymoreView.payFail();
                }
            }
        });
    }

    private void initPay(final PayType payType) {
        sOrderId = null;
        sVacCode = null;
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String ss = getCode(payType);
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
                            paymoreView.showToast("获取订单失败，请稍后再试");
                            paymoreView.svpDismiss();
                        } else {
                            paymoreView.payView(sOrderId, sVacCode, payType);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.i("onError");
                        paymoreView.showToast("获取订单失败，请稍后再试");
                        paymoreView.svpDismiss();
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.i("onComplete:");
                    }
                });

    }

    public String getCode(PayType payType) throws JSONException, IOException {
        LogUtils.i("getCode:" + payType.getPayid() + "=price=" + payType.getPrice());
        String url = "http://mobilepay.wocheng.tv:8090/wochengPay/wosdk/generateOrderId?channelCode=50021" +
                "&protocolVersion=1001";
        JSONObject json = new JSONObject();
        json.put("spcode", "1001");
        json.put("appid", "557950046");
        json.put("payid", payType.getPayid());
        json.put("mobile", ProjectApplication.user.getPhone());
        json.put("price", payType.getPrice());
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

}
