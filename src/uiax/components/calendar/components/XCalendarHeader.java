package uiax.components.calendar.components;

import uia.application.ui.component.text.ComponentText;
import uia.core.rendering.geometry.GeometryCollection;
import uia.core.ui.style.TextHorizontalAlignment;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.component.WrapperView;
import uia.core.ui.style.TextVerticalAlignment;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.core.ui.callbacks.OnClick;
import uia.core.basement.Collidable;
import uia.core.rendering.font.Font;
import uia.utility.MathUtility;
import uia.core.ui.style.Style;
import uia.core.ui.ViewGroup;
import uia.core.ui.ViewText;
import uia.core.ui.View;

import java.util.function.Consumer;

/**
 * Calendar subcomponent.
 */

public class XCalendarHeader extends WrapperView {

    public XCalendarHeader(View view, Font font, Consumer<Boolean> onNextMonth) {
        super(new ComponentGroup(view));
        // group style
        Style groupStyle = getStyle();
        groupStyle
                .setBackgroundColor(ColorCollection.TRANSPARENT)
                .setTextColor(ColorCollection.WHITE)
                .setFont(font);

        String id = view.getID();

        // calendar month
        ViewText headerText = new ComponentText(
                new Component(id + "_month", 0.35f, 0.5f, 0.7f, 1f)
        );
        headerText.getStyle()
                .setBackgroundColor(ColorCollection.TRANSPARENT)
                .setTextAlignment(TextVerticalAlignment.CENTER)
                .setTextAlignment(TextHorizontalAlignment.LEFT)
                .setTextColor(groupStyle.getTextColor())
                .setFont(font);

        // calendar header left arrow
        View leftArrow = new Component(id + "_left_arrow", 0.75f, 0.5f, 0.05f, 0.4f)
                .setExpanseLimit(1.2f, 1.2f);
        leftArrow.registerCallback((OnClick) touches -> onNextMonth.accept(false));
        leftArrow.setColliderPolicy(Collidable.ColliderPolicy.AABB);
        leftArrow.getStyle()
                .setGeometry(GeometryCollection::arrow, false)
                .setTextColor(groupStyle.getBackgroundColor())
                .setRotation(MathUtility.PI);

        // calendar header right arrow
        View rightArrow = new Component(id + "_right_arrow", 0.965f, 0.5f, 0.05f, 0.4f)
                .setExpanseLimit(1.2f, 1.2f);
        rightArrow.registerCallback((OnClick) touches -> onNextMonth.accept(true));
        rightArrow.setColliderPolicy(Collidable.ColliderPolicy.AABB);
        rightArrow.getStyle()
                .setTextColor(groupStyle.getBackgroundColor())
                .setGeometry(GeometryCollection::arrow, false);

        // populates header
        ViewGroup.insert(getView(), headerText, leftArrow, rightArrow);
    }

    /**
     * Sets the month and year.
     *
     * @param month the month
     * @param year  the year
     */

    public void setMonthAndYear(String month, int year) {
        ViewGroup group = getView();

        String monthViewID = getID() + "_month";
        ViewText monthView = (ViewText) group.get(monthViewID);
        monthView.setText(month + " " + year);
    }
}
