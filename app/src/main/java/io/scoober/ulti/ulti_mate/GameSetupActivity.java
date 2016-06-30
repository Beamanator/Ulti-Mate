package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class GameSetupActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int NUM_PAGES = 2;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Button createGameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_setup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        createGameDisplay= (Button) findViewById(R.id.createGameDisplay);
        createGameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGameDisplay(view);
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.setupViewPager);
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

    private void launchGameDisplay(View view) {
        Game game = createGameFromSetup();

        // store game to database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
        gameDbAdapter.open();
        Game newGame = gameDbAdapter.createGame(game);
        gameDbAdapter.close();

        // Start Game Display Activity
        Intent intent = new Intent(view.getContext(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, newGame.getId());
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

        // Get game data from widgets
        String gameName = gameNameField.getText().toString();
        int winningScore = 0;
        if (!winningScoreField.getText().toString().isEmpty()) {
            winningScore = Integer.valueOf(winningScoreField.getText().toString());
        }
        boolean timeCaps = timeCapsBox.isChecked();
        long softCap = 0;
        long hardCap = 0;
        if (timeCaps) {
            // Currently stored as milliseconds without date
            softCap = Utils.getMilliFrom12HrString(softCapTimeButton.getText().toString());
            hardCap = Utils.getMilliFrom12HrString(hardCapTimeButton.getText().toString());
        }

        // Get team data from widgets
        String team1Name = team1NameField.getText().toString();
        String team2Name = team2NameField.getText().toString();

        // TODO think of a better way to get the fragment or store this data
        GameSetupTeamFragment teamSetupFrag = (GameSetupTeamFragment) mSectionsPagerAdapter.getItem(1);
        int team1Color = teamSetupFrag.getTeamColor(1);
        int team2Color = teamSetupFrag.getTeamColor(2);

        Game newGame = new Game(gameName,winningScore,team1Name,team1Color,team2Name,team2Color,softCap,hardCap);
        return newGame;
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private GameSetupDetailFragment detailFrag;
        private GameSetupTeamFragment teamFrag;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    if (detailFrag == null) {
                        detailFrag = new GameSetupDetailFragment();
                    }
                    return detailFrag;
                case 1:
                    if (teamFrag == null) {
                        teamFrag = new GameSetupTeamFragment();
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
}
