package uiax.components.list;

import uia.application.ui.component.WrapperView;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.group.ComponentGroup;
import uia.application.ui.component.Component;
import uia.core.rendering.color.Color;
import uia.core.ui.ViewGroup;
import uia.core.ui.View;

import uiax.components.list.positioner.ViewPositionerFactory;
import uiax.components.list.positioner.ViewPositioner;
import uiax.components.UIScrollbar;

import java.util.Iterator;

/**
 * UIListView has been designed to handle a list of views.
 * <br>
 * Specifically, it is a layout with vertical and horizontal scrollbars.
 */

public final class UIListView extends WrapperView implements ViewGroup {
    private static final Color SCROLLBAR_BACKGROUND = Color.createColor(0, 0, 0, 110);
    private static final float SCROLLBAR_THICKNESS = 15f;

    private final UIScrollbar horizontalBar;
    private final UIScrollbar verticalBar;
    private final ViewGroup containerGroup;
    private final ViewGroup viewsContainer;
    private ViewPositioner viewPositioner;

    private float barWidth = 0f;
    private float barHeight = 0f;

    public UIListView(View view) {
        super(new ComponentGroup(view));

        viewPositioner = ViewPositionerFactory.create(this, 1.01f, true);

        verticalBar = new UIScrollbar(
                new Component("VERTICAL_SCROLLBAR_" + getID(), 0f, 0f, 0f, 0.995f),
                true
        );
        verticalBar.setInputConsumer(InputConsumer.SCREEN_TOUCH, true);
        verticalBar.setVisible(false);
        verticalBar.getStyle()
                .setBackgroundColor(SCROLLBAR_BACKGROUND)
                .setMaxWidth(SCROLLBAR_THICKNESS)
                .setMinWidth(SCROLLBAR_THICKNESS);

        horizontalBar = new UIScrollbar(
                new Component("HORIZONTAL_SCROLLBAR_" + getID(), 0f, 0f, 0f, 0f),
                false
        );
        horizontalBar.setVisible(false);
        horizontalBar.getStyle()
                .setBackgroundColor(SCROLLBAR_BACKGROUND)
                .setMaxHeight(SCROLLBAR_THICKNESS)
                .setMinHeight(SCROLLBAR_THICKNESS);

        viewsContainer = new ComponentGroup(
                new Component("SKELETON" + getID(), 0.5f, 0.5f, 1f, 1f)
        );
        viewsContainer.setInputConsumer(InputConsumer.SCREEN_TOUCH, false);
        viewsContainer.getStyle().setBackgroundColor(ColorCollection.TRANSPARENT);
        viewsContainer.setClip(false);

        containerGroup = getView();
        ViewGroup.insert(containerGroup, viewsContainer, horizontalBar, verticalBar);
    }

    @Override
    public void setClip(boolean clipRegion) {
        containerGroup.setClip(clipRegion);
    }

    @Override
    public boolean hasClip() {
        return viewsContainer.hasClip();
    }

    @Override
    public boolean insert(int i, View view) {
        boolean result = viewsContainer.insert(i, view);
        if (result) {
            notifyCallbacks(OnViewAdded.class, view);
        }
        return result;
    }

    @Override
    public boolean remove(View view) {
        boolean result = viewsContainer.remove(view);
        if (result) {
            notifyCallbacks(OnViewRemoved.class, view);
        }
        return result;
    }

    @Override
    public void removeAll() {
        viewsContainer.removeAll();

        // resets scrollbars value
        horizontalBar.reset();
        verticalBar.reset();
    }

    @Override
    public int size() {
        return viewsContainer.size();
    }

    @Override
    public View get(int i) {
        return viewsContainer.get(i);
    }

    @Override
    public View get(String id) {
        return viewsContainer.get(id);
    }

    @Override
    public int indexOf(View view) {
        return viewsContainer.indexOf(view);
    }

    @Override
    public float[] boundsContent() {
        return viewsContainer.boundsContent();
    }

    @Override
    public Iterator<View> iterator() {
        return viewsContainer.iterator();
    }

    /**
     * Sets a new ViewPositioner.
     *
     * @param viewPositioner a {@link ViewPositioner}; it could be null
     */

    public void setViewPositioner(ViewPositioner viewPositioner) {
        this.viewPositioner = viewPositioner;
    }

    /**
     * Sets the current scroll value.
     *
     * @param x the scroll value on the x-axis between [0, contentWidth]
     * @param y the scroll value on the y-axis between [0, contentHeight]
     */

    public void setScrollValue(float x, float y) {
        horizontalBar.setValue(x);
        verticalBar.setValue(y);
    }

    /**
     * @return the scroll values on the x-axis between [0, contentWidth]
     * and on the y-axis between [0, contentHeight]
     */

    public float[] getScrollValue() {
        return new float[]{
                horizontalBar.getValue(),
                verticalBar.getValue()
        };
    }

    /**
     * Helper function. Updates the view positioner.
     */

    private void updatePositioner() {
        if (viewPositioner != null) {
            for (int i = 0; i < size(); i++) {
                viewPositioner.place(get(i), i);
            }
        }
    }

    /**
     * Helper function. Updates the horizontal scrollbar.
     * <p>
     * Depends on vertical scrollbar. Vertical scrollbar must be updated first.
     */

    private void updateHorizontalScrollbar(float[] bounds, float[] boundsContent) {
        float width = Math.max(0f, boundsContent[2] - bounds[2]);
        float offsetX = Math.max(0f, width / bounds[2]);

        barWidth = 1f / (offsetX + 1f);
        horizontalBar.setVisible(barWidth < 1f);

        if (horizontalBar.isVisible()) {
            boolean verticalBarVisible = verticalBar.isVisible();
            float normalizedVerticalBarWidth = SCROLLBAR_THICKNESS / bounds[2];

            float horizontalBarWidth = verticalBarVisible ? 1f - normalizedVerticalBarWidth : 1f;
            horizontalBar.getStyle().setDimension(horizontalBarWidth, 0f);

            float horizontalBarPositionX = verticalBarVisible ? 0.5f - normalizedVerticalBarWidth / 2 : 0.5f;
            float normalizedScrollbarHeight = SCROLLBAR_THICKNESS / bounds[3];
            horizontalBar.getStyle().setPosition(
                    horizontalBarPositionX,
                    1f - 0.5f * normalizedScrollbarHeight
            );
            horizontalBar.setMaxValue(width);
        }
    }

    /**
     * Helper function. Updates the vertical scrollbar.
     */

    private void updateVerticalScrollbar(float[] bounds, float[] boundsContent) {
        float height = Math.max(0f, boundsContent[3] - bounds[3]);
        float offsetY = Math.max(0f, height / bounds[3]);

        barHeight = 1f / (offsetY + 1f);
        verticalBar.setVisible(barHeight < 1f);

        if (verticalBar.isVisible()) {
            verticalBar.getStyle().setPosition(1f - 0.5f * SCROLLBAR_THICKNESS / bounds[2], 0.5f);
            verticalBar.setMaxValue(height);
        }
    }

    @Override
    public void update(View parent) {
        horizontalBar.setInternalBarSize(barWidth);
        verticalBar.setInternalBarSize(barHeight);

        super.update(parent);
        // updates the component a second time.
        // It needs to be studied further.
        updatePositioner();
        super.update(parent);

        if (isVisible()) {
            float[] bounds = getBounds();
            float[] boundsContent = viewsContainer.boundsContent();

            // updates scrollbars
            updateVerticalScrollbar(bounds, boundsContent);
            updateHorizontalScrollbar(bounds, boundsContent);

            // adjusts the container position according to the scroll value
            viewsContainer.getStyle().setPosition(
                    0.5f - horizontalBar.getValue() / bounds[2],
                    0.5f - verticalBar.getValue() / bounds[3]
            );
        }
    }
}
