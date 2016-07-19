package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.Serializable;

public class GameSetupActivity extends AppCompatActivity
        implements GameSetupFragment.OnCardClickedListener {

    public static String GAME_EXTRA = "Game_Extra";
    public enum Card {GAME_SETUP, TEAM_SETUP};
    // Intent information
    MainMenuActivity.SetupToLaunch setupToLaunch;
    private long gameId;
    private long templateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        /*
        Setup the toolbar and define an up button
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from the intent and bundle it for the fragments to use
        getIntentData();

        // Create fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Pass parameters to GameSetupFragment
        GameSetupFragment setupFrag = new GameSetupFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA, setupToLaunch);
        setupFrag.setArguments(bundle);
        setupFrag.setGame(getGameFromIntent(getBaseContext()));

        // Add the fragment and commit changes
        fragmentTransaction.add(R.id.setupContainer, setupFrag, "GAME_SETUP_FRAGMENT");
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_setup, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_template_create:
                //TODO action for template creation
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCardClicked(Game game, Card card) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Go to game setup
        if (card == Card.GAME_SETUP) {
            GameSetupDetailFragment detailFragment =
                    (GameSetupDetailFragment) fm.findFragmentByTag("GAME_DETAIL_SETUP_FRAGMENT");
            if (detailFragment == null) {
                detailFragment = new GameSetupDetailFragment();
            }
            detailFragment.setGame(game);
            ft.replace(R.id.setupContainer, detailFragment, "GAME_DETAIL_SETUP_FRAGMENT");
        }

        // Go to team setup
        if (card == Card.TEAM_SETUP) {
            GameSetupTeamFragment teamFragment =
                    (GameSetupTeamFragment) fm.findFragmentByTag("GAME_TEAM_SETUP_FRAGMENT");
            if (teamFragment == null) {
                teamFragment = new GameSetupTeamFragment();
            }
            teamFragment.setGame(game);
            ft.replace(R.id.setupContainer, teamFragment, "GAME_DETAIL_SETUP_FRAGMENT");
        }


        ft.addToBackStack(null);
        ft.commit();

    }

    /**
     * Returns a game from the intent passed to the activity
     */
    private Game getGameFromIntent(Context ctx) {
        Game game = null;
        if (gameId > 0) {
            game = Utils.getGameDetails(ctx, gameId);
        } else if (templateId > 0) {
            game = Utils.getGameDetails(ctx, templateId);
        }

        return game;
    }

    /**
     * Get data from the intent, specifically
     *      Which game setup to launch (GAME_SETUP_ARG_EXTRA)
     *      Game ID (GAME_ID_EXTRA)
     *      Template ID (TEMPLATE_ID_EXTRA)
     */
    private void getIntentData() {
        Intent priorIntent = getIntent();
        setupToLaunch = (MainMenuActivity.SetupToLaunch)
                priorIntent.getSerializableExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA);

        gameId = 0;
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.UPDATE_GAME) {
            gameId = priorIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA);
        }

        templateId = 0;
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_GAME ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
            templateId = priorIntent.getExtras().getLong(MainMenuActivity.TEMPLATE_ID_EXTRA);
        }
    }

    /**
     * Function found SO here:
     * http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
