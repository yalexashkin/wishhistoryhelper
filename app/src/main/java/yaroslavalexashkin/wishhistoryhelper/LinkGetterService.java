package yaroslavalexashkin.wishhistoryhelper;

import android.app.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LinkGetterService extends Service implements LinkReciever {
    public static boolean isServiceRunning;
    private final String LinkGetter_ChannelID = "Link getter service";
    private final String ShowLink_ChannelID = "Got link msg";
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
        if (intent != null) {
            if (intent.getIntExtra("destroyService", 0) == 1) {
                stopSelf();
                return Service.START_NOT_STICKY;
            }
            String link = intent.getStringExtra("link");
            if (link != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("link", link);
                clipboard.setPrimaryClip(clip);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancel(intent.getIntExtra("notificationId", 123));
                Toast.makeText(this, R.string.link_copied_text, Toast.LENGTH_SHORT).show();
                return Service.START_NOT_STICKY;
            }
        }
        Intent deleteIntent = new Intent(this, LinkGetterService.class);
        deleteIntent.putExtra("destroyService", 1);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                123,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notification = new NotificationCompat.Builder(this, LinkGetter_ChannelID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.link_getter_notification_title))
                .setContentText(getString(R.string.link_getter_notification_content))
                .setOngoing(true)
                .setContentIntent(deletePendingIntent)
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
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel(
                LinkGetter_ChannelID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
        ));
        getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel(
                ShowLink_ChannelID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
        ));
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        if (runner != null) {
            runner.destroy();
            runner = null;
        }
        stopForeground(Service.STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }
    @Override
    public void processLink(@NotNull WishLink wl) {
        int notificationId = Math.abs(new Random().nextInt());
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, ShowLink_ChannelID)
                .setSmallIcon(R.drawable.link)
                .setContentTitle(String.format(getString(R.string.link_notification_title), wl.region, wl.game))
                .setContentText(getString(R.string.click_to_copy_text));
        Intent intent = new Intent(this, LinkGetterService.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("link", wl.link);
        PendingIntent pi = PendingIntent.getService(this,notificationId,intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        nb.setContentIntent(pi);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(notificationId, nb.build());
    }
}