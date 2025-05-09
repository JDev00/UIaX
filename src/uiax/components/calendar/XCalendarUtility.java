package uiax.components.calendar;

import java.util.Calendar;

/**
 * The XCalendarUtility contains functions to manipulate dates.
 */

public final class XCalendarUtility {

    private XCalendarUtility() {
    }

    /**
     * @return the current date as an array of three elements:
     * <ul>
     *     <li>the day between [1,31];</li>
     *     <li>the month between [1,12];</li>
     *     <li>the year.</li>
     * </ul>
     */

    public static int[] getDate() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        };
    }

    /**
     * Given an integer, returns the day of the week, where 0 is monday and 6 is sunday.
     *
     * @param day an integer
     * @return the day of the week otherwise -1
     */

    public static int getDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return -1;
        }
    }

    /**
     * @param month the month between [1, 12]
     * @param year  the year
     * @return the days of the given month or -1 if the month is not valid
     */

    public static int getDaysOfTheMonth(int month, int year) {
        switch (month) {
            case 1:  // january
            case 3:  // march
            case 5:  // may
            case 7:  // july
            case 8:  // august
            case 10: // september
            case 12: // december
                return 31;
            case 2:
                return year % 4 == 0 ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return -1;
        }
    }

    /**
     * Checks if the given string is equal to the given value.
     *
     * @param in    a not null String to compare to
     * @param value a value to compare to
     */

    private static boolean equals(String in, int value) {
        return in != null && Integer.parseInt(in) == value;
    }

    /**
     * Check if the given date contains the given day.
     *
     * @param date a not null date to control
     * @param day  the day to look for
     * @return true if the date contains the given day
     */

    public static boolean isDay(String date, int day) {
        return date != null && equals(date.substring(0, (date.charAt(1) == '/') ? 1 : 2), day);
    }

    /**
     * Checks if the given date contains the specified month.
     *
     * @param date  a not null date to control
     * @param month the month to look for
     * @return true if the date contains the given month
     */

    public static boolean isMonth(String date, int month) {
        if (date != null) {
            int i = date.indexOf("/") + 1;
            return equals(date.substring(i, i + (date.charAt(i + 1) == '/' ? 1 : 2)), month);
        }
        return false;
    }

    /**
     * Checks if the given date contains the specified year.
     *
     * @param date a not null date to control
     * @param year the year to look for
     * @return true if the date contains the given year
     */

    public static boolean isYear(String date, int year) {
        return date != null && equals(date.substring(date.lastIndexOf("/") + 1), year);
    }
}
