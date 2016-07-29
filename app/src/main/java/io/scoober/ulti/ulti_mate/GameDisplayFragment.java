package io.scoober.ulti.ulti_mate;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameDisplayFragment extends Fragment {

    private final static int PULLING_STROKE_SIZE = 8;
    private final static int TEAM_CIRCLE_SIZE = 100;

    private StatusChangeListener statusListener;

    private Button setupFieldButton, startButton, endButton;
    private TextView gameTitleView, gameStatusText, timeCapTimer;
    private TextView timeCapTimerText, softCapTimeView, hardCapTimeView;
    private GradientDrawable leftCircle, rightCircle;
    private ImageView leftTeamCircle, rightTeamCircle;

    private CountDownTimer softCapTimer, hardCapTimer;

    private ViewSwitcher timeCapBar;
    private LinearLayout gameImagesLayout;
    private long gameId;
    private Game game;

    private Map<Integer, GameDisplayActivity.TeamViewHolder> teamViewMap;

    public interface StatusChangeListener {
        void onStatusChange(Game.Status newStatus);
    }

    public GameDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            statusListener = (StatusChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StatusChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_game_display,
                container, false);

        Bundle args = getArguments();
        gameId = args.getLong(MainMenuActivity.GAME_ID_EXTRA, 0);

        // set up widget references
        getWidgetReferences(fragmentLayout);

        // set up score button listeners:
        setupScoreButtonListeners(1);
        setupScoreButtonListeners(2);

        // build start / end button functionality:
        startGame(startButton, endButton);

        // Build listener & dialogs for field setup:
        setupFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(getActivity(), GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, MainMenuActivity.SetupToLaunch.UPDATE_GAME);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
                intent.putExtra(MainMenuActivity.GAME_SETUP_FIELD_ONLY_EXTRA, true);
                startActivity(intent);
            }
        });

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Stop timers when fragment is stopped.
        //  this prevents duplicate timers from being created
        if (softCapTimer != null) {
            softCapTimer.cancel();
        }
        if (hardCapTimer != null) {
            hardCapTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // inflate team data and populate private variables
        game = Utils.getGameDetails(getActivity(), gameId);
        refreshWidgets();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_game_settings).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void refreshWidgets() {
        // set Game title:
        gameTitleView.setText(game.getGameName());

        // set team details:
        teamViewMap.get(1).nameView.setText(game.getTeam(1).getName());
        teamViewMap.get(2).nameView.setText(game.getTeam(2).getName());
        teamViewMap.get(1).scoreView.setText(Integer.toString(game.getScore(1)));
        teamViewMap.get(2).scoreView.setText(Integer.toString(game.getScore(2)));

        // populate team circles with colors from database and black stroke
        int strokeColor = getResources().getColor(R.color.stroke_color);
        leftCircle = Utils.createGradientDrawableCircle(TEAM_CIRCLE_SIZE,
                game.getTeam(1).getColor(), 0, strokeColor);
        rightCircle = Utils.createGradientDrawableCircle(TEAM_CIRCLE_SIZE,
                game.getTeam(2).getColor(), 0, strokeColor);
        leftTeamCircle.setImageDrawable(leftCircle);
        rightTeamCircle.setImageDrawable(rightCircle);

        // Hard cap / Soft cap bar information:
        Game.Status status = game.getStatus();
        if (game.getHardCapTime() < 1 && game.getSoftCapTime() < 1) {
            timeCapBar.setVisibility(View.GONE);
        } else {
            timeCapBar.setVisibility(View.VISIBLE);
            if (status == Game.Status.NOT_STARTED) {
                // display soft / hard cap times - only if they exist!
                if (game.getSoftCapTime() < 1) {
                    softCapTimeView.setText("None");
                } else {
                    softCapTimeView.setText(Utils.getTimeString(game.getSoftCapTime()));
                }
                if (game.getHardCapTime() < 1) {
                    hardCapTimeView.setText("None");
                } else {
                    hardCapTimeView.setText(Utils.getTimeString(game.getHardCapTime()));
                }
            } else {
                // display soft / hard cap timers!
                if (timeCapBar.getCurrentView().getId() != R.id.timeCapTimerBar) {
                    timeCapBar.showNext();
                }
                // Start game cap timers
                startTimers();

                // TODO: change background color based of soft / hard cap
            }
        }

        // Set the field layout
        if (game.getInitLeftTeamPos() != 0) {
            showFieldLayout();
            setFieldOrientation();
        }

        // Hide start button / unhide end button if game has already started
        if (status != Game.Status.NOT_STARTED) {
            if (endButton.getVisibility() != View.VISIBLE) {
                endButton.setVisibility(View.VISIBLE);
            }
            if (startButton.getVisibility() != View.GONE) {
                startButton.setVisibility(View.GONE);
            }
        }

        // Set the game status
        setGameStatusText(status);

        /*
        Enable or disable the score buttons, depending on the score
        Not started will be handled by the
         */
        GameDisplayActivity.enableDisableScoreButtons(1, game, teamViewMap, true);
        GameDisplayActivity.enableDisableScoreButtons(2, game, teamViewMap, true);

        /*
        Enable or disable the field setup button, depending on status
         */
        if (status.compareTo(Game.Status.HALFTIME) >= 0) {
            setupFieldButton.setEnabled(false);
        } else {
            setupFieldButton.setEnabled(true);
        }
    }

    private void setupScoreButtonListeners(final int team) {

        final GameDisplayActivity.TeamViewHolder teamViewHolder = teamViewMap.get(team);
        teamViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.Status prevStatus = game.getStatus();
                changeScore(game, team, true);
                afterPointsChange(prevStatus, game.getStatus());
            }
        });

        teamViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.Status prevStatus = game.getStatus();
                changeScore(game, team, false);
                afterPointsChange(prevStatus, game.getStatus());
            }
        });
    }

    /**
     * Increments or decrements the score
     * @param game              Game object
     * @param teamPos           Team that is gaining/losing a point
     * @param incrementScore    True - increment, false - decrement
     */
    private void changeScore(Game game, int teamPos, boolean incrementScore) {
        if (incrementScore) {
            game.incrementScore(teamPos);
        } else {
            game.decrementScore(teamPos);
        }
        Utils.saveGameDetails(getActivity().getBaseContext(), game);

    }

    /**
     * Function defines what happens to the view after the score changes
     * @param prevStatus    previous status
     * @param newStatus     new status
     */
    private void afterPointsChange(Game.Status prevStatus, Game.Status newStatus) {
        // Handle game statuses
        if (newStatus == Game.Status.HALFTIME) {
            showHalftimeNotification();
        }

        if (prevStatus != newStatus) {
            statusListener.onStatusChange(newStatus);
        }

        refreshWidgets();
    }

    /**
     * Function to allow users to start changing scores and viewing game statuses.
     *
     * @param startButton Start / Resume button. Once clicked, game is started / resumed
     * @param endButton   End button stores data to database and locks game settings
     */
    private void startGame(final Button startButton, final Button endButton) {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the game status to started if it hasn't been started yet
                game.start();
                Utils.saveGameDetails(getActivity(),game);

                // Start time cap timer & display timer
                if (timeCapBar.getVisibility() == View.VISIBLE) {
                    timeCapBar.showNext();
                    startTimers();
                }

                refreshWidgets();
            }
        });

        // TODO: set endButton builder text to strings.xml file
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
                confirmBuilder.setTitle("Confirm Game End");
                confirmBuilder.setMessage("Are you sure you want to end the game?");
                confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGame(v);
                    }
                });
                confirmBuilder.setNegativeButton("No", null);
                confirmBuilder.create();
                confirmBuilder.show();
            }
        });
    }

    private void startTimers() {
        Calendar softCap = Calendar.getInstance();
        Calendar hardCap = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        softCap.setTimeInMillis(game.getSoftCapTime());
        hardCap.setTimeInMillis(game.getHardCapTime());

        boolean timerCreated = startCapTimer(now);
        if (timerCreated) {
            // NO need to check to see if alarms already exist. If they do, they'll be
            //  canceled & overwritten
            Resources r = getResources();
            if (Utils.isFuture(now, softCap)) {
                setAlarm(softCap.getTimeInMillis(),
                        Utils.getGameNotificationID(game.getId(),
                                GameDisplayActivity.GameNotificationType.SOFT_CAP),
                        r.getString(R.string.notific_message_soft_cap_hit),
                        r.getString(R.string.notific_message_text_game_hint),
                        r.getString(R.string.notific_message_alert_time_cap_hit));
            }
            if (Utils.isFuture(now, hardCap)) {
                setAlarm(hardCap.getTimeInMillis(),
                        Utils.getGameNotificationID(game.getId(),
                                GameDisplayActivity.GameNotificationType.HARD_CAP),
                        r.getString(R.string.notific_message_hard_cap_hit),
                        r.getString(R.string.notific_message_text_game_hint),
                        r.getString(R.string.notific_message_alert_time_cap_hit));
            }
        }
    }

    private boolean startCapTimer(Calendar now) {
        Calendar softCap = Calendar.getInstance();
        Calendar hardCap = Calendar.getInstance();

        softCap.setTimeInMillis(game.getSoftCapTime());
        hardCap.setTimeInMillis(game.getHardCapTime());

        Resources r = getResources();

        // Create timer based on which time is next in future
        if (Utils.isFuture(now, softCap)) {
            softCapTimer = createCountDownTimer(now.getTimeInMillis(), game.getSoftCapTime(),
                    1000, true);
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.soft_cap_timer_text));
            softCapTimer.start();

            timeCapBar.setBackgroundColor(r.getColor(R.color.md_amber_500));
            return true;
        } else if (Utils.isFuture(now, hardCap)) {
            hardCapTimer = createCountDownTimer(now.getTimeInMillis(), game.getHardCapTime(),
                    1000, false);
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.hard_cap_timer_text));
            hardCapTimer.start();

            timeCapBar.setBackgroundColor(r.getColor(R.color.md_red_A700));
            return true;
        } else {
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.hard_cap_timer_text));
            timeCapTimer.setText(getActivity().getResources().getString(
                    R.string.time_basic_h_m_s, "00", "00", "00") );

            timeCapBar.setBackgroundColor(r.getColor(R.color.md_red_A700));

            return false;
        }
    }

    public void setAlarm(long alertTime, int notificId, String msg, String msgText, String msgAlert) {

        Intent alertIntent = new Intent(getActivity(), AlertReceiver.class);

        // put notification extras into intent
        alertIntent.putExtra(MainMenuActivity.NOTIFICATION_NEXT_CLASS, GameDisplayActivity.class);
        alertIntent.putExtra(MainMenuActivity.NOTIFICATION_MESSAGE, msg);
        alertIntent.putExtra(MainMenuActivity.NOTIFICATION_MESSAGE_TEXT, msgText);
        alertIntent.putExtra(MainMenuActivity.NOTIFICATION_MESSAGE_ALERT, msgAlert);
        alertIntent.putExtra(MainMenuActivity.NOTIFICATION_ID, notificId);

        // put game extras into intent
        alertIntent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        alertIntent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                MainMenuActivity.DisplayToLaunch.RESUME);

        AlarmManager alarmManager = (AlarmManager)
                getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(getActivity(), notificId, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private CountDownTimer createCountDownTimer(final long now, long capTime, long countDownInterval,
                                                final boolean startNext) {

        // Calculate time between now and capTime
        final long millisInFuture = capTime - now;

        return new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / 3600000;
                long minutes = (millisUntilFinished - (hours * 3600000)) / 60000;
                long seconds = (millisUntilFinished - (hours * 3600000) - (minutes * 60000)) / 1000;

                String sHours = String.format(Locale.getDefault(), "%02d", hours);
                String sMinutes = String.format(Locale.getDefault(), "%02d", minutes);
                String sSeconds = String.format(Locale.getDefault(), "%02d", seconds);

                timeCapTimer.setText(getActivity().getResources().getString(
                        R.string.time_basic_h_m_s,
                        sHours, sMinutes, sSeconds
                ));
            }

            @Override
            public void onFinish() {
                timeCapTimer.setText(getActivity().getResources().getString(
                        R.string.time_basic_h_m_s, "00" , "00", "00") );

                if (startNext) {
                    // Start next timer (hard cap timer)
                    hardCapTimer = createCountDownTimer(Calendar.getInstance().getTimeInMillis(),
                            game.getHardCapTime(), 1000,
                            false);
                    hardCapTimer.start();

                    timeCapBar.setBackgroundColor(getResources().getColor(R.color.md_red_A700));

                    // Change text of soft cap timer to "Hard Cap"
                    timeCapTimerText.setText(getActivity().getResources()
                            .getString(R.string.hard_cap_timer_text));
                }
            }
        };
    }

    /**
     * Function that handles the end game sequence. Actions taken by this function:
     * 1. Sends user back to GameDisplayActivity to deal with VIEW case
     * 2. Tells Game that game status = GAME_OVER
     * 3. Stores end data
     * @param v     view used to get Context for saving game + creating a new Intent
     */
    private void endGame(View v) {
        // set new game status and endDate, then save game:
        game.end();
        Utils.saveGameDetails(v.getContext(), game);

        statusListener.onStatusChange(game.getStatus());
    }

    /**
     * Function to simplify setting the gameStatusText TextView.
     * @param status    status in the enum Game.status
     */
    private void setGameStatusText(Game.Status status) {
        gameStatusText.setText(Game.getStatusText(status,
                getActivity().getBaseContext()));
    }

    /**
     * Function creates and shows a notification when a game's status reaches halftime
     */
    private void showHalftimeNotification() {
        Intent intent = new Intent(getActivity(), GameDisplayActivity.class);

        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                MainMenuActivity.DisplayToLaunch.RESUME);

        int id = Utils.getGameNotificationID(gameId,
                GameDisplayActivity.GameNotificationType.HALFTIME);

        PendingIntent notificationIntent = PendingIntent.getActivity(getActivity(), id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String name1 = game.getTeam(1).getName();
        String name2 = game.getTeam(2).getName();

        // Truncate team name if it's too long
        if (name1.length() > GameDisplayActivity.MAX_TEAM_NAME_LENGTH) {
            name1 = name1.substring(0, GameDisplayActivity.MAX_TEAM_NAME_LENGTH);
            name1 += (char) 8230;
        }
        if (name2.length() > GameDisplayActivity.MAX_TEAM_NAME_LENGTH) {
            name2 = name2.substring(0, GameDisplayActivity.MAX_TEAM_NAME_LENGTH);
            name2 += (char) 8230;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_hourglass_empty_black_24dp)
                .setContentTitle(getResources().getString(
                        R.string.notific_message_halftime_title,
                        game.getScore(1), game.getScore(2)
                ))
                .setContentText(getResources().getString(
                        R.string.notific_message_halftime_text,
                        name1, name2
                ))
                .setTicker(getResources().getString(R.string.notific_message_halftime_alert))
                .setAutoCancel(true)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        NotificationManager notifManager = (NotificationManager)
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        notifManager.notify(id, builder.build());
    }

    private void getWidgetReferences(View v) {

        gameTitleView = (TextView) v.findViewById(R.id.gameTitle);
        setupFieldButton = (Button) v.findViewById(R.id.fieldSetup);
        startButton = (Button) v.findViewById(R.id.startButton);
        endButton = (Button) v.findViewById(R.id.endButton);
        gameStatusText = (TextView) v.findViewById(R.id.gameStatusText);

        gameImagesLayout = (LinearLayout) v.findViewById(R.id.gameImagesLayout);

        // Image Views
        leftTeamCircle = (ImageView) v.findViewById(R.id.leftTeamImageButton);
        rightTeamCircle = (ImageView) v.findViewById(R.id.rightTeamImageButton);

        // Time Cap Views
        timeCapBar = (ViewSwitcher) v.findViewById(R.id.timeCapBar);
        timeCapTimer = (TextView) v.findViewById(R.id.capTimer);
        timeCapTimerText = (TextView) v.findViewById(R.id.capTimerText);
        softCapTimeView = (TextView) v.findViewById(R.id.softCapTime);
        hardCapTimeView = (TextView) v.findViewById(R.id.hardCapTime);

        getTeamViews(v);

    }

    /**
     * This function initializes the teamViewMap, which maps team number to a TeamViewHolder,
     * which contains all of the views related to that particular team.
     * @param v View that contains all of the buttons
     */
    private void getTeamViews(View v) {
        // Team 1
        GameDisplayActivity.TeamViewHolder team1View = new GameDisplayActivity.TeamViewHolder();
        team1View.nameView = (TextView) v.findViewById(R.id.leftTeam);
        team1View.scoreView = (TextView) v.findViewById(R.id.leftTeamScore);
        team1View.addButton = (Button) v.findViewById(R.id.leftTeamAdd);
        team1View.subtractButton = (Button) v.findViewById(R.id.leftTeamSubtract);

        // Team 2
        GameDisplayActivity.TeamViewHolder team2View = new GameDisplayActivity.TeamViewHolder();
        team2View.nameView = (TextView) v.findViewById(R.id.rightTeam);
        team2View.scoreView = (TextView) v.findViewById(R.id.rightTeamScore);
        team2View.addButton = (Button) v.findViewById(R.id.rightTeamAdd);
        team2View.subtractButton = (Button) v.findViewById(R.id.rightTeamSubtract);

        // Setup the view map
        teamViewMap = new HashMap<>();
        teamViewMap.put(1,team1View);
        teamViewMap.put(2,team2View);
    }

    private void setFieldOrientation() {

        // Set the team positions
        int leftTeamPos = game.getLeftTeamPos();
        int rightTeamPos = game.getOpposingTeamPos(leftTeamPos);

        leftCircle.setColor(game.getTeam(leftTeamPos).getColor());
        rightCircle.setColor(game.getTeam(rightTeamPos).getColor());

        // Set the pulling team
        @ColorInt int strokeColor = getResources().getColor(R.color.stroke_color);
        int pullingTeamPos = game.getPullingTeamPos();
        int leftCircleStrokeSize = 0;
        int rightCircleStrokeSize = 0;
        if (pullingTeamPos == leftTeamPos) {
            leftCircleStrokeSize = PULLING_STROKE_SIZE;
        } else if (pullingTeamPos == rightTeamPos) {
            rightCircleStrokeSize = PULLING_STROKE_SIZE;
        }

        leftCircle.setStroke(leftCircleStrokeSize, strokeColor);
        rightCircle.setStroke(rightCircleStrokeSize, strokeColor);




    }


    /**
     * Function hides setupFieldButton and makes gameImagesLayout visible
     */
    private void showFieldLayout() {
        setupFieldButton.setVisibility(View.GONE);
        gameImagesLayout.setVisibility(View.VISIBLE);
    }
}
