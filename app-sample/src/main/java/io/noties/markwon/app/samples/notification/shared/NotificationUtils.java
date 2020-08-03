package io.noties.markwon.app.samples.notification.shared;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import io.noties.markwon.app.R;

public abstract class NotificationUtils {

  private static final int ID = 2;
  private static final String CHANNEL_ID = "2";

  public static void display(@NonNull Context context, @NonNull CharSequence cs) {
    final NotificationManager manager = context.getSystemService(NotificationManager.class);
    if (manager == null) {
      return;
    }

    ensureChannel(manager, CHANNEL_ID);

    final Notification.Builder builder = new Notification.Builder(context)
      .setSmallIcon(R.drawable.ic_stat_name)
      .setContentTitle(context.getString(R.string.app_name))
      .setContentText(cs)
      .setStyle(new Notification.BigTextStyle().bigText(cs));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID);
    }

    manager.notify(ID, builder.build());
  }

  public static void display(@NonNull Context context, @NonNull RemoteViews remoteViews) {
    final NotificationManager manager = context.getSystemService(NotificationManager.class);
    if (manager == null) {
      return;
    }

    ensureChannel(manager, CHANNEL_ID);

    final Notification.Builder builder = new Notification.Builder(context)
      .setSmallIcon(R.drawable.ic_stat_name)
      .setContentTitle(context.getString(R.string.app_name));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      builder
        .setCustomContentView(remoteViews)
        .setCustomBigContentView(remoteViews);
    } else {
      builder.setContent(remoteViews);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID);
    }

    manager.notify(ID, builder.build());
  }

  @SuppressWarnings("SameParameterValue")
  private static void ensureChannel(@NonNull NotificationManager manager, @NonNull String channelId) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return;
    }

    final NotificationChannel channel = manager.getNotificationChannel(channelId);
    if (channel == null) {
      manager.createNotificationChannel(new NotificationChannel(
        channelId,
        channelId,
        NotificationManager.IMPORTANCE_DEFAULT
      ));
    }
  }

  private NotificationUtils() {
  }
}
