package io.scoober.ulti.ulti_mate;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GameDisplayFragment extends Fragment {

    private final static int PULLING_STROKE_SIZE = 8;
    private final static int TEAM_CIRCLE_SIZE = 100;

    private onGameEndListener endListener;

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

    private Map<Integer,GameDisplayActivity.TeamViewHolder> teamViewMap;

    public interface onGameEndListener {
        void onGameEnd();
    }

    public GameDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            endListener = (onGameEndListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onGameEndListener");
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
        final View fl = fragmentLayout;
        setupFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                buildFieldSetupDialogs(fl);
            }
        });

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
        game = Utils.getGameDetails(getActivity(),gameId);
        refreshWidgets();
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
        if (game.getHardCapTime() < 1 && game.getSoftCapTime() < 1) {
            timeCapBar.setVisibility(View.GONE);
        } else {
            timeCapBar.setVisibility(View.VISIBLE);
            if (game.getStatus() == Game.Status.NOT_STARTED) {
                // display soft / hard cap times - only if they exist!
                if (game.getSoftCapTime() < 1) {
                    softCapTimeView.setText("None");
                } else {
                    softCapTimeView.setText( Utils.getTimeString(game.getSoftCapTime()) );
                }
                if (game.getHardCapTime() < 1) {
                    hardCapTimeView.setText("None");
                } else {
                    hardCapTimeView.setText( Utils.getTimeString(game.getHardCapTime()) );
                }
            } else {
                // display soft / hard cap timers!
                if (timeCapBar.getCurrentView().getId() != R.id.timeCapTimerBar) {
                    timeCapBar.showNext();
                }
                // Start game cap timer
                startCapTimer();
            }
        }

        // Set the field layout
        if (game.getInitPullingTeamPos() != 0 && game.getInitLeftTeamPos() != 0) {
            showFieldLayout();
            setFieldOrientation();
        }

        // Hide start button / unhide end button if game has already started
        if (game.getStatus() != Game.Status.NOT_STARTED) {
            if (endButton.getVisibility() != View.VISIBLE) {
                endButton.setVisibility(View.VISIBLE);
            }
            if (startButton.getVisibility() != View.GONE) {
                startButton.setVisibility(View.GONE);
            }
        }

        // Set the game status
        setGameStatusText(game.getStatus());

        /*
        Enable or disable the score buttons, depending on the score
        Not started will be handled by the
         */
        GameDisplayActivity.enableDisableScoreButtons(1, game, teamViewMap, true);
        GameDisplayActivity.enableDisableScoreButtons(2, game, teamViewMap, true);
    }

    private void setupScoreButtonListeners(final int team) {

        final GameDisplayActivity.TeamViewHolder teamViewHolder = teamViewMap.get(team);
        teamViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.incrementScore(team);
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                afterPointsChange();

            }
        });

        teamViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.decrementScore(team);
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                afterPointsChange();
            }
        });
    }

    /**
     * Function defines what happens to the view after the score changes
     */
    private void afterPointsChange() {
        // Handle game statuses
        Game.Status status = game.getStatus();
        if (status == Game.Status.HALFTIME) {
            showHalftimeNotification();
        }

        refreshWidgets();
    }

    /**
     * Function to allow users to start changing scores and viewing game statuses.
     * @param startButton   Start / Resume button. Once clicked, game is started / resumed
     * @param endButton     End button stores data to database and locks game settings
     */
    private void startGame(final Button startButton, final Button endButton) {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the game status to started if it hasn't been started yet
                game.start();
                Utils.saveGameDetails(getActivity(),game);

                // Start time cap timer
                if (timeCapBar.getVisibility() == View.VISIBLE) {
                    timeCapBar.showNext();
                    startCapTimer();
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

    private void startCapTimer() {
        // TODO: change 60000 [1 min] to final variable
        // TODO: insert logic to figure out if soft cap or hard cap is next.

        Calendar softCap = Calendar.getInstance();
        Calendar hardCap = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        softCap.setTimeInMillis(game.getSoftCapTime());
        hardCap.setTimeInMillis(game.getHardCapTime());

        // Create timer based on which time is next in future
        if (Utils.isFuture(now, softCap)) {
            softCapTimer = createCountDownTimer(game.getSoftCapTime(), 1000, true);
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.soft_cap_timer_text));
            softCapTimer.start();
        } else if (Utils.isFuture(now, hardCap)) {
            hardCapTimer = createCountDownTimer(game.getHardCapTime(), 1000, false);
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.hard_cap_timer_text));
            hardCapTimer.start();
        } else {
            timeCapTimerText.setText(getActivity().getResources()
                    .getString(R.string.hard_cap_timer_text));
            timeCapTimer.setText(getActivity().getResources().getString(
                    R.string.time_basic_h_m_s, 0, 0, 0) );

            /*
            Time has passed Hard cap. Maybe create a SnackBar that recommends ending game?
             */

            // TODO: create notification for user
        }
    }

    private CountDownTimer createCountDownTimer(long capTime, long countDownInterval,
                                                final boolean startNext) {
        // Get current time
        long now = Calendar.getInstance().getTimeInMillis();

        // Calculate time between now and capTime
        long millisInFuture = capTime - now;

        return new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCapTimer.setText(getActivity().getResources().getString(
                        R.string.time_basic_h_m_s,
                        (millisUntilFinished / 3600000),
                        (millisUntilFinished / 60000),
                        (millisUntilFinished / 1000)
                ));
            }

            @Override
            public void onFinish() {
                timeCapTimer.setText(getActivity().getResources().getString(
                        R.string.time_basic_h_m_s, 0, 0, 0) );

                // TODO: add to String resource file
                String message;

                if (startNext) {
                    // Start next timer (hard cap timer)
                    hardCapTimer = createCountDownTimer(game.getHardCapTime(), 1000,
                            false);
                    hardCapTimer.start();

                    // Change text of soft cap timer to "Hard Cap"
                    timeCapTimerText.setText(getActivity().getResources()
                            .getString(R.string.hard_cap_timer_text));

                    // Soft cap hit.
                    message = "Soft Cap Limit Hit!";
                } else {
                    // Hard cap hit.
                    message = "Hard Cap Limit Hit!";
                }

                // TODO: Create Notification for user to hear / feel
                // Text to send is stored in message variable
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

        endListener.onGameEnd();
    }

    /**
     * Function to simplify setting the gameStatusText TextView.
     * @param status    status in the enum Game.status
     */
    private void setGameStatusText(Game.Status status) {
        gameStatusText.setText(Game.getStatusText(status,
                getActivity().getBaseContext()));
    }

    private void showHalftimeNotification() {
        //TODO
    }

    private void getWidgetReferences(View v) {

        gameTitleView = (TextView) v.findViewById(R.id.gameTitle);
        setupFieldButton = (Button) v.findViewById(R.id.fieldSetup);
        startButton = (Button) v.findViewById(R.id.startButton);
        endButton = (Button) v.findViewById(R.id.endButton);
        gameStatusText = (TextView) v.findViewById(R.id.gameStatusText);

        gameImagesLayout = (LinearLayout) v.findViewById(R.id.gameImagesLayout);

        // Image Views
        leftTeamCircle = (ImageView) v.findViewById(R.id.leftTeamCircle);
        rightTeamCircle = (ImageView) v.findViewById(R.id.rightTeamCircle);

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

    /*
    Dialogs
     */
    public void buildFieldSetupDialogs(final View v) {
        AlertDialog pullDialog;

        final String t1Name = game.getTeam(1).getName();
        final String t2Name = game.getTeam(2).getName();

        // items to display in dialog boxes:
        final String[] items = {t1Name, t2Name};

        // First dialog box gets initial pulling team:
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(v.getContext());
        dialogBox.setTitle(R.string.dialog_init_pulling_team);
        dialogBox.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        game.setInitPullingTeamPos(1);
                        break;
                    case 1:
                        game.setInitPullingTeamPos(2);
                        break;
                }
                dialog.dismiss();
                buildTeamOrientationDialog(game, t1Name, v);
            }
        });

        pullDialog = dialogBox.create();
        pullDialog.show();
    }

    private void buildTeamOrientationDialog(final Game g, final String t1,
                                            final View v) {

        AlertDialog orientationDialog;

        // Strings to show in Dialog with Radio Buttons:
        Resources res = v.getResources();
        final CharSequence[] items = {res.getString(R.string.left), res.getString(R.string.right)};

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(v.getContext());

        dialogBox.setTitle(res.getString(R.string.dialog_left_team, t1));

        // 2nd param = automatically checked item
        dialogBox.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        g.setInitLeftTeamPos(1);
                        break;
                    case 1:
                        g.setInitLeftTeamPos(2);
                        break;
                }
                dialog.dismiss();

                // At this point, both dialog boxes must have been hit & populated Game.
                Utils.saveGameDetails(v.getContext(), g);

                refreshWidgets();
            }
        });

        orientationDialog = dialogBox.create();
        orientationDialog.show();
    }

    /**
     * Function hides setupFieldButton and makes gameImagesLayout visible
     */
    private void showFieldLayout() {
        setupFieldButton.setVisibility(View.GONE);
        gameImagesLayout.setVisibility(View.VISIBLE);
    }
}
