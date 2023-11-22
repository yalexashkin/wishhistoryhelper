package yaroslavalexashkin.wishhistoryhelper;

import android.app.*;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class LinkGetterService extends Service implements LinkReciever {
    /*LinkGetterService() {
        Notification notification =
                new NotificationCompat.Builder(this, "Link getter service")
                        // Create the notification to display while the service
                        // is running
                        .build();
        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
        }
        ServiceCompat.startForeground(
                /* service = *//*
                this,
                /* id = *//* 100, // Cannot be 0
                /* notification = *//* notification,
                /* foregroundServiceType = *//* type
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }*/


    public static boolean isServiceRunning;
    private final String CHANNEL_ID = "Link getter service";
    Notification notification;
    LogcatRunner runner = null;

    public LinkGetterService() {
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        runner = new LogcatRunner(this, this);
        isServiceRunning = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent nintent = new Intent(this, LinkGetterService.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,nintent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Link getter is running (tap to stop)")
                .setContentText("open wish history to capture a link")
                //.addAction(new NotificationCompat.Action(R.drawable.ic_launcher_background, "Stop", null))
                .setOngoing(true)
                .setContentIntent(pi)
                .build();

        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
        }
        ServiceCompat.startForeground(
                /* service = */
                this,
                /* id = */ 100, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */ type
        );
        return START_STICKY;
    }

    private void createNotificationChannel() {
        getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
        ));
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        if (runner != null) {
            runner.destroy();
            runner = null;
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void processLink(WishLink wl) {
        //send notification
        Log.w("hgfvkjuygk", "got link");
    }
}