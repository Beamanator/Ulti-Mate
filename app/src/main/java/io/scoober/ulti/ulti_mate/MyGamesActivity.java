package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MyGamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_games);

        // Setup ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Create bundle for fragment with games to show
        Intent priorIntent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainMenuActivity.GAMES_TO_SHOW_EXTRA,
                priorIntent.getSerializableExtra(MainMenuActivity.GAMES_TO_SHOW_EXTRA));

        //TODO move data from bundle

        // Manage all fragments -> so we can add our bundle as arguments:
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MyGamesListFragment myGamesFrag = new MyGamesListFragment();
        myGamesFrag.setArguments(bundle);
        fragmentTransaction.add(R.id.myGamesContainer, myGamesFrag, "GAME_DISPLAY_FRAGMENT");
        fragmentTransaction.commit();
    }
}
