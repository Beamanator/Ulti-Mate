package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class GameDisplayActivity extends AppCompatActivity {

    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        // get data from intent
        Intent oldIntent = getIntent();
//        long id = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);

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

    public Game getGameDetails(long gameID) {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        Game newGame = gameDbAdapter.getGame(gameID);
        gameDbAdapter.close();
        return newGame;
    }
}
