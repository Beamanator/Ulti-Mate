package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GameDisplayActivity extends AppCompatActivity {

    private MainMenuActivity.DisplayToLaunch displayToLaunch;
    private long gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get data from intent
        Intent oldIntent = getIntent();
//        long id = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        gameId = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA,0);

        // Manage all fragments -> so we can add our display / edit display fragments:
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // handle different cases for where Activity is called from
        switch (displayToLaunch) {
            case NEW:
                GameDisplayFragment gameFrag1 = new GameDisplayFragment();
                fragmentTransaction.add(R.id.game_container, gameFrag1, "GAME_DISPLAY_FRAGMENT");
                break;
            case RESUME:
                GameDisplayFragment gameFrag2 = new GameDisplayFragment();
                fragmentTransaction.add(R.id.game_container, gameFrag2, "GAME_DISPLAY_FRAGMENT");
                break;
            case EDIT:
                GameDisplayEditFragment gameEditFrag1 = new GameDisplayEditFragment();
                fragmentTransaction.add(R.id.game_container, gameEditFrag1, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
            case VIEW:
                GameDisplayEditFragment gameEditFrag2 = new GameDisplayEditFragment();
                fragmentTransaction.add(R.id.game_container, gameEditFrag2, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
        }

        // commit changes so everything works.
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_share:
                return true;
            case R.id.action_game_settings:
                Intent intent = new Intent(getBaseContext(), GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
