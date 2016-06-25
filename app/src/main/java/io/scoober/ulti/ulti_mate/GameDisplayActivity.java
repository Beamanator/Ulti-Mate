package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameDisplayActivity extends AppCompatActivity {

    private String pullingTeamName;
    private String team1Side;

    private TextView leftTeam;
    private TextView rightTeam;
    private TextView leftTeamScore;
    private Button leftTeamAddButton;
    private Button leftTeamSubtractButton;
    private TextView rightTeamScore;
    private Button rightTeamAddButton;
    private Button rightTeamSubtractButton;

    private String team1Name, team2Name;
    private int team1Score, team2Score;

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        TextView gameTitleView = (TextView) findViewById(R.id.gameTitle);
        Button setupFieldButton = (Button) findViewById(R.id.fieldSetup);
        leftTeam = (TextView) findViewById(R.id.leftTeam);
        rightTeam = (TextView) findViewById(R.id.rightTeam);
        Button startPauseButton = (Button) findViewById(R.id.startPauseButton);
        Button endButton = (Button) findViewById(R.id.endButton);
        // TODO: Hook up start / end buttons

        leftTeamScore = (TextView) findViewById(R.id.leftTeamScore);
        leftTeamAddButton = (Button) findViewById(R.id.leftTeamAdd);
        leftTeamSubtractButton = (Button) findViewById(R.id.leftTeamSubtract);
        rightTeamScore = (TextView) findViewById(R.id.rightTeamScore);
        rightTeamAddButton = (Button) findViewById(R.id.rightTeamAdd);
        rightTeamSubtractButton = (Button) findViewById(R.id.rightTeamSubtract);

        Intent intent = getIntent();
        // Get Game id from GameSetupActivity. If no game, set id to 0
        long id = intent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        game = getGameDetails(id);

        team1Name = game.getTeam1Name();
        team2Name = game.getTeam2Name();
        String gameTitle = game.getGameName();
        String initTeamLeft = game.getInitTeamLeft();
        team1Score = game.getTeam1Score();
        team2Score = game.getTeam2Score();
        String team1Color = game.getTeam1Color();
        String team2Color = game.getTeam2Color();

        // set up basic game details
        gameTitleView.setText(gameTitle);
        // TODO: add soft / hard cap info from Game object

        // set up team details
        //TODO: add team colors
        inflateTeamData(team1Name, team2Name, team1Color, team2Color);
        setupScoreButtonListeners(team1Name, leftTeamScore, leftTeamAddButton, leftTeamSubtractButton);
        setupScoreButtonListeners(team2Name, rightTeamScore, rightTeamAddButton, rightTeamSubtractButton);

        buildFieldSetupDialogListener(team1Name, team2Name, setupFieldButton);
        //TODO: do something with variable pullingTeamName
        //TODO: do something with variable team1Side
        //TODO: think about creating a "swapTeams" function in case user wants this
    }

    private void setupScoreButtonListeners(final String team, final TextView score, Button addButton, final Button subtractButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team.equals(team1Name)) {
                    game.incrementScore(1);
                    team1Score += 1;
                    score.setText(Integer.toString(team1Score));
                } else if (team.equals(team2Name)) {
                    game.incrementScore(2);
                    team2Score += 1;
                    score.setText(Integer.toString(team2Score));
                } else {
                    return;
                }
                saveGameDetails(game);
                subtractButton.setEnabled(true);
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team.equals(team1Name)) {
                    game.decrementScore(1);
                    team1Score -= 1;
                    score.setText(Integer.toString(team1Score));
                    if (team1Score == 0) {
                        subtractButton.setEnabled(false);
                    }
                } else if (team.equals(team2Name)){
                    game.decrementScore(2);
                    team2Score -= 1;
                    score.setText(Integer.toString(team2Score));
                    if (team2Score == 0) {
                        subtractButton.setEnabled(false);
                    }
                } else {
                    return;
                }
                saveGameDetails(game);
            }
        });
    }

    private void inflateTeamData(String t1, String t2, String c1, String c2) {
        //TODO: possibly set variables based on team orientation?
        leftTeam.setText(t1);
        rightTeam.setText(t2);

        leftTeamScore.setText(Integer.toString(team1Score));
        rightTeamScore.setText(Integer.toString(team2Score));

        if (team1Score > 0) {
            leftTeamSubtractButton.setEnabled(true);
        }
        if (team2Score > 0) {
            rightTeamSubtractButton.setEnabled(true);
        }

        //TODO: do something with colors!
    }

    private void buildFieldSetupDialogListener(final String t1, final String t2, Button setupButton) {

        setupButton.setOnClickListener(new View.OnClickListener() {
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
                                // TODO: Instead of this, set Game variable initPullingTeam to t1,
                                //   or team1Name if possible
                                pullingTeamName = t1;
                                break;
                            case 1:
                                // TODO: Instead of this, set Game variable initPullingTeam to t2,
                                //   or team2Name if possible
                                pullingTeamName = t2;
                                break;
                        }
                        dialog.dismiss();
                        // TODO: maybe hide setup Button here?
                        buildTeamOrientationDialog(t1, v);
                    }
                });

                pullDialog = dialogBox.create();
                pullDialog.show();
            }
        });
    }

    private void buildTeamOrientationDialog(final String t1, View v) {

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
                        team1Side = "Left";
                        // TODO: instead, set game variable here.
                        break;
                    case 1:
                        team1Side = "Right";
                        // TODO: instead, set game variable here.
                        break;
                }
                dialog.dismiss();
                // TODO: hide setupButton here if we didn't do this elsewhere
            }
        });

        orientationDialog = dialogBox.create();
        orientationDialog.show();
    }

    public long saveGameDetails(Game g) {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        long gameID = gameDbAdapter.saveGame(g);
        gameDbAdapter.close();
        return gameID;
    }

    public Game getGameDetails(long gameID) {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        Game newGame = gameDbAdapter.getGame(gameID);
        gameDbAdapter.close();
        return newGame;
    }
}
