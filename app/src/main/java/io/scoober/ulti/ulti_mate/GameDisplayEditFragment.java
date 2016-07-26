package io.scoober.ulti.ulti_mate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameDisplayEditFragment extends Fragment {

    private Button editButton, saveButton;
    private ImageButton gameLengthButton;

    private ViewSwitcher gameTitleSwitcher, team1NameSwitcher, team2NameSwitcher;
    private ViewSwitcher viewEditButtonSwitcher;

    private TextView gameTitleView, gameLengthText;

    private EditText gameTitleEdit;

    private Game game;
    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    private Map<Integer,GameDisplayActivity.TeamViewHolder> teamViewMap;

    private GameLengthDialogFragment gameLengthDialogFrag;

    public GameDisplayEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_game_display_edit,
                container, false);

        // get data from intent:
        Intent intent = getActivity().getIntent();
        long id = intent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                intent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        game = Utils.getGameDetails(getActivity().getBaseContext(), id);

        // set up widget references
        getWidgetReferences(fragmentLayout);

        // inflate team data and populate private variables
        inflateTeamData(fragmentLayout);

        switch (displayToLaunch) {
            case EDIT:
                setupAndEnableWidgets();
                break;
            case VIEW:
                // TODO: refactor to use setupAndEnableWidgets?
                setupEditButtonListener();
                toggleViewTypes();
                break;
        }

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    private void setupScoreButtonListeners(final int team) {
        final GameDisplayActivity.TeamViewHolder teamViewHolder = teamViewMap.get(team);
        teamViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.incrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team, game, teamViewMap, false);
            }
        });

        teamViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.decrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team, game, teamViewMap, false);
            }
        });
    }

    /**
     * Sets listeners and enables buttons
     */
    private void setupAndEnableWidgets() {

        // save Button
        setupSaveButtonListener();

        // color button
        setupColorButtonListeners();

        // set up score button listeners:
        setupScoreButtonListeners(1);
        setupScoreButtonListeners(2);

        // Enable Color Buttons
        teamViewMap.get(1).colorButton.setClickable(true);
        teamViewMap.get(2).colorButton.setClickable(true);

        // Enable Score Buttons
        GameDisplayActivity.enableDisableScoreButtons(1, game, teamViewMap, false);
        GameDisplayActivity.enableDisableScoreButtons(2, game, teamViewMap, false);

        // Add listeners to text buttons
        gameTitleEdit.addTextChangedListener(new TextValidator(gameTitleEdit) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.hint_game_name);
            }
        });
        EditText team1Edit = teamViewMap.get(1).nameEdit;
        team1Edit.addTextChangedListener(new TextValidator(team1Edit) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.team_1_name_hint);
            }
        });
        EditText team2Edit = teamViewMap.get(2).nameEdit;
        team2Edit.addTextChangedListener(new TextValidator(team2Edit) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.team_2_name_hint);
            }
        });

        // Add listener for changing game start / stop times
        gameLengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create custom dialog fragment
                String title = getActivity().getResources()
                        .getString(R.string.text_dialog_fragment_title);
                gameLengthDialogFrag = GameLengthDialogFragment.newInstance(title, game);

                // make dialog fullscreen
                gameLengthDialogFrag.setStyle(android.app.DialogFragment.STYLE_NO_FRAME,
                        android.R.style.Theme_Holo_Light);

                gameLengthDialogFrag.show(getActivity().getFragmentManager());
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener startTimePickerListener(final Calendar startTime) {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (Utils.timePickerTimeChanged(startTime, hourOfDay, minute)) {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);
                    game.setStartDate(startTime.getTimeInMillis());
                    Utils.saveGameDetails(getActivity(), game);
                }

                DatePickerDialog startDateDialog = createDatePickerDialog(startTime,
                        startDatePickerListener(startTime));

                startDateDialog.show();
            }
        };
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener(final Calendar startDate) {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (Utils.datePickerDateChanged(startDate, year, monthOfYear, dayOfMonth)) {
                    startDate.set(Calendar.YEAR, year);
                    startDate.set(Calendar.MONTH, monthOfYear);
                    startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    game.setStartDate(startDate.getTimeInMillis());
                    Utils.saveGameDetails(getActivity(), game);
                }
            }
        };
    }

    private TimePickerDialog createTimePickerDialog(Calendar c,
                                                    TimePickerDialog.OnTimeSetListener listener) {
        return new TimePickerDialog(getActivity(), listener, c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), false);
    }

    private DatePickerDialog createDatePickerDialog(Calendar c,
                                                    DatePickerDialog.OnDateSetListener listener) {
        return new DatePickerDialog(getActivity(), listener, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    private void setupColorButtonListeners() {
        final TeamImageButton team1ColorButton = teamViewMap.get(1).colorButton;
        final TeamImageButton team2ColorButton = teamViewMap.get(2).colorButton;
        team1ColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(team1ColorButton);
            }
        });

        team2ColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(team2ColorButton);
            }
        });
    }

    /**
     * Starts GameDisplayActivity which will pull in GameDisplayEditFragment (edit mode)
     */
    private void setupEditButtonListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameDisplayActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
                intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                        MainMenuActivity.DisplayToLaunch.EDIT);
                startActivity(intent);
            }
        });
    }

    /**
     * Starts GameDisplayActivity which will pull in GameDisplayEditFragment (view mode)
     */
    private void setupSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFields()) {
                    showValidationFailedDialog();
                    return;
                }

                // Get the TeamViewHolder
                GameDisplayActivity.TeamViewHolder team1View = teamViewMap.get(1);
                GameDisplayActivity.TeamViewHolder team2View = teamViewMap.get(2);

                // Get values from user input
                String gameName = gameTitleEdit.getText().toString();
                String team1Name = team1View.nameEdit.getText().toString();
                String team2Name = team2View.nameEdit.getText().toString();
                int team1Score = Integer.parseInt(team1View.scoreView.getText().toString());
                int team2Score = Integer.parseInt(team2View.scoreView.getText().toString());
                @ColorInt int team1Color = team1View.colorButton.getColor();
                @ColorInt int team2Color = team2View.colorButton.getColor();

                Team firstTeam = game.getTeam(1);
                Team secondTeam = game.getTeam(2);
                game.setGameName(gameName);
                game.setTeamScore(1,team1Score);
                game.setTeamScore(2,team2Score);

                firstTeam.setName(team1Name);
                firstTeam.setColor(team1Color);
                secondTeam.setName(team2Name);
                secondTeam.setColor(team2Color);

                Utils.saveGameDetails(v.getContext(), game);

                Intent intent = new Intent(v.getContext(), GameDisplayActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
                intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                        MainMenuActivity.DisplayToLaunch.VIEW);
                startActivity(intent);
            }
        });
    }

    /**
     * Get references to all widgets in the view
     * @param v     View to find widgets in
     */
    private void getWidgetReferences(View v) {
        editButton = (Button) v.findViewById(R.id.editButton);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        gameLengthButton = (ImageButton) v.findViewById(R.id.buttonLengthSettings);

        gameTitleEdit = (EditText) v.findViewById(R.id.gameTitleEdit);
        gameTitleView = (TextView) v.findViewById(R.id.gameTitleView);
        gameLengthText = (TextView) v.findViewById(R.id.gameLengthText);

        gameTitleSwitcher = (ViewSwitcher) v.findViewById(R.id.gameTitleSwitcher);
        team1NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team1NameSwitcher);
        team2NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team2NameSwitcher);
        viewEditButtonSwitcher = (ViewSwitcher) v.findViewById(R.id.viewEditButtonSwitcher);

        setupTeamViews(v);
    }

    private void setupTeamViews(View v) {
        // Team 1
        GameDisplayActivity.TeamViewHolder team1View = new GameDisplayActivity.TeamViewHolder();
        team1View.nameView = (TextView) v.findViewById(R.id.leftTeamNameView);
        team1View.nameEdit = (EditText) v.findViewById(R.id.leftTeamNameEdit);
        team1View.scoreView = (TextView) v.findViewById(R.id.leftTeamScoreEdit);
        team1View.addButton = (Button) v.findViewById(R.id.leftTeamAddEdit);
        team1View.subtractButton = (Button) v.findViewById(R.id.leftTeamSubtractEdit);
        team1View.colorButton = (TeamImageButton) v.findViewById(R.id.leftTeamColorButton);

        // Team 2
        GameDisplayActivity.TeamViewHolder team2View = new GameDisplayActivity.TeamViewHolder();
        team2View.nameView = (TextView) v.findViewById(R.id.rightTeamNameView);
        team2View.nameEdit = (EditText) v.findViewById(R.id.rightTeamNameEdit);
        team2View.scoreView = (TextView) v.findViewById(R.id.rightTeamScoreEdit);
        team2View.addButton = (Button) v.findViewById(R.id.rightTeamAddEdit);
        team2View.subtractButton = (Button) v.findViewById(R.id.rightTeamSubtractEdit);
        team2View.colorButton = (TeamImageButton) v.findViewById(R.id.rightTeamColorButton);

        // Setup the view map
        teamViewMap = new HashMap<>();
        teamViewMap.put(1,team1View);
        teamViewMap.put(2,team2View);
    }

    /**
     * Function to switch all EditText widgets to TextView widgets, and vice versa
     */
    private void toggleViewTypes() {
        gameTitleSwitcher.showNext();
        team1NameSwitcher.showNext();
        team2NameSwitcher.showNext();
        viewEditButtonSwitcher.showNext();

        gameLengthButton.setVisibility(View.GONE);
    }

    private void inflateTeamData(View v) {
        // set Game title:
        gameTitleEdit.setText(game.getGameName());
        gameTitleView.setText(game.getGameName());

        // set team details:
        // Get the TeamViewHolder
        GameDisplayActivity.TeamViewHolder team1View = teamViewMap.get(1);
        GameDisplayActivity.TeamViewHolder team2View = teamViewMap.get(2);
        Team firstTeam = game.getTeam(1);
        Team secondTeam = game.getTeam(2);
        team1View.nameEdit.setText(firstTeam.getName());
        team1View.nameView.setText(firstTeam.getName());
        team2View.nameEdit.setText(secondTeam.getName());
        team2View.nameView.setText(secondTeam.getName());
        team1View.scoreView.setText(Integer.toString(game.getScore(1)));
        team2View.scoreView.setText(Integer.toString(game.getScore(2)));

        @ColorInt int leftTeamColor = firstTeam.getColor();
        @ColorInt int rightTeamColor = secondTeam.getColor();

        team1View.colorButton.build(150, leftTeamColor);
        team2View.colorButton.build(150, rightTeamColor);
        // TODO: maybe add color to team text / background

        // Game length bar information:
        displayGameLength();
    }

    /**
     * Validates the setup for the game display edit. If any of the required EditTexts are not
     * populated, then return false.
     * @return  Whether validation passed.
     */
    private boolean validateFields() {
        EditText requiredFields[] = {gameTitleEdit, teamViewMap.get(1).nameEdit,
                teamViewMap.get(2).nameEdit};
        return Utils.validateFieldsNotEmpty(requiredFields);
    }

    /**
     * Function calculates and displays game length based on game.startDate and game.endDate
     */
    private void displayGameLength() {
        long startTime = game.getStartDate();
        long endTime = game.getEndDate();
        String gameLength = "";
        Resources r = getResources();

        // TODO: fix logic for start time / end time missing
        if (startTime == 0 || endTime == 0) {
            gameLength = r.getString(R.string.text_game_in_progress);
            gameLengthText.setText(gameLength);
            return;
        }

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        startDate.setTimeInMillis(startTime);
        endDate.setTimeInMillis(endTime);

        int dayDiff = Utils.getDayDifference(startDate, endDate);
        int hourDiff = Utils.getHourDifference(startDate, endDate);
        int minDiff = Utils.getMinuteDifference(startDate, endDate);

        // TODO: Don't make it possible to choose end date before start date.
        // All 'diff' variables will be -1 if endDate is before startDate, so only check one:
        if (dayDiff == -1) {
            gameLength = r.getString(R.string.text_invalid_dates);
            gameLengthText.setText(gameLength);
            return;
        }

        if (dayDiff == 0 && hourDiff == 0 && minDiff == 0) {
            gameLength = "~ 0 " + r.getString(R.string.text_minute_full_plural);
        } else {
            if (startDate.get(Calendar.HOUR_OF_DAY) > endDate.get(Calendar.HOUR_OF_DAY))
            {
                if (dayDiff > 0) {
                    dayDiff -= 1;
                } else {
                    // TODO: care if dayDiff is 0 [same day, different years?]
                }
            }
            if (startDate.get(Calendar.MINUTE) > endDate.get(Calendar.MINUTE)) {
                if (hourDiff > 0) {
                    hourDiff -= 1;
                } else if (hourDiff == 0) {
                    hourDiff = 23;
                    if (dayDiff > 0) {
                        dayDiff -= 1;
                    } else {
                        // TODO: handle year case?
                    }
                }
            }

            if (dayDiff > 0) {
                gameLength += dayDiff;
                if (dayDiff > 1) gameLength += r.getString(R.string.text_day_abbv_plural);
                else gameLength += r.getString(R.string.text_day_abbv_singular);
            }
            if (hourDiff > 0) {
                if (dayDiff > 0) gameLength += ", ";
                gameLength += hourDiff;
                if (hourDiff > 1) gameLength += r.getString(R.string.text_hour_abbv_plural);
                else gameLength += r.getString(R.string.text_hour_abbv_singular);
            }
            if (minDiff > 0) {
                if (dayDiff > 0 || hourDiff > 0) gameLength += ", ";
                gameLength += minDiff;
                if (minDiff > 1) gameLength += r.getString(R.string.text_minute_abbv_plural);
                else gameLength += r.getString(R.string.text_minute_abbv_singular);
            }
        }

        gameLengthText.setText(gameLength);
    }

    /**
     * Show a dialog that alerts the user that required fields must be populated.
     */
    private void showValidationFailedDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_validation_failed)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create()
                .show();
    }

    private void showColorPicker(final TeamImageButton imageButton) {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.team_colors)
                .setSelectedColor(imageButton.getColor())
                .setDismissOnColorSelected(true)
                .setTitle(R.string.dialog_color_picker)
                .setNegativeButtonText(R.string.dialog_cancel)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            imageButton.setColor(color);
                        }
                    }
                }).build().show(getFragmentManager(),"COLOR_PICKER_DIALOG");
    }

    public void refreshGameLength() {
        displayGameLength();
    }
}
