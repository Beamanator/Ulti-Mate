package io.scoober.ulti.ulti_mate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainMenuActivity extends AppCompatActivity
        implements MainMenuFragment.MainMenuFragmentListener{

    // Intent constants
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

    public static final int UPDATE_GAME_NOTIFICATION_ID = 1;

    public enum DisplayToLaunch {NEW, RESUME, VIEW, EDIT, UPDATE}
    public enum SetupToLaunch {CREATE_GAME, UPDATE_GAME,
        CREATE_TEMPLATE, EDIT_TEMPLATE}
    public enum GamesToShow {ACTIVE, ENDED}

    private static final String TAG_MAIN_MENU_FRAGMENT = "MAIN_MENU_FRAGMENT";
    private static final String TAG_SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add the main menu fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainActivityContent, new MainMenuFragment(), TAG_MAIN_MENU_FRAGMENT)
                .commit();
    }

    @Override
    public void onSettingsSelected() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivityContent, new SettingsFragment(), TAG_SETTINGS_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }
}
