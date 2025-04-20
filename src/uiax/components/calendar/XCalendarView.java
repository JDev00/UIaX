package uiax.components.calendar;

import uia.core.ui.View;

/**
 * XCalendarView ADT.
 * <br>
 * It defines the calendar widget functionalities.
 */

public interface XCalendarView extends View {
    String[] WEEK = {
            "M", "T", "W", "T", "F", "S", "S"
    };
    String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    /**
     * Sets the calendar date.
     *
     * @param day   the calendar day between [1, days of the month]
     * @param month the calendar month between [1, 12]
     * @param year  the calendar year
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                      <li>{@code day < 1 || day > days of the month}</li>
     *                                      <li>{@code month < 1 || month > 12}</li>
     *                                  </ul>
     */

    void setDate(int day, int month, int year);

    /**
     * @return the calendar set date as an array of three elements:
     * <ul>
     *     <li>the day between [1, days of the month];</li>
     *     <li>the month between [1, 12];</li>
     *     <li>the year.</li>
     * </ul>
     */

    int[] getSetDate();

    /**
     * Changes the calendar date but keeps the set date.
     *
     * @param month the new calendar month between [1, 12]
     * @param year  the new calendar year
     * @throws IllegalArgumentException if {@code month < 1 || month > 12}
     */

    void changeDate(int month, int year);

    /**
     * @return the current calendar date. It could be different from {@link #getSetDate()}.
     */

    int[] getDate();

    // day selection

    /**
     * Selects the specified day.
     *
     * @param day the day to be selected
     * @throws IllegalArgumentException if {@code day < 1 || day > days of the month}
     */

    void selectDay(int day);

    /**
     * Deselects the specified day or all days.
     *
     * @param day the day to be deselected; if -1 is given, all the selected days will be deselected
     * @throws IllegalArgumentException      if {@code day < 1 || day > days of the month}
     * @throws UnsupportedOperationException if this method is not supported on the specific implementation
     */

    void deselectDay(int day);

    /**
     * Deselects all the selected days.
     */

    void clearDaySelection();

    /**
     * @return the selected days
     */

    int[] getSelectedDays();

    // day task

    /**
     * Marks the specified day to have or not to have a task.
     *
     * @param day     the day between [1, days of the month] to be marked or unmarked
     * @param hasTask true to mark the day; false to leave the mark to it
     * @throws IllegalArgumentException if {@code day < 1 || day > days of the month}
     */

    void markDayWithTask(int day, boolean hasTask);

    /**
     * Checks if the given day is marked as consequence of a task.
     *
     * @param day the day between [1, days of the month] to check the mark for
     * @return true if the day is marked; false otherwise
     * @throws IllegalArgumentException if {@code day < 1 || day > days of the month}
     */

    boolean hasTask(int day);
}
