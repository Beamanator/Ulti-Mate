package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class NewGameActivity extends AppCompatActivity {

    private Button noTemplateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        noTemplateButton = (Button) findViewById(R.id.button_no_template);
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
}
