package com.bigdo;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.xuexiang.xpage.AppPageConfig;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.XUI;


public class MyAPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化UI框架
        XUI.init(this);
        XUI.debug(true);

        //Xpage初始化
        PageConfig.getInstance()
                .setPageConfiguration(context -> {
                    //自动注册页面,是编译时自动生成的，build一下就出来了。如果你还没使用@Page的话，暂时是不会生成的。
                    return AppPageConfig.getInstance().getPages(); //自动注册页面
                })
                .debug("PageLog")       //开启调试
                .setContainActivityClazz(XPageActivity.class) //设置默认的容器Activity
                .enableWatcher(false)   //设置是否开启内存泄露监测
                .init(this);  //初始化页面配置

        //全局异常捕获
        new Handler(Looper.getMainLooper()).post(() -> {
            //主线程异常拦截
            while (true) {
                try {
                    Looper.loop();//主线程的异常会从这里抛出
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        //所有线程异常拦截，由于主线程的异常都被我们catch住了，所以下面的代码拦截到的都是子线程的异常
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        });
    }
}
