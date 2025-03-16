package uiax.components.calendar;

import uia.core.ui.View;

import java.util.Objects;

import static uiax.components.calendar.XCalendarView.*;

/**
 * The XCalendarFactory is responsible for creating the desired calendar.
 */

public class XCalendarFactory {

    private XCalendarFactory() {
    }

    /**
     * Creates a new {@link XCalendarView} instance with {@link XCalendarView#WEEK} and {@link XCalendarView#MONTHS}.
     *
     * @param view               the backbone view of the calendar
     * @param singleDaySelection true to create a calendar with a single selectable day; false to create
     *                           a calendar with multiple selectable days
     * @return a new {@link XCalendarView} instance
     * @throws NullPointerException if {@code view == null}
     */

    public static XCalendarView create(View view, boolean singleDaySelection) {
        Objects.requireNonNull(view);

        XCalendarView result;
        if (singleDaySelection) {
            result = new XSingleDaySelectionCalendar(view, WEEK, MONTHS);
        } else {
            result = new XRangeDaySelectionCalendar(view, WEEK, MONTHS);
        }

        return result;
    }
}
