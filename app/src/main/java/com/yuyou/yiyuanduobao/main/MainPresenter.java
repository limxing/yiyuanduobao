package com.yuyou.yiyuanduobao.main;

/**
 * @author FengTing
 * @date 2017/04/24 16:00:40
 */
public class MainPresenter implements MainPreInterface {
    private MainView mainView;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void destory() {
        mainView = null;
    }
}
