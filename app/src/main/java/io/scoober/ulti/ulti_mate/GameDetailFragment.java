package io.scoober.ulti.ulti_mate;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameDetailFragment extends Fragment {


    public GameDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View gameDetailView = inflater.inflate(R.layout.fragment_game_setup, container, false);

        CheckBox timeCapsBox = (CheckBox) gameDetailView.findViewById(R.id.timeCapsCheckbox);
        final RelativeLayout timeCapsContainer = (RelativeLayout) gameDetailView.findViewById(R.id.timeCapsContainer);
        final Button softCapButton = (Button) gameDetailView.findViewById(R.id.softCapInput);
        final Button hardCapButton = (Button) gameDetailView.findViewById(R.id.hardCapInput);

        // Hide the soft/hard caps if the time caps is unchecked.
        timeCapsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeCapsContainer.setVisibility(View.VISIBLE);
                } else {
                    timeCapsContainer.setVisibility(View.GONE);
                }
            }
        });

        softCapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog softCapTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //TODO Validation on date and time
                        softCapButton.setText(to12Hr(hourOfDay, minute));
                    }
                },hour,minute,false);
                softCapTimePicker.show();
            }
        });

        hardCapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog hardCapTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //TODO Validation on date and time (must be greater than current)
                                hardCapButton.setText(to12Hr(hourOfDay, minute));
                            }
                        },hour,minute,false);
                hardCapTimePicker.show();
            }
        });

        return gameDetailView;
    }

    private static String to12Hr(int hourOfDay, int minute) {
        String timeString = hourOfDay + ":" + minute;
        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date timeAsDate = sdf24.parse(timeString);
            SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return sdf12.format(timeAsDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
