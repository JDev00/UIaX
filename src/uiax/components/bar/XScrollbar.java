package uiax.components.bar;

import uia.application.message.systemessages.ScreenTouchMessage;
import uia.application.message.messagingsystem.MessageLocker;
import uia.application.ui.component.utility.ComponentUtility;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.core.ui.callbacks.OnMouseHover;
import uia.core.ui.primitives.ScreenTouch;
import uia.core.ui.callbacks.OnMouseExit;
import uia.core.ui.callbacks.OnClick;
import uia.utility.MathUtility;
import uia.core.ui.style.Style;
import uia.core.ui.ViewGroup;
import uia.core.ui.View;

/**
 * The XScrollbar represents a scrollbar component. It consists of two elements:
 * <ul>
 *     <li>an external bar that is accessed and modified using the standard interface;</li>
 *     <li>an internal bar that represents the current scroll value.</li>
 * </ul>
 * The internal bar can't be accessed directly.
 * <br>
 * By design, XScrollbar supports dragging and moving the internal bar. The operation you have to
 * implement on your own is the scrolling caused by a mouse wheeling event.
 * <br>
 * The scroll value (accessed with {@link #getValue()}) is bounded between [0, max]. The maximum scroll value is set
 * with {@link #setMaxValue(float)}.
 *
 * @apiNote XScrollbar doesn't fully support rotation, so rotate it carefully.
 */

public final class XScrollbar extends WrapperView {
    private final MessageLocker messageLocker = MessageLocker.getInstance();

    private final View internalBar;
    private float val;
    private float max = 1f;
    private float barDragOffset;

    private final boolean vertical;
    private boolean locked = false;
    private boolean updateInternalBar = false;

    public XScrollbar(View view, boolean vertical) {
        super(new ComponentGroup(view));

        this.vertical = vertical;

        getStyle()
                .setBackgroundColor(ColorCollection.DARK_GRAY)
                .setGeometry(
                        geometry -> ComponentUtility.buildRect(geometry, getWidth(), getHeight(), 1f),
                        true
                );
        registerCallback((OnClick) touches -> {
            ScreenTouch touch = touches[0];
            updateScroll(touch.getX(), touch.getY());
        });
        registerCallback((OnMouseHover) touches -> {
            ScreenTouch touch = touches[0];
            if (touch.getAction().equals(ScreenTouch.Action.DRAGGED)) {
                if (requestLock()) {
                    barDragOffset = getBarDragOffset(touch.getX(), touch.getY());
                }
                updateScroll(touch.getX() - barDragOffset, touch.getY() - barDragOffset);
            }
            if (touch.getAction().equals(ScreenTouch.Action.RELEASED) && releaseLock()) {
                barDragOffset = 0f;
            }
        });
        registerCallback((OnMouseExit) o -> {
            if (releaseLock()) {
                barDragOffset = 0f;
            }
        });

        internalBar = vertical ? createVerticalBar() : createHorizontalBar();
        internalBar.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        internalBar.getStyle()
                .setBackgroundColor(ColorCollection.LIGHT_GRAY)
                .setGeometry(geometry -> ComponentUtility.buildRect(
                        geometry,
                        internalBar.getWidth(),
                        internalBar.getHeight(),
                        1f), true
                );

        ViewGroup group = getView();
        group.setClip(false);
        ViewGroup.insert(group, internalBar);
    }

    /**
     * Requests to lock the touch messages.
     */

    private boolean requestLock() {
        boolean result = false;
        if (!locked) {
            result = true;
            locked = true;
            messageLocker.requestLockOn(getID(), ScreenTouchMessage.class);
        }
        return result;
    }

    /**
     * Releases the lock on the touch messages.
     */

    private boolean releaseLock() {
        boolean result = false;
        if (locked) {
            result = true;
            locked = false;
            messageLocker.releaseLockOn(ScreenTouchMessage.class);
        }
        return result;
    }

    /**
     * Helper function.
     *
     * @param x the screen touch position on the x-axis
     * @return the bar drag offset on the x-axis
     */

    private float getBarDragOffsetX(float x) {
        float[] bounds = internalBar.getBounds();
        float xOff = bounds[0] - getBounds()[0];
        return MathUtility.constrain(Math.max(0f, x - xOff), 0f, bounds[2]);
    }

    /**
     * Helper function.
     *
     * @param y the screen touch position on the y-axis
     * @return the bar drag offset on the y-axis
     */

    private float getBarDragOffsetY(float y) {
        float[] bounds = internalBar.getBounds();
        float yOff = bounds[1] - getBounds()[1];
        return MathUtility.constrain(Math.max(0f, y - yOff), 0f, bounds[3]);
    }

    /**
     * Helper function.
     * Return the bar dragging offset position on the x-axis or y-axis according to the bar alignment
     *
     * @param x the screen touch position on the x-axis
     * @param y the screen touch position on the y-axis
     * @return the bar dragging offset position on the x-axis or the y-axis
     */

    private float getBarDragOffset(float x, float y) {
        return vertical ? getBarDragOffsetY(y) : getBarDragOffsetX(x);
    }

    /**
     * Helper function. Updates the scroll value according to the given point
     */

    private void updateScroll(float x, float y) {
        float[] bounds = getBounds();
        float scrollValue;
        if (vertical) {
            float factor = 1f - internalBar.getBounds()[3] / bounds[3];
            scrollValue = factor > 0
                    ? max * (y / factor) / bounds[3]
                    : 0f;
        } else {
            float factor = 1f - internalBar.getBounds()[2] / bounds[2];
            scrollValue = factor > 0
                    ? max * (x / factor) / bounds[2]
                    : 0f;
        }
        setValue(scrollValue);
    }

    /**
     * Helper method. Update the internal bar position.
     */

    private void updateInternalBarPosition() {
        if (vertical) {
            float off = 0.5f * internalBar.getHeight() / getHeight();
            internalBar.getStyle().setPosition(
                    0.5f,
                    MathUtility.map(val / max, 0f, 1f, off, 1f - off));
        } else {
            float off = 0.5f * internalBar.getWidth() / getWidth();
            internalBar.getStyle().setPosition(
                    MathUtility.map(val / max, 0f, 1f, off, 1f - off),
                    0.5f);
        }
    }

    /**
     * Set the internal bar size. More specifically, set the internal bar height when it is vertical
     * and its width when it is horizontal.
     *
     * @param size the internal bar size between [0, 1]
     */

    public void setInternalBarSize(float size) {
        size = MathUtility.constrain(size, 0, 1f);
        if (vertical) {
            internalBar.getStyle().setDimension(0.9f, size);
        } else {
            internalBar.getStyle().setDimension(size, 0.9f);
        }
        updateInternalBar = true;
    }

    /**
     * @return the internal bar {@link Style} object
     */

    public Style getInternalBarStyle() {
        return internalBar.getStyle();
    }

    /**
     * Sets the scrollbar max value
     *
     * @param max the maximum value greater than or equal to 1
     */

    public void setMaxValue(float max) {
        this.max = Math.max(1f, max);
    }

    /**
     * Set the scrollbar value
     *
     * @param value the scrollbar value between [0, max]
     */

    public void setValue(float value) {
        val = MathUtility.constrain(value, 0f, max);
        updateInternalBarPosition();
    }

    /**
     * @return the current value between [0, max]
     */

    public float getValue() {
        return val;
    }

    /**
     * Scroll this scrollbar of the specified amount
     *
     * @param scrollAmount a value between [0, max]
     */

    public void scroll(float scrollAmount) {
        setValue(val + scrollAmount);
    }

    /**
     * Resets the scroll and the max value of this scrollbar.
     */

    public void reset() {
        releaseLock();
        barDragOffset = 0f;
        val = 0f;
        max = 0f;
    }

    @Override
    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        if (!isVisible && releaseLock()) {
            barDragOffset = 0f;
        }
    }

    @Override
    public void update(View parent) {
        // bugfix
        if (val > max) {
            setValue(max);
        }
        if (updateInternalBar) {
            updateInternalBar = false;
            updateInternalBarPosition();
        }
        super.update(parent);
    }

    /**
     * Create a new vertical bar of standard dimension and geometry
     */

    private static View createHorizontalBar() {
        return new Component("SCROLLBAR_INTERNAL_BAR", 0.25f, 0.5f, 0.5f, 0.9f);
    }

    /**
     * Create a new vertical bar of standard dimension and geometry
     */

    private static View createVerticalBar() {
        return new Component("SCROLLBAR_INTERNAL_BAR", 0.5f, 0.25f, 0.9f, 0.5f);
    }
}
