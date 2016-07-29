package io.scoober.ulti.ulti_mate;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.GregorianCalendar;

public class MainMenuActivity extends AppCompatActivity {

    public static final String PACKAGE_NAME = "io.scoober.ulti.ulti_mate";
    public static final String GAME_ID_EXTRA = PACKAGE_NAME + ".Game_Id";
    public static final String TEMPLATE_ID_EXTRA = PACKAGE_NAME + ".Template_Id";
    public static final String GAME_DISPLAY_ARG_EXTRA = PACKAGE_NAME + ".Game_Display_To_Launch";
    public static final String GAME_SETUP_ARG_EXTRA = PACKAGE_NAME + ".Game_Setup_To_Launch";
    public static final String GAME_SETUP_FIELD_ONLY_EXTRA = PACKAGE_NAME + ".Game_Setup_Field_Only";
    public static final String GAMES_TO_SHOW_EXTRA = PACKAGE_NAME + ".Games_To_Show";

    // Extras for AlertReceiver Notification
    public static final String NOTIFICATION_MESSAGE = PACKAGE_NAME + ".Notification_Message";
    public static final String NOTIFICATION_MESSAGE_TEXT = PACKAGE_NAME + ".Notification_Message_Text";
    public static final String NOTIFICATION_MESSAGE_ALERT = PACKAGE_NAME + ".Notification_Message_Alert";
    public static final String NOTIFICATION_NEXT_CLASS = PACKAGE_NAME + ".Notification_Next_Class";
    public static final String NOTIFICATION_ID = PACKAGE_NAME + ".Notification_ID";

    public static final int PERSISTENT_GAME_NOTIFICATION_ID = 1;

    public enum DisplayToLaunch {NEW, RESUME, VIEW, EDIT, UPDATE}
    public enum SetupToLaunch {CREATE_GAME, UPDATE_GAME,
        CREATE_TEMPLATE, EDIT_TEMPLATE}
    public enum GamesToShow {ACTIVE, ENDED}

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
