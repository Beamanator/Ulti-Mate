package io.scoober.ulti.ulti_mate;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Navin on 6/27/2016.
 */
public class Utils {
    /*
    Date Utilities
     */

    /**
     * Function returns a MM/dd/yyyy date string from time in milliseconds
     * @param milli Time in milliseconds
     * @return      MM/dd/yyyy formatted date string
     */
    public static String getDateString(long milli) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return sdf.format(new Date(milli));
    }

    /**
     * Function to get date in milliseconds from MM/dd/yyyy formatted time. For example, 02/33/2016
     * @param dateString    Date as string formatted MM/dd/yyyy
     * @return              Date in milliseconds
     */
    public static long getMilliFromDateString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date tempDate = sdf.parse(dateString);
            return tempDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Function returns a 'h:mm a' date string from time in milliseconds
     * @param milli Time in milliseconds
     * @return      MM/dd/yyyy formatted date string
     */
    public static String getTimeString(long milli) {
        SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf12.format(new Date(milli));
    }

    /**
     * Function to get date in milliseconds from a 12 hr formatted time. For example, 12:15 AM
     * @param timeString    Time as a string formatted h:mm a
     * @return              Time in milliseconds
     */
    public static long getMilliFrom12HrString(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date tempDate = sdf.parse(timeString);
            return tempDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Function to get today's date in milliseconds from a 12 hour formatted time.
     * Assumes the timeString is in format 'h:mm a' and is for today's date
     * @param timeString    Time as a string formatted h:mm a
     * @return              Time in milliseconds
     */
    public static long getTodayMilliFrom12HrString(String timeString) {
        // gets milliseconds past Jan 1, 1970:
        long milli = getMilliFrom12HrString(timeString);
        int year;
        int dayOfYear;

        // get current date and extract year and day of year
        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        // set date to Jan 1, 1970 with correct time of day
        date.setTimeInMillis(milli);

        // set correct year and day of year
        date.set(Calendar.YEAR, year);
        date.set(Calendar.DAY_OF_YEAR, dayOfYear);

        return date.getTimeInMillis();
    }

    /**
     * Function to get a date string formatted as 'h:mm a' for display purposes from a given
     * hour and minute (as is returned by timePickerDialog)
     * @param hourOfDay Hour of the time
     * @param minute    Minute of the time
     * @return          'h:mm a' formatted string containing the time
     */
    public static String to12Hr(int hourOfDay, int minute) {
        String timeString = hourOfDay + ":" + minute;
        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date timeAsDate = sdf24.parse(timeString);
            SimpleDateFormat sdf12 = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return sdf12.format(timeAsDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toMMddyyyy(int year, int monthOfYear, int dayOfMonth) {
        String timeString = monthOfYear + "/" + dayOfMonth + "/" + year;
        return timeString;
    }

    /**
     * Function to get a date string formatted as H:mm a for display purposes from a given
     * hour and minute (ex: soft cap time remaining)
     * @param hour      Hours
     * @param minute    Minutes
     * @return          H:mm formatted string containing the time
     */
    public static String to24HMM(int hour, int minute) {
        String timeString = hour + ":" + minute;
        SimpleDateFormat sdf24 = new SimpleDateFormat("H:mm", Locale.getDefault());
        try {
            Date timeAsDate = sdf24.parse(timeString);
            return sdf24.format(timeAsDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function adds year, month, day of year from date param Calendar to the time Calendar. This
     * function effectively will take one Calendar with correct hour & minute and one with the
     * correct year, month, day of year and return a new Calendar with the correct hour,
     * minute, year, month, day of year.
     * @param time      Calendar with desired hour and minute
     * @param date      Calendar with desired year, month, day of year
     * @return          Calendar with desired hour, minute, year, month, day of year
     */
    public static Calendar addDateToTime(Calendar time, Calendar date) {
        Calendar newCal = time;
        newCal.set(Calendar.YEAR, date.get(Calendar.YEAR));
        newCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
        newCal.set(Calendar.DAY_OF_YEAR, date.get(Calendar.DAY_OF_YEAR));

        return newCal;
    }

    /**
     * Function gets hour and minute information and calculates if that time is in the future on
     * the same day.
     * NOTE:    This function returns false if passed time is exactly the same as the current time
     * @param pHour         Int Hour of past time to check
     * @param pMin          Int Minute of past time to check
     * @param fHour         Int Hour of future time to check
     * @param fMin          Int Minute of future time to check
     * @return              True if past times are before future times
     */
    public static boolean isFutureToday(int pHour, int pMin, int fHour, int fMin) {
        if (fHour > pHour) {
            return true;
        } else if (fHour == pHour && fMin > pMin) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function takes two calendar parameters (past and future) and confirms if the
     * past Calendar date is actually before the future Calendar date
     * @param p     past Calendar date
     * @param f     future Calendar date
     * @return      returns Boolean (true) if passed Calendar date is before future Calendar date
     */
    public static boolean isFuture(Calendar p, Calendar f) {
        // get day of the year for each date
        int pDayOfYear = p.get(Calendar.DAY_OF_YEAR);
        int fDayOfYear = f.get(Calendar.DAY_OF_YEAR);

        if (fDayOfYear > pDayOfYear) {
            return true;
        } else if (fDayOfYear < pDayOfYear) {
            return false;
        } else {
            return Utils.isFutureToday(
                    p.get(Calendar.HOUR_OF_DAY),
                    p.get(Calendar.MINUTE),
                    f.get(Calendar.HOUR_OF_DAY),
                    f.get(Calendar.MINUTE)
            );
        }
    }

    /**
     * Function returns the absolute value of the difference in time between now and passed
     * time in the String format h:mm
     * @param time   Calendar of specific cap time
     * @return       String format of time difference in H:mm format
     */
    public static String timeFromNowHmm(Calendar time) {
        Calendar now = Calendar.getInstance();

        int timeDay = time.get(Calendar.DAY_OF_YEAR);
        int nowDay = now.get(Calendar.DAY_OF_YEAR);
        int timeHour = time.get(Calendar.HOUR_OF_DAY);
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int timeMin = time.get(Calendar.MINUTE);
        int nowMin = now.get(Calendar.MINUTE);

        int dayDiff;
        int hourDiff;
        int minDiff;

        if (time.getTimeInMillis() >= now.getTimeInMillis()) {
            // time is in the future compared to now.
            dayDiff = timeDay - nowDay;
            hourDiff = ((24 * dayDiff) + timeHour) - nowHour;
            if (timeMin >= nowMin) {
                minDiff = timeMin - nowMin;
            } else {
                hourDiff -= 1;
                minDiff = (60 + timeMin) - nowMin;
            }
        } else {
            // now is in the future compared to time.
            dayDiff = nowDay - timeDay;
            hourDiff = ((24 * dayDiff) + nowHour) - timeHour;
            if (nowMin >= timeMin) {
                minDiff = nowMin - timeMin;
            } else {
                hourDiff -= 1;
                minDiff = (60 + nowMin) - timeMin;
            }
        }

        return to24HMM(hourDiff, minDiff);
    }

    /**
     * Function calculates the number of days between Calendars d1 and d2
     * Assumes d1 comes before d2
     * @param d1    Calendar date
     * @param d2    Calendar date
     * @return      integer number of days between d1 and d2
     *              -1 if invalid params
     */
    public static int getDayDifference(Calendar d1, Calendar d2) {
        if (d1.getTimeInMillis() > d2.getTimeInMillis()) return -1;
        else if (d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR)){
            return ( d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR) );
        } else {
            // TODO: set up logic spanning year-ends
            Log.d("Utils","Haven't set up year-splitting logic for getDayDifference");
            return -1;
        }
    }

    /**
     * Function calculates the number of hours between Calendars d1 and d2
     * Assumes d1 comes before d2
     * @param d1    Calendar date
     * @param d2    Calendar date
     * @return      integer number of hours between d1 and d2
     *              -1 if invalid params
     */
    public static int getHourDifference (Calendar d1, Calendar d2) {
        int h1, h2;

        if (d1.getTimeInMillis() > d2.getTimeInMillis()) return -1;
        else {
            h1 = d1.get(Calendar.HOUR_OF_DAY);
            h2 = d2.get(Calendar.HOUR_OF_DAY);

            if (h2 >= h1) { return h2 - h1; }
            else { return (h2 + 24) - h1; }
        }
    }

    /**
     * Function calculates the number of minutes between Calendars d1 and d2
     * Assumes d1 comes before d2
     * @param d1    Calendar date
     * @param d2    Calendar date
     * @return      integer number of minutes between d1 and d2
     *              -1 if invalid params
     */
    public static int getMinuteDifference (Calendar d1, Calendar d2) {
        int m1, m2;

        if (d1.getTimeInMillis() > d2.getTimeInMillis()) return -1;
        else {
            m1 = d1.get(Calendar.MINUTE);
            m2 = d2.get(Calendar.MINUTE);

            if (m2 >= m1) { return m2 - m1; }
            else { return (m2 + 60) - m1; }
        }
    }

    /**
     * Function gets game information from database via a given id and a specified context.
     * @param c         Context to pass to GameDbAdapter
     * @param gameID    ID of game to be returned
     * @return          Game object with given ID
     */
    public static Game getGameDetails(Context c, long gameID) {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(c);
        gameDbAdapter.open();
        Game newGame = gameDbAdapter.getGame(gameID);
        gameDbAdapter.close();
        return newGame;
    }

    /**
     * Function saves game information to database via a given id and a specified context
     * @param ctx         Context to pass to GameDbAdapter
     * @param game         Game object to save to database
     * @return          long ID of game saved
     */
    public static long saveGameDetails(Context ctx, Game game) {
        // store game to database
        GameDbAdapter gameDbAdapter = new GameDbAdapter(ctx);
        gameDbAdapter.open();
        long gameId;
        if (game.getId() > 0) {
            gameId = gameDbAdapter.saveGame(game);
        } else {
            gameId = gameDbAdapter.createGame(game);
        }
        gameDbAdapter.close();

        return gameId;
    }

    /**
     * Function checks if given Calendar time has same hour and minute as passed in variables.
     * @param time      Calendar time that is checked against hour and minute
     * @param hour      int hour from timepicker
     * @param minute    int minute from timepicker
     * @return          returns boolean (true) if time has different hour / minute
     */
    public static boolean timePickerTimeChanged(Calendar time, int hour, int minute) {
        if (time.get(Calendar.HOUR_OF_DAY) != hour) { return true; }
        if (time.get(Calendar.MINUTE) != minute) { return true; }
        return false;
    }

    /**
     * Function checks if given Calendar date has same year, month of year, and day of month
     * as passed in variables
     * @param date          Calendar date that is checked against year and month and day of month
     * @param year          int year from datepicker
     * @param monthOfYear   int month of year from datepicker
     * @param dayOfMonth    int day of month from datepicker
     * @return              returns boolean (true) if time has different year / month / day of month
     */
    public static boolean datePickerDateChanged(Calendar date, int year,
                                                int monthOfYear, int dayOfMonth) {
        if (date.get(Calendar.YEAR) != year) { return true; }
        if (date.get(Calendar.MONTH) != monthOfYear) { return true; }
        if (date.get(Calendar.DAY_OF_MONTH) != dayOfMonth) { return true; }
        return false;
    }

    /**
     * Function deletes a game from the database
     * @param c         Context to pass to GameDbAdapter
     * @param g         Game object to delete
     * @return          ID of the deleted game
     */
    public static long deleteGame(Context c, Game g) {
        GameDbAdapter dbAdapter = new GameDbAdapter(c);
        dbAdapter.open();
        long gameId = dbAdapter.deleteGame(g.getId());
        dbAdapter.close();
        return gameId;
    }

    /*
    Drawable Utils
     */

    public static GradientDrawable createGradientDrawableCircle(
            int size, int color, int strokeSize, int strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setShape(GradientDrawable.OVAL);
        gd.setStroke(strokeSize, strokeColor);
        gd.setSize(size,size);
        return gd;
    }

    /*
    Validation Utils
     */

    /**
     * Validates that a TextView is not empty and throws an error if it is
     * @param text          Text contained within the textView
     * @param textView      TextView to modify with error
     * @param res           Resources from the context
     * @param fieldNameRes  Field name resource to convert to string
     * @return              True if the text is not empty, false otherwise
     */
    public static boolean validateTextNotEmpty(String text, TextView textView,
                                            Resources res, @StringRes int fieldNameRes) {
        // if the text is not empty, return true
        if (!text.isEmpty()) {
            return true;
        }

        String textViewName = res.getString(fieldNameRes);
        String errorText = res.getString(R.string.error_text_empty, textViewName);
        textView.setError(errorText);
        return false;
    }

    /**
     * Validates that fields aren't empty and notifies caller if they are.
     * @param fields    Fields to validate
     * @return          true if fields are populated, false otherwise
     */
    public static boolean validateFieldsNotEmpty(TextView[] fields) {
        for (TextView field : fields) {
            if (field.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Show a dialog that alerts the user that required fields must be populated.
     */
    public static void showValidationFailedDialog(Context ctx) {
        new AlertDialog.Builder(ctx)
                .setMessage(R.string.dialog_validation_failed)
                .setPositiveButton(R.string.dialog_confirm, null)
                .create()
                .show();
    }

    /*
    View Utilities
     */
    public static View createCustomTitle(Activity activity, ViewGroup container,
                                         String text) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_view_tpd_title, container);
        TextView title = (TextView) view.findViewById(R.id.tpdTitle);
        title.setText(text);
        return view;
    }

    /*
    Listener Utilities
     */

    /**
     * Sets a listener for a view to open the context menu of a specified activity
     * @param clickable     View that is enabled for clicking
     * @param contextView   View to show the context menu for
     * @param ctx           Context
     */
    public static void setContextMenuListener(final View clickable,
                                       final View contextView, final Context ctx) {


        clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAndOpenContextMenu(clickable, contextView, ctx);
            }
        });

        /*
        Override the onLongClickListener, such that we don't also open the
        list item's LongClickListener.
        */

        clickable.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerAndOpenContextMenu(clickable, contextView, ctx);
                return true;
            }
        });
    }

    /**
     * Helper function that registers the clickable view for the context menu and opens the
     * context menu
     * @param clickable     View that is enabled for clicking
     * @param contextView   View to show the context menu for
     * @param ctx           Context
     */
    private static void registerAndOpenContextMenu(View clickable, View contextView, Context ctx) {
        Activity activity = (Activity) ctx;
        activity.registerForContextMenu(clickable);
        activity.openContextMenu(contextView);
    }

    /*
    Timer Utilities
     */

    /**
     * Function attempts to convert a long to an integer. Thorws error if unable.
     * @param num
     */
    public static int convertLongToInt(long num) {
        if ( num > (long)Integer.MAX_VALUE ) {
            // x is too big to convert, throw an exception or something useful
            Log.e("Utils", "Game ID is too large to convert to notification id");
            return 0;
        }
        else {
            return (int) num;
        }
    }

    /**
     * Function converts a long game ID to an integer game notification Id based on the
     * notification type
     * @param gameID            long game ID
     * @param notificationType  enum describing the type of game ID needed
     * @return                  (int) (gameID * 100) + 1000 + type
     */
    public static int getGameNotificationID(long gameID,
                                               GameDisplayActivity.GameNotificationType notificationType) {
        /*
        Gives us the space of 1-1000 for other notifications and 100 different game notifications'
        TODO clean this up if we make this into a web app
         */
        int baseID = convertLongToInt(gameID) * 100 + 1000;
        if (baseID == 0) {
            Log.e("Utils","Notification ID cannot be created");
            return 0;
        } else {
            int gameNotId;
            switch (notificationType) {
                case SOFT_CAP:
                    gameNotId = baseID + 1;
                    break;
                case HARD_CAP:
                    gameNotId = baseID + 2;
                    break;
                case HALFTIME:
                    gameNotId = baseID + 3;
                    break;
                case UPDATE:
                    gameNotId = baseID + 4;
                    break;
                default:
                    gameNotId = 0;
                    break;
            }
            return gameNotId;
        }
    }
//
//    public static int getHardCapNotificationID(long gameID,
//                                               GameDisplayActivity.GameNotificationType notificationType) {
//        int baseID = getBaseCapIdFromGameId(gameID);
//        if (baseID == 0) {
//            Log.e("Utils","Hard Cap Notification ID cannot be created");
//            return 0;
//        } else {
//            return baseID + 2;
//        }
//    }
}
