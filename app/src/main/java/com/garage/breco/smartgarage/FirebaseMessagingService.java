package com.garage.breco.smartgarage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Created by stormset on 2016. 11. 03.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String[] from = Objects.requireNonNull(remoteMessage.getFrom()).split("/");
        int topic = Integer.parseInt(from[from.length - 1]);
        String payload = remoteMessage.getData().get("payload");
        if (topic == getResources().getInteger(R.integer.door_left_open_id)) {
            int seconds = Integer.parseInt(payload);

            long days = TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24L);
            long minutes = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);

            payload = Utils.timeToDisplayString(getApplicationContext(), days, hours, minutes);
        } else if (topic == getResources().getInteger(R.integer.car_arrived_id)) {
            payload = getResources().getString(R.string.car_arrived);
        } else if (topic == getResources().getInteger(R.integer.car_left_id)) {
            payload = getResources().getString(R.string.car_left);
        } else if (topic == getResources().getInteger(R.integer.close_id)) {
            payload = getResources().getString(R.string.door_closed);
        } else if (topic == getResources().getInteger(R.integer.open_id)) {
            payload = getResources().getString(R.string.door_opened);
        }

        Context context = getApplicationContext();

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (!powerManager.isInteractive()) { // if screen is not already on, turn it on (get wake_lock for 10 seconds)
                PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "smartgarage:MH24_SCREENLOCK");
                wl.acquire(5000);
                PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "smartgarage:MH24_SCREENLOCK");
                wl_cpu.acquire(5000);
            }
        } else {
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "smartgarage:MH24_SCREENLOCK");
            wl.acquire(5000);
            PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "smartgarage:MH24_SCREENLOCK");
            wl_cpu.acquire(5000);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, FirebaseMessagingService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(pendingIntent)
                .setContentTitle("SmartGarage")
                .setContentText(payload)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
}
