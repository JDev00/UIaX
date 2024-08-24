package uiax.components.calendar.callbacks;

import uia.core.basement.Callback;

/**
 * OnDaySelected is triggered when a day is selected/deselected on the calendar.
 * <br>
 * It provides the selected/deselected day between [1, 31].
 */

public interface OnDaySelected extends Callback<Integer> {
}
