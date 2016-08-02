package io.scoober.ulti.ulti_mate;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import io.scoober.ulti.ulti_mate.widgets.TeamImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameSetupFieldFragment extends Fragment {

    private RelativeLayout layoutPullingInit;
    private TeamImageButton leftTeamImage, rightTeamImage;
    private RadioGroup radioLeftFieldAnswer, radioPullingAnswer, radioPullingInitAnswer;
    private RadioButton answerTeam1LeftQuestion, answerTeam2LeftQuestion,
            answerTeam1PullingQuestion, answerTeam2PullingQuestion,
            answerTeam1PullingInitQuestion, answerTeam2PullingInitQuestion;
    private FloatingActionButton completeSetupButton;

    private Game game, tempGame;
    private onCompleteFieldListener completeListener;

    public interface onCompleteFieldListener {
        void onFieldComplete();
    }

    public GameSetupFieldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            completeListener = (onCompleteFieldListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onCompleteFieldListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gameFieldSetupView = inflater.inflate(R.layout.fragment_game_setup_field, container, false);

        getWidgetReferences(gameFieldSetupView);

        setListeners();
        return gameFieldSetupView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeWidgets();
    }

    /**
     * Stores the passed game object into the class variable
     * @param game  game object passed in
     */
    public void setGame(Game game) {
        this.game = game;
        this.tempGame = new Game(game);
    }

    /**
     * Gets references to widgets for this fragment
     */
    private void getWidgetReferences(View v) {

        layoutPullingInit = (RelativeLayout) v.findViewById(R.id.layoutPullingInit);

        leftTeamImage = (TeamImageButton) v.findViewById(R.id.leftTeamImageButton);
        rightTeamImage = (TeamImageButton) v.findViewById(R.id.rightTeamImageButton);

        radioLeftFieldAnswer = (RadioGroup) v.findViewById(R.id.radioLeftFieldAnswer);
        radioPullingAnswer = (RadioGroup) v.findViewById(R.id.radioPullingAnswer);
        radioPullingInitAnswer = (RadioGroup) v.findViewById(R.id.radioPullingInitAnswer);

        answerTeam1LeftQuestion = (RadioButton) v.findViewById(R.id.answerTeam1LeftQuestion);
        answerTeam2LeftQuestion = (RadioButton) v.findViewById(R.id.answerTeam2LeftQuestion);
        answerTeam1PullingQuestion = (RadioButton) v.findViewById(R.id.answerTeam1PullingQuestion);
        answerTeam2PullingQuestion = (RadioButton) v.findViewById(R.id.answerTeam2PullingQuestion);
        answerTeam1PullingInitQuestion = (RadioButton) v.findViewById(R.id.answerTeam1PullingInitQuestion);
        answerTeam2PullingInitQuestion = (RadioButton) v.findViewById(R.id.answerTeam2PullingInitQuestion);

        completeSetupButton = (FloatingActionButton) v.findViewById(R.id.completeSetupButton);
    }

    /**
     * This function initializes the image buttons and answers to questions
     */
    private void initializeWidgets() {

        Team firstTeam = tempGame.getTeam(1);
        Team secondTeam = tempGame.getTeam(2);

        String firstTeamName = firstTeam.getName();
        String secondTeamName = secondTeam.getName();

        // Set answers to questions
        answerTeam1LeftQuestion.setText(firstTeamName);
        answerTeam2LeftQuestion.setText(secondTeamName);
        answerTeam1PullingQuestion.setText(firstTeamName);
        answerTeam2PullingQuestion.setText(secondTeamName);
        answerTeam1PullingInitQuestion.setText(firstTeamName);
        answerTeam2PullingInitQuestion.setText(secondTeamName);

        // Check RadioButtons depending on game info
        int leftTeamPos = tempGame.getLeftTeamPos();
        if (leftTeamPos != 0) {
            if(leftTeamPos == 1) {
                answerTeam1LeftQuestion.setChecked(true);
            } else {
                answerTeam2LeftQuestion.setChecked(true);
            }
        }

        int pullingTeamPos = tempGame.getPullingTeamPos();
        if (pullingTeamPos != 0) {
            if(pullingTeamPos == 1) {
                answerTeam1PullingQuestion.setChecked(true);
            } else {
                answerTeam2PullingQuestion.setChecked(true);
            }
        }

        int initPullingTeamPos = tempGame.getInitPullingTeamPos();
        if (initPullingTeamPos != 0) {
            if(initPullingTeamPos == 1) {
                answerTeam1PullingInitQuestion.setChecked(true);
            } else {
                answerTeam2PullingInitQuestion.setChecked(true);
            }
        }

        // Make the last question visible, if the score is not 0-0
        if (tempGame.getScore(1) > 0 || tempGame.getScore(2) > 0) {
            layoutPullingInit.setVisibility(View.VISIBLE);
        }

        // Set field orientation
        GameDisplayActivity.setFieldOrientation(tempGame, getActivity(), leftTeamImage, rightTeamImage);

        // Set listeners for radio groups
        setListeners();

    }

    private void setListeners() {
        radioLeftFieldAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.answerTeam1LeftQuestion:
                        tempGame.setLeftTeamPos(1);
                        break;
                    case R.id.answerTeam2LeftQuestion:
                        tempGame.setLeftTeamPos(2);
                        break;
                }
                GameDisplayActivity.setFieldOrientation(tempGame, getActivity(), leftTeamImage, rightTeamImage);
            }
        });

        radioPullingAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.answerTeam1PullingQuestion:
                        tempGame.setPullingTeamPos(1);
                        break;
                    case R.id.answerTeam2PullingQuestion:
                        tempGame.setPullingTeamPos(2);
                        break;
                }
                GameDisplayActivity.setFieldOrientation(tempGame, getActivity(), leftTeamImage, rightTeamImage);
            }
        });

        radioPullingInitAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.answerTeam1PullingInitQuestion:
                        tempGame.setInitPullingTeamPos(1);
                        break;
                    case R.id.answerTeam2PullingInitQuestion:
                        tempGame.setInitPullingTeamPos(2);
                        break;
                }
            }
        });

        completeSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setLeftTeamPos(tempGame.getLeftTeamPos());
                game.setInitPullingTeamPos(tempGame.getInitPullingTeamPos());
                game.setPullingTeamPos(tempGame.getPullingTeamPos());
                completeListener.onFieldComplete();
            }
        });
    }

}
