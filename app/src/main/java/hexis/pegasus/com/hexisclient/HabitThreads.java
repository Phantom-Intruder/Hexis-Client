package hexis.pegasus.com.hexisclient;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static hexis.pegasus.com.hexisclient.MainActivity.bluetoothSocket;

class HabitThreads {
    private Thread restrictedWebsiteThread;
    private Thread prolongedConversationThread;

    private void restrictedWebsite(){
         restrictedWebsiteThread = new Thread(new Runnable(){
            @Override
            public void run() {
                BluetoothSocket socket = bluetoothSocket;

                Log.d(TAG, "Devices"+ socket.getRemoteDevice()+ " --- " + socket.isConnected());
                char dataToSend = '2';
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeChar(dataToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prolongedConversation(){
        prolongedConversationThread = new Thread(new Runnable(){
            @Override
            public void run() {
                BluetoothSocket socket = bluetoothSocket;

                Log.d(TAG, "Devices"+ socket.getRemoteDevice()+ " --- " + socket.isConnected());
                //TODO: Handle talking
            }
        });
    }

    void visitedRestrictedWebsite(){
        restrictedWebsite();
        restrictedWebsiteThread.start();
    }


    void talkedForTooLong(){
        prolongedConversation();
        prolongedConversationThread.start();
    }

}
