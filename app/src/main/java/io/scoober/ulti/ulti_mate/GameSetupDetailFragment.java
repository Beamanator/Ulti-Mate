package io.scoober.ulti.ulti_mate;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

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
    private CoordinatorLayout gameSetupDetailCLayout;
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
        gameSetupDetailCLayout = (CoordinatorLayout)
                getActivity().findViewById(R.id.gameSetupCoordinatorLayout);
        softCapButton = (Button) v.findViewById(R.id.softCapInput);
        hardCapButton = (Button) v.findViewById(R.id.hardCapInput);
    }

    private void setListeners() {
        // Set default soft / hard cap times. These will be # of minutes + current time
        final int softCapMinuteOffset = 75;
        final int hardCapMinuteOffset = 90;
        final Context c = getActivity().getBaseContext();

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
                // get current softCapButton time
                String prevTime = softCapButton.getText().toString();
                final Calendar capTime = Calendar.getInstance();
                final long buttonTimeMilli;

                if (prevTime.equals(c.getString(R.string.button_set_time))) {
                    // add softCapOffset to current time
                    capTime.add(Calendar.MINUTE, softCapMinuteOffset);
                } else {
                    // use button time
                    buttonTimeMilli = Utils.getMilliFrom12HrString(prevTime);
                    capTime.setTimeInMillis(buttonTimeMilli);
                }

                int hour = capTime.get(Calendar.HOUR_OF_DAY);
                int minute = capTime.get(Calendar.MINUTE);

                TimePickerDialog softCapTimePicker = createTimePickerDialog(
                        hour, minute, softCapButton, "soft");

                softCapTimePicker.setTitle(c.getString(R.string.timepicker_soft_cap_title));

                softCapTimePicker.show();
            }
        });

        hardCapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current hardCapButton time
                String prevTime = hardCapButton.getText().toString();
                final Calendar capTime = Calendar.getInstance();
                final long buttonTimeMilli;

                if (prevTime.equals(c.getString(R.string.button_set_time))) {
                    // add hard cap minute offset to current time:
                    capTime.add(Calendar.MINUTE, hardCapMinuteOffset);
                } else {
                    // use button time
                    buttonTimeMilli = Utils.getMilliFrom12HrString(prevTime);
                    capTime.setTimeInMillis(buttonTimeMilli);
                }

                int hour = capTime.get(Calendar.HOUR_OF_DAY);
                int minute = capTime.get(Calendar.MINUTE);

                TimePickerDialog hardCapTimePicker = createTimePickerDialog(
                        hour, minute, hardCapButton, "hard");

                hardCapTimePicker.setTitle(c.getString(R.string.timepicker_hard_cap_title));

                hardCapTimePicker.show();
            }
        });
    }

    /**
     * Function creates a TimePickerDialog object for setting soft and hard caps.
     * @param hour      int Hour for TimePickerDialog to default to
     * @param minute    int Minute for TimePickerDialog to default to
     * @param button    Button to store time picked by dialog
     * @param capType   String Cap type ("soft" or "hard")
     * @return          Returns the new TimePickerDialog
     */
    private TimePickerDialog createTimePickerDialog(int hour, int minute, final Button button,
                                                    final String capType) {
        return new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // set up initial variables
                        String timePicked = Utils.to12Hr(hourOfDay, minute);
                        Calendar capTime = Calendar.getInstance();
                        Calendar currentTime = Calendar.getInstance();
                        Calendar dateGameCreated = Calendar.getInstance();
                        final Context c = getActivity().getBaseContext();

                        // Use Utils to set the time picked by picker to capTime
                        capTime.setTimeInMillis(
                                Utils.getTodayMilliFrom12HrString(timePicked)
                        );

                        // get date game was created
                        long dateCreated;
                        if (game != null) {
                            dateCreated = game.getDate();
                        } else {
                            // if game doesn't exist yet, get current date
                            dateCreated = currentTime.getTimeInMillis();
                        }
                        dateGameCreated.setTimeInMillis(dateCreated);

                        // If time picked is before current time, set day + 1
                        if (!Utils.isFutureToday(
                                dateGameCreated.get(Calendar.HOUR_OF_DAY),
                                dateGameCreated.get(Calendar.MINUTE),
                                hourOfDay,
                                minute)) {
                            capTime.add(Calendar.DAY_OF_YEAR, 1);
                        }

                        // Set button text to picked time (make string readable)
                        button.setText(timePicked);

                        // add tag to be referenced in validation
                        button.setTag(capTime.getTimeInMillis());

                        String timeRemaining;
                        String message;
                        Snackbar sb;

                        // validate time picked is allowed
                        if (timePickedAllowed()) {
                            timeRemaining = Utils.timeFromNowHmm(capTime);
                            message = c.getString(R.string.snackbar_message_time_cap,
                                    capType.toLowerCase(), timeRemaining);
                            sb = makeSnackbar(message, 3000);
                            sb.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // reset button data
                                    button.setText(c.getString(R.string.button_cap_default_text));
                                    button.setTag(null);
                                }
                            });
                        } else {
                            message = "Hard cap time must come before Soft cap time.";
                            sb = makeSnackbar(message, Snackbar.LENGTH_INDEFINITE);

                            // reset button data
                            button.setText(c.getString(R.string.button_cap_default_text));
                            button.setTag(null);
                        }

                        sb.show();
                    }
                }, hour, minute, false);
    }

    /**
     * Function determines if time picked by recently-selected TimePickerDialog is allowed.
     * Validates against:   1. soft cap comes before hard cap
     *                      2. hard cap comes before soft cap
     * // TODO: add more validation rules like time > 24 hours in future or < 1 minute in future
     * @return      boolean - returns true if validation rules are not broken
     */
    private boolean timePickedAllowed() {
        // get soft / hard cap times from button tags
        Object softCapTime = softCapButton.getTag();
        Object hardCapTime = hardCapButton.getTag();

        // If either of the buttons is not populated, skip validation
        if (softCapTime == null || hardCapTime == null) {
            return true;
        }

        // create Calendars for each time
        Calendar softDate = Calendar.getInstance();
        Calendar hardDate = Calendar.getInstance();

        // set each Calendar to correct time and cast Objects to Longs
        softDate.setTimeInMillis((Long) softCapTime);
        hardDate.setTimeInMillis((Long) hardCapTime);

        return Utils.isFuture(softDate, hardDate);
    }

    /**
     * Function creates and returns a Snackbar in coordinator layout cl
     * @param message   Message to insert into Snackbar
     * @param length    Length of time Snackbar should be around. Options:
     *                  Snackbar.LENGTH_INDEFINITE
     *                  Snackbar.LENGTH_LONG
     *                  Snackbar.LENGTH_SHORT
     * @return          Returns basic Snackbar
     */
    private Snackbar makeSnackbar(String message, int length) {
        Snackbar snackbar = Snackbar.make(gameSetupDetailCLayout, message, length);
        return snackbar;
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
