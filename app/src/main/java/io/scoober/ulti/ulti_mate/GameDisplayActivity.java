package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class GameDisplayActivity extends AppCompatActivity {

    private MainMenuActivity.DisplayToLaunch displayToLaunch;
    private long gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);

        // Setting our app bar (toolbar) as the support action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get data from intent
        Intent oldIntent = getIntent();
//        long id = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                oldIntent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        gameId = oldIntent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA,0);

        // Manage all fragments -> so we can add our display / edit display fragments:
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // handle different cases for where Activity is called from
        switch (displayToLaunch) {
            case NEW:
                GameDisplayFragment gameFrag1 = new GameDisplayFragment();
                fragmentTransaction.add(R.id.game_container, gameFrag1, "GAME_DISPLAY_FRAGMENT");
                break;
            case RESUME:
                GameDisplayFragment gameFrag2 = new GameDisplayFragment();
                fragmentTransaction.add(R.id.game_container, gameFrag2, "GAME_DISPLAY_FRAGMENT");
                break;
            case EDIT:
                GameDisplayEditFragment gameEditFrag1 = new GameDisplayEditFragment();
                fragmentTransaction.add(R.id.game_container, gameEditFrag1, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
            case VIEW:
                GameDisplayEditFragment gameEditFrag2 = new GameDisplayEditFragment();
                fragmentTransaction.add(R.id.game_container, gameEditFrag2, "GAME_DISPLAY_EDIT_FRAGMENT");
                break;
        }

        // commit changes so everything works.
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_share:
                return true;
            case R.id.action_game_settings:
                Intent intent = new Intent(getBaseContext(), GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
                startActivity(intent);
                return true;
            case R.id.action_edit_field_setup:
                View gameDisplayView = findViewById(R.id.game_container);
                Game game = Utils.getGameDetails(gameDisplayView.getContext(), gameId);
                buildFieldSetupDialogs(game, gameDisplayView);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void buildFieldSetupDialogs(final Game g, final View v) {
        AlertDialog pullDialog;

        final String t1 = g.getTeam1Name();
        final String t2 = g.getTeam2Name();

        // items to display in dialog boxes:
        final CharSequence[] items1 = {t1, t2};

        // First dialog box gets initial pulling team:
        AlertDialog.Builder dialogBox1 = new AlertDialog.Builder(v.getContext());
        dialogBox1.setTitle("Which Team Pulls First?");
        dialogBox1.setSingleChoiceItems(items1, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        g.setInitPullingTeam(t1);
                        break;
                    case 1:
                        g.setInitPullingTeam(t2);
                        break;
                }
                dialog.dismiss();
                buildTeamOrientationDialog(g, t1, t2, v);
            }
        });

        pullDialog = dialogBox1.create();
        pullDialog.show();
    }

    private static void buildTeamOrientationDialog(final Game g, final String t1, final String t2,
                                                   final View v) {

        AlertDialog orientationDialog;

        // Strings to show in Dialog with Radio Buttons:
        final CharSequence[] items = {"Left", "Right"};

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(v.getContext());

        dialogBox.setTitle("Which Side is " + t1 + " on?");

        // 2nd param = automatically checked item
        dialogBox.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        g.setInitTeamLeft(t1);
                        break;
                    case 1:
                        g.setInitTeamLeft(t2);
                        break;
                }
                dialog.dismiss();

                // At this point, both dialog boxes must have been hit & populated Game.
                Utils.saveGameDetails(v.getContext(), g);

                showFieldLayout(v);
            }
        });

        orientationDialog = dialogBox.create();
        orientationDialog.show();
    }

    public static void showFieldLayout(View v) {
        Button setupFieldButton = (Button) v.findViewById(R.id.fieldSetup);
        LinearLayout gameImagesLayout = (LinearLayout) v.findViewById(R.id.gameImagesLayout);

        setupFieldButton.setVisibility(View.GONE);
        gameImagesLayout.setVisibility(View.VISIBLE);
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
