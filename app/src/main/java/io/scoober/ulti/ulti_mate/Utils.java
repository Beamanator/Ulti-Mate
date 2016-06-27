package io.scoober.ulti.ulti_mate;

import java.text.SimpleDateFormat;
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
}
