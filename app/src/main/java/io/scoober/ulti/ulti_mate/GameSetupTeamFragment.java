package io.scoober.ulti.ulti_mate;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.thebluealliance.spectrum.SpectrumDialog;

import io.scoober.ulti.ulti_mate.CustomViews.TeamImageButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupTeamFragment extends Fragment {

    private TeamImageButton team1Image, team2Image;
    private EditText team1NameText, team2NameText;

    private Game game;

    public GameSetupTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_setup_team, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            game = GameSetupActivity.getGameFromBundle(bundle, getActivity().getBaseContext());
        }

        getWidgetReferences(gameDetailSetupView);

        //Set to correct image
        initializeImageButtons();
        setListeners();

        return gameDetailSetupView;
    }

    /**
     * This function gets references to widgets on this fragment
     */
    private void getWidgetReferences(View v) {
        team1Image = (TeamImageButton) v.findViewById(R.id.team1ImageButton);
        team2Image = (TeamImageButton) v.findViewById(R.id.team2ImageButton);

        team1NameText = (EditText) v.findViewById(R.id.team1Name);
        team2NameText = (EditText) v.findViewById(R.id.team2Name);
    }

    /**
     * This function initializes the image buttons by setting the default color and creating
     * a drawable with that color
     */
    private void initializeImageButtons() {

        Resources res = getResources();
        int team1Color = res.getColor(R.color.default_team_1);
        int team2Color = res.getColor(R.color.default_team_2);

        if (game != null) {
            team1Color = game.getTeam1Color();
            team2Color = game.getTeam2Color();
            team1NameText.setText(game.getTeam1Name());
            team2NameText.setText(game.getTeam2Name());
        }

        team1Image.build(150, team1Color);
        team2Image.build(150, team2Color);

    }

    /**
     * This function sets the listeners for our view
     */
    private void setListeners() {
        //Add onClickListeners for the image buttons to invoke our color picker
        team1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(team1Image);
            }
        });

        team2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start color picker
                showColorPicker(team2Image);
            }
        });
    }

    private void showColorPicker(final TeamImageButton imageButton) {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.team_colors)
                .setSelectedColor(imageButton.getColor())
                .setDismissOnColorSelected(true)
                .setTitle(R.string.dialog_color_picker)
                .setNegativeButtonText(R.string.cancel_button)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            imageButton.setColor(color);
                        }
                    }
                }).build().show(getFragmentManager(),"COLOR_PICKER_DIALOG");
    }

}
