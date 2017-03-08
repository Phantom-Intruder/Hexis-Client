package hexis.pegasus.com.hexisclient;

import  android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    private OkHttpClient client;
    private Request request;
    private String[] logData = new String[]{"Please turn on internet connection"};
    private int[] graphData = new int[]{0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case android.R.id.home:
                        finish();
                        return true;
                }
                return true;
            }
        });
    }

    public void getDataFromServerAndPresent(View view) {
        String url = "http://www.cybertechparadise.com/get_log.php?habitId=1%20OR%202%20OR%203%20%20OR%204";
        client = new OkHttpClient();

        request = new Request.Builder()
                .url(url)
                .build();

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    Call call = client.newCall(request);
                    Response response = null;
                    try {
                        response = call.execute();
                        logData = response.body().string().split(",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });

        thread.start();
        // Find the ListView resource.
        while (thread.isAlive()) {

        }
        mainListView = (ListView) findViewById( R.id.mainListView );

        // Create and populate a List of planet names.
        ArrayList<String> logsList = new ArrayList<String>();
        logsList.addAll( Arrays.asList(logData) );

        // Create ArrayAdapter using the planet list.
        graphData = new int[logData.length];
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, logsList);
        for (int i=0; i <= logData.length-1; i++ ){
            int data = Character.getNumericValue(logData[i].charAt(6));
            graphData[i] = data;
            Log.d(TAG, "dsfsdf "+ i + " sfsdf " + logData.length + " adsd " + graphData[i] + " dfs" + data);
        }

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ActivityLogFragment(), "LOGS");
        adapter.addFragment(new ChartsFragment(), "CHARTS");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void pressed(View view) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        if (graph == null){
            Log.d(TAG, "Null stuff");
        }

        for (int i=0; i < graphData.length; i++ ){
            graphData[i] = Character.getNumericValue(logData[i].charAt(6));
        }

        int first =0;
        int second =0;
        int third =0;
        for (int aGraphData : graphData) {
            if (aGraphData == 1) {
                first++;
            } else if (aGraphData == 2) {
                second++;
            } else {
                third++;
            }
        }
        Log.d(TAG, "qweasd"+ first + "---" + second+ "---" +third);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1, first),
                new DataPoint(2, second),
                new DataPoint(3, third)
        });
        graph.addSeries(series);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
