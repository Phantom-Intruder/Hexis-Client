package hexis.pegasus.com.hexisclient;

import android.content.Context;
import android.os.Vibrator;
import android.telecom.Call;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class CallReceiver extends PhonecallReceiver {

    private static Timer timer;
    private boolean callEnded = true;


    // now the initialization is done only within the constructor


    void setCallEnded(boolean value){
        callEnded = value;
    }

    boolean getCallEnded(){
        return callEnded;
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start)
    {

    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        CallReceiver.timer = new Timer ();
        final int[] times = {0};
        setCallEnded(false);
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
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

        CallReceiver.timer.schedule (hourlyTask, 0l, 5000);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        setCallEnded(true);
        Log.d(TAG, "Call stateC"+getCallEnded());
        CallReceiver.timer.cancel();
        CallReceiver.timer.purge();
        HabitThreads prolongedConversationOff = new HabitThreads();
        prolongedConversationOff.talkedForTooLongOff();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        CallReceiver.timer = new Timer ();
        final int[] times = {0};
        setCallEnded(false);
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                if ((times[0] == 0)||(times[0] == 1)) {
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

        CallReceiver.timer.schedule (hourlyTask, 0l, 5000);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        setCallEnded(true);
        Log.d(TAG, "Call stateC"+getCallEnded());
        CallReceiver.timer.cancel();
        CallReceiver.timer.purge();
        HabitThreads prolongedConversationOff = new HabitThreads();
        prolongedConversationOff.talkedForTooLongOff();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
    }

}