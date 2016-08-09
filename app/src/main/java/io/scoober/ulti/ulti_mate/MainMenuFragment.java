package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuFragment extends Fragment {

    public MainMenuFragment() {
    }

    private MainMenuFragmentListener fragListener;

    public interface MainMenuFragmentListener {
        void onSettingsSelected();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragListener = (MainMenuFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MainMenuFragmentListener");
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fragListener.onSettingsSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
