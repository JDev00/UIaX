package uiax.components.calendar;

import uia.core.ui.View;

import java.util.Objects;

import static uiax.components.calendar.CalendarView.*;

/**
 * CalendarFactory is responsible for creating the desired calendar.
 */

public class CalendarFactory {

    private CalendarFactory() {
    }

    /**
     * Creates a new {@link CalendarView} instance with {@link CalendarView#WEEK} and {@link CalendarView#MONTHS}.
     *
     * @param view               the backbone view of the calendar
     * @param singleDaySelection true to create a calendar with a single selectable day; false to create
     *                           a calendar with multiple selectable days
     * @return a new {@link CalendarView} instance
     * @throws NullPointerException if {@code view == null}
     */

    public static CalendarView create(View view, boolean singleDaySelection) {
        Objects.requireNonNull(view);

        CalendarView result;
        if (singleDaySelection) {
            result = new SingleDaySelectionCalendar(view, WEEK, MONTHS);
        } else {
            result = new RangeDaySelectionCalendar(view, WEEK, MONTHS);
        }

        return result;
    }
}
