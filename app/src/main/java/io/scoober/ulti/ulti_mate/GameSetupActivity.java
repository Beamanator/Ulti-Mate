package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameSetupActivity extends AppCompatActivity
        implements GameSetupFragment.OnCardClickedListener,
        GameSetupDetailFragment.onCompleteDetailListener,
        GameSetupTeamFragment.onCompleteTeamListener {

    public enum Setup {GAME_SETUP, TEAM_SETUP, OVERVIEW_SETUP}

    private Setup currentSetupFrag;
    private Game game;

    ActionBar actionBar;
    Menu actionMenu;

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
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get data from the intent and bundle it for the fragments to use
        getIntentData();
        game = getGameFromIntent();

        // Create fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Pass parameters to GameSetupFragment
        GameSetupFragment setupFrag = new GameSetupFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA, setupToLaunch);
        setupFrag.setArguments(bundle);
        setupFrag.setGame(game);

        // Add the fragment and commit changes
        fragmentTransaction.add(R.id.setupContainer, setupFrag, "GAME_SETUP_FRAGMENT");
        fragmentTransaction.commit();

        currentSetupFrag = Setup.OVERVIEW_SETUP;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_setup, menu);
        actionMenu = menu;

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
                showTemplateNameDialog();
                return true;
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
                    (GameSetupDetailFragment) fm.findFragmentByTag("GAME_DETAIL_SETUP_FRAGMENT");
            if (detailFragment == null) {
                detailFragment = new GameSetupDetailFragment();
                detailFragment.setGame(game);
            }
            ft.replace(R.id.setupContainer, detailFragment, "GAME_DETAIL_SETUP_FRAGMENT");
        }

        // Go to team setup
        if (fragmentToLaunch == Setup.TEAM_SETUP) {
            GameSetupTeamFragment teamFragment =
                    (GameSetupTeamFragment) fm.findFragmentByTag("GAME_TEAM_SETUP_FRAGMENT");
            if (teamFragment == null) {
                teamFragment = new GameSetupTeamFragment();
                teamFragment.setGame(game);
            }
            ft.replace(R.id.setupContainer, teamFragment, "GAME_TEAM_SETUP_FRAGMENT");
        }


        ft.addToBackStack(null);
        ft.commit();

        currentSetupFrag = fragmentToLaunch;

        // Hide menu options for creating from template
        actionMenu.findItem(R.id.action_template_create).setVisible(false);
    }

    @Override
    public void onDetailComplete() {
        returnToOverview();
        onReturnToOverview();
    }

    @Override
    public void onTeamComplete() {
        returnToOverview();
        onReturnToOverview();
    }

    @Override
    public void onBackPressed() {
        if (currentSetupFrag != Setup.OVERVIEW_SETUP) {
            onReturnToOverview();
        }
        super.onBackPressed();
    }

    private void returnToOverview() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        GameSetupFragment setupFragment =
                (GameSetupFragment) fm.findFragmentByTag("GAME_SETUP_FRAGMENT");
        ft.replace(R.id.setupContainer, setupFragment, "GAME_SETUP_FRAGMENT");
        ft.commit();
    }

    /**
     * Performs actions that occur after returning to the overview from another setup page
     */
    private void onReturnToOverview() {
        currentSetupFrag = Setup.OVERVIEW_SETUP;
        actionMenu.findItem(R.id.action_template_create).setVisible(true);
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
            } else {
                game = new Game(template.getGameName(), template.getWinningScore(), template.getTeam1Name(),
                        template.getTeam1Color(), template.getTeam2Name(), template.getTeam2Color(),
                        template.getSoftCapTime(), template.getHardCapTime());
            }

        } else {
            game = new Game(
                    res.getString(R.string.default_game_name),
                    0,
                    res.getString(R.string.default_team_1_name),
                    res.getColor(R.color.default_team_1),
                    res.getString(R.string.default_team_2_name),
                    res.getColor(R.color.default_team_2),
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
            gameId = priorIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA);
        }

        templateId = 0;
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_GAME ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
            templateId = priorIntent.getExtras().getLong(MainMenuActivity.TEMPLATE_ID_EXTRA);
        }
    }

    /**
     * Shows a dialog that allows the user to create a template with a given name
     */
    private void showTemplateNameDialog() {

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.templateNameEdit);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_name_template)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String templateName = nameEdit.getText().toString();
                        createTemplate(templateName);
                        showSaveTemplateSnackbar(templateName);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();

        dialog.show();

        /*
        Add listener to the editText and disable positive button if validation fails
         */
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        nameEdit.addTextChangedListener(new TextValidator(nameEdit) {
            @Override
            public void validate(TextView textView, String text) {
                boolean valid = Utils.validateTextNotEmpty(text, textView,
                        getResources(), R.string.dialog_name_template);
                positiveButton.setEnabled(valid);
            }
        });
    }

    private void showSaveTemplateSnackbar(String templateName) {
        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.gameSetupCoordinatorLayout);
        String message = getResources().getString(R.string.snackbar_template_save_successful, templateName);
        Snackbar.make(cl,message,Snackbar.LENGTH_LONG).show();
    }

    private void createTemplate(String templateName) {
        FragmentManager fm = getSupportFragmentManager();
        GameSetupFragment setupFragment =
                (GameSetupFragment) fm.findFragmentByTag("GAME_SETUP_FRAGMENT");
        setupFragment.createTemplate(templateName);
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
