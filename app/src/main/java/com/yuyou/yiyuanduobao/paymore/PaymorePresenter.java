package com.yuyou.yiyuanduobao.paymore;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public class PaymorePresenter implements PaymorePreInterface {
    private PaymoreView paymoreView;

    public PaymorePresenter(PaymoreView paymoreView) {
        this.paymoreView = paymoreView;
    }

    @Override
    public void destory() {
        paymoreView = null;
    }
}
