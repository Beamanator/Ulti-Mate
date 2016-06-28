package io.scoober.ulti.ulti_mate;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameDisplayEditFragment extends Fragment {

    Game game;
    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    public GameDisplayEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_game_display_edit,
                container, false);

        // get data from intent:
        Intent intent = getActivity().getIntent();
        long id = intent.getExtras().getLong(MainMenuActivity.GAME_ID_EXTRA, 0);
        displayToLaunch = (MainMenuActivity.DisplayToLaunch)
                intent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        game = Utils.getGameDetails(getActivity().getBaseContext(), id);

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

}
