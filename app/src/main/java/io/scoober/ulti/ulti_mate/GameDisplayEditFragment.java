package io.scoober.ulti.ulti_mate;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.HashMap;
import java.util.Map;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameDisplayEditFragment extends Fragment {

    private Button editButton, saveButton;

    private ViewSwitcher gameTitleSwitcher, team1NameSwitcher, team2NameSwitcher;
    private ViewSwitcher viewEditButtonSwitcher;

    private TextView gameTitleView, gameLengthText;

    private EditText gameTitleEdit;

    private Game game;
    private MainMenuActivity.DisplayToLaunch displayToLaunch;

    private Map<Integer,GameDisplayActivity.TeamViewHolder> teamViewMap;

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

        // set up widget references
        getWidgetReferences(fragmentLayout);

        // inflate team data and populate private variables
        inflateTeamData(fragmentLayout);

        switch (displayToLaunch) {
            case EDIT:
                setupAndEnableButtons();
                break;
            case VIEW:
                // TODO: refactor to use setupAndEnableButtons?
                setupEditButtonListener();
                toggleViewTypes();
                break;
        }

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    private void setupScoreButtonListeners(final int team) {
        final GameDisplayActivity.TeamViewHolder teamViewHolder = teamViewMap.get(team);
        teamViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.incrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team,game,teamViewMap);
            }
        });

        teamViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = game.decrementScore(team);
                teamViewHolder.scoreView.setText(Integer.toString(score));
                Utils.saveGameDetails(getActivity().getBaseContext(), game);
                GameDisplayActivity.enableDisableScoreButtons(team,game,teamViewMap);
            }
        });
    }

    /**
     * Sets listeners and enables buttons
     */
    private void setupAndEnableButtons() {

        // save Button
        setupSaveButtonListener();

        // color button
        setupColorButtonListeners();

        // set up score button listeners:
        setupScoreButtonListeners(1);
        setupScoreButtonListeners(2);

        // Enable Color Buttons
        teamViewMap.get(1).colorButton.setClickable(true);
        teamViewMap.get(2).colorButton.setClickable(true);

        // Enable Score Buttons
        GameDisplayActivity.enableDisableScoreButtons(1, game, teamViewMap);
        GameDisplayActivity.enableDisableScoreButtons(2, game, teamViewMap);
    }

    private void setupColorButtonListeners() {
        final TeamImageButton team1ColorButton = teamViewMap.get(1).colorButton;
        final TeamImageButton team2ColorButton = teamViewMap.get(2).colorButton;
        team1ColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(team1ColorButton);
            }
        });

        team2ColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start color picker
                showColorPicker(team2ColorButton);
            }
        });
    }

    /**
     * Starts GameDisplayActivity which will pull in GameDisplayEditFragment (edit mode)
     */
    private void setupEditButtonListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameDisplayActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
                intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                        MainMenuActivity.DisplayToLaunch.EDIT);
                startActivity(intent);
            }
        });
    }

    /**
     * Starts GameDisplayActivity which will pull in GameDisplayEditFragment (view mode)
     */
    private void setupSaveButtonListener() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the TeamViewHolder
                GameDisplayActivity.TeamViewHolder team1View = teamViewMap.get(1);
                GameDisplayActivity.TeamViewHolder team2View = teamViewMap.get(2);

                // Get values from user input
                String gameName = gameTitleEdit.getText().toString();
                String team1Name = team1View.nameEdit.getText().toString();
                String team2Name = team2View.nameEdit.getText().toString();
                int team1Score = Integer.parseInt(team1View.scoreView.getText().toString());
                int team2Score = Integer.parseInt(team2View.scoreView.getText().toString());
                @ColorInt int team1Color = team1View.colorButton.getColor();
                @ColorInt int team2Color = team2View.colorButton.getColor();

                game.setGameName(gameName);
                game.setTeam1Name(team1Name);
                game.setTeam2Name(team2Name);
                game.setTeam1Score(team1Score);
                game.setTeam2Score(team2Score);
                game.setTeam1Color(team1Color);
                game.setTeam2Color(team2Color);

                Utils.saveGameDetails(v.getContext(), game);

                Intent intent = new Intent(v.getContext(), GameDisplayActivity.class);
                intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
                intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA,
                        MainMenuActivity.DisplayToLaunch.VIEW);
                startActivity(intent);
            }
        });
    }

    /**
     * Get references to all widgets in the view
     * @param v     View to find widgets in
     */
    private void getWidgetReferences(View v) {
        editButton = (Button) v.findViewById(R.id.editButton);
        saveButton = (Button) v.findViewById(R.id.saveButton);

        gameTitleEdit = (EditText) v.findViewById(R.id.gameTitleEdit);
        gameTitleView = (TextView) v.findViewById(R.id.gameTitleView);
        gameLengthText = (TextView) v.findViewById(R.id.gameLengthText);

        gameTitleSwitcher = (ViewSwitcher) v.findViewById(R.id.gameTitleSwitcher);
        team1NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team1NameSwitcher);
        team2NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team2NameSwitcher);
        viewEditButtonSwitcher = (ViewSwitcher) v.findViewById(R.id.viewEditButtonSwitcher);

        getTeamViews(v);
    }

    private void getTeamViews(View v) {
        // Team 1
        GameDisplayActivity.TeamViewHolder team1View = new GameDisplayActivity.TeamViewHolder();
        team1View.nameView = (TextView) v.findViewById(R.id.leftTeamNameView);
        team1View.nameEdit = (EditText) v.findViewById(R.id.leftTeamNameEdit);
        team1View.scoreView = (TextView) v.findViewById(R.id.leftTeamScoreEdit);
        team1View.addButton = (Button) v.findViewById(R.id.leftTeamAddEdit);
        team1View.subtractButton = (Button) v.findViewById(R.id.leftTeamSubtractEdit);
        team1View.colorButton = (TeamImageButton) v.findViewById(R.id.leftTeamColorButton);

        // Team 2
        GameDisplayActivity.TeamViewHolder team2View = new GameDisplayActivity.TeamViewHolder();
        team2View.nameView = (TextView) v.findViewById(R.id.rightTeamNameView);
        team2View.nameEdit = (EditText) v.findViewById(R.id.rightTeamNameEdit);
        team2View.scoreView = (TextView) v.findViewById(R.id.rightTeamScoreEdit);
        team2View.addButton = (Button) v.findViewById(R.id.rightTeamAddEdit);
        team2View.subtractButton = (Button) v.findViewById(R.id.rightTeamSubtractEdit);
        team2View.colorButton = (TeamImageButton) v.findViewById(R.id.rightTeamColorButton);

        // Setup the view map
        teamViewMap = new HashMap<>();
        teamViewMap.put(1,team1View);
        teamViewMap.put(2,team2View);
    }

    /**
     * Function to switch all EditText widgets to TextView widgets, and vice versa
     */
    private void toggleViewTypes() {
        gameTitleSwitcher.showNext();
        team1NameSwitcher.showNext();
        team2NameSwitcher.showNext();
        viewEditButtonSwitcher.showNext();
    }

    private void inflateTeamData(View v) {
        // set Game title:
        gameTitleEdit.setText(game.getGameName());
        gameTitleView.setText(game.getGameName());

        // set team details:
        // Get the TeamViewHolder
        GameDisplayActivity.TeamViewHolder team1View = teamViewMap.get(1);
        GameDisplayActivity.TeamViewHolder team2View = teamViewMap.get(2);
        team1View.nameEdit.setText(game.getTeam1Name());
        team1View.nameView.setText(game.getTeam1Name());
        team2View.nameEdit.setText(game.getTeam2Name());
        team2View.nameView.setText(game.getTeam2Name());
        team1View.scoreView.setText(Integer.toString(game.getTeam1Score()));
        team2View.scoreView.setText(Integer.toString(game.getTeam2Score()));

        @ColorInt int leftTeamColor = game.getTeam1Color();
        @ColorInt int rightTeamColor = game.getTeam2Color();

        team1View.colorButton.build(150, leftTeamColor);
        team2View.colorButton.build(150, rightTeamColor);
        // TODO: maybe add color to team text / background

        // Game length bar information:
        // TODO: add game start / end data to database, then add to bar here
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
