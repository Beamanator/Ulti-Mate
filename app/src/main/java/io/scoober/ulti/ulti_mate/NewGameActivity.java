package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class NewGameActivity extends AppCompatActivity {

    private static String TEMPLATE_READ_PREF_KEY = "template_read";

    private Button noTemplateButton;
    private Button instructionsReadButton;
    private CardView templateInstructionsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        getWidgetReferences();
        setupInstructions();
        noTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, MainMenuActivity.SetupToLaunch.CREATE_GAME);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_new_game, menu);
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
            case R.id.action_create_new_template:
                Intent intent = new Intent(getBaseContext(),GameSetupActivity.class);
                intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, MainMenuActivity.SetupToLaunch.CREATE_TEMPLATE);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getWidgetReferences() {
        instructionsReadButton = (Button) findViewById(R.id.confirmRead);
        noTemplateButton = (Button) findViewById(R.id.button_no_template);

        templateInstructionsCard = (CardView) findViewById(R.id.templateInstructionsCard);
    }

    private void setupInstructions() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        boolean instructionsRead = preferences.getBoolean(TEMPLATE_READ_PREF_KEY, false);
        if (instructionsRead) {
            templateInstructionsCard.setVisibility(View.GONE);
        }

        instructionsReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(TEMPLATE_READ_PREF_KEY, true);
                editor.apply();

                templateInstructionsCard.setVisibility(View.GONE);
            }
        });
    }
}
