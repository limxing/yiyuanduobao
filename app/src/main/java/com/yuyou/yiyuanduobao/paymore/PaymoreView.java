package com.yuyou.yiyuanduobao.paymore;


import com.yuyou.yiyuanduobao.bean.PayType;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public interface PaymoreView {
    void showLoading(String msg);

    void getListSuccess();

    void setAdapter(PaymoreAdapter adapter);

    void svpDismiss();

    void showToast(String msg);

    void payView(String sOrderId, String sVacCode, PayType payType);

    void paySuccess();

    void payFail();

    void getListFail();
}
