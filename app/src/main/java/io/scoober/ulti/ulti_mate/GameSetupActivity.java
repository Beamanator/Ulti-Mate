package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.setupViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.smartTab);
        //viewPagerTab.setViewPager(mViewPager);

        Button createGameDisplay= (Button) findViewById(R.id.createGameDisplay);
        createGameDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = getGameFromSetup();

                // store game to database
                GameDbAdapter gameDbAdapter = new GameDbAdapter(getBaseContext());
                gameDbAdapter.open();
                Game newGame = gameDbAdapter.createGame(game);
                gameDbAdapter.close();

                // Start Game Display Activity
                Intent intent = new Intent(v.getContext(),GameDisplayActivity.class);
                startActivity(intent);
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

    private Game getGameFromSetup() {

        // Get required widgets
        EditText gameNameField = (EditText) findViewById(R.id.gameTitleEditor);
        EditText team1NameField = (EditText) findViewById(R.id.team1Name);
        EditText team2NameField = (EditText) findViewById(R.id.team2Name);
        EditText winningScoreField = (EditText) findViewById(R.id.winningScore);
        CheckBox timeCapsBox = (CheckBox) findViewById(R.id.timeCapsCheckbox);
        Button softCapTimeButton = (Button) findViewById(R.id.softCapInput);
        Button hardCapTimeButton = (Button) findViewById(R.id.hardCapInput);

        // Get data from widgets
        String gameName = gameNameField.getText().toString();
        String team1Name = team1NameField.getText().toString();
        String team2Name = team2NameField.getText().toString();
        int winningScore = Integer.valueOf(winningScoreField.getText().toString());
        boolean timeCaps = timeCapsBox.isChecked();
        long softCap = 0;
        long hardCap = 0;
        if (timeCaps) {
            // Currently stored as milliseconds without date
            softCap = getMilliFrom12HrString(softCapTimeButton.getText().toString());
            hardCap = getMilliFrom12HrString(hardCapTimeButton.getText().toString());
        }

        Game newGame = new Game(gameName,winningScore,team1Name,null,team2Name,null,softCap,hardCap);
        return newGame;
    }

    private long getMilliFrom12HrString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date tempDate = sdf.parse(dateString);
            return tempDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new GameDetailSetupFragment();
                case 1:
                    return new GameDetailFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
