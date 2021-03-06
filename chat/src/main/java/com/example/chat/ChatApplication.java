package com.example.chat;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.example.chat.base.Constant;
import com.example.chat.bean.CustomInstallation;
import com.example.chat.dagger.ChatMainComponent;
import com.example.chat.dagger.ChatMainModule;
import com.example.chat.dagger.DaggerChatMainComponent;
import com.example.chat.manager.LocationManager;
import com.example.chat.util.LogUtil;
import com.example.commonlibrary.BaseApplication;
import com.example.commonlibrary.module.IAppLife;
import com.example.commonlibrary.module.IModuleConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.statistics.AppStat;
import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.util.DeviceUtils;
import okhttp3.OkHttpClient;

/**
 * 项目名称:    NewFastFrame
 * 创建人:        陈锦军
 * 创建时间:    2017/10/9      15:32
 * QQ:             1981367757
 */

public class ChatApplication implements IModuleConfig,IAppLife {


    private static ChatMainComponent chatMainComponent;
    @Override
    public void injectAppLifecycle(Context context, List<IAppLife> iAppLifes) {
        iAppLifes.add(this);
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycleCallbackses) {

    }

    @Override
    public void attachBaseContext(Context base) {

    }

    public static ChatMainComponent getChatMainComponent() {
        return chatMainComponent;
    }

    @Override
    public void onCreate(Application application) {
        chatMainComponent= DaggerChatMainComponent.builder().appComponent(BaseApplication.getAppComponent())
                .chatMainModule(new ChatMainModule()).build();
        Bmob.initialize(application, Constant.KEY);
        AppStat.i(Constant.KEY, null);
        LogUtil.e("1服务器端初始化完成");
        CustomInstallation.getCurrentInstallation(application).save();
        LogUtil.e("设备ID在这里上传了");
        BmobPush.startWork(application);
        LogUtil.e("推送服务初始化完成");
        initOkHttp();
        initSmallVideo(application);
        initLocationClient();
    }


    private void initLocationClient() {
//                LocationManager.getInstance().registerLocationListener(this);
        LocationManager.getInstance().startLocation();
    }


    private void initSmallVideo(Application application) {
        LogUtil.e("初始化小视频缓存目录");
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/chen/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        VCamera.setDebugMode(true);
        VCamera.initialize(application);
    }

    private void initOkHttp() {
        OkHttpUtils.initClient(new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build());
    }

    @Override
    public void onTerminate(Application application) {
        chatMainComponent=null;
    }
}
