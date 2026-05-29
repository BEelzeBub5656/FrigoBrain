package com.frigobrain.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.frigobrain.service.ExpiryCheckService;
import com.frigobrain.service.IotDataSyncService;

/** 开机自启：启动后台同步服务和过期检查服务 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 启动IoT数据同步
            Intent syncIntent = new Intent(context, IotDataSyncService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(syncIntent);
            } else {
                context.startService(syncIntent);
            }

            // 启动过期检查
            Intent checkIntent = new Intent(context, ExpiryCheckService.class);
            context.startService(checkIntent);
        }
    }
}
