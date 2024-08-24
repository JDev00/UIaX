package uiax.components.calendar.callbacks;

import uia.core.basement.Callback;

/**
 * OnDateChanged is triggered when the calendar month or year is changed.
 * <br>
 * It provides the current date as an array consisting of: day, month and year.
 */

public interface OnDateChanged extends Callback<int[]> {
}
