package com.yuyou.yiyuanduobao.main;

import android.widget.Toast;

import com.unicom.xiaowo.Pay;
import com.yuyou.yiyuanduobao.utils.HttpSend;
import com.yuyou.yiyuanduobao.utils.SecUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainPresenter implements MainPreInterface {
    private MainView mainView;
    private String sOrderId;
    private String sVacCode;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void destory() {
        mainView = null;
    }

    @Override
    public void pay() {
        mainView.showLoading("正在提交订单");
        initPay();
    }

    private void initPay() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ss = getCode();
                    JSONObject json = new JSONObject(ss);
                    sOrderId = json.getString("orderid");
                    sVacCode = json.getString("vacCode");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (sOrderId == null || sVacCode == null) {
                    mainView.showToast("订单号为空");
                    return;
                }

                mainView.payView(sOrderId, sVacCode);

            }

        });
        t.start();
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
}
