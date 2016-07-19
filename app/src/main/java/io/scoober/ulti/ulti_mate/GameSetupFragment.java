package io.scoober.ulti.ulti_mate;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;


public class GameSetupFragment extends Fragment {


    private Button createFromSetupButton;
    private CardView detailSetupCard, teamSetupCard;
    private TeamImageButton team1ImageButton, team2ImageButton;
    private EditText templateTitleText;
    private TextView gameTitleText, winningScoreText, timeCapsText,
            team1NameText, team2NameText, templateTitleLabelText;

    // Argument information
    MainMenuActivity.SetupToLaunch setupToLaunch;
    private long gameId, templateId;
    private Game game;
    private OnCardClickedListener cardListener;

    public GameSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cardListener = (OnCardClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCardClickedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gameSetupFragView = inflater.inflate(R.layout.fragment_game_setup, container, false);

        Bundle args = getArguments();
        gameId = args.getLong(MainMenuActivity.GAME_ID_EXTRA);
        setupToLaunch = (MainMenuActivity.SetupToLaunch)
                args.getSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA);

        getWidgetReferences(gameSetupFragView);

        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_TEMPLATE ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
            templateTitleText.setVisibility(View.VISIBLE);
            templateTitleLabelText.setVisibility(View.VISIBLE);
        }

        createFromSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Validate the setup. If the setup is not valid, alert the user and stop
                further processing.
                 */
                boolean valid = validateSetup();
                if (!valid) {
                    Utils.showValidationFailedDialog(getActivity());
                    return;
                }

                // Store the game depending on which setup was launched
                switch (setupToLaunch) {
                    case CREATE_GAME:
                    case UPDATE_GAME:
                        gameId = storeGame(game);
                        launchGameDisplay();
                        break;
                    case CREATE_TEMPLATE:
                        createTemplate(templateTitleText.getText().toString());
                        launchNewGameActivity();
                        break;
                    case EDIT_TEMPLATE:
                        editTemplate();
                        break;
                }
            }
        });
        return gameSetupFragView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        Change the setupToLaunch parameter depending on whether or not the game/template IDs
        are populated, likely indicating that the user pressed the back button
         */
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_GAME &&
                gameId > 0) {
            setupToLaunch = MainMenuActivity.SetupToLaunch.UPDATE_GAME;
        }

        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_TEMPLATE &&
                templateId > 0) {
            setupToLaunch = MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE;
        }

        setCreateButtonText();
        initializeWidgets();
    }

    /**
     * Gets references to widgets to use in other functions
     */
    private void getWidgetReferences(View v) {
        createFromSetupButton = (Button) v.findViewById(R.id.createFromSetupButton);
        team1ImageButton = (TeamImageButton) v.findViewById(R.id.team1ImageButton);
        team2ImageButton = (TeamImageButton) v.findViewById(R.id.team2ImageButton);

        detailSetupCard = (CardView) v.findViewById(R.id.detailSetupCard);
        teamSetupCard = (CardView) v.findViewById(R.id.teamSetupCard);

        gameTitleText = (TextView) v.findViewById(R.id.gameTitle);
        winningScoreText = (TextView) v.findViewById(R.id.winningScore);
        timeCapsText = (TextView) v.findViewById(R.id.timeCapsDescription);
        team1NameText = (TextView) v.findViewById(R.id.team1Name);
        team2NameText = (TextView) v.findViewById(R.id.team2Name);
        templateTitleLabelText = (TextView) v.findViewById(R.id.templateNameLabel);

        templateTitleText = (EditText) v.findViewById(R.id.templateNameEdit);
    }


    /**
     * Populates data on cards and sets onClickListeners
     */
    private void initializeWidgets() {
        Resources res = getActivity().getResources();

        String templateName = game.getTemplateName();
        if (templateName == null) {
            templateTitleText.setText(R.string.default_template_name);
        } else {
            templateTitleText.setText(game.getTemplateName());
        }
        // Set game values to values from game
        gameTitleText.setText(game.getGameName());

        // Set the winning score string
        int winningScore = game.getWinningScore();
        String winningScoreString;
        if (winningScore == 0) {
            winningScoreString = res.getString(R.string.description_no_winning_score);
        } else {
            winningScoreString = res.getString(R.string.description_winning_score, winningScore);
        }
        winningScoreText.setText(winningScoreString);

        // Set the time caps string
        String timeCapString = "";
        if (game.getSoftCapTime() > 0) {
            timeCapString = timeCapString + res.getString(R.string.description_soft_cap,
                    Utils.getTimeString(game.getSoftCapTime()));
        }
        if (game.getHardCapTime() > 0) {
            if (!timeCapString.isEmpty()) {
                timeCapString += '\n';
            }
            timeCapString = timeCapString + res.getString(R.string.description_hard_cap,
                    Utils.getTimeString(game.getHardCapTime()));
        }
        if (timeCapString.isEmpty()) {
            timeCapString = res.getString(R.string.description_no_time_caps);
        }
        timeCapsText.setText(timeCapString);

        // Set the team information
        team1ImageButton.build(80, game.getTeam1Color());
        team2ImageButton.build(80, game.getTeam2Color());
        team1NameText.setText(game.getTeam1Name());
        team2NameText.setText(game.getTeam2Name());

        // Set OnClickListeners
        detailSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardListener.onCardClicked(GameSetupActivity.Setup.GAME_SETUP);
            }
        });

        teamSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardListener.onCardClicked(GameSetupActivity.Setup.TEAM_SETUP);
            }
        });

        templateTitleText.addTextChangedListener(new TextValidator(templateTitleText) {
            @Override
            public void validate(TextView textView, String text) {
                Utils.validateTextNotEmpty(text, textView, getResources(), R.string.dialog_name_template);
            }
        });

        templateTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                game.setTemplateName(templateTitleText.getText().toString());
            }
        });

    }

    /**
     * This function sets the text for the createFromSetupButton, depending on:
     *      1. The setupToLaunch enum that was passed in from the intent
     *      2. Whether the gameId is populated from database creation
     */
    private void setCreateButtonText() {
        switch (setupToLaunch) {
            case CREATE_GAME:
                createFromSetupButton.setText(R.string.button_create_game);
                break;
            case UPDATE_GAME:
                createFromSetupButton.setText(R.string.button_update_game);
                break;
            case CREATE_TEMPLATE:
                createFromSetupButton.setText(R.string.button_create_template);
                break;
            case EDIT_TEMPLATE:
                createFromSetupButton.setText(R.string.button_update_template);
                break;
        }
    }

    /**
     * Stores the passed game object into the class variable
     * @param game  game object passed in
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Stores the game to the database, returning the database ID
     * @param game      Game object to store
     * @return          ID generated from the SQL database
     */
    private long storeGame(Game game) {
        // store game to database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        long newId;
        if (game.getId() > 0) {
            newId = gameDbAdapter.saveGame(game);
        } else {
            newId = gameDbAdapter.createGame(game);
        }
        gameDbAdapter.close();

        return newId;

    }

    /**
     * Creates templates from user populated fields in the game setup activity
     * @param templateName    Name of the new template
     */
    public void createTemplate(String templateName) {
        Game template = new Game(game.getGameName(), game.getWinningScore(),
                game.getTeam1Name(), game.getTeam1Color(), game.getTeam2Name(),
                game.getTeam2Color(), game.getSoftCapTime(), game.getHardCapTime());
        template.convertToTemplate(templateName);
        templateId = storeGame(template);
    }

    /**
     * Edits templates from user populated fields in the game setup activity
     */
    private void editTemplate() {
        boolean valid = validateSetup();
        if (!valid) {
            Utils.showValidationFailedDialog(getActivity());
            return;
        }
        storeGame(game); // set templateId in case the user presses back
        launchNewGameActivity();
    }

    /**
     * Launches the new game activity
     */
    private void launchNewGameActivity() {
        Intent intent = new Intent(getActivity().getBaseContext(), NewGameActivity.class);
        startActivity(intent);
    }

    /**
     * Launches the game display activity
     */
    private void launchGameDisplay() {

        MainMenuActivity.DisplayToLaunch displayToLaunch;
        if (setupToLaunch == MainMenuActivity.SetupToLaunch.UPDATE_GAME) {
            displayToLaunch = MainMenuActivity.DisplayToLaunch.UPDATE;
        } else {
            displayToLaunch = MainMenuActivity.DisplayToLaunch.NEW;
        }

        // Start Game Display Activity
        Intent intent = new Intent(getActivity().getBaseContext(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, gameId);
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, displayToLaunch);
        startActivity(intent);
    }

    /**
     * Validates the setup for the game setup activity. If any of the required game fields are not
     * filled out, then return false.
     * @return  Whether validation passed.
     */
    private boolean validateSetup() {
        EditText requiredFields[] = {templateTitleText};
        return Utils.validateFieldsNotEmpty(requiredFields);
    }

    public interface OnCardClickedListener {
        void onCardClicked(GameSetupActivity.Setup setupToLaunch);
    }

}
