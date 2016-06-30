package io.scoober.ulti.ulti_mate;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupTeamFragment extends Fragment {

    private ImageButton team1Image;
    private ImageButton team2Image;

    private int team1Color;
    private int team2Color;

    public GameSetupTeamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_setup_team, container, false);

        team1Image = (ImageButton) gameDetailSetupView.findViewById(R.id.team1ImageButton);
        team2Image = (ImageButton) gameDetailSetupView.findViewById(R.id.team2ImageButton);

        //Set to correct image
        initializeImageButtons();

        return gameDetailSetupView;
    }

    public int getTeamColor(int team) {
        if (team == 1) {
            return team1Color;
        } else if (team == 2) {
            return team2Color;
        }

        return -1;
    }

    /**
     * This function intializes the image buttons by setting the default color and creating
     * a drawable with that color
     */
    private void initializeImageButtons() {

        team1Color = getResources().getColor(R.color.team1default);
        team2Color = getResources().getColor(R.color.team2default);

        team1Image.setImageDrawable(getTeamGradientDrawable(team1Color));
        team2Image.setImageDrawable(getTeamGradientDrawable(team2Color));

        //Add onClickListeners for the image buttons to invoke our color picker
        team1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(1,team1Color);
            }
        });

        team2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start color picker
                showColorPicker(2,team2Color);
            }
        });
    }

    private GradientDrawable getTeamGradientDrawable(int color) {
        return Utils.createGradientDrawableCircle(150, color, 0, 0);
    }

    private void showColorPicker(final int team, int initialColor) {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.team_colors)
                .setSelectedColor(initialColor)
                .setDismissOnColorSelected(true)
                .setTitle(R.string.dialog_color_picker)
                .setNegativeButtonText(R.string.cancel_button)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            setTeamImage(team, color);
                        }
                    }
                }).build().show(getFragmentManager(),"COLOR_PICKER_DIALOG");
    }

    // TODO replace with a custom component that can handle this
    private void setTeamImage(int team, int color) {

        if (team == 1) {
            team1Color = color;
            team1Image.setImageDrawable(getTeamGradientDrawable(team1Color));
        } else if (team == 2) {
            team2Color = color;
            team2Image.setImageDrawable(getTeamGradientDrawable(team2Color));
        }

    }

}
