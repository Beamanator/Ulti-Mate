package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.os.Bundle;
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
    }
}
