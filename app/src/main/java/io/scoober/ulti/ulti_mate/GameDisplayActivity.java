package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameDisplayActivity extends AppCompatActivity {

    private String pullingTeamName;
    private String team1Side;

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        TextView gameTitleView = (TextView) findViewById(R.id.gameTitle);
        Button setupFieldButton = (Button) findViewById(R.id.fieldSetup);
        TextView team1 = (TextView) findViewById(R.id.team1Name);
        TextView team2 = (TextView) findViewById(R.id.team2Name);
        Button startPauseButton = (Button) findViewById(R.id.startPauseButton);
        Button endButton = (Button) findViewById(R.id.endButton);
        // TODO: Hook up start / end buttons

        Intent intent = getIntent();
        // Get Game id from GameSetupActivity. If no game, set id to 0
        long id = intent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);

        // Get game data from database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        game = gameDbAdapter.getGame(id);
        gameDbAdapter.close();

        String team1Name = game.getTeam1Name();
        String team2Name = game.getTeam2Name();
        String gameTitle = game.getGameName();

        team1.setText(team1Name);
        team2.setText(team2Name);
        gameTitleView.setText(gameTitle);

        buildFieldSetupDialog(team1Name, team2Name, setupFieldButton);
        //TODO: do something with variable pullingTeamName
        //TODO: do something with variable team1Side

    }

    private void buildFieldSetupDialog(final String t1, final String t2, Button setupButton) {

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
}
