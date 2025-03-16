package uiax.components.calendar;

import uia.application.ui.component.utility.ComponentUtility;
import uia.core.rendering.geometry.GeometryCollection;
import uia.core.rendering.color.ColorCollection;
import uia.core.ui.style.StyleFunction;
import uia.core.ui.style.Style;
import uia.core.ui.View;

import uiax.components.calendar.callbacks.OnSelectionCleared;
import uiax.components.calendar.components.XAbstractCalendarView;
import uiax.components.calendar.callbacks.OnDaySelected;

/**
 * Gregorian calendar with single day selection.
 */

public class XSingleDaySelectionCalendar extends XAbstractCalendarView {
    private final StyleFunction deselectedCellPaint = style -> style
            .setBackgroundColor(ColorCollection.TRANSPARENT)
            .setTextColor(ColorCollection.WHITE);
    private final StyleFunction selectedCellPaint = style -> style
            .setBackgroundColor(ColorCollection.ROYAL_BLUE)
            .setTextColor(ColorCollection.WHITE);

    public XSingleDaySelectionCalendar(View view, String[] weekdays, String[] months) {
        super(view, weekdays, months);

        // updates day selection
        registerCallback((OnDaySelected) day -> {
            boolean isDayAlreadySelected = isDayMarkedAsSelected(day);
            deselectAllDays();
            if (!isDayAlreadySelected) {
                _selectDay(day);
            }
            updateDayStyle();
        });
    }

    /**
     * Helper function. Deselects all days.
     */

    private void deselectAllDays() {
        for (int i = 1; i <= 31; i++) {
            setDayCellGeometry(i, GeometryCollection::rect, false);
            markDayAsSelected(i, false);
        }
    }

    /**
     * Helper function. Selects the specified day.
     */

    private void _selectDay(int day) {
        markDayAsSelected(day, true);
        setDayCellGeometry(day,
                geometry -> ComponentUtility.buildRect(geometry, getDayCelWidth(), getDayCelHeight(), 1f),
                true);
    }

    /**
     * Helper function. Updates the day cells selection color.
     */

    private void updateDayStyle() {
        for (int i = 1; i <= 31; i++) {
            Style cellStyle = getDayCellStyle(i);
            if (isDayMarkedAsSelected(i)) {
                cellStyle.applyStyleFunction(selectedCellPaint);
            } else {
                cellStyle.applyStyleFunction(deselectedCellPaint);
            }
        }
    }

    @Override
    public void selectDay(int day) {
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("the day must be between [1, 31]. " + day + " provided");
        }

        notifyCallbacks(OnDaySelected.class, day);
    }

    @Override
    public void deselectDay(int day) {
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("the day must be between [1, 31]. " + day + " provided");
        }

        if (isDayMarkedAsSelected(day)) {
            notifyCallbacks(OnDaySelected.class, day);
        }
    }

    @Override
    public void clearDaySelection() {
        deselectAllDays();
        updateDayStyle();
        notifyCallbacks(OnSelectionCleared.class, this);
    }
}
