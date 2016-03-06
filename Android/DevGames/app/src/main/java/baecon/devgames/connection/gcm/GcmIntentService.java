package baecon.devgames.connection.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import baecon.devgames.R;
import baecon.devgames.ui.activity.MainActivity;
import baecon.devgames.util.PreferenceManager;


public class GcmIntentService extends IntentService{

    private static final String TAG = GcmIntentService.class.getName();
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public GcmIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();

            if (extras == null || extras.isEmpty()) {
                Log.i(TAG, "onHandleIntent: Empty push message received");
                return;
            }

            GcmMessageType type = GcmMessageType.valueOf(intent.getStringExtra("message"));

            String notificationText = null;

            switch (type) {

                case HIGH_SCORE_CHANGED:

                        notificationText = intent.getStringExtra("text");
                        if ((notificationText == null || notificationText.isEmpty())) {
                            notificationText = getString(R.string.new_message);
                        }

                        showNotification(
                                this,
                                GcmMessageType.HIGH_SCORE_CHANGED.ordinal(),
                                getString(R.string.app_name),
                                notificationText,
                                notificationText,
                                false
                        );

                    break;



                default:
                    Log.w(TAG, "onHandleIntent: Type is not a known type in Message.Type: " + String.valueOf(type));
            }


        }
        finally {
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    protected void showNotification(Context context, final int id, String title, String content, String ticker,
                                    boolean forceNotification) {

        PreferenceManager preferenceManager = PreferenceManager.get(context);

        // Skip isNotificationsEnabled() check when forcing a notification
        if (!forceNotification) {

            // Exit this function when the user does not like to get notifications
            if (!preferenceManager.isNotificationsEnabled()) {
                Log.d(TAG, "showNotification: Showing notification skipped, disabled by user setting");
                return;
            }
        }
        else {
            Log.w(TAG, "showNotification: Forcing a notification, while the user preferred to have no notifications!");
        }

        // If the given title is null, use the app name
        if (title == null) {
            title = context.getString(R.string.app_name);
        }

        if (content == null) {
            content = "";
        }

        if (ticker == null) {
            ticker = "";
        }

        final NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;

        // Build the intent that will be fired when the user clicks the notification
        Intent notificationIntent = new Intent(context, MainActivity.class)

                // We don't like to have the same activity alive twice
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationIntent.putExtra("random", System.currentTimeMillis());

        // The pending intent enables android to fire notification intent when the user clicks the notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true) // Removes the notification from the notification centre when clicked
                .setContentIntent(contentIntent)
                .setTicker(ticker) // The text that scrolls through the notification bar when the notification is added
                .setContentTitle(title) // The title of the notification
                .setContentText(content) // The second line of the notification
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.devgames_logo_icon))
                .setSmallIcon(R.drawable.devgames_logo_icon)
                .setWhen(System.currentTimeMillis()) // The time when the event for this notification happened
                .build();

        // Retrieve the URI of the ringtone that the user set for notifications
        String customSoundUri = preferenceManager.getNotificationRingtone();

        // An empty uri means no ringtone. This is displayed to the user in settings as 'silent'
        if (customSoundUri.length() > 0) {

            // Parse the uri
            notification.sound = Uri.parse(customSoundUri);

            // This is a notification. Other audio streams will be semi muted if the given ringtone is played
            notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
        }

        // Retrieve whether the device has to vibrate when adding the notification
        boolean vibrate = PreferenceManager.get(context).isNotificationVibrationEnabled();
        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        // Fire the notification on the UI thread
        final Notification finalNotification = notification;
        Runnable runnable = new Runnable() {

            public void run() {
                nm.notify(id, finalNotification);
            }
        };

        uiThreadHandler.post(runnable);
    }

}
