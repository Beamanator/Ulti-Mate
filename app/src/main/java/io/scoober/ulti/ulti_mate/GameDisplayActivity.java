package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        // TODO: Pull Game data from database

        TextView gameTitle = (TextView) findViewById(R.id.gameTitle);
        Button setupFieldButton = (Button) findViewById(R.id.fieldSetup);
        TextView team1 = (TextView) findViewById(R.id.team1Name);
        TextView team2 = (TextView) findViewById(R.id.team2Name);
        Button startPauseButton = (Button) findViewById(R.id.startPauseButton);
        Button endButton = (Button) findViewById(R.id.endButton);
        // TODO: Hook up start / end buttons

        String team1Name = team1.getText().toString();
        String team2Name = team2.getText().toString();

        buildPullingTeamDialog(team1Name, team2Name, setupFieldButton);
        //TODO: do something with variable pullingTeamName
        //TODO: do something with variable team1Side

    }

    private void buildPullingTeamDialog(final String t1, final String t2, Button setupButton) {

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
                                pullingTeamName = t1;
                                break;
                            case 1:
                                pullingTeamName = t2;
                                break;
                        }
                        dialog.dismiss();

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
                        break;
                    case 1:
                        team1Side = "Right";
                        break;
                }
                dialog.dismiss();
            }
        });

        orientationDialog = dialogBox.create();
        orientationDialog.show();
    }
}
