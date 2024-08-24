package uiax.example;

import uia.application.ui.component.WrapperView;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.core.context.Context;
import uia.core.ui.ViewGroup;

import uiax.components.calendar.SingleDaySelectionCalendar;
import uiax.components.calendar.CalendarFactory;
import uiax.components.calendar.CalendarView;

/**
 * Demonstrative example. Creates and displays a calendar.
 */

public class CalendarExample extends WrapperView {

    public CalendarExample() {
        super(new ComponentGroup(
                new Component("calendar_example", 0.5f, 0.5f, 1f, 1f)
        ));

        // creates the calendar
        CalendarView calendar = CalendarFactory.create(
                SingleDaySelectionCalendar.class,
                new Component("calendar", 0.5f, 0.5f, 0.5f, 0.5f)
        );

        // displays the calendar
        ViewGroup group = getView();
        ViewGroup.insert(group, calendar);
    }

    public static void main(String[] args) {
        Context context = Utility.startApplication();
        context.setView(new CalendarExample());
    }
}
