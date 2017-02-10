package hexis.pegasus.com.hexisclient;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.DataOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static hexis.pegasus.com.hexisclient.MainActivity.mmSocket;


public class HabitNotificationService extends FirebaseMessagingService {
    private DataOutputStream outputStream;

    public HabitNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 400 milliseconds
        v.vibrate(400);
        BluetoothSocket socket = mmSocket;
        try {
            mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Devices"+ mmSocket.getRemoteDevice()+ " --- " + mmSocket.isConnected());
        char two = '2';
        try {
            outputStream = new DataOutputStream(mmSocket.getOutputStream());
            outputStream.writeChar(two);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
