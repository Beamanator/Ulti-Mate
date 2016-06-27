package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.Image;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.test.suitebuilder.TestMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameDisplayActivity extends AppCompatActivity {

    private Button setupFieldButton, startButton, endButton;

    private LinearLayout gameImagesLayout;

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

    private GradientDrawable leftCircle, rightCircle;

    private LinearLayout timeCapBar;
    private TextView timeCapType, timeCapTimer;

    private ImageView leftTeamCircle, rightTeamCircle;

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        TextView gameTitleView = (TextView) findViewById(R.id.gameTitle);
        setupFieldButton = (Button) findViewById(R.id.fieldSetup);
        leftTeam = (TextView) findViewById(R.id.leftTeam);
        rightTeam = (TextView) findViewById(R.id.rightTeam);
        startButton = (Button) findViewById(R.id.startButton);
        endButton = (Button) findViewById(R.id.endButton);
        // TODO: Hook up start / end buttons

        gameImagesLayout = (LinearLayout) findViewById(R.id.gameImagesLayout);

        leftTeamScore = (TextView) findViewById(R.id.leftTeamScore);
        leftTeamAddButton = (Button) findViewById(R.id.leftTeamAdd);
        leftTeamSubtractButton = (Button) findViewById(R.id.leftTeamSubtract);
        rightTeamScore = (TextView) findViewById(R.id.rightTeamScore);
        rightTeamAddButton = (Button) findViewById(R.id.rightTeamAdd);
        rightTeamSubtractButton = (Button) findViewById(R.id.rightTeamSubtract);

        // Image Views
        leftTeamCircle = (ImageView) findViewById(R.id.leftTeamCircle);
        rightTeamCircle = (ImageView) findViewById(R.id.rightTeamCircle);

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
        timeCapBar = (LinearLayout) findViewById(R.id.timeCapBar);
        timeCapType = (TextView) findViewById(R.id.capText);
        timeCapTimer = (TextView) findViewById(R.id.capTimer);

        // set up team details
        //TODO: add team colors from game object
        inflateTeamData(team1Name, team2Name, team1Color, team2Color);
        setupScoreButtonListeners(team1Name, leftTeamScore, leftTeamAddButton, leftTeamSubtractButton);
        setupScoreButtonListeners(team2Name, rightTeamScore, rightTeamAddButton, rightTeamSubtractButton);

        // build start / end button functionality:
        startGame(startButton, endButton);

        buildFieldSetupDialogListener(team1Name, team2Name);
        //TODO: do something with variable pullingTeamName
        //TODO: do something with variable team1Side

        //TODO: think about creating a "swapTeams" function in case user wants this
    }

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

                // TODO: set status bar to reflect game has started / halftime / etc.

                endButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }
        });
    }

    private GradientDrawable createGradientDrawable(int color, int strokeSize, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setShape(GradientDrawable.OVAL);
        gd.setStroke(strokeSize, strokeColor);
        return gd;
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

    private void setupScoreButtonListeners(final String team, final TextView score, final Button addButton, final Button subtractButton) {
        final int finalScore = game.getWinningScore();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (team.equals(team1Name)) {
                    game.incrementScore(1);
                    // TODO: replace team1Score with game.getTeam1Score()?
                    team1Score += 1;
                    score.setText(Integer.toString(team1Score));
                    if (team1Score == 99) {
                        addButton.setEnabled(false);
                    }
                } else if (team.equals(team2Name)) {
                    game.incrementScore(2);
                    team2Score += 1;
                    score.setText(Integer.toString(team2Score));
                    // win by 2 logic
                    if (team2Score == 99) {
                        addButton.setEnabled(false);
                    }
                } else {
                    return;
                }
                saveGameDetails(game);
                subtractButton.setEnabled(true);

                toggleTeamColors();

            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team.equals(team1Name)) {
                    game.decrementScore(1);
                    team1Score -= 1;
                    score.setText(Integer.toString(team1Score));

                    if (team1Score == 0) { subtractButton.setEnabled(false); }
                    if (team1Score == 98) {
                        addButton.setEnabled(true);
                    }
                } else if (team.equals(team2Name)){
                    game.decrementScore(2);
                    team2Score -= 1;
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
                saveGameDetails(game);

                toggleTeamColors();
            }
        });
    }

    private void inflateTeamData(String t1, String t2, String c1, String c2) {
        //TODO: possibly set variables based on team orientation?
        leftTeam.setText(t1);
        rightTeam.setText(t2);

        leftTeamScore.setText(Integer.toString(team1Score));
        rightTeamScore.setText(Integer.toString(team2Score));

        // If hard & soft caps are 0, they must have not been clicked
        // TODO: make sure there's a default time added if the time cap checkbox is true
        if (game.getHardCapTime() < 1 && game.getSoftCapTime() < 1) {
            timeCapBar.setVisibility(View.GONE);
        } else {
            // TODO: insert logic to figure out if soft cap or hard cap is next.
            // TODO: format time text better
            timeCapTimer.setText(Long.toString(game.getSoftCapTime()));
        }

        String team1Color = game.getTeam1Color();
        String team2Color = game.getTeam2Color();
        if (team1Color == null) {
            leftCircle = createGradientDrawable(Color.RED, 8, Color.BLACK);
        } // TODO: add code for when color is defined by game setup activity
        if (team2Color == null) {
            rightCircle = createGradientDrawable(Color.BLUE, 0, Color.BLACK);
        } // TODO: add code for when color is defined by game setup activity
        leftTeamCircle.setImageDrawable(leftCircle);
        rightTeamCircle.setImageDrawable(rightCircle);

        // On "Resume Game" change text of Start button to "Resume"
        Log.d("Game Display", "t1: " + game.getTeam1Score() + " - t2: " + game.getTeam2Score());
        if (game.getTeam1Score() > 0 || game.getTeam2Score() > 0) {
            startButton.setText(R.string.start_resume_button);
        }
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
