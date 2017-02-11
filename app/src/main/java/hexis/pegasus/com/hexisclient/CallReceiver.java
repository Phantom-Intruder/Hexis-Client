package hexis.pegasus.com.hexisclient;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class CallReceiver extends PhonecallReceiver {

    private boolean callEnded = true;

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start)
    {
        //

    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        //
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                if (callEnded == false){
                boolean conversationState  = MainActivity.getProlongedConversationsState;
                if (conversationState) {
                    HabitThreads prolongedConversation = new HabitThreads();
                    prolongedConversation.talkedForTooLong();
                }}
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 5000);   // 1000*10*60 every 10 minut
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
        callEnded = true;
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
    }

}
