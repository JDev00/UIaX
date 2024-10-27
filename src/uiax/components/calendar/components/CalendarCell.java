package uiax.components.calendar.components;

import uia.application.ui.component.text.ComponentText;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.core.ui.style.TextVerticalAlignment;
import uia.application.ui.component.Component;
import uia.core.ui.callbacks.OnMouseEnter;
import uia.core.ui.callbacks.OnMouseExit;
import uia.core.ui.style.Style;
import uia.core.ui.ViewGroup;
import uia.core.ui.ViewText;
import uia.core.ui.View;

/**
 * Subcomponent. CalendarCell is designed to be used as a calendar day or a week day.
 */

public class CalendarCell extends WrapperView {
    private final ViewText viewText;
    private final View viewTask;

    public boolean selected = false;
    public boolean current = false;
    public boolean active = false;
    public boolean hasTask = false;

    public CalendarCell(String id) {
        super(new ComponentGroup(
                new Component(id, 0f, 0f, 0f, 0f)
        ));
        super.getStyle().setBackgroundColor(ColorCollection.TRANSPARENT);
        setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        registerCallback((OnMouseEnter) touches -> active = true);
        registerCallback((OnMouseExit) touches -> active = false);

        // cell text
        viewText = new ComponentText(
                new Component(id + "_text", 0.5f, 0.5f, 1f, 1f)
        );
        viewText.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        viewText.getStyle()
                .setBackgroundColor(ColorCollection.TRANSPARENT)
                .setTextAlignment(TextVerticalAlignment.CENTER);

        // task highlight
        viewTask = new Component(id + "_task", 0.5f, 0.9f, 0.275f, 0.1f);
        viewTask.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        viewTask.getStyle()
                .setBackgroundColor(ColorCollection.WHITE)
                .setMaxHeight(2);

        // adds components to the cell
        ViewGroup.insert(getView(), viewText, viewTask);
    }

    /**
     * Sets the cell text.
     *
     * @param text the cell text
     */

    void setText(String text) {
        viewText.setText(text);
    }

    /**
     * @return the cell text
     */

    public String getText() {
        return viewText.getText();
    }

    /**
     * @return the style of the component used to indicate that the cell is marked for a task
     */

    public Style getTaskStyle() {
        return viewTask.getStyle();
    }

    @Override
    public void update(View parent) {
        super.update(parent);

        viewTask.setVisible(hasTask);

        Style style = getStyle();
        viewText.getStyle()
                .setTextColor(style.getTextColor())
                .setFont(style.getFont());
    }

    /**
     * Creates a new CalendarCell suitable for representing a week day.
     *
     * @param weekDay the day of the week
     * @return a new {@link CalendarCell}
     */

    public static CalendarCell createWeekDay(String weekDay) {
        CalendarCell result = new CalendarCell(weekDay);
        result.setText(weekDay);
        return result;
    }

    /**
     * Creates a new CalendarCell suitable for representing a day.
     *
     * @param day the calendar day
     * @return a new {@link CalendarCell}
     */

    public static CalendarCell createDay(String day) {
        CalendarCell result = new CalendarCell(day);
        result.setText(day);
        return result;
    }
}
