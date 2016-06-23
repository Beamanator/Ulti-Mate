package io.scoober.ulti.ulti_mate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameDetailSetupFragment extends Fragment {


    public GameDetailSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_details_setup, container, false);

        Log.d("GameDetailSetupFragment","Test");

        return gameDetailSetupView;
    }

}
