package io.scoober.ulti.ulti_mate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Map;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;

public class GameDisplayActivity extends AppCompatActivity
        implements  GameLengthDialogFragment.OnTimeSelectedListener,
                    GameLengthDialogFragment.OnDateSelectedListener,
                    GameLengthDialogFragment.OnNegativeButtonClickListener,
                    GameLengthDialogFragment.OnPositiveButtonClickListener{

    private MainMenuActivity.DisplayToLaunch displayToLaunch;
    private long gameId;

    GameDisplayFragment gameFrag;

    public static class TeamViewHolder {
        public Button addButton;
        public Button subtractButton;
        public TeamImageButton colorButton;
        public TextView scoreView;
        public TextView nameView;
        public EditText nameEdit;
    }

    private GameDisplayEditFragment gameEditFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        // Setting our app bar (toolbar) as the support action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get data from intent
        Intent oldIntent = getIntent();
//        long id = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        gameId = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA,0);

        // Manage all fragments -> so we can add our display / edit display fragments:
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // handle different cases for where Activity is called from
        switch (displayToLaunch) {
            case NEW:
            case RESUME:
            case UPDATE:
                gameFrag = new GameDisplayFragment();
                fragmentTransaction.add(R.id.game_container, gameFrag, "GAME_DISPLAY_FRAGMENT");
                break;
            case EDIT:
            case VIEW:
                gameEditFrag = new GameDisplayEditFragment();
                fragmentTransaction.add(R.id.game_container, gameEditFrag, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
        }

        // commit changes so everything works.
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (displayToLaunch == MainMenuActivity.DisplayToLaunch.NEW ||
                displayToLaunch == MainMenuActivity.DisplayToLaunch.RESUME ||
                displayToLaunch == MainMenuActivity.DisplayToLaunch.UPDATE ) {
            getMenuInflater().inflate(R.menu.menu_game_display, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_share:
                return true;
            case R.id.action_game_settings:
                Intent intent = new Intent(getBaseContext(), GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, MainMenuActivity.SetupToLaunch.UPDATE_GAME);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
                startActivity(intent);
                return true;
            case R.id.action_edit_field_setup:
                View gameDisplayView = findViewById(R.id.game_container);
                Game game = Utils.getGameDetails(this, gameId);
                gameFrag.buildFieldSetupDialogs(gameDisplayView);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Utility Functions
     */

    /**
     * This function handles enabling and disabling score buttons for the game displays
     * @param team          Number corresponding to the team for which the buttons will be enabled
     * @param game          Game object
     * @param teamViewMap   Object that contains the views for each team
     * @param respectStatus Boolean indicating whether status should be respected if true,
     *                      NOT_STARTED and GAME_OVER will disable both buttons
     */
    public static void enableDisableScoreButtons(int team, Game game,
                                           Map<Integer,TeamViewHolder> teamViewMap,
                                                 boolean respectStatus) {
        GameDisplayActivity.TeamViewHolder teamView = teamViewMap.get(team);

        boolean addButtonEnabled, subtractButtonEnabled;
        Game.Status status = game.getStatus();

        if (respectStatus &&
                (status == Game.Status.NOT_STARTED || status == Game.Status.GAME_OVER)) {
            addButtonEnabled = false;
            subtractButtonEnabled = false;

        } else {
            /*
            If the score is equal to or greater than the max score, disable the add button
            Otherwise enable it.
            */
            addButtonEnabled = game.getScore(team) < Game.MAX_SCORE;

            /*
            If the score is equal to or less than the min score, disable the subtract button
            Otherwise enable it.
            */
            subtractButtonEnabled = game.getScore(team) > Game.MIN_SCORE;
        }

        teamView.addButton.setEnabled(addButtonEnabled);
        teamView.subtractButton.setEnabled(subtractButtonEnabled);
    }

    /**
     * Function found SO here:
     * http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /**
     * Interface Function from GameLengthDialogFragment.OnTimeSelectedListener
     * @param tv    TextView
     */
    public void onTimeSelected(final TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                long buttonTimeMilli = Utils.getMilliFrom12HrString(tv.getText().toString());

                time.setTimeInMillis(buttonTimeMilli);

                int hour = time.get(Calendar.HOUR_OF_DAY);
                int minute = time.get(Calendar.MINUTE);

                TimePickerDialog picker = new TimePickerDialog(GameDisplayActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String newTime = Utils.to12Hr(hourOfDay, minute);
                                tv.setText(newTime);
                            }
                        }, hour, minute, false);

                View customTitleView = Utils.createCustomTitle(GameDisplayActivity.this, null,
                        "Edit Time");
                picker.setCustomTitle(customTitleView);

                picker.show();
            }
        });
    }

    /**
     * Interface Function from GameLengthDialogFragment.OnDateSelectedListener
     * @param tv    TextView
     */
    public void onDateSelected(final TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar time = Calendar.getInstance();

                // get time in milli from date text
                long buttonTimeMilli = Utils.getMilliFromDateString(tv.getText().toString());

                time.setTimeInMillis(buttonTimeMilli);

                int year = time.get(Calendar.YEAR);
                int month = time.get(Calendar.MONTH);
                int day = time.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(GameDisplayActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Add 1 to month of year to fix offset
                        monthOfYear += 1;

                        String newTime = Utils.toMMddyyyy(year, monthOfYear, dayOfMonth);
                        tv.setText(newTime);
                    }
                }, year, month, day);

                View customTitleView = Utils.createCustomTitle(GameDisplayActivity.this, null,
                        "Edit Date");
                picker.setCustomTitle(customTitleView);

                picker.show();
            }
        });
    }

    /**
     * Interface function from GameLengthDialogFragment.OnNegativeButtonClickListener
     * @param tv    TextView representing negative button
     */
    public void onNegativeButtonClick(TextView tv) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get game length dialog fragment from fragmentManager
                GameLengthDialogFragment frag = (GameLengthDialogFragment) getFragmentManager()
                        .findFragmentByTag("GameLengthDialogFragment");

                if (frag != null) {
                    frag.dismiss();
                } else {
                    Log.d("GameDisplayActivity",
                            "Trying to dismiss a fragment that doesn't exist.");
                }
            }
        });
    }

    /**
     * Interface function from GameLengthDialogFragment.OnPositiveButtonClickListener
     * @param tv    TextView representing positive button
     * @param g     Game to be saved
     */
    public void onPositiveButtonClick(TextView tv, final Game g) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get game length dialog fragment from fragmentManager
                GameLengthDialogFragment frag = (GameLengthDialogFragment) getFragmentManager()
                        .findFragmentByTag("GameLengthDialogFragment");

                long start = frag.getGameStartMilli();
                long end = frag.getGameEndMilli();

                g.setStartDate(start);
                g.setEndDate(end);

                Utils.saveGameDetails(GameDisplayActivity.this, g);

                frag.dismiss();

                gameEditFrag.refreshGameLength();
            }
        });
    }
}
