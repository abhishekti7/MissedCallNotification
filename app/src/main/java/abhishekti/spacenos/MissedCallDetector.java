package abhishekti.spacenos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MissedCallDetector extends BroadcastReceiver {

    static boolean ring = false;
    static boolean callReceived = false;
    private static String callerPhoneNumber;


    @Override
    public void onReceive(final Context mContext, Intent intent) {

        // Get the current Phone State
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state == null)
            return;

        // If phone state "Ringing"
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            ring = true;
            // Get the Caller's Phone Number
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                callerPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            }
            callerPhoneNumber = bundle.getString("incoming_number");
        }


        // If incoming call is received
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            callReceived = true;
        }


        // If phone is Idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
            if (ring == true && callReceived == false) {
                Toast.makeText(mContext, "It was A MISSED CALL from : " + callerPhoneNumber, Toast.LENGTH_LONG).show();
                //Database operations
                HandlerThread handlerThread = new HandlerThread("database_helper");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseHelper databaseHelper= new DatabaseHelper(mContext);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date currentTime = Calendar.getInstance().getTime();

                        boolean res = databaseHelper.insertCallRecord(callerPhoneNumber, dateFormat.format(currentTime));
                        Log.i("INSERTIONS", res+" "+dateFormat.format(currentTime));
                    }
                });

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(callerPhoneNumber, null, "I am busy. Call me back later? "+callerPhoneNumber, null, null);
            }
            ring = false;
            callReceived = false;
        }

    }
}