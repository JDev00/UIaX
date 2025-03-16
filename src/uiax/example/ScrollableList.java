package uiax.example;

import uia.application.ui.component.text.ComponentText;
import uia.core.rendering.geometry.GeometryCollection;
import uia.core.rendering.color.ColorCollection;
import uia.application.ui.component.WrapperView;
import uia.application.ui.group.ComponentGroup;
import uia.core.ui.style.TextVerticalAlignment;
import uia.application.ui.component.Component;
import uia.core.context.Context;
import uia.core.ui.ViewGroup;
import uia.core.ui.ViewText;
import uia.core.ui.View;

import uiax.components.list.XListView;

/**
 * Demonstrative example. Creates and displays a scrollable list of items.
 */

public class ScrollableList extends WrapperView {

    public ScrollableList() {
        super(new ComponentGroup(
                new Component("CONTAINER", 0.5f, 0.5f, 1f, 1f)
        ));

        // creates the list of items
        XListView listView = new XListView(
                new Component("listview", 0.5f, 0.5f, 0.8f, 0.8f)
        );
        listView.getStyle()
                .setBackgroundColor(ColorCollection.LIGHT_GRAY)
                .setGeometry(geometry -> GeometryCollection.rect(
                        geometry, GeometryCollection.STD_VERT,
                        GeometryCollection.STD_ROUND, 0f, 0f, GeometryCollection.STD_ROUND,
                        listView.getBounds()[2] / listView.getBounds()[3]), true);
        View[] items = createListItems();
        ViewGroup.insert(listView, items);

        ViewGroup.insert(getView(), listView);
    }

    /**
     * Helper function. Creates the list of items.
     */

    private static View[] createListItems() {
        View[] result = new View[1_500];
        for (int i = 0; i < result.length; i++) {
            ViewText item = new ComponentText(
                    new Component("item_" + i, 0, 0, 1f, 0.1f)
            );
            item.setText("Item number " + i);
            item.getStyle()
                    .setBackgroundColor(ColorCollection.LIGHT_CORAL)
                    .setTextAlignment(TextVerticalAlignment.CENTER)
                    .setTextColor(ColorCollection.WHITE);
            result[i] = item;
        }
        return result;
    }

    public static void main(String[] args) {
        Context context = Utility.startApplication();
        context.setView(new ScrollableList());
    }
}
