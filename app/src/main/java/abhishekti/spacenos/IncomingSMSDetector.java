package abhishekti.spacenos;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class IncomingSMSDetector extends BroadcastReceiver {
    final SmsManager smsManager = SmsManager.getDefault();
    private String smsSender;
    private String smsBody;
    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.getDisplayOriginatingAddress();
                smsBody += smsMessage.getMessageBody();
                Log.i("SENDER, MESSAGE", smsSender+" "+smsBody);

            }
        } else {
            Bundle smsBundle = intent.getExtras();
            if (smsBundle != null) {
                final Object[] pdusObj = (Object[])smsBundle.get("pdus");
                for (int i=0; i<pdusObj.length; i++){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    smsSender = phoneNumber;
                    smsBody = currentMessage.getDisplayMessageBody();
                }
            }
        }
        //Database operations
        HandlerThread handlerThread = new HandlerThread("database_helper");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper databaseHelper= new DatabaseHelper(context);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date currentTime = Calendar.getInstance().getTime();

                boolean result = databaseHelper.checkIfCallMissed(smsSender);
                Log.i("RESULT",result+" hehehe");
                if(result){
                    boolean res = databaseHelper.insertSMSRecord(smsSender, dateFormat.format(currentTime), smsBody);
                    Log.i("SMS RECORD INSERTED", res+" "+dateFormat.format(currentTime));
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pushNotification("URGENT CALL MISSED", "You missed an important call from "+smsSender,intent,0 );
                }

            }
        });
    }

    public void pushNotification(String title, String message, Intent intent, int pushid){
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "default")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

    }
}
