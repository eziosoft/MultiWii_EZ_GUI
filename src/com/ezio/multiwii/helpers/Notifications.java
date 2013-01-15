package com.ezio.multiwii.helpers;

import java.util.Random;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.ezio.multiwii.R;

public class Notifications {

	Random rnd = new Random();
	NotificationManager mNotificationManager;
	Context context;

	public Notifications(Context context) {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		this.context = context;
	}

	public void displayNotification(String title, String text, boolean Sound, int Id, boolean isPresistant) {
		if (Id == 0) {
			Id = rnd.nextInt();
		}
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon).setContentTitle(title).setContentText(text);
		if (Sound)
			mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		// mBuilder.setOnlyAlertOnce(false);
		mBuilder.setTicker(title + ":" + text);
		mBuilder.setOngoing(isPresistant);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(Id, mBuilder.build());
	}

	public void Cancel(int Id) {
		mNotificationManager.cancel(Id);
	}
}
