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
                break;
            case VIEW:
                toggleViewTypes();
                break;
        }

        // Inflate the layout for this fragment
        return fragmentLayout;
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

        // TODO: disable buttons n stuff
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
