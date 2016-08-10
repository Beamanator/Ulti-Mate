package io.scoober.ulti.ulti_mate;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;


public class GameSetupOverviewFragment extends Fragment {

    private Button createFromSetupButton;
    private CardView detailSetupCard, teamSetupCard, fieldSetupCard;
    private TeamImageButton team1TeamSetupImage, team2TeamSetupImage,
            leftTeamFieldSetupImage, rightTeamFieldSetupImage;
    private EditText templateTitleText;
    private TextView gameTitleText, winningScoreText, timeCapsText,
            team1NameText, team2NameText, templateTitleLabelText;

    // Argument information
    MainMenuActivity.SetupToLaunch setupToLaunch;
    private long gameId;
    private Game game;
    private OnFragActionListener fragListener;

    public GameSetupOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragListener = (OnFragActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gameSetupFragView = inflater.inflate(R.layout.fragment_game_setup_overview, container, false);

        Bundle args = getArguments();
        gameId = args.getLong(MainMenuActivity.GAME_ID_EXTRA);
        setupToLaunch = (MainMenuActivity.SetupToLaunch)
                args.getSerializable(MainMenuActivity.GAME_SETUP_ARG_EXTRA);

        getWidgetReferences(gameSetupFragView);
        initializeWidgets();
        setCreateButtonText();

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
                //TODO Create a listener interface for launching next activities
                switch (setupToLaunch) {
                    case CREATE_GAME:
                        gameId = Utils.saveGameDetails(getActivity(), game);
                        launchGameDisplay();
                        break;
                    case UPDATE_GAME:
                        gameId = Utils.saveGameDetails(getActivity(), game);
                        finishActivity();
                        break;
                    case CREATE_TEMPLATE:
                        createTemplate(templateTitleText.getText().toString(), true);
                        finishActivity();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_game_setup, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_template_create:
                showTemplateNameDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets references to widgets to use in other functions
     */
    private void getWidgetReferences(View v) {
        createFromSetupButton = (Button) v.findViewById(R.id.createFromSetupButton);
        team1TeamSetupImage = (TeamImageButton) v.findViewById(R.id.team1ImageButton);
        team2TeamSetupImage = (TeamImageButton) v.findViewById(R.id.team2ImageButton);
        leftTeamFieldSetupImage = (TeamImageButton) v.findViewById(R.id.leftTeamImageButton);
        rightTeamFieldSetupImage = (TeamImageButton) v.findViewById(R.id.rightTeamImageButton);

        detailSetupCard = (CardView) v.findViewById(R.id.detailSetupCard);
        teamSetupCard = (CardView) v.findViewById(R.id.teamSetupCard);
        fieldSetupCard = (CardView) v.findViewById(R.id.fieldSetupCard);

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

        // Template information
        if (templateName == null) {
            templateTitleText.setText(R.string.default_template_name);
        } else {
            templateTitleText.setText(game.getTemplateName());
        }

        if (setupToLaunch == MainMenuActivity.SetupToLaunch.CREATE_TEMPLATE ||
                setupToLaunch == MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE) {
            templateTitleText.setVisibility(View.VISIBLE);
            templateTitleLabelText.setVisibility(View.VISIBLE);
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
        team1TeamSetupImage.build(GameDisplayActivity.TEAM_CIRCLE_SIZE, game.getTeam(1).getColor());
        team2TeamSetupImage.build(GameDisplayActivity.TEAM_CIRCLE_SIZE, game.getTeam(2).getColor());
        team1NameText.setText(game.getTeam(1).getName());
        team2NameText.setText(game.getTeam(2).getName());

        // Set the field information
        GameDisplayActivity.setFieldOrientation(game, getActivity(),
                leftTeamFieldSetupImage, rightTeamFieldSetupImage);

        // Set OnClickListeners
        detailSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragListener.onCardClicked(GameSetupActivity.Setup.GAME_SETUP);
            }
        });

        teamSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragListener.onCardClicked(GameSetupActivity.Setup.TEAM_SETUP);
            }
        });

        fieldSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragListener.onCardClicked(GameSetupActivity.Setup.FIELD_SETUP);
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
     * Creates templates from user populated fields in the game setup activity
     * @param templateName      Name of the new template
     * @param storeToGame       Set to true if the game should be stored for further editing by
     *                          the fragment. This should be used, for example, after creating
     *                          a template so that it can be edited, but not when creating a template
     *                          from a game ad hoc
     */
    public void createTemplate(String templateName, boolean storeToGame) {
        Game template = new Game(game.getGameName(), game.getWinningScore(),
                game.getTeam(1), game.getTeam(2), game.getSoftCapTime(), game.getHardCapTime());
        template.convertToTemplate(templateName);
        Utils.saveGameDetails(getActivity(), template);

        if (storeToGame) {
            game = template;
        }
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
        Utils.saveGameDetails(getActivity(), game);
        finishActivity();
    }

    /**
     * Shows a dialog that allows the user to create a template with a given name
     */
    private void showTemplateNameDialog() {

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
                        createTemplate(templateName, false);
                        fragListener.onTemplateSaved(templateName);
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

    /**
     * Launches the new game activity
     */
    private void finishActivity() {
        getActivity().finish(); // returns to the previously tracked activity - like pushing back button
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
        getActivity().finish();
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

    public interface OnFragActionListener {
        void onCardClicked(GameSetupActivity.Setup setupToLaunch);
        void onTemplateSaved(String templateName);
    }

}
