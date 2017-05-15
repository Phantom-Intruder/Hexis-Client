package hexis.pegasus.com.hexisclient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static boolean getRestrictedWebsiteHabitState = false;
    public static boolean getProlongedConversationsState = false;
    public static boolean getProlongedSittingState = false;
    public static boolean getSmokingState = false;
    private Toolbar mToolbar;
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static BluetoothSocket bluetoothSocket = null;
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    Fragment viewFragment;
    Fragment settingsFragment;
    Fragment aboutFragment;

    public void turnOnRestrictWebsiteAccess(View view) {
        getRestrictedWebsiteHabitState = !getRestrictedWebsiteHabitState;
        if (!getRestrictedWebsiteHabitState){
            Toast.makeText(getApplicationContext(), "Disabled. Disable extension to prevent alerts", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Enabled. Enable extension to get alerts", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void turnOnConversationRestriction(View view) {
        getProlongedConversationsState = !getProlongedConversationsState;
        if (!getProlongedConversationsState){
            Toast.makeText(getApplicationContext(), "Disabled.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Enabled.", Toast.LENGTH_LONG).show();
        }
    }

    public void turnOnProlongedSitting(View view) {
        //TODO: Send the data to the device
        getProlongedSittingState = !getProlongedSittingState;
        if (!getProlongedSittingState){
            HabitThreads prolongedSittingThread = new HabitThreads();
            prolongedSittingThread.IsMonitoringSitting();
            Toast.makeText(getApplicationContext(), "Disabled.", Toast.LENGTH_LONG).show();
        }else{
            HabitThreads prolongedSittingThread = new HabitThreads();
            prolongedSittingThread.IsMonitoringSitting();
            Toast.makeText(getApplicationContext(), "Enabled.", Toast.LENGTH_LONG).show();
        }
    }

    public void turnOnSmoking(View view) {
        //TODO: Send the data to the device
        getSmokingState = !getSmokingState;
        if (!getSmokingState){
            Toast.makeText(getApplicationContext(), "Disabled.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Enabled.", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void openFacebookApp(View view) {
        openApp(this, "com.example.dmax.login");
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Code to run when item gets clicked
            selectItem(position);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        titles = getResources().getStringArray(R.array.menu_drawer);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View header = getLayoutInflater().inflate(R.layout.header, null);
        drawerList.addHeaderView(header);
        mainActivity = this;
        getRestrictedWebsiteHabitState = false;
        getProlongedConversationsState = false;
        getProlongedSittingState = true;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();
        //Populate the ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState == null) {
            selectItem(0);
        }
        //Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            //Called when a drawer has settled in a completely open state
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Display the correct fragment
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
        } else {
            selectItem(0);
        }
        connectWithDevice();
    }


    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               Toast.makeText(MainActivity.this, "Sending signal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void connectWithDevice() {

        if (!bluetoothAdapter.isEnabled()) {
            showBluetoothMenu();
            Thread bluetoothTurnedOnThread = waitForBluetoothToTurnOnAndConnectToSocket();
            bluetoothTurnedOnThread.start();
        } else {
            connectToBluetoothSocket();
        }
    }

    @NonNull
    private Thread waitForBluetoothToTurnOnAndConnectToSocket() {
        return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!bluetoothAdapter.isEnabled()) {

                        }
                        connectToBluetoothSocket();
                    }
                });
    }

    private void showBluetoothMenu() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    private void connectToBluetoothSocket() {
        String address = "98:D3:34:90:96:36";
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        BluetoothSocket temporaryBluetoothSocket = null;

        UUID MY_UUID = DeviceUuidFactory.getDeviceUuid();
        temporaryBluetoothSocket = createBluetoothSocket(device, temporaryBluetoothSocket, MY_UUID);
        bluetoothSocket = temporaryBluetoothSocket;
        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Devices" + bluetoothSocket.getRemoteDevice() + " --- " + bluetoothSocket.isConnected());
        char dataToSend = '1';
        //sendDataToDevice(dataToSend);

        Thread receiveDataFromDeviceThread = receiveDataFromDevice();
        receiveDataFromDeviceThread.start();

    }

    @NonNull
    private Thread receiveDataFromDevice() {
        return new Thread(new Runnable() {
                @Override
                public void run() {
                        try {

                            Thread prolongedSittingThread = new Thread(new Runnable(){
                                @Override
                                public void run() {

                                    try {
                                        DataInputStream inputStream = new DataInputStream(bluetoothSocket.getInputStream());

                                        while (true) {
                                            byte DataIn = 'e';
                                            DataIn = (byte) inputStream.readChar();

                                            if (DataIn  == 'e'){
                                                continue;
                                            }else{
                                                Log.d(TAG, "Data rec: "+DataIn );
                                                BluetoothSocket socket = bluetoothSocket;

                                                Log.d(TAG, "Devices" + socket.getRemoteDevice() + " --- " + socket.isConnected());
                                                //TODO: Handle sitting
                                                char dataToSend = '1';
                                                try {
                                                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                                                    outputStream.writeChar(dataToSend);
                                                    String data = "http://hexis-band.azurewebsites.net/report_activity.php?habitId=4";
                                                    Log.d(TAG, "Problem ");
                                                    OkHttpClient client = new OkHttpClient();

                                                    Request request = new Request.Builder()
                                                            .url(data)
                                                            .build();

                                                    client.newCall(request).execute();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    }catch (Exception e){
                                        //Do nothing
                                    }
                                }
                            });
                            //prolongedSittingThread.start();

                        } catch (NullPointerException n){
                            Toast.makeText(getApplicationContext(), "There was a problem connecting to the device", Toast.LENGTH_LONG).show();
                        }
                    }

            });
    }


    private void sendDataToDevice(char dataToSend) {
        try {
            DataOutputStream outputStream = new DataOutputStream(bluetoothSocket.getOutputStream());
            outputStream.writeChar(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, BluetoothSocket tmp, UUID MY_UUID) {
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(device, 1);
        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    //Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //If the drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onPostCreate(Bundle saveInstanceState) {
        super.onPostCreate(saveInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment = null;
        switch (position) {
            case 1:
                if (viewFragment == null) {
                    viewFragment = new ViewFragment();
                    fragment = viewFragment;
                }else{
                    fragment = viewFragment;
                }
                break;
            case 2:
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    fragment = settingsFragment;
                }else {
                    fragment = settingsFragment;
                }
                break;
            case 3:
                if (aboutFragment == null){
                    aboutFragment = new AboutFragment();
                    fragment = aboutFragment;
                }else {
                    fragment = aboutFragment;
                }
                break;
            default:
                if (viewFragment == null) {
                    viewFragment = new ViewFragment();
                    fragment = viewFragment;
                }else{
                    fragment = viewFragment;
                 }
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    public void openWebsite(View view){
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("hexis-band.azurewebsites.net"));
            startActivity(browserIntent);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "There was a problem opening the default browser.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_view_charts) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
