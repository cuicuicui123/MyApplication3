package com.cui.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Cui on 2017/3/6.
 *
 * @Description 保活管理工具类，用于打开和销毁KeepLiveActivity
 */

public class KeepLiveManager {
    private static volatile KeepLiveManager mInstance;
    private Activity mActivity;

    private KeepLiveManager(){

    }
    public static KeepLiveManager getInstance(){
        if (mInstance == null) {
            synchronized (KeepLiveManager.class) {
                if (mInstance == null) {
                    mInstance = new KeepLiveManager();
                }
            }
        }
        return mInstance;
    }

    public void startKeepLiveActivity(Context context){
        Intent it = new Intent(context, KeepAliveActivity.class);
        context.startActivity(it);
    }

    public void setActivity(Activity activity){
        if (mActivity != null) {
            mActivity.finish();
        }
        mActivity = activity;
    }

}
