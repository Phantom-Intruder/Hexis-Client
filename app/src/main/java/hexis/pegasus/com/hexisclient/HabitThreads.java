package hexis.pegasus.com.hexisclient;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.Calendar;


import static android.content.ContentValues.TAG;
import static hexis.pegasus.com.hexisclient.MainActivity.bluetoothSocket;

class HabitThreads {
    private Thread restrictedWebsiteThread;
    private Thread prolongedConversationThread;
    private Thread prolongedConversationOffThread;

    private void restrictedWebsite(){
         restrictedWebsiteThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    BluetoothSocket socket = bluetoothSocket;

                    char dataToSend = '1';
                    try {
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.writeChar(dataToSend);
                        String data = "http://hexis-band.azurewebsites.net/report_activity.php?habitId=3";
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(data)
                                .build();

                        client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    //Do nothing
                }
            }
        });

    }



    private void prolongedConversation(){
        prolongedConversationThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                BluetoothSocket socket = bluetoothSocket;

                Log.d(TAG, "Devices"+ socket.getRemoteDevice()+ " --- " + socket.isConnected());
                //TODO: Handle talking
                char dataToSend = '1';
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeChar(dataToSend);
                    String data = "http://hexis-band.azurewebsites.net/report_activity.php?habitId=2";
                    Log.d(TAG, "cuber  " +data);
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(data)
                            .build();

                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }catch (Exception e){
                    //Do nothing
                }
            }
        });
    }

    private void prolongedConversationOff(){
        prolongedConversationOffThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                BluetoothSocket socket = bluetoothSocket;

                //TODO: Handle talking
                char dataToSend = '2';
                try {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeChar(dataToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }catch (Exception e){
                    //Do nothing
                }
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

    void talkedForTooLongOff(){
        prolongedConversationOff();
        prolongedConversationOffThread.start();
    }
}
