package uiax.components.calendar.components;

import uia.application.ui.component.utility.ComponentUtility;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.core.rendering.geometry.Geometry;
import uia.core.rendering.color.Color;
import uia.core.ui.callbacks.OnClick;
import uia.core.rendering.font.Font;
import uia.core.ui.style.Style;
import uia.core.ui.ViewGroup;
import uia.core.ui.View;

import uiax.components.calendar.callbacks.OnDateChanged;
import uiax.components.calendar.callbacks.OnDaySelected;
import uiax.components.calendar.callbacks.OnDateSet;
import uiax.components.calendar.CalendarUtility;
import uiax.components.calendar.CalendarView;
import uiax.colors.DarculaColorCollection;

import java.util.function.Consumer;
import java.util.GregorianCalendar;
import java.util.stream.IntStream;
import java.util.Calendar;
import java.util.Objects;

/**
 * Abstract representation of the Gregorian calendar.
 * <br>
 * It provides all the calendar basement operations expect day selection.
 */

public abstract class AbstractCalendarView extends WrapperView implements CalendarView {
    public static final String STYLE_DAY_TASK_COLOR_MARKER = "dayMarkerTaskColor";

    private final Calendar calendar = GregorianCalendar.getInstance();

    private final CalendarCell[] cells = new CalendarCell[38];
    private final CalendarHeader header;
    private final View overlayCell;
    private Color currentDayColor = ColorCollection.PINK;
    private final String[] months;
    private final Font font;

    private int days;
    private int offset;
    private final int[] currentDate = {1, 1, 2024};
    private final int[] setDate = {1, 1, 2024};

    protected AbstractCalendarView(View view, String[] weekdays, String[] months) {
        super(new ComponentGroup(view));
        getStyle().setBackgroundColor(DarculaColorCollection.DARK_GRAY);

        this.months = months;

        font = Font.createDesktopFont(Font.FontStyle.ITALIC);

        Consumer<Boolean> shiftDate = isNextMonth -> {
            int monthOffset = Boolean.TRUE.equals(isNextMonth) ? 1 : -1;
            int month = currentDate[1] + monthOffset;
            int year = currentDate[2];
            if (month > 12) {
                month = 1;
                year++;
            } else if (month < 1) {
                month = 12;
                year--;
            }
            changeDate(month, year);
        };
        header = new CalendarHeader(
                new Component("calendar_header_" + getID(), 0.5f, 0.15f, 0.75f, 0.2f),
                font, shiftDate
        );

        overlayCell = new Component("CALENDAR_OVERLAY_" + getID(), 0f, 0f, 0f, 0f);
        overlayCell.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        overlayCell.setVisible(false);
        overlayCell.getStyle()
                .setBackgroundColor(Color.createColor(150, 150, 150, 100))
                .setGeometry(
                        geometry -> ComponentUtility.buildRect(geometry, overlayCell.getWidth(), overlayCell.getHeight(), 1f),
                        true
                );

        for (int i = 0; i < 7; i++) {
            cells[i] = CalendarCell.createWeekDay(weekdays[i]);
            cells[i].getStyle().setTextColor(ColorCollection.SILVER);
        }

        for (int i = 0; i < 31; i++) {
            CalendarCell cell = CalendarCell.createDay(String.valueOf(i + 1));
            cell.getStyle().setTextColor(ColorCollection.WHITE);
            cell.registerCallback((OnClick) touches -> {
                int day = Integer.parseInt(cell.getText());
                notifyCallbacks(OnDaySelected.class, day);
            });
            cells[i + 7] = cell;
        }

        for (CalendarCell cell : cells) {
            cell.getStyle()
                    .setBackgroundColor(ColorCollection.TRANSPARENT)
                    .setFont(font);
        }

        ViewGroup group = getView();
        ViewGroup.insert(group, cells);
        ViewGroup.insert(group, header, overlayCell);

        // sets the current date
        int[] nowDate = CalendarUtility.getDate();
        setDate(nowDate[0], nowDate[1], nowDate[2]);
    }

    /**
     * Helper function. Throws an error if the day is out of range.
     *
     * @param day the day to check
     * @throws IndexOutOfBoundsException if {@code day < 1 || day > days of the month}
     */

    private void validateDay(int day) {
        int month = setDate[1];
        int year = setDate[2];
        int daysOfTheMonth = CalendarUtility.getDaysOfTheMonth(month, year);
        if (day < 1 || day > daysOfTheMonth) {
            throw new IllegalArgumentException("the day must be between [1, " + daysOfTheMonth + "]");
        }
    }

    /**
     * Marks the specified as selected.
     *
     * @param day      the day to be marked between [1, 31]
     * @param selected true to mark the day as selected
     * @throws NullPointerException if {@code day < 1 || day > 31}
     */

    protected void markDayAsSelected(int day, boolean selected) {
        validateDay(day);
        cells[7 + day - 1].selected = selected;
    }

    /**
     * @return true if the specified day is marked as selected
     * @throws NullPointerException if {@code day < 1 || day > 31}
     */

    protected boolean isDayMarkedAsSelected(int day) {
        validateDay(day);
        return cells[7 + day - 1].selected;
    }

    /**
     * Sets the day geometry.
     */

    protected void setDayCellGeometry(int day,
                                      Consumer<Geometry> builder, boolean inTimeBuilding) {
        cells[7 + day - 1].getStyle().setGeometry(builder, inTimeBuilding);
    }

    /**
     * @return the Style associated to the specified day
     */

    protected Style getDayCellStyle(int day) {
        return cells[7 + day - 1].getStyle();
    }

    protected float getDayCelWidth() {
        CalendarCell cell = cells[7];
        return cell.getWidth();
    }

    protected float getDayCelHeight() {
        CalendarCell cell = cells[7];
        return cell.getHeight();
    }

    /**
     * Sets the color used to paint the current day.
     *
     * @param color the current day color
     * @throws NullPointerException if {@code color == null}
     */

    public void setCurrentDayColor(Color color) {
        Objects.requireNonNull(color);
        currentDayColor = color;
    }

    /**
     * Helper function. Marks the current date cell.
     *
     * @param day the day between [1, 31]
     */

    private void markCurrentDateCell(int day) {
        for (CalendarCell cell : cells) {
            cell.current = false;
        }
        // update current cell
        cells[7 + day - 1].current = true;
    }

    /**
     * Helper function. Updates the calendar date.
     */

    private void updateDate(int day, int month, int year) {
        // updates internal calendar
        calendar.clear();
        calendar.set(year, month - 1, 1);

        // update data
        days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        offset = CalendarUtility.getDay(calendar.get(Calendar.DAY_OF_WEEK));

        // deselects the previous current day
        if (currentDate[0] > 0) {
            Color dayTextColor = cells[7 + currentDate[0] % 31].getStyle().getTextColor();
            cells[7 + currentDate[0] - 1].getStyle().setTextColor(dayTextColor);

            // makes all the cells non-current
            for (int i = 0; i < 31; i++) {
                cells[7 + i].current = false;
            }
        }

        // update current date
        currentDate[0] = day;
        currentDate[1] = month;
        currentDate[2] = year;

        // marks the current day
        if (currentDate[0] > 0) {
            markCurrentDateCell(currentDate[0]);
        }

        // update month and year
        header.setMonthAndYear(months[month - 1], year);
    }

    @Override
    public void setDate(int day, int month, int year) {
        validateDay(day);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("the month must be between [1, 12]");
        }

        // sets the calendar date
        setDate[0] = day;
        setDate[1] = month;
        setDate[2] = year;

        updateDate(day, month, year);

        // notifies clients
        int[] date = getDate();
        notifyCallbacks(OnDateSet.class, date);
    }

    @Override
    public int[] getSetDate() {
        return new int[]{setDate[0], setDate[1], setDate[2]};
    }

    @Override
    public void changeDate(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("the month must be between [1, 12]");
        }

        int day = 0;
        int[] dateSet = getSetDate();
        if (dateSet[1] == month && dateSet[2] == year) {
            day = dateSet[0];
        }

        updateDate(day, month, year);

        // notifies clients
        int[] date = getDate();
        notifyCallbacks(OnDateChanged.class, date);
    }

    @Override
    public int[] getDate() {
        return new int[]{currentDate[0], currentDate[1], currentDate[2]};
    }

    @Override
    public int[] getSelectedDays() {
        return IntStream.range(1, 32)
                .filter(index -> cells[7 + index - 1].selected)
                .toArray();
    }

    @Override
    public void markDayWithTask(int day, boolean hasTask) {
        validateDay(day);
        cells[7 + day - 1].hasTask = hasTask;
    }

    @Override
    public boolean hasTask(int day) {
        validateDay(day);
        return cells[7 + day - 1].hasTask;
    }

    /**
     * Helper function. Adjusts the font size according to the calendar View dimension.
     */

    private void updateFontSize(float width, float height) {
        int fontSize = (int) Math.min(
                Math.min(0.75f * width * getWidth(), 0.75f * height * getHeight()),
                Font.DESKTOP_SIZE
        );
        if (fontSize != (int) font.getSize()) {
            font.setSize(fontSize);
        }
    }

    /**
     * Helper function. Updates the day cells.
     */

    private void updateDayCells(float posY, float[] cellDim) {
        int inactiveCells = 0;
        float gap = (0.95f - posY) / 5f;
        for (int i = 0; i < 31; i++) {
            float px = 0.15f + cellDim[0] * ((i + offset) % 7);
            float py = posY + gap * ((i + offset) / 7);

            CalendarCell cell = cells[7 + i];
            cell.getStyle().setPosition(px, py);
            cell.setVisible(i < days);

            if (cell.active) {
                overlayCell.getStyle().setPosition(px, py);
                overlayCell.setVisible(true);
                overlayCell.update(this);
            } else {
                inactiveCells++;
            }
        }

        if (inactiveCells == 31) {
            overlayCell.setVisible(false);
        }
    }

    /**
     * Helper function. Colors the marker task of each day.
     */

    private void colorDayMarkerTask() {
        try {
            Color dayMarkerTaskColor = getStyle().getAttribute(STYLE_DAY_TASK_COLOR_MARKER);
            for (int i = 7; i < cells.length; i++) {
                cells[i].getTaskStyle().setBackgroundColor(dayMarkerTaskColor);
            }
        } catch (Exception ignored) {
            // ignored
        }
    }

    @Override
    public void update(View container) {
        super.update(container);

        if (isVisible()) {
            colorDayMarkerTask();

            // highlights the current day
            if (currentDate[0] > 0) {
                cells[7 + currentDate[0] - 1].getStyle().setTextColor(currentDayColor);
            }

            float[] cellDim = {0.7f / 6f, 0.08f};

            updateFontSize(cellDim[0], cellDim[1]);

            overlayCell.getStyle().setDimension(cellDim[0], cellDim[1]);

            float weekCellPosY = 1f / 3f;
            for (int i = 0; i < 7; i++) {
                cells[i].getStyle().setPosition(0.15f + cellDim[0] * i, weekCellPosY);
            }

            float dayCellPosY = weekCellPosY + cellDim[0];
            updateDayCells(dayCellPosY, cellDim);

            for (CalendarCell cell : cells) {
                cell.getStyle().setDimension(cellDim[0], cellDim[1]);
                cell.update(this);
            }
        }
    }
}
