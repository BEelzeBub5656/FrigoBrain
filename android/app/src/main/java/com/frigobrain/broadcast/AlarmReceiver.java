package com.frigobrain.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.frigobrain.service.ExpiryCheckService;
import com.frigobrain.service.IotDataSyncService;

/** AlarmManager 定时触发：重新启动后台服务 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 重新启动过期检查
        Intent checkIntent = new Intent(context, ExpiryCheckService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(checkIntent);
        } else {
            context.startService(checkIntent);
        }

        // 重新启动同步服务
        Intent syncIntent = new Intent(context, IotDataSyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(syncIntent);
        } else {
            context.startService(syncIntent);
        }
    }
}
