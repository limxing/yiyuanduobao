package com.yuyou.yiyuanduobao.welcome;

/**
 * @author FengTing
 * @date 2017/04/25 10:39:52
 */
public class WelcomePresenter implements WelcomePreInterface {
    private WelcomeView welcomeView;

    public WelcomePresenter(WelcomeView welcomeView) {
        this.welcomeView = welcomeView;
    }

    @Override
    public void destory() {
        welcomeView = null;
    }
}
