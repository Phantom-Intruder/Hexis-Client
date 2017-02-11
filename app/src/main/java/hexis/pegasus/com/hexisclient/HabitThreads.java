package hexis.pegasus.com.hexisclient;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static hexis.pegasus.com.hexisclient.MainActivity.bluetoothSocket;

class HabitThreads {
    private Thread restrictedWebsiteThread;
    private void restrictedWebsite(){
         restrictedWebsiteThread = new Thread(new Runnable(){
            @Override
            public void run() {
                BluetoothSocket socket = bluetoothSocket;

                Log.d(TAG, "Devices"+ socket.getRemoteDevice()+ " --- " + socket.isConnected());
                char dataToSend = '2';
                try {
                    Log.d(TAG, "Came here");
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeChar(dataToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void visitedRestrictedWebsite(){
        restrictedWebsite();
        restrictedWebsiteThread.start();
    }

}
