package com.yuyou.yiyuanduobao.paymore;

import com.yuyou.yiyuanduobao.bean.PayType;

/**
 * @author FengTing
 * @date 2017/04/24 17:36:13
 */
public interface PaymorePreInterface {
     void destory();

    void paySuccess(PayType payType);
}
