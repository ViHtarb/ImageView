package android.support.design.widget;

import com.google.android.material.appbar.AppBarLayout;

/**
 * @deprecated Use {@link AppBarLayout#getMinimumHeightForVisibleOverlappingContent()} instead.
 */
@Deprecated
public final class AppBarLayoutUtils {

    @Deprecated
    public static int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        return appBarLayout.getMinimumHeightForVisibleOverlappingContent();
    }
}
