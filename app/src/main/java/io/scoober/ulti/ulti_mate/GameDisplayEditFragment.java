package io.scoober.ulti.ulti_mate;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameDisplayEditFragment extends Fragment {

    private Button editButton, saveButton, leftTeamAddButton;
    private Button leftTeamSubtractButton, rightTeamAddButton, rightTeamSubtractButton;

    private ViewSwitcher gameTitleSwitcher, team1NameSwitcher, team2NameSwitcher;
    private ViewSwitcher viewEditButtonSwitcher;

    private TextView leftTeamScore, rightTeamScore, gameLengthText;
    private TextView gameTitleView, leftTeamNameView, rightTeamNameView;

    private EditText gameTitleEdit, leftTeamNameEdit, rightTeamNameEdit;

    private Game game;
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

        // set up widget references
        getWidgetReferences(fragmentLayout);

        // inflate team data and populate private variables
        inflateTeamData(fragmentLayout);

        switch (displayToLaunch) {
            case EDIT:
                setupSaveButtonListener();
                enableScoreButtons(game.getTeam1Score(), game.getTeam2Score());

                // set up score button listeners:
                setupScoreButtonListeners(game.getTeam1Name(), leftTeamScore,
                        leftTeamAddButton, leftTeamSubtractButton);
                setupScoreButtonListeners(game.getTeam2Name(), rightTeamScore,
                        rightTeamAddButton, rightTeamSubtractButton);

                break;
            case VIEW:
                setupEditButtonListener();
                toggleViewTypes();
                break;
        }

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    private void setupScoreButtonListeners(final String team, final TextView score, final Button addButton, final Button subtractButton) {
        // TODO: fix bug where buttons link to same score when teams have same name
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int team1Score, team2Score;
                if (team.equals(game.getTeam1Name())) {
                    team1Score = game.incrementScore(1);
                    score.setText(Integer.toString(team1Score));
                    if (team1Score == 99) { addButton.setEnabled(false); }
                    if (team1Score == 1) { subtractButton.setEnabled(true); }
                } else if (team.equals(game.getTeam2Name())) {
                    team2Score = game.incrementScore(2);
                    score.setText(Integer.toString(team2Score));
                    if (team2Score == 99) { addButton.setEnabled(false); }
                    if (team2Score == 1) { subtractButton.setEnabled(true); }
                } else {
                    return;
                }
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int team1Score, team2Score;
                if (team.equals(game.getTeam1Name())) {
                    team1Score = game.decrementScore(1);
                    score.setText(Integer.toString(team1Score));

                    if (team1Score == 0) { subtractButton.setEnabled(false); }
                    if (team1Score == 98) { addButton.setEnabled(true); }
                } else if (team.equals(game.getTeam2Name())){
                    team2Score = game.decrementScore(2);
                    score.setText(Integer.toString(team2Score));

                    // Disable minus button if score is 0
                    if (team2Score == 0) { subtractButton.setEnabled(false); }
                    // Enable add button if score is less than 99 (so if it equals 98)
                    if (team2Score == 98) { addButton.setEnabled(true); }
                } else {
                    return;
                }
            }
        });
    }

    private void enableScoreButtons(int t1, int t2) {
        if (t1 != 99) { leftTeamAddButton.setEnabled(true); }
        if (t1 != 0) { leftTeamSubtractButton.setEnabled(true); }
        if (t2 != 99) { rightTeamAddButton.setEnabled(true); }
        if (t2 != 0) { rightTeamSubtractButton.setEnabled(true); }
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
                String gameName = gameTitleEdit.getText().toString();
                String team1Name = leftTeamNameEdit.getText().toString();
                String team2Name = rightTeamNameEdit.getText().toString();
                int team1Score = Integer.parseInt(leftTeamScore.getText().toString());
                int team2Score = Integer.parseInt(rightTeamScore.getText().toString());

                game.setGameName(gameName);
                game.setTeam1Name(team1Name);
                game.setTeam2Name(team2Name);
                game.setTeam1Score(team1Score);
                game.setTeam2Score(team2Score);

                // TODO: get new colors as well

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
        leftTeamAddButton = (Button) v.findViewById(R.id.leftTeamAddEdit);
        leftTeamSubtractButton = (Button) v.findViewById(R.id.leftTeamSubtractEdit);
        rightTeamAddButton = (Button) v.findViewById(R.id.rightTeamAddEdit);
        rightTeamSubtractButton = (Button) v.findViewById(R.id.rightTeamSubtractEdit);

        gameTitleEdit = (EditText) v.findViewById(R.id.gameTitleEdit);
        leftTeamNameEdit = (EditText) v.findViewById(R.id.leftTeamNameEdit);
        rightTeamNameEdit = (EditText) v.findViewById(R.id.rightTeamNameEdit);

        gameTitleView = (TextView) v.findViewById(R.id.gameTitleView);
        leftTeamNameView = (TextView) v.findViewById(R.id.leftTeamNameView);
        rightTeamNameView = (TextView) v.findViewById(R.id.rightTeamNameView);
        leftTeamScore = (TextView) v.findViewById(R.id.leftTeamScoreEdit);
        rightTeamScore = (TextView) v.findViewById(R.id.rightTeamScoreEdit);
        gameLengthText = (TextView) v.findViewById(R.id.gameLengthText);

        gameTitleSwitcher = (ViewSwitcher) v.findViewById(R.id.gameTitleSwitcher);
        team1NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team1NameSwitcher);
        team2NameSwitcher = (ViewSwitcher) v.findViewById(R.id.team2NameSwitcher);
        viewEditButtonSwitcher = (ViewSwitcher) v.findViewById(R.id.viewEditButtonSwitcher);
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
        leftTeamNameEdit.setText(game.getTeam1Name());
        leftTeamNameView.setText(game.getTeam1Name());
        rightTeamNameEdit.setText(game.getTeam2Name());
        rightTeamNameView.setText(game.getTeam2Name());
        leftTeamScore.setText(Integer.toString(game.getTeam1Score()));
        rightTeamScore.setText(Integer.toString(game.getTeam2Score()));

        int t1Color = game.getTeam1Color();
        int t2Color = game.getTeam2Color();
        // TODO: maybe add color to team text / background

        // Game length bar information:
        // TODO: add game start / end data to database, then add to bar here
    }
}
