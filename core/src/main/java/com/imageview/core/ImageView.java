/*
 *  The MIT License (MIT)
 *  <p/>
 *  Copyright (c) 2016. Viнt@rь
 *  <p/>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  <p/>
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  <p/>
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.imageview.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.animation.MotionSpec;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.DescendantOffsetUtils;
import com.google.android.material.internal.ViewUtils;

import java.util.List;

import androidx.annotation.AnimatorRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatImageHelper;
import androidx.appcompat.widget.AppCompatImageHelperUtils;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.TintableBackgroundView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TintableImageSourceView;

/**
 * That {@code ImageView} is copied some {@link FloatingActionButton} features and
 * implements circling and corner rounding supports for icon and background.
 *
 * <p>As this class descends from {@link ImageView}, you can control the icon which is displayed
 * via {@link #setImageDrawable(Drawable)}.
 *
 * <p>The background color of this view defaults is {@link Color#TRANSPARENT}. If you
 * wish to change this at runtime then you can do so via {@link #setBackgroundTintList(ColorStateList)}.
 * <p>
 * Changing view form with changing src drawable form - works with local drawables and don`t works with
 * transition drawables from Glide may be need initiate reload drawable on changing view form? // disabled 15.10.2017
 * <p>
 * TODO try to implement rounding vector drawables(i mean its real with layer drawable https://stackoverflow.com/questions/36070223/how-to-put-a-vector-in-a-shape-in-android)
 * TODO fix measuring view - fixed for android L and biggest
 * TODO for pre-lollipop need fix shadow drawing for square form and for square form with corners
 * TODO reformat project
 * TODO think about current tinting
 */
@CoordinatorLayout.DefaultBehavior(ImageView.Behavior.class)
public abstract class ImageView extends VisibilityAwareImageView implements TintableBackgroundView, TintableImageSourceView {

    private static final String LOG_TAG = ImageView.class.getSimpleName();

    /**
     * Callback to be invoked when the visibility of a ImageView changes.
     */
    public abstract static class OnVisibilityChangedListener {

        /**
         * Called when a {@code ImageView} has been
         * {@link #show(OnVisibilityChangedListener) shown}.
         *
         * @param imageView the ImageView that was shown.
         */
        public void onShown(ImageView imageView) {
        }

        /**
         * Called when a {@code ImageView} has been
         * {@link #hide(OnVisibilityChangedListener) hidden}.
         *
         * @param imageView the ImageView that was hidden.
         */
        public void onHidden(ImageView imageView) {
        }
    }

    private boolean isCircle;
    private boolean isCompatPadding;

    private float mCornerRadius;
    private float mBorderWidth;

    private ColorStateList mBorderColor;

    private ColorStateList mBackgroundTint;
    private PorterDuff.Mode mBackgroundTintMode;

    private final Rect mShadowPadding = new Rect();
    //private final Rect mTouchArea = new Rect();

    private final AppCompatImageHelper mImageHelper;

    private final ImageViewImpl mImpl;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

        /*isCircle = a.getBoolean(R.styleable.ImageView_circle, false);
        isCompatPadding = a.getBoolean(R.styleable.ImageView_useCompatPadding, false);

        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mBorderColor = a.getColorStateList(R.styleable.ImageView_borderColor);

        final float elevation = a.getDimension(R.styleable.ImageView_elevation, a.getDimension(R.styleable.ImageView_android_elevation, 0f));
        final float pressedTranslationZ = a.getDimension(R.styleable.ImageView_pressedTranslationZ, 0f);
        final float hoveredFocusedTranslationZ = a.getDimension(R.styleable.ImageView_hoveredFocusedTranslationZ, 0f);

        mBackgroundTint = a.getColorStateList(R.styleable.ImageView_backgroundTint);
        mBackgroundTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.ImageView_backgroundTintMode, -1), null);

        MotionSpec showMotionSpec = MotionSpec.createFromAttribute(context, a, R.styleable.ImageView_showMotionSpec);
        MotionSpec hideMotionSpec = MotionSpec.createFromAttribute(context, a, R.styleable.ImageView_hideMotionSpec);*/

        mImpl = createImpl(); // TODO
        mImpl.loadFromAttributes(a);

        a.recycle();

        mImageHelper = new AppCompatImageHelper(this);
        mImageHelper.loadFromAttributes(attrs, defStyleAttr);

/*        getImpl().setImageDrawable(getDrawable());
        getImpl().setBackgroundDrawable(mBackgroundTint, mBackgroundTintMode, isCircle, mCornerRadius, mBorderWidth, mBorderColor);
        getImpl().setElevation(elevation);
        getImpl().setPressedTranslationZ(pressedTranslationZ);
        getImpl().setHoveredFocusedTranslationZ(hoveredFocusedTranslationZ);
        getImpl().setShowMotionSpec(showMotionSpec);
        getImpl().setHideMotionSpec(hideMotionSpec);*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getImpl().onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getImpl().onDetachedFromWindow();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return AppCompatImageHelperUtils.hasOverlappingRendering(mImageHelper) && super.hasOverlappingRendering();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        getImpl().onDrawableStateChanged(getDrawableState());

        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        getImpl().jumpDrawableToCurrentState();
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resId) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Nullable
    @Override
    public ColorStateList getBackgroundTintList() {
        return mBackgroundTint;
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (mBackgroundTint != tint) {
            mBackgroundTint = tint;
            getImpl().setBackgroundTintList(tint);
        }
    }

    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return mBackgroundTintMode;
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mBackgroundTintMode != tintMode) {
            mBackgroundTintMode = tintMode;
            getImpl().setBackgroundTintMode(tintMode);
        }
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return getBackgroundTintList();
    }

    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        setBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return getBackgroundTintMode();
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        setBackgroundTintMode(tintMode);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setImageResource(int resId) {
        mImageHelper.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        getImpl().setImageDrawable(getDrawable()); // TODO check if setImageURI not call setImageDrawable method
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        getImpl().setImageDrawable(drawable);
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        getImpl().setImageDrawable(getDrawable());
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void setSupportImageTintList(@Nullable ColorStateList tint) {
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.setSupportImageTintList(mImageHelper, tint);
        }
    }

    @Nullable
    @Override
    public ColorStateList getSupportImageTintList() {
        return mImageHelper != null ? AppCompatImageHelperUtils.getSupportImageTintList(mImageHelper) : null;
    }

    @Override
    public void setSupportImageTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.setSupportImageTintMode(mImageHelper, tintMode);
        }
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportImageTintMode() {
        return mImageHelper != null ? AppCompatImageHelperUtils.getSupportImageTintMode(mImageHelper) : null;
    }

    /**
     * @return
     */
    public boolean isCircle() {
        return isCircle;
    }

    /**
     * @param isCircle
     */
    public void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) {
            this.isCircle = isCircle;
            getImpl().setCircle(isCircle);
        }
    }

    /**
     * Returns whether ImageView will add inner padding on platforms Lollipop and after.
     *
     * @return true if ImageView is adding inner padding on platforms Lollipop and after,
     * to ensure consistent dimensions on all platforms.
     * @attr ref R.styleable#ImageView_useCompatPadding
     * @see #setUseCompatPadding(boolean)
     */
    public boolean getUseCompatPadding() {
        return isCompatPadding;
    }

    /**
     * Set whether ImageView should add inner padding on platforms Lollipop and after,
     * to ensure consistent dimensions on all platforms.
     *
     * @param useCompatPadding true if ImageView is adding inner padding on platforms
     *                         Lollipop and after, to ensure consistent dimensions on all platforms.
     * @attr ref R.styleable#ImageView_useCompatPadding
     * @see #getUseCompatPadding()
     */
    public void setUseCompatPadding(boolean useCompatPadding) {
        if (isCompatPadding != useCompatPadding) {
            isCompatPadding = useCompatPadding;
            getImpl().onCompatShadowChanged();
        }
    }

    /**
     * Returns the backward compatible elevation of the ImageView.
     *
     * @return the backward compatible elevation in pixels.
     * @attr ref R.styleable#ImageView_elevation
     * @attr ref R.styleable#ImageView_android_elevation
     * @see #setCompatElevation(float)
     */
    public float getCompatElevation() {
        return getImpl().getElevation();
    }

    /**
     * Updates the backward compatible elevation of the ImageView.
     *
     * @param elevation The backward compatible elevation in pixels.
     * @attr ref R.styleable#ImageView_elevation
     * @attr ref R.styleable#ImageView_android_elevation
     * @see #getCompatElevation()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatElevation(float elevation) {
        getImpl().setElevation(elevation);
    }

    /**
     * Updates the backward compatible elevation of the ImageView.
     *
     * @param resId The resource id of the backward compatible elevation.
     * @attr ref R.styleable#ImageView_elevation
     * @attr ref R.styleable#ImageView_android_elevation
     * @see #getCompatElevation()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatElevationResource(@DimenRes int resId) {
        setCompatElevation(getResources().getDimension(resId));
    }

    /**
     * Returns the backward compatible pressed translationZ of the ImageView.
     *
     * @return the backward compatible pressed translationZ in pixels.
     * @attr ref com.google.android.material.R.styleable#FloatingActionButton_pressedTranslationZ
     * @see #setCompatPressedTranslationZ(float)
     */
    public float getCompatPressedTranslationZ() {
        return getImpl().getPressedTranslationZ();
    }

    /**
     * Updates the backward compatible pressed translationZ of the ImageView.
     *
     * @param translationZ The backward compatible pressed translationZ in pixels.
     * @attr R.styleable#ImageView_pressedTranslationZ
     * @see #getCompatPressedTranslationZ()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatPressedTranslationZ(float translationZ) {
        getImpl().setPressedTranslationZ(translationZ);
    }

    /**
     * Updates the backward compatible pressed translationZ of the ImageView.
     *
     * @param resId The resource id of the backward compatible pressed translationZ.
     * @attr ref R.styleable#ImageView_pressedTranslationZ
     * @see #getCompatPressedTranslationZ()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatPressedTranslationZ(@DimenRes int resId) {
        setCompatPressedTranslationZ(getResources().getDimension(resId));
    }

    /**
     * Returns the backward compatible hovered/focused translationZ of the ImageView.
     *
     * @return the backward compatible hovered/focused translationZ in pixels.
     * @attr ref R.styleable#ImageView_hoveredFocusedTranslationZ
     * @see #setCompatHoveredFocusedTranslationZ(float)
     */
    public float getCompatHoveredFocusedTranslationZ() {
        return getImpl().getHoveredFocusedTranslationZ();
    }

    /**
     * Updates the backward compatible hovered/focused translationZ of the ImageView.
     *
     * @param translationZ The backward compatible hovered/focused translationZ in pixels.
     * @attr ref R.styleable#ImageView_hoveredFocusedTranslationZ
     * @see #getCompatHoveredFocusedTranslationZ()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatHoveredFocusedTranslationZ(float translationZ) {
        getImpl().setHoveredFocusedTranslationZ(translationZ);
    }

    /**
     * Updates the backward compatible hovered/focused translationZ of the ImageView.
     *
     * @param resId The resource id of the backward compatible hovered/focused translationZ.
     * @attr ref R.styleable#ImageView_hoveredFocusedTranslationZ
     * @see #getCompatHoveredFocusedTranslationZ()
     * @see #setUseCompatPadding(boolean)
     */
    public void setCompatHoveredFocusedTranslationZ(@DimenRes int resId) {
        setCompatHoveredFocusedTranslationZ(getResources().getDimension(resId));
    }

    /**
     * @return
     */
    public float getCornerRadius() {
        return mCornerRadius;
    }

    /**
     * @param radius
     */
    public void setCornerRadius(float radius) {
        if (mCornerRadius != radius) {
            mCornerRadius = radius;
            getImpl().setCornerRadius(radius);
        }
    }

    /**
     * @param resId
     */
    public void setCornerRadius(@DimenRes int resId) {
        setCornerRadius(getResources().getDimension(resId));
    }

    /**
     * @return
     */
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * @param width
     */
    public void setBorderWidth(float width) {
        if (mBorderWidth != width) {
            mBorderWidth = width;
            getImpl().setBorderWidth(mBorderWidth);
        }
    }

    /**
     * @param resId
     */
    public void setBorderWidth(@DimenRes int resId) {
        setBorderWidth(getResources().getDimension(resId));
    }

    /**
     * @return
     */
    public ColorStateList getBorderColor() {
        return mBorderColor;
    }

    /**
     * @param color
     */
    public void setBorderColor(@ColorInt int color) {
        setBorderColor(ColorStateList.valueOf(color));
    }

    /**
     * @param color
     */
    public void setBorderColor(ColorStateList color) {
        if (mBorderColor != color) {
            mBorderColor = color;
            getImpl().setBorderColor(color);
        }
    }

    /**
     * @param resId
     */
    public void setBorderColorResource(@ColorRes int resId) {
        setBorderColor(ResourcesCompat.getColor(getResources(), resId, null));
    }

    /**
     * Returns the motion spec for the show animation.
     */
    public MotionSpec getShowMotionSpec() {
        return getImpl().getShowMotionSpec();
    }

    /**
     * Updates the motion spec for the show animation.
     *
     * @attr ref R.styleable#ImageView_showMotionSpec
     */
    public void setShowMotionSpec(MotionSpec spec) {
        getImpl().setShowMotionSpec(spec);
    }

    /**
     * Updates the motion spec for the show animation.
     *
     * @attr ref R.styleable#ImageView_showMotionSpec
     */
    public void setShowMotionSpec(@AnimatorRes int id) {
        setShowMotionSpec(MotionSpec.createFromResource(getContext(), id));
    }

    /**
     * Returns the motion spec for the hide animation.
     */
    public MotionSpec getHideMotionSpec() {
        return getImpl().getHideMotionSpec();
    }

    /**
     * Updates the motion spec for the hide animation.
     *
     * @attr ref R.styleable#ImageView_hideMotionSpec
     */
    public void setHideMotionSpec(MotionSpec spec) {
        getImpl().setHideMotionSpec(spec);
    }

    /**
     * Updates the motion spec for the hide animation.
     *
     * @attr ref R.styleable#ImageView_hideMotionSpec
     */
    public void setHideMotionSpec(@AnimatorRes int id) {
        setHideMotionSpec(MotionSpec.createFromResource(getContext(), id));
    }

    /**
     * Shows the image view.
     * <p>This method will animate the image view show if the view has already been laid out.</p>
     */
    public void show() {
        show(null);
    }

    /**
     * Shows the image view.
     * <p>This method will animate the image view show if the view has already been laid out.</p>
     *
     * @param listener the listener to notify when this view is shown
     */
    public void show(@Nullable final OnVisibilityChangedListener listener) {
        show(listener, true);
    }

    private void show(OnVisibilityChangedListener listener, boolean fromUser) {
        getImpl().show(wrapOnVisibilityChangedListener(listener), fromUser);
    }

    /**
     * Hides the image view.
     * <p>This method will animate the image view hide if the view has already been laid out.</p>
     */
    public void hide() {
        hide(null);
    }

    /**
     * Hides the image view.
     * <p>This method will animate the image view hide if the view has already been laid out.</p>
     *
     * @param listener the listener to notify when this view is hidden
     */
    public void hide(@Nullable OnVisibilityChangedListener listener) {
        hide(listener, true);
    }

    private void hide(@Nullable OnVisibilityChangedListener listener, boolean fromUser) {
        getImpl().hide(wrapOnVisibilityChangedListener(listener), fromUser);
    }

    @Nullable
    private ImageViewImpl.InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener listener) {
        if (listener == null) {
            return null;
        }

        return new ImageViewImpl.InternalVisibilityChangedListener() {
            @Override
            public void onShown() {
                listener.onShown(ImageView.this);
            }

            @Override
            public void onHidden() {
                listener.onHidden(ImageView.this);
            }
        };
    }

    private ImageViewImpl getImpl() {
       //if (mImpl == null) {
            //mImpl = createImpl();
        //}
        return mImpl;
    }

    private ImageViewImpl createImpl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ImageViewLollipop(this, new ViewDelegateImpl());
        }
        return new ImageViewImpl(this, new ViewDelegateImpl());
    }

    private class ViewDelegateImpl implements ViewDelegate {

        @Override
        public float getRadius() {
            return (getWidth() - (mShadowPadding.right + mShadowPadding.left)) / 2f;
        }

        @Override
        public void setShadowPadding(int left, int top, int right, int bottom) {
            //mShadowPadding.set(left, top, right, bottom);
            //setPadding(left + (int) getBorderWidth(), top + (int) getBorderWidth(), right + (int) getBorderWidth(), bottom + (int) getBorderWidth());
        }

        @Override
        public void setBackgroundDrawable(Drawable background) {
            ImageView.super.setBackgroundDrawable(background);
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            ImageView.super.setImageDrawable(drawable);
        }

        @Override
        public boolean isCompatPadding() {
            return isCompatPadding;
        }
    }

    /**
     * Behavior designed for use with {@link ImageView} instances. Its main function
     * is to move {@link ImageView} views so that any displayed {@link com.google.android.material.snackbar.Snackbar}s do
     * not cover them.
     * <p>
     * TODO reimplement this after Google reimplement same behavior in FloatingActionButton
     */
    public static class Behavior extends CoordinatorLayout.Behavior<ImageView> {
        private static final boolean AUTO_HIDE_DEFAULT = true;

        private Rect mTmpRect;
        private OnVisibilityChangedListener mInternalAutoHideListener;
        private boolean mAutoHideEnabled;

        public Behavior() {
            super();
            mAutoHideEnabled = AUTO_HIDE_DEFAULT;
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView_Behavior_Layout);
            mAutoHideEnabled = a.getBoolean(R.styleable.ImageView_Behavior_Layout_behavior_autoHide, AUTO_HIDE_DEFAULT);
            a.recycle();
        }

        /**
         * Sets whether the associated FloatingActionButton automatically hides when there is
         * not enough space to be displayed. This works with {@link AppBarLayout}
         * and {@link BottomSheetBehavior}.
         *
         * @param autoHide true to enable automatic hiding
         * @attr ref android.support.design.R.styleable#ImageView_Behavior_Layout_behavior_autoHide
         */
        public void setAutoHideEnabled(boolean autoHide) {
            mAutoHideEnabled = autoHide;
        }

        /**
         * Returns whether the associated FloatingActionButton automatically hides when there is
         * not enough space to be displayed.
         *
         * @return true if enabled
         * @attr ref android.support.design.R.styleable#ImageView_Behavior_Layout_behavior_autoHide
         */
        public boolean isAutoHideEnabled() {
            return mAutoHideEnabled;
        }

        @Override
        public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
            if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
                // If the developer hasn't set dodgeInsetEdges, lets set it to BOTTOM so that
                // we dodge any Snackbars
                lp.dodgeInsetEdges = Gravity.BOTTOM;
            }
        }

        @Override
        public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
            if (dependency instanceof AppBarLayout) {
                // If we're depending on an AppBarLayout we will show/hide it automatically
                // if the FAB is anchored to the AppBarLayout
                updateViewVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child);
            } else if (isBottomSheet(dependency)) {
                updateViewVisibilityForBottomSheet(dependency, child);
            }
            return false;
        }

        private static boolean isBottomSheet(@NonNull View view) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            return lp instanceof CoordinatorLayout.LayoutParams && ((CoordinatorLayout.LayoutParams) lp).getBehavior() instanceof BottomSheetBehavior;
        }

        @VisibleForTesting
        void setInternalAutoHideListener(OnVisibilityChangedListener listener) {
            mInternalAutoHideListener = listener;
        }

        private boolean shouldUpdateVisibility(View dependency, ImageView child) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (!mAutoHideEnabled) {
                return false;
            }

            if (lp.getAnchorId() != dependency.getId()) {
                // The anchor ID doesn't match the dependency, so we won't automatically
                // show/hide the FAB
                return false;
            }

            //noinspection RedundantIfStatement
            if (child.getUserSetVisibility() != VISIBLE) {
                // The view isn't set to be visible so skip changing its visibility
                return false;
            }

            return true;
        }

        @SuppressLint("RestrictedApi")
        private boolean updateViewVisibilityForAppBarLayout(CoordinatorLayout parent, AppBarLayout appBarLayout, ImageView child) {
            if (!shouldUpdateVisibility(appBarLayout, child)) {
                return false;
            }

            if (mTmpRect == null) {
                mTmpRect = new Rect();
            }

            // First, let's get the visible rect of the dependency
            final Rect rect = mTmpRect;
            DescendantOffsetUtils.getDescendantRect(parent, appBarLayout, rect);

            if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                // If the anchor's bottom is below the seam, we'll animate our FAB out
                child.hide(mInternalAutoHideListener, false);
            } else {
                // Else, we'll animate our FAB back in
                child.show(mInternalAutoHideListener, false);
            }
            return true;
        }

        private boolean updateViewVisibilityForBottomSheet(View bottomSheet, ImageView child) {
            if (!shouldUpdateVisibility(bottomSheet, child)) {
                return false;
            }
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (bottomSheet.getTop() < child.getHeight() / 2 + lp.topMargin) {
                child.hide(mInternalAutoHideListener, false);
            } else {
                child.show(mInternalAutoHideListener, false);
            }
            return true;
        }

        @Override
        public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull ImageView child, int layoutDirection) {
            // First, let's make sure that the visibility of the FAB is consistent
            final List<View> dependencies = parent.getDependencies(child);
            for (int i = 0, count = dependencies.size(); i < count; i++) {
                final View dependency = dependencies.get(i);
                if (dependency instanceof AppBarLayout) {
                    if (updateViewVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child)) {
                        break;
                    }
                } else if (isBottomSheet(dependency)) {
                    if (updateViewVisibilityForBottomSheet(dependency, child)) {
                        break;
                    }
                }
            }
            // Now let the CoordinatorLayout lay out the FAB
            parent.onLayoutChild(child, layoutDirection);
            // Now offset it if needed
            offsetIfNeeded(parent, child);
            return true;
        }

        @Override
        public boolean getInsetDodgeRect(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull Rect rect) {
            // Since we offset so that any internal shadow padding isn't shown, we need to make
            // sure that the shadow isn't used for any dodge inset calculations
            final Rect shadowPadding = child.mShadowPadding;
            rect.set(child.getLeft() + shadowPadding.left,
                    child.getTop() + shadowPadding.top,
                    child.getRight() - shadowPadding.right,
                    child.getBottom() - shadowPadding.bottom);
            return true;
        }

        /**
         * Pre-Lollipop we use padding so that the shadow has enough space to be drawn. This method
         * offsets our layout position so that we're positioned correctly if we're on one of
         * our parent's edges.
         */
        private void offsetIfNeeded(CoordinatorLayout parent, ImageView fab) {
            final Rect padding = fab.mShadowPadding;

            if (padding != null && padding.centerX() > 0 && padding.centerY() > 0) {
                final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

                int offsetTB = 0, offsetLR = 0;

                if (fab.getRight() >= parent.getWidth() - lp.rightMargin) {
                    // If we're on the right edge, shift it the right
                    offsetLR = padding.right;
                } else if (fab.getLeft() <= lp.leftMargin) {
                    // If we're on the left edge, shift it the left
                    offsetLR = -padding.left;
                }
                if (fab.getBottom() >= parent.getHeight() - lp.bottomMargin) {
                    // If we're on the bottom edge, shift it down
                    offsetTB = padding.bottom;
                } else if (fab.getTop() <= lp.topMargin) {
                    // If we're on the top edge, shift it up
                    offsetTB = -padding.top;
                }

                if (offsetTB != 0) {
                    ViewCompat.offsetTopAndBottom(fab, offsetTB);
                }
                if (offsetLR != 0) {
                    ViewCompat.offsetLeftAndRight(fab, offsetLR);
                }
            }
        }
    }
}