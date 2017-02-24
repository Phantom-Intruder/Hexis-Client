package hexis.pegasus.com.hexisclient;

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
import android.util.Log;
import android.view.Menu;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

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

    public void turnOnRestrictWebsiteAccess(View view) {
        getRestrictedWebsiteHabitState = !getRestrictedWebsiteHabitState;
        if (!getRestrictedWebsiteHabitState){
            Toast.makeText(getApplicationContext(), "Disabled. Disable extension to prevent alerts", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Enabled. Enable extension to get alerts", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "Disabled.", Toast.LENGTH_LONG).show();
        }else{
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
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
        }
        connectWithDevice();
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
        sendDataToDevice(dataToSend);

        Thread receiveDataFromDeviceThread = receiveDataFromDevice();
        receiveDataFromDeviceThread.start();

    }

    @NonNull
    private Thread receiveDataFromDevice() {
        return new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        DataInputStream inputStream;
                        try {
                            inputStream = new DataInputStream(bluetoothSocket.getInputStream());
                            char inData = inputStream.readChar();

                            Log.d(TAG, "Data rec: "+inData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ViewFragment();
                break;
            case 1:
                fragment = new SettingsFragment();
                break;
            case 2:
                fragment = new AboutFragment();
                break;
            default:
                fragment = new ViewFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Set the action bar title
        setActionBarTitle(position);
        //Close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
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
        if (id == R.id.action_view_charts) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    void showErrorMessage(){
        Toast.makeText(getApplicationContext(), "The app is not connected to the device.", Toast.LENGTH_LONG).show();

    }
}
