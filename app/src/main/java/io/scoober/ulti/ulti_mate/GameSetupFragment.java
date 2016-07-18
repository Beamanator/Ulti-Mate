package io.scoober.ulti.ulti_mate;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;


public class GameSetupFragment extends Fragment {


    private Button createFromSetupButton;

    private Game game;

    // Intent information
    MainMenuActivity.SetupToLaunch setupToLaunch;
    private long gameId;
    private long templateId;

    public GameSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gameSetupFragView = inflater.inflate(R.layout.fragment_game_setup, container, false);

        Bundle args = getArguments();
        gameId = args.getLong(MainMenuActivity.GAME_ID_EXTRA);
        templateId = args.getLong(MainMenuActivity.TEMPLATE_ID_EXTRA);
        setupToLaunch = (MainMenuActivity.SetupToLaunch)
                args.getSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA);
        game = GameSetupActivity.getGameFromBundle(args, getActivity().getBaseContext());

        initializeCards(gameSetupFragView);

        createFromSetupButton = (Button) gameSetupFragView.findViewById(R.id.createFromSetupButton);
        createFromSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Validate the setup. If the setup is not valid, alert the user and stop
                further processing.
                 */
                boolean valid = validateSetup();
                if (!valid) {
                    showValidationFailedDialog();
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
                        createTemplate(true);
                        break;
                    case EDIT_TEMPLATE:
                        editTemplate(true);
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
    }

    /**
     * Gets references to widgets to use in other
     */
    private void initializeCards(View v) {
        TeamImageButton team1ImageButton = (TeamImageButton) v.findViewById(R.id.team1ImageButton);
        TeamImageButton team2ImageButton = (TeamImageButton) v.findViewById(R.id.team2ImageButton);
        TextView gameTitleText = (TextView) v.findViewById(R.id.gameTitle);
        TextView winningScoreText = (TextView) v.findViewById(R.id.winningScore);
        TextView timeCapsText = (TextView) v.findViewById(R.id.timeCapsDescription);
        TextView team1NameText = (TextView) v.findViewById(R.id.team1Name);
        TextView team2NameText = (TextView) v.findViewById(R.id.team2Name);

        Resources res = getActivity().getResources();

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

    }

    /**
     * This function sets the text for the createFromSetupButton, depending on:
     *      1. The setupToLaunch enum that was passed in from the intent
     *      2. Whether the gameId or templateId is populated from database creation
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
     * @param launchActivity    boolean to determine whether the NewGameActivity should be launched
     */
    private void createTemplate(boolean launchActivity) {
        boolean valid = validateSetup();
        if (!valid) {
            showValidationFailedDialog();
            return;
        }
        showTemplateNameDialog(launchActivity);
    }

    /**
     * Edits templates from user populated fields in the game setup activity
     * @param launchActivity    boolean to determine whether the NewGameActivity should be launched
     */
    private void editTemplate(boolean launchActivity) {
        boolean valid = validateSetup();
        if (!valid) {
            showValidationFailedDialog();
            return;
        }
        //TODO consider if this has any issues now that we store the game as a class variable
        Game template = game;
        storeGame(template); // set templateId in case the user presses back
        if (launchActivity) {
            launchNewGameActivity();
        } else {
            showSaveTemplateSnackbar(template.getTemplateName());
        }
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
        if (game.getGameName().isEmpty() || game.getTeam1Name().isEmpty() || game.getTeam2Name().isEmpty()) {
            return false;
        }
        return true;
    }

    private void showTemplateNameDialog(final boolean launchActivity) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.templateNameEdit);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_name_template)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String templateName = nameEdit.getText().toString();
                        //TODO we do not want to convert all games to templates
                        Game template = game;
                        template.convertToTemplate(templateName);
                        templateId = storeGame(game);
                        if (launchActivity) {
                            launchNewGameActivity();
                        } else {
                            showSaveTemplateSnackbar(templateName);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();

        dialog.show();

        /*
        Add listener to the editText and disable positive button if validation fails
         */
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        nameEdit.addTextChangedListener(new TextValidator(nameEdit) {
            @Override
            public void validate(TextView textView, String text) {
                boolean valid = Utils.validateTextNotEmpty(text, textView,
                        getResources(), R.string.dialog_name_template);
                positiveButton.setEnabled(valid);
            }
        });
    }



    private void showSaveTemplateSnackbar(String templateName) {
        CoordinatorLayout cl = (CoordinatorLayout) getActivity().findViewById(R.id.gameSetupCoordinatorLayout);
        String message = getResources().getString(R.string.snackbar_template_save_successful, templateName);
        Snackbar.make(cl,message,Snackbar.LENGTH_LONG).show();

    }

    /**
     * Show a dialog that alerts the user that required fields must be populated.
     */
    private void showValidationFailedDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_validation_failed)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create()
                .show();
    }

}
