package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.CheckBox;
import android.widget.EditText;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;

public class GameSetupActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;

    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    private Button createFromSetupButton;

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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), bundle);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.setupViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1 && createFromSetupButton.getVisibility() != View.VISIBLE) {
                    createFromSetupButton.setVisibility(View.VISIBLE);
                }
            }
        });

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.smartTab);
        viewPagerTab.setViewPager(mViewPager);

        createFromSetupButton = (Button) findViewById(R.id.createFromSetupButton);
        createFromSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game;
                switch (setupToLaunch) {
                    case CREATE_GAME:
                        game = createGameFromSetup(0);
                        gameId = storeGame(game); // set gameId in case the user presses back
                        launchGameDisplay();
                        break;
                    case UPDATE_GAME:
                        game = createGameFromSetup(gameId);
                        storeGame(game);
                        launchGameDisplay();
                        break;
                    case CREATE_TEMPLATE:
                        createTemplate(true);
                        break;
                    case EDIT_TEMPLATE:
                        editTemplate(true);
                        break;
                }
            }
        });


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
            case R.id.action_template_save:
                if (templateId > 0) {
                    editTemplate(false);
                } else {
                    createTemplate(false);
                }
                return true;
            case R.id.action_template_save_as:
                createTemplate(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        Change the setupToLaunch parameter depending on whether or not the game/template IDs
        are populated, likely indicating that the user pressed the back button
         */
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_GAME &&
                gameId > 0) {
            setupToLaunch = MainMenuActivity.SetupToLaunch.UPDATE_GAME;
        }

        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_TEMPLATE &&
                templateId > 0) {
            setupToLaunch = MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE;
        }

        setCreateButtonText();

        // Make the button visible if we're updating a game or editing a template
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.UPDATE_GAME) {
            if (createFromSetupButton.getVisibility() != View.VISIBLE) {
                createFromSetupButton.setVisibility(View.VISIBLE);
            }
        }
    }

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
     * This function sets the text for the createFromSetupButton, depending on:
     *      1. The setupToLaunch enum that was passed in from the intent
     *      2. Whether the gameId or templateId is populated from database creation
     */
    private void setCreateButtonText() {
        switch (setupToLaunch) {
            case CREATE_GAME:
                createFromSetupButton.setText(R.string.button_create_game);
                break;
            case UPDATE_GAME:
                createFromSetupButton.setText(R.string.button_update_game);
                break;
            case CREATE_TEMPLATE:
                createFromSetupButton.setText(R.string.button_create_template);
                break;
            case EDIT_TEMPLATE:
                createFromSetupButton.setText(R.string.button_update_template);
                break;
        }
    }

    private long storeGame(Game game) {
        // store game to database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        long newId;
        if (game.getId() > 0) {
            newId = gameDbAdapter.saveGame(game);
        } else {
            newId = gameDbAdapter.createGame(game);
        }
        gameDbAdapter.close();

        return newId;

    }

    private void launchGameDisplay() {

        if (gameId > 0) {
            displayToLaunch = MainMenuActivity.DisplayToLaunch.UPDATE;
        } else {
            displayToLaunch = MainMenuActivity.DisplayToLaunch.NEW;
        }

        // Start Game Display Activity
        Intent intent = new Intent(getBaseContext(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, displayToLaunch);
        startActivity(intent);
    }

    private void launchNewGameActivity() {
        Intent intent = new Intent(getBaseContext(), NewGameActivity.class);
        startActivity(intent);
    }

    /**
     * Creates templates from user populated fields in the game setup activity
     * @param launchActivity    boolean to determine whether the NewGameActivity should be launched
     */
    private void createTemplate(boolean launchActivity) {
        showTemplateNameDialog(launchActivity);
    }

    /**
     * Edits templates from user populated fields in the game setup activity
     * @param launchActivity    boolean to determine whether the NewGameActivity should be launched
     */
    private void editTemplate(boolean launchActivity) {
        Game template = createGameFromSetup(templateId); // returns a template game
        storeGame(template); // set templateId in case the user presses back
        if (launchActivity) {
            launchNewGameActivity();
        } else {
            showSaveTemplateSnackbar(template.getTemplateName());
        }
    }

    private Game createGameFromSetup(long gameId) {

        // Get required widgets
        EditText gameNameField = (EditText) findViewById(R.id.gameTitleEditor);
        EditText team1NameField = (EditText) findViewById(R.id.team1Name);
        EditText team2NameField = (EditText) findViewById(R.id.team2Name);
        EditText winningScoreField = (EditText) findViewById(R.id.winningScore);
        CheckBox timeCapsBox = (CheckBox) findViewById(R.id.timeCapsCheckbox);
        Button softCapTimeButton = (Button) findViewById(R.id.softCapInput);
        Button hardCapTimeButton = (Button) findViewById(R.id.hardCapInput);
        TeamImageButton team1ImageButton = (TeamImageButton) findViewById(R.id.team1ImageButton);
        TeamImageButton team2ImageButton = (TeamImageButton) findViewById(R.id.team2ImageButton);

        // Get game data from widgets
        String gameName = gameNameField.getText().toString();
        int winningScore = 0;
        if (!winningScoreField.getText().toString().isEmpty()) {
            winningScore = Integer.valueOf(winningScoreField.getText().toString());
        }

        long softCap = 0;
        long hardCap = 0;
        boolean timeCaps = timeCapsBox.isChecked();
        if (timeCaps) {
            // Currently stored as milliseconds with today's date
            softCap = Utils.getTodayMilliFrom12HrString(softCapTimeButton.getText().toString());
            hardCap = Utils.getTodayMilliFrom12HrString(hardCapTimeButton.getText().toString());

            // New plan: Use current date [or next day] for date, plus milliseconds
        }

        // Get team data from widgets
        String team1Name = team1NameField.getText().toString();
        String team2Name = team2NameField.getText().toString();

        int team1Color = team1ImageButton.getColor();
        int team2Color = team2ImageButton.getColor();

        /*
        If we already have a game ID, then a game should already exist. We should not create a new
        game if the game ID already exists.
         */
        Game game;
        if (gameId > 0) {
            game = Utils.getGameDetails(getBaseContext(), gameId);
            // Set new items in the game
            game.setGameName(gameName);
            game.setWinningScore(winningScore);
            game.setTeam1Name(team1Name);
            game.setTeam1Color(team1Color);
            game.setTeam2Name(team2Name);
            game.setTeam2Color(team2Color);
            game.setSoftCapTime(softCap);
            game.setHardCapTime(hardCap);
        } else {
            game = new Game(gameName,winningScore,team1Name,team1Color,team2Name,team2Color,
                    softCap,hardCap);
        }

        return game;
    }

    private void showTemplateNameDialog(final boolean launchActivity) {

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.templateNameEdit);

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_name_template)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Game game = createGameFromSetup(0);
                        String templateName = nameEdit.getText().toString();
                        game.convertToTemplate(templateName);
                        templateId = storeGame(game);
                        if (launchActivity) {
                            launchNewGameActivity();
                        } else {
                            showSaveTemplateSnackbar(templateName);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show();
    }

    private void showSaveTemplateSnackbar(String templateName) {
        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.gameSetupCoordinatorLayout);
        String message = getResources().getString(R.string.snackbar_template_save_successful, templateName);
        Snackbar.make(cl,message,Snackbar.LENGTH_LONG).show();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private GameSetupDetailFragment detailFrag;
        private GameSetupTeamFragment teamFrag;

        private Bundle fragmentData;

        public SectionsPagerAdapter(FragmentManager fm, Bundle fragmentData) {
            super(fm);
            this.fragmentData = fragmentData;
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    if (detailFrag == null) {
                        detailFrag = new GameSetupDetailFragment();
                        detailFrag.setArguments(fragmentData);
                    }
                    return detailFrag;
                case 1:
                    if (teamFrag == null) {
                        teamFrag = new GameSetupTeamFragment();
                        teamFrag.setArguments(fragmentData);
                    }
                    return teamFrag;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return NUM_PAGES;
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
