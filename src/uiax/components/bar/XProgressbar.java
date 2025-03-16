package uiax.components.bar;

import uia.application.ui.component.utility.ComponentUtility;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.utility.MathUtility;
import uia.core.ui.style.Style;
import uia.core.ui.ViewGroup;
import uia.core.ui.View;

import java.util.Objects;

/**
 * The XProgressbar is a widget used to display the progress of a task.
 * <br>
 * It consists of two elements:
 * <ul>
 *     <li>an external bar that gives the progress bar its shape;</li>
 *     <li>an internal bar that displays the progress.</li>
 * </ul>
 * <br>
 *
 * @apiNote Designed to support rotation
 */

public final class XProgressbar extends WrapperView {
    private final View internalBar;

    private float value;
    private float min;
    private float max;

    public XProgressbar(View view) {
        super(new ComponentGroup(view));
        getStyle()
                .setBackgroundColor(ColorCollection.DARK_GRAY)
                .setGeometry(
                        geometry -> ComponentUtility.buildRect(geometry, getWidth(), getHeight(), 1f),
                        true
                );

        internalBar = new Component("internal_bar_" + getID(), 0.5f, 0.5f, 1f, 1f);
        internalBar.getStyle()
                .setBackgroundColor(ColorCollection.LIME)
                .setGeometry(geometry -> {
                    float xVertex = value - 0.5f;
                    geometry.removeAllVertices().addVertices(
                            -0.5f, -0.5f,
                            xVertex, -0.5f,
                            xVertex, 0.5f,
                            -0.5f, 0.5f
                    );
                }, true);

        ViewGroup.insert(getView(), internalBar);

        setRange(0f, 1f);
        setValue(value);
    }

    /**
     * @return the internal bar {@link Style}
     */

    public Style getInternalBarStyle() {
        return internalBar.getStyle();
    }

    /**
     * Set the progress bar value range.
     *
     * @param min the minimum value
     * @param max the maximum value
     */

    public void setRange(float min, float max) {
        if (min < max) {
            this.min = min;
            this.max = max;
            setValue(value);
        }
    }

    /**
     * Set the progress bar value.
     *
     * @param value the progress bar value between [min, max]
     */

    public void setValue(float value) {
        this.value = MathUtility.constrain(value, min, max);
    }

    /**
     * @return the minimum value
     */

    public float getMin() {
        return min;
    }

    /**
     * @return the maximum value
     */

    public float getMax() {
        return max;
    }

    /**
     * @return the current value
     */

    public float getValue() {
        return value;
    }

    /**
     * Creates a new horizontal progress bar based of the specified View.
     *
     * @param view a not null {@link View}
     * @throws NullPointerException if {@code view == null}
     */

    public static XProgressbar createHorizontal(View view) {
        Objects.requireNonNull(view);
        return new XProgressbar(view);
    }

    /**
     * Creates a new vertical progress bar based of the specified View.
     *
     * @param view a not null {@link View}
     * @throws NullPointerException if {@code view == null}
     */

    public static XProgressbar createVertical(View view) {
        Objects.requireNonNull(view);
        XProgressbar result = new XProgressbar(view);
        result.getStyle().setRotation(-MathUtility.HALF_PI);
        return result;
    }
}
