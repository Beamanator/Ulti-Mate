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

    public MainMenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainMenuLayout = inflater.inflate(R.layout.fragment_main_menu, container, false);
        Button resume = (Button) mainMenuLayout.findViewById(R.id.resumeButton);
        Button newGameButton = (Button) mainMenuLayout.findViewById(R.id.new_game_button);

        //TODO Resume button

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GameSetupActivity.class);
                startActivity(intent);
            }
        });

        return mainMenuLayout;
    }
}
