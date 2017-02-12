package hexis.pegasus.com.hexisclient;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.security.acl.LastOwnerException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class CallReceiver extends PhonecallReceiver {

    private boolean callEnded;
    Timer timer;
    Thread outgoingCallThread;

    void setCallEnded(boolean value) {
        callEnded = value;
    }

    boolean getCallEnded() {
        return callEnded;
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        //

    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        timer = new Timer();
        final int[] times = {0};
        setCallEnded(false);
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                if (times[0] == 0) {
                    times[0]++;
                } else {
                    if (!getCallEnded()) {

                        boolean conversationState = MainActivity.getProlongedConversationsState;

                        if (conversationState) {
                            Log.d(TAG, "Call stateC" + getCallEnded() + " --- ");
                            HabitThreads prolongedConversation = new HabitThreads();
                            prolongedConversation.talkedForTooLong();
                        }
                    } else {
                        Log.d(TAG, "Prob");
                    }
                }
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 0l, 5000);   // 1000*10*60 every 10 minut
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        //
        setCallEnded(true);
        Log.d(TAG, "Call stateC" + getCallEnded());
        try {
            timer.cancel();
            timer.purge();
        } catch (NullPointerException e) {
            Log.d(TAG, "Error here");
        }
        HabitThreads prolongedConversationOff = new HabitThreads();
        prolongedConversationOff.talkedForTooLongOff();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        final int[] times = {0};
        setCallEnded(false);
        outgoingCallThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!getCallEnded()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    boolean conversationState = MainActivity.getProlongedConversationsState;
                    if (conversationState) {
                        Log.d(TAG, "Call stateC" + getCallEnded() + " --- ");
                        HabitThreads prolongedConversation = new HabitThreads();
                        prolongedConversation.talkedForTooLong();

                    }
                }
            }
        });
        outgoingCallThread.start();
    }


    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        setCallEnded(true);
        Log.d(TAG, "Call stateC" + getCallEnded());

        HabitThreads prolongedConversationOff = new HabitThreads();
        prolongedConversationOff.talkedForTooLongOff();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        //
    }

}