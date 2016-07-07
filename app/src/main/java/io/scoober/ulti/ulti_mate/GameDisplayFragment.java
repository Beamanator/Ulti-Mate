package io.scoober.ulti.ulti_mate;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class GameDisplayFragment extends Fragment {

    private Button setupFieldButton, startButton, endButton;

    private TextView statusBar;
    private TextView timeCapType, timeCapTimer, gameTitleView, gameStatusText;

    private GradientDrawable leftCircle, rightCircle;
    private LinearLayout timeCapBar, gameImagesLayout;
    private ImageView leftTeamCircle, rightTeamCircle;
    private Game game;
    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    private Map<Integer,GameDisplayActivity.TeamViewHolder> teamViewMap;

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

        // set up score button listeners:
        setupScoreButtonListeners(1);
        setupScoreButtonListeners(2);

        // build start / end button functionality:
        startGame(startButton, endButton);

        // Build listener & dialogs for field setup:
        buildFieldSetupDialogListener(fragmentLayout);

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
        teamViewMap.get(1).nameView.setText(game.getTeam1Name());
        teamViewMap.get(2).nameView.setText(game.getTeam2Name());
        teamViewMap.get(1).scoreView.setText(Integer.toString(game.getTeam1Score()));
        teamViewMap.get(2).scoreView.setText(Integer.toString(game.getTeam2Score()));
        //TODO: possibly set variables based on team orientation?

        // populate team circles with colors from database and black stroke
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

    private void setupScoreButtonListeners(final int team) {

        final GameDisplayActivity.TeamViewHolder teamViewHolder = teamViewMap.get(team);
        teamViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.incrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team,game,teamViewMap);
                toggleTeamColors();
                calculateGameStatus(1, team);
            }
        });

        teamViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.decrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team,game,teamViewMap);
                toggleTeamColors();
                calculateGameStatus(-1, team);
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

                // Enable or disable the score buttons, depending on the score
                GameDisplayActivity.enableDisableScoreButtons(1,game,teamViewMap);;
                GameDisplayActivity.enableDisableScoreButtons(2,game,teamViewMap);;

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
                confirmBuilder.setNegativeButton("No", null);
                confirmBuilder.create();
                confirmBuilder.show();
            }
        });
    }

    /**
     * Function that handles the end game sequence. Actions taken by this function:
     * 1. Sends user back to GameDisplayActivity to deal with VIEW case
     * 2. Tells Game that game status = GAME_OVER
     * @param v     view used to get Context for saving game + creating a new Intent
     */
    private void endGame(View v) {
        // set new game status:
        game.setGameStatus(Game.GameStatus.GAME_OVER);
        Utils.saveGameDetails(v.getContext(), game);

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

    private void buildFieldSetupDialogListener(final View fl) {
        // Check if data has already been populated
        if (game.getInitPullingTeam() != null) {
            showFieldLayout();
            return;
        }

        // else, set up listener
        setupFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                GameDisplayActivity.buildFieldSetupDialogs(game, fl);
            }
        });
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
        timeCapBar = (LinearLayout) v.findViewById(R.id.timeCapBar);
        timeCapType = (TextView) v.findViewById(R.id.capText);
        timeCapTimer = (TextView) v.findViewById(R.id.capTimer);

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



    private void toggleTeamColors() {
        // TODO: worry about score eventually [halfime n such] + initTeamLeft
        int totalScore = game.getTeam1Score() + game.getTeam2Score();
        int team1Color = game.getTeam1Color();
        int team2Color = game.getTeam2Color();

        if (totalScore % 2 == 0) {
            leftCircle.setColor(team1Color);
            rightCircle.setColor(team2Color);
        } else {
            leftCircle.setColor(team2Color);
            rightCircle.setColor(team1Color);
        }
    }

    /**
     * Function hides setupFieldButton and makes gameImagesLayout visible
     */
    private void showFieldLayout() {
        setupFieldButton.setVisibility(View.GONE);
        gameImagesLayout.setVisibility(View.VISIBLE);
    }
}
