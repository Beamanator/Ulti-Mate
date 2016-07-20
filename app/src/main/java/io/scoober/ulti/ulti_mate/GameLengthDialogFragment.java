package io.scoober.ulti.ulti_mate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Poo on 7/18/2016.
 */
public class GameLengthDialogFragment extends DialogFragment{

    OnTimeSelectedListener timeListener;
    OnDateSelectedListener dateListener;
    OnNegativeButtonClickListener negativeButtonListener;
    OnPositiveButtonClickListener positiveButtonListener;

    private View layout;
    private static String fragTitle;
    private static Game game;

    TextView editStartTime;
    TextView editStartDate;
    TextView editEndTime;
    TextView editEndDate;
    TextView negativeButton;
    TextView positiveButton;

    private long startDateMilli;
    private long endDateMilli;


    /**
     * Function used to create a new instance of CustomDialogFragment.
     * Note: never use new CustomDialogFragment() if your fragment needs a title
     * @param title     String title for fragment
     * @return          New instance of CustomDialogFragment
     */
    static GameLengthDialogFragment newInstance(String title, Game g) {
        GameLengthDialogFragment newFrag = new GameLengthDialogFragment();

        fragTitle = title;
        game = g;

        return newFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.custom_dialog_fragment_time_picker,
                container, false);

        TextView fragTitleView = getTextView(R.id.dialogFragmentTitle);
        fragTitleView.setText(fragTitle);

        getWidgetReferences();
        populateWidgets();

        // Setup time and date listeners
        timeListener.onTimeSelected(editStartTime);
        dateListener.onDateSelected(editStartDate);
        timeListener.onTimeSelected(editEndTime);
        dateListener.onDateSelected(editEndDate);

        // Setup negative / positive buttons
        negativeButtonListener.onNegativeButtonClick(negativeButton);
        positiveButtonListener.onPositiveButtonClick(positiveButton, game);

        return layout;
    }

    /**
     * Function gets specified textView from layout
     * @param id    layout widget ID
     * @return      TextView widget
     */
    public TextView getTextView(int id) {
        if (layout == null) {
            return null;
        } else {
            return (TextView) layout.findViewById(id);
        }
    }

    /**
     * Function starts up DialogFragment
     * @param manager   FragmentManager
     */
    public void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, "GameLengthDialogFragment");
        ft.commit();
    }

    // Before API 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // OnTimeSelectedListener interface
        try {
            timeListener = (OnTimeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnTimeSelectedListener");
        }

        // OnDateSelectedListener interface
        try {
            dateListener = (OnDateSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnDateSelectedListener");
        }

        // OnNegativeButtonClickListener interface
        try {
            negativeButtonListener = (OnNegativeButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnNegativeButtonClickListener");
        }

        // OnPositiveButtonClickListener interface
        try {
            positiveButtonListener = (OnPositiveButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnPositiveButtonClickListener");
        }
    }

    // API 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // OnTimeSelectedListener interface
        try {
            timeListener = (OnTimeSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                " must implement OnTimeSelectedListener");
        }

        // OnDateSelectedListener interface
        try {
            dateListener = (OnDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnDateSelectedListener");
        }

        // OnNegativeButtonClickListener interface
        try {
            negativeButtonListener = (OnNegativeButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnNegativeButtonClickListener");
        }

        // OnPositiveButtonClickListener interface
        try {
            positiveButtonListener = (OnPositiveButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnPositiveButtonClickListener");
        }
    }

    // Container Activity (/ Fragment?) must implement these interfaces
    public interface OnTimeSelectedListener {
        void onTimeSelected (TextView tv);
    }

    public interface OnDateSelectedListener {
        void onDateSelected(TextView tv);
    }

    public interface OnNegativeButtonClickListener {
        void onNegativeButtonClick(TextView tv);
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(TextView tv, Game g);
    }

    /**
     * Function gets game start time in milliseconds (accurate to year, month, day, hour, minute)
     * @return  Game start date / time in milliseconds
     */
    public long getGameStartMilli() {
        String timeStart = editStartTime.getText().toString();
        String dateStart = editStartDate.getText().toString();
        Calendar timeStartCalendar = Calendar.getInstance();
        Calendar dateStartCalendar = Calendar.getInstance();

        long timeStartMilli = Utils.getMilliFrom12HrString(timeStart);
        long dateStartMilli = Utils.getMilliFromDateString(dateStart);

        timeStartCalendar.setTimeInMillis(timeStartMilli);
        dateStartCalendar.setTimeInMillis(dateStartMilli);

        return Utils.addDateToTime(timeStartCalendar, dateStartCalendar).getTimeInMillis();
    }

    /**
     * Function gets game end time in milliseconds (accurate to year, month, day, hour, minute)
     * @return  Game end date / time in milliseconds
     */
    public long getGameEndMilli() {
        String timeEnd = editEndTime.getText().toString();
        String dateEnd = editEndDate.getText().toString();
        Calendar timeEndCalendar = Calendar.getInstance();
        Calendar dateEndCalendar = Calendar.getInstance();

        long timeEndMilli = Utils.getMilliFrom12HrString(timeEnd);
        long dateEndMilli = Utils.getMilliFromDateString(dateEnd);

        timeEndCalendar.setTimeInMillis(timeEndMilli);
        dateEndCalendar.setTimeInMillis(dateEndMilli);

        return Utils.addDateToTime(timeEndCalendar, dateEndCalendar).getTimeInMillis();
    }

    /**
     * Fills time / date TextViews with respective Game times / dates
     */
    private void populateWidgets() {
        startDateMilli = game.getStartDate();
        endDateMilli = game.getEndDate();

        editStartTime.setText(Utils.getTimeString(startDateMilli));
        editStartDate.setText(Utils.getDateString(startDateMilli));
        editEndTime.setText(Utils.getTimeString(endDateMilli));
        editEndDate.setText(Utils.getDateString(endDateMilli));
    }

    /**
     * Function to get references to all textViews
     */
    private void getWidgetReferences() {
        editStartTime = getTextView(R.id.editStartTime);
        editStartDate = getTextView(R.id.editStartDate);
        editEndTime = getTextView(R.id.editEndTime);
        editEndDate = getTextView(R.id.editEndDate);
        negativeButton = getTextView(R.id.negativeButton);
        positiveButton = getTextView(R.id.positiveButton);
    }
}
