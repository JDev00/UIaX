package uiax.components.calendar;

import uia.core.rendering.geometry.GeometryCollection;
import uia.core.rendering.color.ColorCollection;
import uia.core.ui.style.StyleFunction;
import uia.core.ui.style.Style;
import uia.core.ui.View;

import uiax.components.calendar.components.XAbstractCalendarView;
import uiax.components.calendar.callbacks.OnSelectionCleared;
import uiax.components.calendar.callbacks.OnDaySelected;

/**
 * Gregorian calendar with day range selection.
 */

public class XRangeDaySelectionCalendar extends XAbstractCalendarView {
    private final StyleFunction deselectedCellPaint = style -> style
            .setBackgroundColor(ColorCollection.TRANSPARENT)
            .setTextColor(ColorCollection.WHITE);
    private final StyleFunction selectedCellPaint = style -> style
            .setBackgroundColor(ColorCollection.ROYAL_BLUE)
            .setTextColor(ColorCollection.WHITE);

    private final int[] range = {-1, -1};

    public XRangeDaySelectionCalendar(View view, String[] weekdays, String[] months) {
        super(view, weekdays, months);

        // updates day selection
        registerCallback((OnDaySelected) day -> {
            dayRangeSelection(day);
            updateDayStyle();
        });
    }

    @Override
    public void selectDay(int day) {
        validateDay(day, 1, 31);
        notifyCallbacks(OnDaySelected.class, day);
    }

    @Override
    public void deselectDay(int day) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearDaySelection() {
        deselectsAllDays();
        updateDayStyle();
        notifyCallbacks(OnSelectionCleared.class, this);
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

    /**
     * Helper function. Deselects all days.
     */

    private void deselectsAllDays() {
        range[0] = range[1] = -1;
        for (int i = 1; i <= 31; i++) {
            setDayCellGeometry(i, GeometryCollection::rect, false);
            markDayAsSelected(i, false);
        }
    }

    /**
     * Helper function. Selects a range of days.
     *
     * @param day the selected day; with -1 the range is cleared
     */

    private void dayRangeSelection(int day) {
        // updates range selection
        if (range[0] == -1) {
            range[0] = day;
        } else if (range[1] == -1) {
            range[1] = day;
        } else {
            range[0] = day;
            range[1] = -1;
        }

        // clears the selection range
        for (int i = 1; i <= 31; i++) {
            markDayAsSelected(i, false);
        }

        // update cell selection
        int minValue = Math.min(range[0], range[1]);
        int maxValue = Math.max(range[0], range[1]);
        for (int i = 1; i <= 31; i++) {
            markDayAsSelected(i, i == day || range[1] >= 0 && i >= minValue && i <= maxValue);
            setDayCellGeometry(i, GeometryCollection::rect, false);
        }

        // update cell geometry
        if (range[1] != -1) {
            setDayCellGeometry(
                    minValue,
                    geometry -> GeometryCollection.rect(
                            geometry,
                            GeometryCollection.STD_VERT,
                            1f, 0f, 0f, 1f,
                            getDayCelWidth() / getDayCelHeight()
                    ),
                    true
            );

            setDayCellGeometry(
                    maxValue,
                    geometry -> GeometryCollection.rect(
                            geometry,
                            GeometryCollection.STD_VERT,
                            0f, 1f, 1f, 0f,
                            getDayCelWidth() / getDayCelHeight()
                    ),
                    true
            );

            if (range[0] == range[1]) {
                setDayCellGeometry(
                        range[0],
                        geometry -> GeometryCollection.rect(
                                geometry,
                                GeometryCollection.STD_VERT,
                                1f,
                                getDayCelWidth() / getDayCelHeight()
                        ),
                        true);
            }
        }
    }
}
