package com.aptten.scoreboard;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

/** TimerFragment is a fragment for a dialog that allows the user to set up a new timer function.
 *
 */
public class TimerFragment extends DialogFragment {

    TimerDialogListener listener;
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    NumberPicker secondPicker;

    public TimerFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change theme to match app
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ScoreboardDialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // create callback object to return data to main activity
        listener = (TimerDialogListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // put UI objects into the view
        View v = inflater.inflate(R.layout.fragment_timer_dialog, container, false);

        // set title of dialog box
        getDialog().setTitle("Set Time");

        // set up value limits for each picker
        hourPicker = (NumberPicker) v.findViewById(R.id.hour_picker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(12);

        minutePicker = (NumberPicker) v.findViewById(R.id.minute_picker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        secondPicker = (NumberPicker) v.findViewById(R.id.second_picker);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // set up the return button
        Button button = (Button) v.findViewById(R.id.dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calculate total ms
                long total = (3600000*hourPicker.getValue()) + (60000*minutePicker.getValue())
                        + (1000*secondPicker.getValue());

                // return total to main activity
                listener.onReturnTimer(total);

                // dismiss dialog
                dismiss();
            }
        });

        return v;
    }

    // interface to allow this dialog to return data back to main activity
    public interface TimerDialogListener {
        public void onReturnTimer(long total);
    }

}
