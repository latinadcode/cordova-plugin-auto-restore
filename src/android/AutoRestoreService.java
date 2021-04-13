package com.latinad.cordova.plugin;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

// import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoRestoreService extends Service {
    @Override
    public void onCreate() {
        Log.d("MonitorDebug", "AutoRestoreService onCreate called");
        super.onCreate();
        doForegroundSetting();
        startMonitor();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MonitorDebug", "AutoRestoreService onStartCommand called");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("MonitorDebug", "AutoRestoreService onTaskRemoved called");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.d("MonitorDebug", "AutoRestoreService onDestroy called");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

     private void doForegroundSetting() {
         Log.d("MonitorDebug", "AutoRestoreService doForegroundSetting called");

         String mainPackageName = getApplicationContext().getPackageName();
         String activityToStart = mainPackageName + ".MainActivity";
         try {
             Class<?> c = Class.forName(activityToStart);
             Intent intent = new Intent(getApplicationContext(), c);
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

             PendingIntent pendingIntent = PendingIntent.getActivity((Context)this, 1, intent, 0);

             Resources resources = getApplicationContext().getResources();

             NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder((Context)this, getChannelID());
             Notification notification = notificationBuilder.setOngoing(true)
                     .setSmallIcon(resources.getIdentifier("ic_launcher", "mipmap", mainPackageName))
                     .setContentTitle(getString(resources.getIdentifier("app_name", "string", mainPackageName)))
                     .setCategory(NotificationCompat.CATEGORY_SERVICE)
                     .setContentIntent(pendingIntent)
                     .build();

             startForeground(1000, notification);
         } catch (ClassNotFoundException ignored) {
             // Nothing to do
         }


     }

     private String getChannelID() {
         Log.d("MonitorDebug", "AutoRestoreService getChannelID called");
         if (Build.VERSION.SDK_INT >= 26) {
             String mainPackageName = getApplicationContext().getPackageName();
             Resources resources = getApplicationContext().getResources();

             String appName = getResources().getString(resources.getIdentifier("app_name", "string", mainPackageName));
             // String packageName = getPackageName();
             NotificationChannel notificationChannel = new NotificationChannel(mainPackageName, appName, NotificationManager.IMPORTANCE_DEFAULT);
             notificationChannel.setLockscreenVisibility(0);
             NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
             if (notificationManager != null)
                 notificationManager.createNotificationChannel(notificationChannel);
             return mainPackageName;
         }
         return "";
     }

    public void startMonitor() {
        TimerTask task = new TimerTask() {
            public void run() {
                Date currentTime = Calendar.getInstance().getTime();
                // Log.d("MonitorDebug", "I'm still alive! " + currentTime );
                // Log.d("MonitorDebug", "Restoring...");

                String mainPackageName = getApplicationContext().getPackageName();
                String activityToStart = mainPackageName + ".MainActivity";
                try {
                    Class<?> c = Class.forName(activityToStart);
                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
                    ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
                    if (!componentInfo.getPackageName().equals(mainPackageName)) {
                        Intent intent = new Intent(getApplicationContext(), c);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                } catch (ClassNotFoundException ignored) {
                    // Nothing to do
                }
            }
        };
        //schedule the timer, to wake up every 20 seconds
        Timer timer = new Timer();
        timer.schedule(task, 20000, 20000);
    }
}
