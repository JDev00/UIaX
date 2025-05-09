package uiax.components.button;

import uia.application.ui.component.utility.ComponentUtility;
import uia.application.ui.component.text.ComponentText;
import uia.core.rendering.geometry.GeometryCollection;
import uia.core.ui.style.TextHorizontalAlignment;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.core.ui.style.TextVerticalAlignment;
import uia.application.ui.component.Component;
import uia.core.ui.style.Style;
import uia.utility.MathUtility;
import uia.core.ui.ViewGroup;
import uia.core.ui.ViewText;
import uia.core.ui.View;

/**
 * The XButtonFilled is a button with a text and an icon on
 * the right or on the left.
 */

public final class XButtonFilled extends WrapperView {
    private final ViewText viewText;
    private final View icon;

    public XButtonFilled(View view, boolean right) {
        super(new ComponentGroup(view));
        getStyle().setGeometry(
                geometry -> ComponentUtility.buildRect(geometry, getWidth(), getHeight(), 1f),
                true
        );

        viewText = new ComponentText(
                new Component("BUTTON_FILLED_TEXT_" + getID(), 0.5f + (right ? -0.05f : 0.05f), 0.5f,
                        0.5f, 1f)
        );
        viewText.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        viewText.getStyle()
                .setTextAlignment(right ? TextHorizontalAlignment.LEFT : TextHorizontalAlignment.RIGHT)
                .setBackgroundColor(ColorCollection.TRANSPARENT)
                .setTextAlignment(TextVerticalAlignment.CENTER);

        icon = new Component("BUTTON_FILLED_ICON_" + getID(), right ? 0.875f : 0.125f, 0.5f, 0.125f, 0.4f)
                .setExpanseLimit(1.15f, 1.15f);
        icon.setColliderPolicy(ColliderPolicy.AABB);
        icon.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        icon.getStyle()
                .setGeometry(GeometryCollection::arrow, false)
                .setBackgroundColor(ColorCollection.BLACK)
                .setRotation(right ? 0f : MathUtility.PI);

        ViewGroup.insert(getView(), viewText, icon);
    }

    /**
     * Sets the button text.
     *
     * @param text a String to display; it could be null
     */

    public void setText(String text) {
        viewText.setText(text);
    }

    @Override
    public void update(View parent) {
        super.update(parent);

        Style containerStyle = getStyle();
        Style viewTextStyle = viewText.getStyle();
        viewTextStyle
                .setTextColor(containerStyle.getTextColor())
                .setFont(containerStyle.getFont());
    }

    /**
     * @return the {@link View} used to represent the icon
     */

    public View getIcon() {
        return icon;
    }
}
