package io.scoober.ulti.ulti_mate;


import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.spectrum.SpectrumDialog;

import io.scoober.ulti.ulti_mate.CustomViews.TeamImageButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupTeamFragment extends Fragment {

    private TeamImageButton team1Image;
    private TeamImageButton team2Image;

    public GameSetupTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_setup_team, container, false);

        team1Image = (TeamImageButton) gameDetailSetupView.findViewById(R.id.team1ImageButton);
        team2Image = (TeamImageButton) gameDetailSetupView.findViewById(R.id.team2ImageButton);

        //Set to correct image
        initializeImageButtons();
        //TODO Modify focus behavior of the game title (should collapse keyboard when not selected)
        //TODO Add onClick/onFocus listener or attribute to select all text for team names when selected

        return gameDetailSetupView;
    }

    /**
     * This function intializes the image buttons by setting the default color and creating
     * a drawable with that color
     */
    private void initializeImageButtons() {

        Resources res = getResources();
        int team1Color = res.getColor(R.color.default_team_1);
        int team2Color = res.getColor(R.color.default_team_2);

        team1Image.build(150, team1Color);
        team2Image.build(150, team2Color);

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
