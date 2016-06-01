package com.xs.stocksettings;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class KillMmsReceiver extends BroadcastReceiver {
    public KillMmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        final Context mContext = context;
        new Timer().schedule(new TimerTask() {
            public void run() {
                ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

                try {
                    Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
                    method.invoke(mActivityManager, "com.android.mms");
                    Log.d("StockSettings", "Force stop com.android.mms successfully!");
                } catch (Exception e) {
                    Log.d("StockSettings", "Force stop com.android.mms failed !");
                }

                Intent intent = new Intent();
                intent.setAction("android.intent.action.BOOT_COMPLETED");
                intent.setPackage("com.android.mms");
                mContext.sendBroadcast(intent);
                Log.d("StockSettings", "Send broadcast(\"android.intent.action.BOOT_COMPLETED\") to com.android.mms !");
            }
        }, 5000);
    }
}
