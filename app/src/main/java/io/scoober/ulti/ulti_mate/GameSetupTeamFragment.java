package io.scoober.ulti.ulti_mate;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupTeamFragment extends Fragment {

    private TeamImageButton team1Image, team2Image;
    private EditText team1NameText, team2NameText;
    private FloatingActionButton completeSetupButton;

    private Game game;
    private onCompleteTeamListener completeListener;

    public interface onCompleteTeamListener {
        void onTeamComplete();
    }

    public GameSetupTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            completeListener = (onCompleteTeamListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onCompleteTeamListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameDetailSetupView = inflater.inflate(R.layout.fragment_game_setup_team, container, false);

        getWidgetReferences(gameDetailSetupView);

        setListeners();

        return gameDetailSetupView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeWidgets();
    }

    /**
     * This function gets references to widgets on this fragment
     */
    private void getWidgetReferences(View v) {
        team1Image = (TeamImageButton) v.findViewById(R.id.team1ImageButton);
        team2Image = (TeamImageButton) v.findViewById(R.id.team2ImageButton);
        team1NameText = (EditText) v.findViewById(R.id.team1Name);
        team2NameText = (EditText) v.findViewById(R.id.team2Name);
        completeSetupButton = (FloatingActionButton) v.findViewById(R.id.completeSetupButton);
    }

    /**
     * This function initializes the image buttons by setting the default color and creating
     * a drawable with that color
     */
    private void initializeWidgets() {

        Team firstTeam = game.getTeam(1);
        Team secondTeam = game.getTeam(2);
        team1NameText.setText(firstTeam.getName());
        team2NameText.setText(secondTeam.getName());

        team1Image.build(150, firstTeam.getColor());
        team2Image.build(150, secondTeam.getColor());

    }

    /**
     * Stores the passed game object into the class variable
     * @param game  game object passed in
     */
    public void setGame(Game game) {
        this.game = game;
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

        /*
        Text validators to ensure that team names are not empty
         */
        team1NameText.addTextChangedListener(new TextValidator(team1NameText) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.team_1_name_hint);
            }
        });

        team2NameText.addTextChangedListener(new TextValidator(team2NameText) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.team_2_name_hint);
            }
        });

        completeSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean valid = validateSetup();
                if (!valid) {
                    Utils.showValidationFailedDialog(getActivity());
                    return;
                }

                String team1Name = team1NameText.getText().toString();
                String team2Name = team2NameText.getText().toString();

                int team1Color = team1Image.getColor();
                int team2Color = team2Image.getColor();

                Team firstTeam = game.getTeam(1);
                Team secondTeam = game.getTeam(2);
                firstTeam.setName(team1Name);
                firstTeam.setColor(team1Color);
                secondTeam.setName(team2Name);
                secondTeam.setColor(team2Color);

                completeListener.onTeamComplete();
            }
        });
    }

    /**
     * Validates the setup for the game setup activity. If any of the required EditTexts are not
     * populated, then return false.
     * @return  Whether validation passed.
     */
    private boolean validateSetup() {
        EditText requiredFields[] = {team2NameText, team2NameText};
        return Utils.validateFieldsNotEmpty(requiredFields);
    }

    private void showColorPicker(final TeamImageButton imageButton) {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.team_colors)
                .setSelectedColor(imageButton.getColor())
                .setDismissOnColorSelected(true)
                .setTitle(R.string.dialog_color_picker)
                .setNegativeButtonText(R.string.dialog_cancel)
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
