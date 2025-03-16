package uiax.components.loading;

import uia.core.rendering.geometry.GeometryCollection;
import uia.application.ui.component.WrapperView;
import uia.core.rendering.geometry.Geometry;
import uia.utility.MathUtility;
import uia.core.ui.View;

/**
 * The XLoadingWheel represents a standard spinning wheel component.
 */

public final class XLoadingWheel extends WrapperView {
    public static final float THICKNESS = 0.04f;
    public static final float VELOCITY = 0.05f;

    private float rotation = 0f;

    public XLoadingWheel(View view, float velocity, float thickness) {
        super(view);
        setColliderPolicy(ColliderPolicy.AABB);
        getStyle().setGeometry(geometry -> {
            if (Math.abs(rotation) >= MathUtility.TWO_PI) {
                rotation = 0f;
            }
            Geometry arc = GeometryCollection.arc(geometry, 50, rotation, thickness);
            Geometry.rotate(arc, rotation);
            rotation += velocity;
        }, true);
    }
}
