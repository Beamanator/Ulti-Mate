package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class GameSetupActivity extends AppCompatActivity
        implements GameSetupOverviewFragment.OnFragActionListener,
        GameSetupDetailFragment.onCompleteDetailListener,
        GameSetupTeamFragment.onCompleteTeamListener,
        GameSetupFieldFragment.onCompleteFieldListener {

    private static final String TAG_GAME_SETUP_OVERVIEW_FRAGMENT = "GAME_OVERVIEW_SETUP_FRAGMENT";
    private static final String TAG_GAME_DETAIL_SETUP_FRAGMENT = "GAME_DETAIL_SETUP_FRAGMENT";
    private static final String TAG_GAME_TEAM_SETUP_FRAGMENT = "GAME_TEAM_SETUP_FRAGMENT";
    private static final String TAG_GAME_FIELD_SETUP_FRAGMENT = "GAME_FIELD_SETUP_FRAGMENT";

    public enum Setup {GAME_SETUP, TEAM_SETUP, FIELD_SETUP, OVERVIEW_SETUP}

    private Setup currentSetupFrag;
    private boolean launchFieldSetupOnly;
    private Game game;

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get data from the intent and bundle it for the fragments to use
        getIntentData();
        game = getGameFromIntent();

        // Create fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        // Pass parameters to GameSetupOverviewFragment
        GameSetupOverviewFragment setupFrag = new GameSetupOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA, setupToLaunch);
        setupFrag.setArguments(bundle);
        setupFrag.setGame(game);

        // Add the fragment and commit changes
        fragmentTransaction.add(R.id.setupContainer, setupFrag, TAG_GAME_SETUP_OVERVIEW_FRAGMENT);
        fragmentTransaction.commit();

        currentSetupFrag = Setup.OVERVIEW_SETUP;

        if(launchFieldSetupOnly) {
            onCardClicked(Setup.FIELD_SETUP);
            currentSetupFrag = Setup.FIELD_SETUP;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if(currentSetupFrag == Setup.OVERVIEW_SETUP) {
                    return false;
                }
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCardClicked(Setup fragmentToLaunch) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Go to game setup
        if (fragmentToLaunch == Setup.GAME_SETUP) {
            GameSetupDetailFragment detailFragment =
                    (GameSetupDetailFragment) fm.findFragmentByTag(TAG_GAME_DETAIL_SETUP_FRAGMENT);
            if (detailFragment == null) {
                detailFragment = new GameSetupDetailFragment();
                detailFragment.setGame(game);
            }
            ft.replace(R.id.setupContainer, detailFragment, TAG_GAME_DETAIL_SETUP_FRAGMENT);
        }

        // Go to team setup
        if (fragmentToLaunch == Setup.TEAM_SETUP) {
            GameSetupTeamFragment teamFragment =
                    (GameSetupTeamFragment) fm.findFragmentByTag(TAG_GAME_TEAM_SETUP_FRAGMENT);
            if (teamFragment == null) {
                teamFragment = new GameSetupTeamFragment();
                teamFragment.setGame(game);
            }
            ft.replace(R.id.setupContainer, teamFragment, TAG_GAME_TEAM_SETUP_FRAGMENT);
        }

        // Go to field setup
        if (fragmentToLaunch == Setup.FIELD_SETUP) {
            GameSetupFieldFragment fieldFragment =
                    (GameSetupFieldFragment) fm.findFragmentByTag(TAG_GAME_FIELD_SETUP_FRAGMENT);
            if (fieldFragment == null) {
                fieldFragment = new GameSetupFieldFragment();
                fieldFragment.setGame(game);
            }
            ft.replace(R.id.setupContainer, fieldFragment, TAG_GAME_FIELD_SETUP_FRAGMENT);
        }


        ft.addToBackStack(null);
        ft.commit();

        currentSetupFrag = fragmentToLaunch;
    }

    @Override
    public void onTemplateSaved(String templateName) {
        String message = getResources().getString(R.string.snackbar_template_save_successful, templateName);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetailComplete() {
        returnToOverview();
    }

    @Override
    public void onTeamComplete() {
        returnToOverview();
    }

    @Override
    public void onFieldComplete() {
        //TODO
        if (launchFieldSetupOnly) {
            Utils.saveGameDetails(this, game);
            finish();
        } else {
            returnToOverview();
        }
    }

    @Override
    public void onBackPressed() {
        if (launchFieldSetupOnly) {
            finish();
            return;
        }

        if (currentSetupFrag != Setup.OVERVIEW_SETUP) {
            onReturnToOverview();
        }

        super.onBackPressed();
    }

    private void returnToOverview() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        GameSetupOverviewFragment setupFragment =
                (GameSetupOverviewFragment) fm.findFragmentByTag(TAG_GAME_SETUP_OVERVIEW_FRAGMENT);
        ft.replace(R.id.setupContainer, setupFragment, TAG_GAME_SETUP_OVERVIEW_FRAGMENT);
        ft.commit();
        onReturnToOverview();
    }

    /**
     * Performs actions that occur after returning to the overview from another setup page
     */
    private void onReturnToOverview() {
        currentSetupFrag = Setup.OVERVIEW_SETUP;
    }

    /**
     * Returns a game from the intent passed to the activity
     *  If a Game ID is passed in, use the game that it's referring to
     *  If a template Id is passed in and we're not editing a template, copy the template to a new game
     *  If we are editing the template, use the template as the game
     *  If neither are passed in, create a default game
     * @return      game object for use by setup
     */
    private Game getGameFromIntent() {
        Game game;
        Resources res = getResources();
        if (gameId > 0) {
            game = Utils.getGameDetails(getBaseContext(), gameId);
        } else if (templateId > 0) {
            Game template = Utils.getGameDetails(getBaseContext(), templateId);
            if (setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
                game = template;
                game.convertCapTimes();
            } else {
                game = new Game(template.getGameName(), template.getWinningScore(),
                        template.getTeam(1), template.getTeam(2),
                        template.getSoftCapTime(), template.getHardCapTime());
            }

        } else {

            // Defaults
            Team firstTeam = new Team(res.getString(R.string.default_team_1_name),
                    res.getColor(R.color.default_team_1));
            Team secondTeam = new Team(res.getString(R.string.default_team_2_name),
                    res.getColor(R.color.default_team_2));
            game = new Game(
                    res.getString(R.string.default_game_name),
                    0,
                    firstTeam,
                    secondTeam,
                    0,
                    0
            );
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
            gameId = priorIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        }

        templateId = 0;
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_GAME ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
            templateId = priorIntent.getExtras().getLong(MainMenuActivity.TEMPLATE_ID_EXTRA, 0);
        }

        launchFieldSetupOnly = priorIntent.getBooleanExtra(MainMenuActivity.GAME_SETUP_FIELD_ONLY_EXTRA, false);
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
