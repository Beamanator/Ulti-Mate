package io.scoober.ulti.ulti_mate;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameDisplayFragment extends Fragment {

    private Button setupFieldButton, startButton, endButton;
    private Button leftTeamAddButton, leftTeamSubtractButton, rightTeamAddButton;
    private Button rightTeamSubtractButton;

    private TextView statusBar, leftTeam, rightTeam, leftTeamScore, rightTeamScore;
    private TextView timeCapType, timeCapTimer, gameTitleView, gameStatusText;

    private int team1Score, team2Score;
    private GradientDrawable leftCircle, rightCircle;
    private LinearLayout timeCapBar, gameImagesLayout;
    private ImageView leftTeamCircle, rightTeamCircle;
    private Game game;
    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    public GameDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_game_display,
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

        // create some local variables:
        String t1Name = game.getTeam1Name();
        String t2Name = game.getTeam2Name();

        // set up score button listeners:
        setupScoreButtonListeners(t1Name, leftTeamScore,
                leftTeamAddButton, leftTeamSubtractButton);
        setupScoreButtonListeners(t2Name, rightTeamScore,
                rightTeamAddButton, rightTeamSubtractButton);

        // build start / end button functionality:
        startGame(startButton, endButton);

        // Build listener & dialogs for field setup:
        buildFieldSetupDialogListener(t1Name, t2Name);

        //TODO: think about creating a "swapTeams" function in case user wants this

        switch (displayToLaunch) {
            case NEW:
                setGameStatusText(Game.GameStatus.NOT_STARTED);
                break;
            case RESUME:
                startButton.setText(R.string.start_resume_button);
                setGameStatusText(Game.GameStatus.PAUSED);
                break;
        }

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    private void inflateTeamData(View v) {
        // set Game title:
        gameTitleView.setText(game.getGameName());

        // set team details:
        leftTeam.setText(game.getTeam1Name());
        rightTeam.setText(game.getTeam2Name());
        leftTeamScore.setText(Integer.toString(game.getTeam1Score()));
        rightTeamScore.setText(Integer.toString(game.getTeam2Score()));
        //TODO: possibly set variables based on team orientation?

        int mdBlack = getResources().getColor(R.color.md_black_1000);
        leftCircle = Utils.createGradientDrawableCircle(100, game.getTeam1Color(), 8, mdBlack);
        rightCircle = Utils.createGradientDrawableCircle(100, game.getTeam2Color(), 0, mdBlack);
        leftTeamCircle.setImageDrawable(leftCircle);
        rightTeamCircle.setImageDrawable(rightCircle);

        // Hard cap / Soft cap bar information:
        // TODO: make sure there's a default time added if the time cap checkbox is true
        if (game.getHardCapTime() < 1 && game.getSoftCapTime() < 1) {
            timeCapBar.setVisibility(View.GONE);
        } else {
            // TODO: insert logic to figure out if soft cap or hard cap is next.
            // TODO: format time text better
            timeCapTimer.setText(Long.toString(game.getSoftCapTime()));
        }
    }

    private void setupScoreButtonListeners(final String team, final TextView score, final Button addButton, final Button subtractButton) {
        final int finalScore = game.getWinningScore();
        // TODO: fix bug where buttons link to same score when teams have same name
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int teamScored;

                if (team.equals(game.getTeam1Name())) {
                    teamScored = 1;
                    team1Score = game.incrementScore(1);
                    score.setText(Integer.toString(team1Score));
                    if (team1Score == 99) {
                        addButton.setEnabled(false);
                    }
                } else if (team.equals(game.getTeam2Name())) {
                    teamScored = 2;
                    team2Score = game.incrementScore(2);
                    score.setText(Integer.toString(team2Score));
                    if (team2Score == 99) {
                        addButton.setEnabled(false);
                    }
                } else {
                    return;
                }
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                subtractButton.setEnabled(true);

                toggleTeamColors();
                calculateGameStatus(1, teamScored);
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int teamScored;
                if (team.equals(game.getTeam1Name())) {
                    teamScored = 1;
                    team1Score = game.decrementScore(1);
                    score.setText(Integer.toString(team1Score));

                    if (team1Score == 0) { subtractButton.setEnabled(false); }
                    if (team1Score == 98) {
                        addButton.setEnabled(true);
                    }
                } else if (team.equals(game.getTeam2Name())){
                    teamScored = 2;
                    team2Score = game.decrementScore(2);
                    score.setText(Integer.toString(team2Score));

                    // Disable minus button if score is 0
                    if (team2Score == 0) { subtractButton.setEnabled(false); }
                    // Enable add button if score is less than 99 (so if it equals 98)
                    if (team2Score == 98) {
                        addButton.setEnabled(true);
                    }
                } else {
                    return;
                }
                Utils.saveGameDetails(getActivity().getBaseContext(), game);

                toggleTeamColors();
                calculateGameStatus(-1, teamScored);
            }
        });
    }

    /**
     * Function to allow users to start changing scores and viewing game statuses.
     * @param startButton   Start / Resume button. Once clicked, game is started / resumed
     * @param endButton     TODO: End button will store data to database and lock game
     */
    private void startGame(final Button startButton, final Button endButton) {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int t1score = game.getTeam1Score();
                int t2score = game.getTeam2Score();

                if (t1score > 0) { leftTeamSubtractButton.setEnabled(true); }
                if (t1score < 99) { leftTeamAddButton.setEnabled(true); }
                if (t2score > 0) { rightTeamSubtractButton.setEnabled(true); }
                if (t2score < 99) { rightTeamAddButton.setEnabled(true); }

                // Update status bar to reflect game status
                calculateGameStatus(0, 0);

                endButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }
        });

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
                confirmBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing?
                        // TODO: remove this click listener?
                    }
                });

                confirmBuilder.create();
                confirmBuilder.show();
            }
        });
    }

    private void endGame(View v) {
        Intent intent = new Intent(v.getContext(),GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                MainMenuActivity.DisplayToLaunch.VIEW);
        startActivity(intent);
    }

    /**
     * Function to simplify setting the gameStatusText TextView.
     * @param status    status in the enum Game.GameStatus
     */
    private void setGameStatusText(Game.GameStatus status) {
        gameStatusText.setText(Game.getStatusText(status,
                getActivity().getBaseContext() ) );
    }

    private void calculateGameStatus(int dir, int team) {
        Game.GameStatus status = game.calculateGameStatus(dir, team);
        // TODO: Create notification for users if halftime?

        setGameStatusText(status);
    }

    private void buildFieldSetupDialogListener(final String t1, final String t2) {

        setupFieldButton.setOnClickListener(new View.OnClickListener() {
            AlertDialog pullDialog;

            // Strings to show in Dialog with Radio Buttons:
            final CharSequence[] items = {t1, t2};

            @Override
            public void onClick(final View v) {
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(v.getContext());

                dialogBox.setTitle("Which Team Pulls First?");

                // 2nd param = automatically checked item
                dialogBox.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                game.setInitPullingTeam(t1);
                                break;
                            case 1:
                                game.setInitPullingTeam(t2);
                                break;
                        }
                        dialog.dismiss();
                        buildTeamOrientationDialog(t1, t2, v);
                    }
                });

                pullDialog = dialogBox.create();
                pullDialog.show();
            }
        });
    }

    private void buildTeamOrientationDialog(final String t1, final String t2, View v) {

        AlertDialog orientationDialog;

        // Strings to show in Dialog with Radio Buttons:
        final CharSequence[] items = {"Left", "Right"};

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(v.getContext());

        dialogBox.setTitle("Which Side is " + t1 + " on?");

        // 2nd param = automatically checked item
        dialogBox.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        game.setInitTeamLeft(t1);
                        break;
                    case 1:
                        game.setInitTeamLeft(t2);
                        break;
                }
                dialog.dismiss();
                // At this point, both dialog boxes must have been hit & populated Game.

                setupFieldButton.setVisibility(View.GONE);
                gameImagesLayout.setVisibility(View.VISIBLE);
            }
        });

        orientationDialog = dialogBox.create();
        orientationDialog.show();
    }

    private void getWidgetReferences(View v) {
        gameTitleView = (TextView) v.findViewById(R.id.gameTitle);
        setupFieldButton = (Button) v.findViewById(R.id.fieldSetup);
        leftTeam = (TextView) v.findViewById(R.id.leftTeam);
        rightTeam = (TextView) v.findViewById(R.id.rightTeam);
        startButton = (Button) v.findViewById(R.id.startButton);
        endButton = (Button) v.findViewById(R.id.endButton);
        gameStatusText = (TextView) v.findViewById(R.id.gameStatusText);

        gameImagesLayout = (LinearLayout) v.findViewById(R.id.gameImagesLayout);

        leftTeamScore = (TextView) v.findViewById(R.id.leftTeamScore);
        leftTeamAddButton = (Button) v.findViewById(R.id.leftTeamAdd);
        leftTeamSubtractButton = (Button) v.findViewById(R.id.leftTeamSubtract);
        rightTeamScore = (TextView) v.findViewById(R.id.rightTeamScore);
        rightTeamAddButton = (Button) v.findViewById(R.id.rightTeamAdd);
        rightTeamSubtractButton = (Button) v.findViewById(R.id.rightTeamSubtract);

        // Image Views
        leftTeamCircle = (ImageView) v.findViewById(R.id.leftTeamCircle);
        rightTeamCircle = (ImageView) v.findViewById(R.id.rightTeamCircle);

        timeCapBar = (LinearLayout) v.findViewById(R.id.timeCapBar);
        timeCapType = (TextView) v.findViewById(R.id.capText);
        timeCapTimer = (TextView) v.findViewById(R.id.capTimer);
    }

    private void toggleTeamColors() {
        // TODO: worry about score eventually [halfime n such]
        int totalScore = game.getTeam1Score() + game.getTeam2Score();
        // TODO: use game.getColor() methods instead of defaults
        if (totalScore % 2 == 0) {
            leftCircle.setColor(Color.RED);
            rightCircle.setColor(Color.BLUE);
        } else {
            leftCircle.setColor(Color.BLUE);
            rightCircle.setColor(Color.RED);
        }
    }
}
