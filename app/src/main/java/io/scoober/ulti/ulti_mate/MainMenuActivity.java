package io.scoober.ulti.ulti_mate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenuActivity extends AppCompatActivity {

    public static final String GAME_ID_EXTRA = "io.scoober.ulti.ulti_mate.Game_Id";
    public static final String TEMPLATE_ID_EXTRA = "io.scoober.ulti.ulti_mate.Template_Id";
    public static final String GAME_DISPLAY_ARG_EXTRA = "io.scoober.ulti.ulti_mate.Game_Display_To_Launch";
    public static final String GAME_SETUP_ARG_EXTRA = "io.scoober.ulti.ulti_mate.Game_Setup_To_Launch";

    public enum DisplayToLaunch {NEW, RESUME, VIEW, EDIT, UPDATE}
    public enum SetupToLaunch {CREATE_GAME, UPDATE_GAME,
        CREATE_TEMPLATE, EDIT_TEMPLATE}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
}
