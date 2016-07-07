package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    private Button createGameDisplay;
    private CoordinatorLayout cl;

    // Game ID passed in via the intent
    private long gameId;

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

        /*
        Get coordination for Snackbar function
         */
        cl = (CoordinatorLayout) findViewById(R.id.gameSetupCoordinatorLayout);

        /*
        From the intent, check to see if the game ID was passed in as a parameter. If so,
        update the game instead of creating a new one.
        TODO handle updating the game
         */
        Intent priorIntent = getIntent();
        Bundle bundle = new Bundle();
        gameId = priorIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA);
        bundle.putLong(MainMenuActivity.GAME_ID_EXTRA,gameId);
        Log.d(this.getClass().getName(),Long.toString(gameId));

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
                if (position == 1 && createGameDisplay.getVisibility() != View.VISIBLE) {
                    createGameDisplay.setVisibility(View.VISIBLE);
                }
            }
        });

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.smartTab);
        viewPagerTab.setViewPager(mViewPager);

        createGameDisplay= (Button) findViewById(R.id.createGameDisplay);
        createGameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeGame();
                launchGameDisplay();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameId > 0) {
            createGameDisplay.setText(R.string.button_update_game);
        }
    }

    public static Game getGameFromBundle(Bundle bundle, Context ctx) {
        long gameId = bundle.getLong(MainMenuActivity.GAME_ID_EXTRA);
        Game bundleGame = null;
        if (gameId > 0) {
            GameDbAdapter gameDbAdapter = new GameDbAdapter(ctx);
            gameDbAdapter.open();
            bundleGame = gameDbAdapter.getGame(gameId);
            gameDbAdapter.close();
        }

        return bundleGame;
    }

    private void storeGame() {
        Game game = createGameFromSetup();

        // store game to database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        if (gameId > 0) {
            gameDbAdapter.saveGame(game);
        } else {
            gameId = gameDbAdapter.createGame(game);
        }
        gameDbAdapter.close();

    }

    private void launchGameDisplay() {

        // Start Game Display Activity
        Intent intent = new Intent(getBaseContext(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                MainMenuActivity.DisplayToLaunch.NEW);
        startActivity(intent);
    }

    private Game createGameFromSetup() {

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
            // Get game from the database
            GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
            gameDbAdapter.open();
            game = gameDbAdapter.getGame(gameId);
            gameDbAdapter.close();

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
            game = new Game(gameName,winningScore,team1Name,team1Color,team2Name,team2Color,softCap,hardCap);
        }

        return game;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
