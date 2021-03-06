package io.scoober.ulti.ulti_mate;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Map;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;

public class GameDisplayActivity extends AppCompatActivity
        implements  GameLengthDialogFragment.OnTimeSelectedListener,
                    GameLengthDialogFragment.OnDateSelectedListener,
                    GameLengthDialogFragment.OnNegativeButtonClickListener,
                    GameLengthDialogFragment.OnPositiveButtonClickListener,
                    GameDisplayFragment.StatusChangeListener {

    public final static int TEAM_CIRCLE_SIZE = 100;
    public final static int PULLING_STROKE_SIZE = 8;
    public final static int MAX_TEAM_NAME_LENGTH = 12;

    private MainMenuActivity.DisplayToLaunch displayToLaunch;
    private long gameId;
    private boolean hideUpdateNotification = false; // indicates whether the app notification should be hidden

    private GameDisplayFragment gameFrag;
    private GameDisplayEditFragment gameEditFrag;

    public static class TeamViewHolder {
        public Button addButton;
        public Button subtractButton;
        public TeamImageButton colorButton;
        public TextView scoreView;
        public TextView nameView;
        public EditText nameEdit;
    }

    public enum GameNotificationType {SOFT_CAP, HARD_CAP, HALFTIME, UPDATE}

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
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        gameId = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA,0);

        // Manage all fragments -> so we can add our display / edit display fragments:
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create arguments Bundle for fragments
        Bundle args = new Bundle();
        args.putSerializable(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, displayToLaunch);
        args.putLong(MainMenuActivity.GAME_ID_EXTRA, gameId);

        // handle different cases for where Activity is called from
        switch (displayToLaunch) {
            case NEW:
            case RESUME:
            case UPDATE:
                gameFrag = new GameDisplayFragment();
                gameFrag.setArguments(args);
                fragmentTransaction.add(R.id.game_container, gameFrag, "GAME_DISPLAY_FRAGMENT");
                break;
            case EDIT:
            case VIEW:
                gameEditFrag = new GameDisplayEditFragment();
                gameEditFrag.setArguments(args);
                fragmentTransaction.add(R.id.game_container, gameEditFrag, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
        }

        // commit changes so everything works.
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        cancelUpdateNotification(this, gameId);
        hideUpdateNotification = false; // reset this onResume
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (displayToLaunch == MainMenuActivity.DisplayToLaunch.NEW ||
                displayToLaunch == MainMenuActivity.DisplayToLaunch.RESUME ||
                displayToLaunch == MainMenuActivity.DisplayToLaunch.UPDATE) {
            if (!hideUpdateNotification && Utils.sendNotification(this)) {
                showUpdateNotification(this, gameId);
            }
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_display, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_share:
                // This should be handled inside the fragment b/c it needs game data.
                //  to make sure fragment can consume this, return false here.
                return false;
            case R.id.action_game_settings:
                hideUpdateNotification = true; // don't show app notification in this case
                Intent intent = new Intent(this, GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, MainMenuActivity.SetupToLaunch.UPDATE_GAME);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusChange(Game.Status newStatus) {
        switch(newStatus) {
            case GAME_OVER:
                onGameEnd();
                break;
        }
    }

    /**
     * Function that handles what happens once the user ends the game, specifically swapping out
     * the game display fragment with the game edit fragment and hiding menu options.
     */
    public void onGameEnd() {

        displayToLaunch = MainMenuActivity.DisplayToLaunch.VIEW;

        // Generate arguments for game edit fragment
        Bundle args = new Bundle();
        args.putSerializable(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, displayToLaunch);
        args.putLong(MainMenuActivity.GAME_ID_EXTRA, gameId);

        // Replace current fragment with gameEditFragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        gameEditFrag = new GameDisplayEditFragment();
        gameEditFrag.setArguments(args);
        ft.replace(R.id.game_container, gameEditFrag, "GAME_DISPLAY_EDIT_FRAGMENT");
        ft.commit();
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
     * Sets the orientation of the field, depending on what information the game has
     */
    public static void setFieldOrientation(Game game, Context ctx,
                                           TeamImageButton leftTeamImage, TeamImageButton rightTeamImage) {
        int leftTeamPos = game.getLeftTeamPos();
        if (game.getLeftTeamPos() != 0) {
            // Set the field orientation
            int rightTeamPos = game.getOpposingTeamPos(leftTeamPos);

            leftTeamImage.setVisibility(View.VISIBLE);
            rightTeamImage.setVisibility(View.VISIBLE);

            // Set the pulling team
            @ColorInt int strokeColor = ctx.getResources().getColor(R.color.stroke_color);
            int pullingTeam = game.getPullingTeamPos();
            int leftCircleStrokeSize = 0;
            int rightCircleStrokeSize = 0;
            if (pullingTeam != 0) {
                if (pullingTeam == leftTeamPos) {
                    leftCircleStrokeSize = PULLING_STROKE_SIZE;
                } else {
                    rightCircleStrokeSize = PULLING_STROKE_SIZE;
                }
            }

            leftTeamImage.build(TEAM_CIRCLE_SIZE, game.getTeam(leftTeamPos).getColor(),
                    leftCircleStrokeSize, strokeColor);
            rightTeamImage.build(TEAM_CIRCLE_SIZE, game.getTeam(rightTeamPos).getColor(),
                    rightCircleStrokeSize, strokeColor);

        } else {
            leftTeamImage.setVisibility(View.INVISIBLE);
            rightTeamImage.setVisibility(View.INVISIBLE);
        }
    }

    /*
    Notifications
     */

    /**
     * Shows a persistent notification for the app that has various functions that it can handle.
     */
    public static void showUpdateNotification(Context ctx, long gameId) {

        // Intent to get to the game display activity
        Intent showGames = new Intent(ctx, GameDisplayActivity.class);
        Game game = Utils.getGameDetails(ctx, gameId);
        showGames.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        showGames.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, MainMenuActivity.DisplayToLaunch.UPDATE);

        // Create back stack and pending intent for setContentIntent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(GameDisplayActivity.class);
        stackBuilder.addNextIntent(showGames);
        PendingIntent showGamePI = stackBuilder.getPendingIntent(
                Utils.getGameNotificationID(gameId, GameNotificationType.UPDATE),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Pending intent to increment Team scores
        PendingIntent incrementTeam1ScorePI = getIncrementPendingIntent(ctx, gameId, 1);
        PendingIntent incrementTeam2ScorePI = getIncrementPendingIntent(ctx, gameId, 2);


        // Setup the notification View
        RemoteViews notificationView = new RemoteViews(ctx.getPackageName(), R.layout.notification_game_display_persistent);
        notificationView.setOnClickPendingIntent(R.id.incrementTeam1Button, incrementTeam1ScorePI);
        notificationView.setOnClickPendingIntent(R.id.incrementTeam2Button, incrementTeam2ScorePI);
        String teamText = ctx.getString(R.string.team_list_text, game.getTeam(1).getName(),
                game.getTeam(2).getName());
        String scoreText = ctx.getString(R.string.score_list_text, game.getScore(1), game.getScore(2));
        notificationView.setTextViewText(R.id.title, game.getGameName());
        notificationView.setTextViewText(R.id.teams, teamText);
        notificationView.setTextViewText(R.id.score, scoreText);

        // Generate the notification
        Notification notification = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.field)
                .setContent(notificationView)
                .setContentIntent(showGamePI)
                .build();

        NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(Utils.getGameNotificationID(gameId, GameNotificationType.UPDATE), notification);

    }

    /**
     * Gets a pending intent that increases the score for the given team
     * @param ctx       Context
     * @param gameId    ID of the game to be updated
     * @param teamPos   Position of the scoring team
     * @return          PendingIntent that will call a service to update the score
     */
    private static PendingIntent getIncrementPendingIntent(Context ctx, long gameId, int teamPos) {
        Intent updateTeamScore = new Intent(ctx, GameUpdateService.class);
        updateTeamScore.setAction(GameUpdateService.ACTION_INCREMENT_SCORE);
        updateTeamScore.putExtra(GameUpdateService.EXTRA_GAME_ID, gameId);
        updateTeamScore.putExtra(GameUpdateService.EXTRA_TEAM_POS, teamPos);

        return PendingIntent.getService(ctx,
                teamPos,
                updateTeamScore,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Cancels the persistent notification for the application
     */
    public static void cancelUpdateNotification(Context ctx, long gameId) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(Utils.getGameNotificationID(gameId, GameNotificationType.UPDATE));
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
