package io.scoober.ulti.ulti_mate;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuActivityFragment extends Fragment {

    public MainMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainMenuLayout = inflater.inflate(R.layout.fragment_main_menu, container, false);
        Button resume = (Button) mainMenuLayout.findViewById(R.id.resumeButton);
        Button newGameButton = (Button) mainMenuLayout.findViewById(R.id.new_game_button);
        Button pastGamesButton = (Button) mainMenuLayout.findViewById(R.id.past_games_button);

        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        ArrayList<Game> activeGames = gameDbAdapter.getActiveGames(2,0);
        ArrayList<Game> endedGames = gameDbAdapter.getEndedGames(1,0);

        //TODO improve the below with selectors
        final int numActive = activeGames.size();
        long gameId = 0;
        if (numActive == 1) {
            gameId = activeGames.get(0).getId();
        }
        final long lastGameId = gameId;
        int numEnded = endedGames.size();
        gameDbAdapter.close();

        // Activate Buttons
        if (numActive > 0) {
            resume.setEnabled(true);
        }

        if (numEnded > 0) {
            pastGamesButton.setEnabled(true);
        }

        /*
        Resume Game
         */
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numActive == 1) {
                    Intent intent = new Intent(getActivity(), GameDisplayActivity.class);
                    intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, lastGameId);
                    intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                            MainMenuActivity.DisplayToLaunch.RESUME);
                    startActivity(intent);
                } else if (numActive > 1) {
                    Intent intent = new Intent(getActivity(), MyGamesActivity.class);
                    intent.putExtra(MainMenuActivity.GAMES_TO_SHOW_EXTRA,
                            MainMenuActivity.GamesToShow.ACTIVE);
                    startActivity(intent);
                }
            }
        });

        /*
        New Game
         */
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NewGameActivity.class);
                startActivity(intent);
            }
        });

        /*
        My Games
         */
        pastGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyGamesActivity.class);
                intent.putExtra(MainMenuActivity.GAMES_TO_SHOW_EXTRA,
                        MainMenuActivity.GamesToShow.ENDED);
                startActivity(intent);
            }
        });

        return mainMenuLayout;
    }

    /**
     * stores latest game Id in Database to variable lastGameId
     */
    private long getLastGameId() {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        long id = gameDbAdapter.getRecentGameId();
        gameDbAdapter.close();

        return id;
    }
}
