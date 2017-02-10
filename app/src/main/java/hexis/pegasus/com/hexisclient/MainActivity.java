package hexis.pegasus.com.hexisclient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.util.CircularArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.content.res.Configuration;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice>pairedDevices;
    private DataOutputStream outputStream;
    static BluetoothSocket mmSocket = null;

    public void connectToDevice(View view) {
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            //Code to run when item gets clicked
            selectItem(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.menu_drawer);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Populate the ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState == null){
            selectItem(0);
        }
        //Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer){
            //Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            //Called when a drawer has settled in a completely open state
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //Display the correct fragment
        if (savedInstanceState != null){
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        }else{
            selectItem(0);
        }
        connectWithDevice();
    }


    private void connectWithDevice() {
        //Bluetooth methods go here
       //String address = "98:D3:34:90:96:36";
        if (!BA.isEnabled()) {
            BA = BluetoothAdapter.getDefaultAdapter();
            BA = BluetoothAdapter.getDefaultAdapter();
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            pairedDevices = BA.getBondedDevices();
            List<String> s = new ArrayList<String>();
            int i = 0;
            for(BluetoothDevice bt : pairedDevices) {
                s.add(bt.getName());
                i++;
                Log.d(TAG, "BT devs"+s.get(i));
            }

        } else {
            Log.d(TAG, "Here");
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
            String address = "98:D3:34:90:96:36";
            BluetoothDevice device = BA.getRemoteDevice(address);
            BluetoothSocket tmp = null;

            UUID MY_UUID = DeviceUuidFactory.getDeviceUuid();
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
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
            mmSocket = tmp;
            try {
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Devices"+ mmSocket.getRemoteDevice()+ " --- " + mmSocket.isConnected());
            char two = '1';
            try {
                outputStream = new DataOutputStream(mmSocket.getOutputStream());
                outputStream.writeChar(two);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //If the drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onPostCreate(Bundle saveInstanceState){
        super.onPostCreate(saveInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position){
        currentPosition = position;
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new ViewFragment();
                break;
            case 1:
                fragment = new ActivityLogFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;
            case 3:
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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    private void setActionBarTitle(int position){
        String title;
        if(position == 0){
            title = getResources().getString(R.string.app_name);
        }else{
            title = titles[position];
        }
        getActionBar().setTitle(title);

    }


}
