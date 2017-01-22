package hexis.pegasus.com.hexisclient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import org.json.JSONObject;

public class HabitActivity extends Activity {

    private Spinner optionSpinner;
    private Switch optionSwitch;
    private int selectedHour = 5;
    private int selectedMinute;
    private boolean zapSwitchState;
    private boolean beepSwitchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onCheckedChanged(View view) {
        optionSwitch = (Switch) view;

        optionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if ((optionSwitch.getId()) == R.id.zap_text) {
                        optionSpinner = (Spinner) findViewById(R.id.zap_spinner);
                        optionSpinner.setEnabled(true);
                        zapSwitchState = true;
                    } else {
                        optionSpinner = (Spinner) findViewById(R.id.beep_spinner);
                        optionSpinner.setEnabled(true);
                        beepSwitchState = true;
                    }
                    optionSpinner = null;
                } else {
                    // The switch is disabled
                    if ((optionSwitch.getId()) == R.id.zap_text) {
                        optionSpinner = (Spinner) findViewById(R.id.zap_spinner);
                        optionSpinner.setEnabled(false);
                        zapSwitchState = false;
                    } else {
                        optionSpinner = (Spinner) findViewById(R.id.beep_spinner);
                        optionSpinner.setEnabled(false);
                        beepSwitchState = false;
                    }
                    optionSpinner = null;
                }
            }
        });
    }

    public void onSaveClick(View view) {
        final Habit habit = new Habit();
        EditText habitName = (EditText) findViewById(R.id.habit_type_input);
        habit.setHabitName(habitName.getText().toString());
        Spinner habitType = (Spinner) findViewById(R.id.habit_option);
        if ((habitType.getSelectedItem().toString()).equalsIgnoreCase("Good habit")) {
            habit.setBadHabit(false);
        } else {
            habit.setBadHabit(true);
        }
        timeHandleMethod(findViewById(R.id.time_picker));
        habit.setHourToGiveReminer(selectedHour);
        habit.setMinuteToGiveReminder(selectedMinute);
        habit.setZapSwitchState(zapSwitchState);
        habit.setBeepSwitchState(beepSwitchState);
        if (zapSwitchState) {
            Spinner NoOfTimesToZap = (Spinner) findViewById(R.id.zap_spinner);
            habit.setNoOfTimesToZap(Integer.parseInt(NoOfTimesToZap.getSelectedItem().toString()));
        } else {
            habit.setNoOfTimesToZap(0);
        }
        if (beepSwitchState) {
            Spinner NoOfTimesToBeep = (Spinner) findViewById(R.id.beep_spinner);
            habit.setNoOfTimesToZap(Integer.parseInt(NoOfTimesToBeep.getSelectedItem().toString()));
        } else {
            habit.setNoOfTimesToBeep(0);
        }
        Log.d("Response", "is Bad " + habit.isBadHabit() + " No of zap " + habit.getNoOfTimesToZap() + " No of beep " + habit.getNoOfTimesToBeep() + " time " + habit.getHourToGiveReminer() + ":" + habit.getMinuteToGiveReminder() + " zap state " + habit.isZapSwitchState() + " beep state " + habit.isBeepSwitchState());
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://cybertechparadise.com/addHabit.php?habitName=" + habit.getHabitName() + "&habitType=" + habit.isBadHabit() + "&timeHours=" + habit.getHourToGiveReminer() + "&timeMinutes=" + habit.getMinuteToGiveReminder() + "&zapState=" + habit.isZapSwitchState() + "&beepState=" + habit.isBeepSwitchState() + "&noOfBeeps=" + habit.getNoOfTimesToBeep() + "&noOfZaps=" + habit.getNoOfTimesToZap();

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", response);
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }

    public void timeHandleMethod(View view) {
        TimePicker timePicker = (TimePicker) view;
        timePicker.clearFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();
        }

    }
}
