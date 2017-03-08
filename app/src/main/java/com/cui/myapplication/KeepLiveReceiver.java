package com.cui.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Cui on 2017/3/6.
 *
 * @Description 监听屏幕状态变化广播，黑屏之后启动KeepLiveActivity
 */

public class KeepLiveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            KeepLiveManager.getInstance().startKeepLiveActivity(context);
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
//            KeepLiveManager.getInstance().finishKeepAliveActivity(context);
        }
    }
}
