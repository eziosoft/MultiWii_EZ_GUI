/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.helpers;

import java.util.Random;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.ezio.multiwii.R;
import com.ezio.multiwii.Main.MainMultiWiiActivity;

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
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_icon).setContentTitle(title).setContentText(text);
		if (Sound)
			mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		// mBuilder.setOnlyAlertOnce(false);
		mBuilder.setTicker(title + ":" + text);
		mBuilder.setOngoing(isPresistant);
		mBuilder.setAutoCancel(true);

		// Intent notificationIntent = new Intent(context,
		// MainMultiWiiActivity.class);

		Intent notificationIntent = new Intent(context, MainMultiWiiActivity.class);
		// notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You
		// need this if starting
		// the activity from a service
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);

		mNotificationManager.notify(Id, mBuilder.build());
	}

	public void Cancel(int Id) {
		mNotificationManager.cancel(Id);
	}
}
