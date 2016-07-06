package io.scoober.ulti.ulti_mate;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupDetailFragment extends Fragment {

    private Game game;

    /*
    Widgets
     */

    private CheckBox timeCapsBox;
    private EditText gameTitleText;
    private EditText winningScoreText;
    private RelativeLayout timeCapsContainer;
    private Button softCapButton;
    private Button hardCapButton;

    public GameSetupDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View gameDetailView = inflater.inflate(R.layout.fragment_game_setup_detail, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            game = GameSetupActivity.getGameFromBundle(bundle, getActivity().getBaseContext());
        }

        getWidgetReferences(gameDetailView);
        if (game != null) {
            initializeWidgets();
        }
        setListeners();

        return gameDetailView;
    }

    private void getWidgetReferences(View v) {
        timeCapsBox = (CheckBox) v.findViewById(R.id.timeCapsCheckbox);
        gameTitleText = (EditText) v.findViewById(R.id.gameTitleEditor);
        winningScoreText = (EditText) v.findViewById(R.id.winningScore);
        timeCapsContainer = (RelativeLayout) v.findViewById(R.id.timeCapsContainer);
        softCapButton = (Button) v.findViewById(R.id.softCapInput);
        hardCapButton = (Button) v.findViewById(R.id.hardCapInput);
    }

    private void setListeners() {
        //TODO Add onClick/onFocus listener or attribute to select all text when selected

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
                                softCapButton.setText(Utils.to12Hr(hourOfDay, minute));
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
                                hardCapButton.setText(Utils.to12Hr(hourOfDay, minute));
                            }
                        },hour,minute,false);
                hardCapTimePicker.show();
            }
        });
    }

    private void initializeWidgets() {
        if (game!= null) {
            gameTitleText.setText(game.getGameName());
            winningScoreText.setText(Integer.toString(game.getWinningScore()));
            long softCap = game.getSoftCapTime();
            long hardCap = game.getHardCapTime();
            if (softCap > 0 || hardCap > 0) {
                timeCapsBox.setChecked(true);
                timeCapsContainer.setVisibility(View.VISIBLE);

                if (softCap > 0) {
                    softCapButton.setText(Utils.getTimeString(softCap));
                }

                if (hardCap > 0) {
                    hardCapButton.setText(Utils.getTimeString(hardCap));
                }

            }
        }
    }



}
