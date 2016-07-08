package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

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
     * Function returns a h:mm a date string from time in milliseconds
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
     * Function to get a date string formatted as h:mm a for display purposes from a given
     * hour and minute (as is returned by timePickerDialog)
     * @param hourOfDay Hour of the time
     * @param minute    Minute of the time
     * @return          h:mm a formatted string containing the time
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
     * Function gets hour and minute information and calculates if that time is in the future on
     * the same day.
     * NOTE:    This function returns false if passed time is exactly the same as the current time
     * @param fHour         Hour of the time to check
     * @param fMin          Minute of the time to check
     * @param date   Creation date of game (in milliseconds)
     * @return              True if passed time is in the future
     */
    public static boolean isFutureToday(int fHour, int fMin, long date) {
        // create Calendar and set time to date instance
        Calendar cTime = Calendar.getInstance();
        cTime.setTimeInMillis(date);

        // get hour of minute of time created
        int cHour = cTime.get(Calendar.HOUR_OF_DAY);
        int cMin = cTime.get(Calendar.MINUTE);

        if (fHour > cHour) {
            return true;
        } else if (fHour == cHour && fMin > cMin) {
            return true;
        } else {
            return false;
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
     * @param c         Context to pass to GameDbAdapter
     * @param g         Game object to save to database
     * @return          long ID of game saved
     */
    public static long saveGameDetails(Context c, Game g) {
        GameDbAdapter gameDbAdapter = new GameDbAdapter(c);
        gameDbAdapter.open();
        long gameID = gameDbAdapter.saveGame(g);
        gameDbAdapter.close();
        return gameID;
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
}
