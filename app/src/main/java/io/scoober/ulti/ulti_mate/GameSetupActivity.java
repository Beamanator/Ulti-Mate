package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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

public class GameSetupActivity extends AppCompatActivity {

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
        Bundle bundle = new Bundle();
        bundle.putLong(MainMenuActivity.GAME_ID_EXTRA, gameId);
        bundle.putLong(MainMenuActivity.TEMPLATE_ID_EXTRA, templateId);
        bundle.putSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA, setupToLaunch);

        // Create fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GameSetupFragment setupFrag = new GameSetupFragment();
        setupFrag.setArguments(bundle);
        fragmentTransaction.add(R.id.setupContainer, setupFrag, "GAME_DISPLAY_FRAGMENT");
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

    /**
     * Returns a game from a bundle passed to the fragment
     * @param bundle    fragment bundle
     * @param ctx       context of the fragment
     * @return          Game object with requested ID.
     */
    public static Game getGameFromBundle(Bundle bundle, Context ctx) {
        long gameId = bundle.getLong(MainMenuActivity.GAME_ID_EXTRA);
        if (gameId == 0) {
            gameId = bundle.getLong(MainMenuActivity.TEMPLATE_ID_EXTRA);
        }

        Game bundleGame = null;
        if (gameId > 0) {
            bundleGame = Utils.getGameDetails(ctx, gameId);
        }

        return bundleGame;
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
