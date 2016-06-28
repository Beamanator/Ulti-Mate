package io.scoober.ulti.ulti_mate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupTeamFragment extends Fragment {


    public GameSetupTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_setup_team, container, false);

        return gameDetailSetupView;
    }

}
