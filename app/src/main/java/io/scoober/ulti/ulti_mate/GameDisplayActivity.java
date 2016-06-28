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

    private MainMenuActivity.DisplayToLaunch displayToLaunch;
    private TextView gameStatusText;
    private Button startButton;

    Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        // get data from intent
        Intent oldIntent = getIntent();
        long id = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);

        // get reference to some widgets
        gameStatusText = (TextView) findViewById(R.id.gameStatusText);
        startButton = (Button) findViewById(R.id.startButton);

        // handle different cases for where Activity is called from
        switch (displayToLaunch) {
            case NEW:
                newIntent = new Intent (this, GameDisplayFragment.class);
                gameStatusText.setText(Game.getStatusText(Game.GameStatus.NOT_STARTED, getBaseContext()));
                break;
            case RESUME:
                newIntent = new Intent (this, GameDisplayFragment.class);
                startButton.setText(R.string.start_resume_button);
                gameStatusText.setText(Game.getStatusText(Game.GameStatus.PAUSED, getBaseContext()));
                break;
            case EDIT:
                // TODO: run edit game fragment - GameDisplayEditFragment.class
                // newIntent = new Intent (this, GameDisplayEditFragment.class);
                break;
            case VIEW:
                newIntent = new Intent (this, GameDisplayFragment.class);
                // TODO: make sure this is what we want
                gameStatusText.setText(Game.getStatusText(Game.GameStatus.GAME_OVER, getBaseContext()));
                break;
        }

        // pass id along from old intent
        newIntent.putExtra(MainMenuActivity.GAME_ID_EXTRA, id);

        // pass displayToLaunch along from old intent
        newIntent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, displayToLaunch);

        // start new activity
        startActivity(newIntent);
    }
}
