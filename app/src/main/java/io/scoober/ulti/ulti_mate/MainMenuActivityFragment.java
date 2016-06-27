package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuActivityFragment extends Fragment {

    long lastGameId;

    public MainMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainMenuLayout = inflater.inflate(R.layout.fragment_main_menu, container, false);
        Button resume = (Button) mainMenuLayout.findViewById(R.id.resumeButton);
        Button newGameButton = (Button) mainMenuLayout.findViewById(R.id.new_game_button);
        Button myGamesButton = (Button) mainMenuLayout.findViewById(R.id.my_games_button);

        getLastGameId(); // get last game ID to activate buttons
        if (lastGameId != -1) {
            resume.setEnabled(true);
            myGamesButton.setEnabled(true);
        }

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GameDisplayActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, lastGameId);
                intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                        MainMenuActivity.DisplayToLaunch.RESUME);
                startActivity(intent);
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GameSetupActivity.class);
                startActivity(intent);
            }
        });

        myGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyGamesActivity.class);
                startActivity(intent);
            }
        });

        return mainMenuLayout;
    }

    private void getLastGameId() {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        lastGameId= gameDbAdapter.getRecentGameId();
        gameDbAdapter.close();
    }
}
