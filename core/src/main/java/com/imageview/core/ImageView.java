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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.material.animation.MotionSpec;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.DescendantOffsetUtils;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.shape.MaterialShapeUtils;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.Shapeable;

import java.util.List;

import androidx.annotation.AnimatorRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.AppCompatImageHelper;
import androidx.appcompat.widget.AppCompatImageHelperUtils;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.TintableBackgroundView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TintableImageSourceView;

import static com.google.android.material.theme.overlay.MaterialThemeOverlay.wrap;

/**
 * That {@code ImageView} is copied some {@link FloatingActionButton} features and
 * implements circling and corner rounding supports for icon and background.
 *
 * <p>As this class descends from {@link ImageView}, you can control the icon which is displayed
 * via {@link #setImageDrawable(Drawable)}.
 *
 * <p>The background color of this view defaults is {@link Color#TRANSPARENT}. If you
 * wish to change this at runtime then you can do so via {@link #setBackgroundTintList(ColorStateList)}.
 */
@SuppressLint({"RestrictedApi", "AppCompatCustomView"})
public abstract class ImageView extends android.widget.ImageView implements TintableBackgroundView, TintableImageSourceView, Shapeable, AttachedBehavior {
    private static final String LOG_TAG = ImageView.class.getSimpleName();

    /**
     * Callback to be invoked when the visibility or the state of an {@code ImageView}
     * changes.
     */
    public abstract static class OnChangedCallback {

        /**
         * Called when a {@code ImageView} has been
         * {@link #show(OnChangedCallback) shown}.
         *
         * @param imageView the ImageView that was shown.
         */
        public void onShown(ImageView imageView) {
        }

        /**
         * Called when a {@code ImageView} has been
         * {@link #hide(OnChangedCallback) hidden}.
         *
         * @param imageView the ImageView that was hidden.
         */
        public void onHidden(ImageView imageView) {
        }
    }

    /**
     * A Property wrapper around the <code>radius</code> functionality handled by the {@link
     * ImageView#setCornerRadius(float)} value.
     */
    public static final Property<ImageView, Float> RADIUS =
            new Property<ImageView, Float>(Float.class, "radius") {
                @Override
                public void set(@NonNull ImageView view, @NonNull Float value) {
                    view.setCornerRadius(value);
                }

                @NonNull
                @Override
                public Float get(@NonNull ImageView view) {
                    return view.getCornerRadius();
                }
            };

    /**
     * A Property wrapper around the <code>stroke</code> functionality handled by the {@link
     * ImageView#setStrokeWidth(float)}} value.
     */
    public static final Property<ImageView, Float> STROKE =
            new Property<ImageView, Float>(Float.class, "stroke") {
                @Override
                public void set(@NonNull ImageView view, @NonNull Float value) {
                    view.setStrokeWidth(value);
                }

                @NonNull
                @Override
                public Float get(@NonNull ImageView view) {
                    return view.getStrokeWidth();
                }
            };

    private static final int ANIM_STATE_NONE = 0;
    private static final int ANIM_STATE_HIDING = 1;
    private static final int ANIM_STATE_SHOWING = 2;

    @StyleRes
    private static final int DEF_STYLE_RES = R.style.Widget_ImageView;

    private final AnimatorTracker mChangeVisibilityTracker = new AnimatorTracker();
    private final MotionStrategy mShowStrategy = new ShowStrategy(mChangeVisibilityTracker);
    private final MotionStrategy mHideStrategy = new HideStrategy(mChangeVisibilityTracker);

    private final ImageViewImpl mImageViewHelper;
    private final AppCompatImageHelper mImageHelper;
    private final Behavior<ImageView> mBehavior;

    private int mAnimState = ANIM_STATE_NONE;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialImageViewStyle);
    }

    @SuppressLint("RestrictedApi")
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
        mBehavior = new Behavior<>(context, attrs);

        if (Build.VERSION.SDK_INT >= 23) {
            mImageViewHelper = new ImageViewImplApi23(this, attrs, defStyleAttr, DEF_STYLE_RES);
        } else if (Build.VERSION.SDK_INT >= 21) {
            mImageViewHelper = new ImageViewImplApi21(this, attrs, defStyleAttr, DEF_STYLE_RES);
        } else {
            mImageViewHelper = new ImageViewImpl(this, attrs, defStyleAttr, DEF_STYLE_RES);
        }

        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.ImageView, defStyleAttr, DEF_STYLE_RES);

        mShowStrategy.setMotionSpec(MotionSpec.createFromAttribute(context, a, R.styleable.ImageView_showMotionSpec));
        mHideStrategy.setMotionSpec(MotionSpec.createFromAttribute(context, a, R.styleable.ImageView_hideMotionSpec));

        a.recycle();

        mImageHelper = new AppCompatImageHelper(this);
        mImageHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @NonNull
    private String getA11yClassName() {
        // Use the platform widget classes so Talkback can recognize this as a button.
        return ImageView.class.getName();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(getA11yClassName());
        info.setClickable(isClickable());
    }

    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(getA11yClassName());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this, mImageViewHelper.getBackgroundDrawable());
    }

    @Override
    public boolean hasOverlappingRendering() {
        return AppCompatImageHelperUtils.hasOverlappingRendering(mImageHelper) && super.hasOverlappingRendering();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mImageViewHelper.onDrawableStateChanged(getDrawableState());

        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        mImageViewHelper.drawableHotspotChanged(x, y);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        mImageViewHelper.jumpDrawableToCurrentState();
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mImageViewHelper.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(@Px int start, @Px int top, @Px int end, @Px int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        mImageViewHelper.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
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
        return mImageViewHelper.getBackgroundTintList();
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        mImageViewHelper.setBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return mImageViewHelper.getBackgroundTintMode();
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        mImageViewHelper.setBackgroundTintMode(tintMode);
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

    @Override
    public void setImageResource(int resId) {
        mImageHelper.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        mImageViewHelper.setImageDrawable(getDrawable());
        if (mImageHelper != null) {
            AppCompatImageHelperUtils.applySupportImageTint(mImageHelper);
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (mImageViewHelper != null) {
            mImageViewHelper.setImageDrawable(drawable);
        } else {
            super.setImageDrawable(drawable);
        }
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

    @Override
    public void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        mImageViewHelper.setShapeAppearanceModel(shapeAppearanceModel);
    }

    @NonNull
    @Override
    public ShapeAppearanceModel getShapeAppearanceModel() {
        return mImageViewHelper.getShapeAppearanceModel();
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return mBehavior;
    }

    /**
     * @return
     */
    public boolean isCircle() {
        return mImageViewHelper.isCircle();
    }

    /**
     * @param isCircle
     */
    public void setCircle(boolean isCircle) {
        mImageViewHelper.setCircle(isCircle);
    }

    /**
     *
     * @return
     */
    public boolean isImageOverlap() {
        return mImageViewHelper.isImageOverlap();
    }

    /**
     *
     * @param isImageOverlap
     */
    public void setImageOverlap(boolean isImageOverlap) {
        mImageViewHelper.setImageOverlap(isImageOverlap);
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
        return mImageViewHelper.getElevation();
    }

    /**
     * Updates the backward compatible elevation of the ImageView.
     *
     * @param elevation The backward compatible elevation in pixels.
     * @attr ref R.styleable#ImageView_elevation
     * @attr ref R.styleable#ImageView_android_elevation
     * @see #getCompatElevation()
     */
    public void setCompatElevation(float elevation) {
        mImageViewHelper.setElevation(elevation);
    }

    /**
     * Gets the corner radius for this image view.
     *
     * @return Corner radius for this image view.
     * @attr ref R.styleable#ImageView_cornerRadius
     * @see #setCornerRadius(float)
     * @see #setCornerRadius(int)
     */
    public float getCornerRadius() {
        return mImageViewHelper.getCornerRadius();
    }

    /**
     * Sets the corner radius for this image view.
     *
     * @param radius Corner radius for this image view.
     * @attr ref R.styleable#ImageView_cornerRadius
     * @see #setCornerRadius(int)
     * @see #getCornerRadius()
     */
    public void setCornerRadius(float radius) {
        mImageViewHelper.setCornerRadius(radius);
    }

    /**
     * Sets the corner radius dimension resource for this image view.
     *
     * @param resId Corner radius dimension resource for this image view.
     * @attr ref R.styleable#ImageView_cornerRadius
     * @see #setCornerRadius(float)
     * @see #getCornerRadius()
     */
    public void setCornerRadius(@DimenRes int resId) {
        setCornerRadius(getResources().getDimension(resId));
    }

    /**
     * Gets the stroke width for this image view.
     *
     * @return Stroke width for this image view.
     * @attr ref R.styleable#ImageView_strokeWidth
     * @see #setStrokeWidth(int)
     * @see #setStrokeWidth(float)
     */
    public float getStrokeWidth() {
        return mImageViewHelper.getStrokeWidth();
    }

    /**
     * Sets the stroke width for this image view. Both stroke color and stroke width must be set for a
     * stroke to be drawn.
     *
     * @param width Stroke width for this image view.
     * @attr ref R.styleable#ImageView_strokeWidth
     * @see #setStrokeWidth(int)
     * @see #getStrokeWidth()
     */
    public void setStrokeWidth(float width) {
        mImageViewHelper.setStrokeWidth(width);
    }

    /**
     * Sets the stroke width dimension resource for this image view. Both stroke color and stroke width
     * must be set for a stroke to be drawn.
     *
     * @param resId Stroke width dimension resource for this image view.
     * @attr ref R.styleable#ImageView_strokeWidth
     * @see #setStrokeWidth(float)
     * @see #getStrokeWidth()
     */
    public void setStrokeWidth(@DimenRes int resId) {
        setStrokeWidth(getResources().getDimension(resId));
    }

    /**
     * Gets the stroke color for this image view.
     *
     * @return The color used for the stroke.
     * @attr ref R.styleable#ImageView_strokeColor
     * @see #setStrokeColor(int)
     * @see #setStrokeColor(ColorStateList)
     * @see #setStrokeColorResource(int)
     */
    @Nullable
    public ColorStateList getStrokeColor() {
        return mImageViewHelper.getStrokeColor();
    }

    /**
     * Sets the stroke color resource for this image view. Both stroke color and stroke width must be set
     * for a stroke to be drawn.
     *
     * @param resId Color resource to use for the stroke.
     * @attr ref R.styleable#ImageView_strokeColor
     * @see #setStrokeColor(int)
     * @see #setStrokeColor(ColorStateList)
     * @see #getStrokeColor()
     */
    public void setStrokeColorResource(@ColorRes int resId) {
        setStrokeColor(ResourcesCompat.getColor(getResources(), resId, null));
    }

    /**
     * Sets the stroke color for this image view. Both stroke color and stroke width must be set for a
     * stroke to be drawn.
     *
     * @param color Color to use for the stroke.
     * @attr ref R.styleable#ImageView_strokeColor
     * @see #setStrokeColor(ColorStateList)
     * @see #setStrokeColorResource(int)
     * @see #getStrokeColor()
     */
    public void setStrokeColor(@ColorInt int color) {
        setStrokeColor(ColorStateList.valueOf(color));
    }

    /**
     * Sets the stroke color for this image view. Both stroke color and stroke width must be set for a
     * stroke to be drawn.
     *
     * @param color Color to use for the stroke.
     * @attr ref R.styleable#ImageView_strokeColor
     * @see #setStrokeColor(int)
     * @see #setStrokeColorResource(int)
     * @see #getStrokeColor()
     */
    public void setStrokeColor(@Nullable ColorStateList color) {
        mImageViewHelper.setStrokeColor(color);
    }

    /**
     * Gets the ripple color for this image view.
     *
     * @return The color used for the ripple.
     * @attr ref R.styleable#ImageView_rippleColor
     * @see #setRippleColor(int)
     * @see #setRippleColor(ColorStateList)
     * @see #setRippleColorResource(int)
     */
    @Nullable
    public ColorStateList getRippleColor() {
        return mImageViewHelper.getRippleColor();
    }

    /**
     * Sets the ripple color for this image view.
     *
     * @param color Color to use for the ripple.
     * @attr ref R.styleable#ImageView_rippleColor
     * @see #setRippleColor(ColorStateList)
     * @see #setRippleColorResource(int)
     * @see #getRippleColor()
     */
    public void setRippleColor(@ColorInt int color) {
        setRippleColor(ColorStateList.valueOf(color));
    }

    /**
     * Sets the ripple color resource for this image view.
     *
     * @param resId Color resource to use for the ripple.
     * @attr ref R.styleable#ImageView_rippleColor
     * @see #setRippleColor(int)
     * @see #setRippleColor(ColorStateList)
     * @see #getRippleColor()
     */
    public void setRippleColorResource(@ColorRes int resId) {
        setRippleColor(ResourcesCompat.getColorStateList(getResources(), resId, null));
    }

    /**
     * Sets the ripple color for this image view
     *
     * @param color Color to use for the ripple.
     * @attr ref R.styleable#ImageView_rippleColor
     * @see #setRippleColor(int)
     * @see #setRippleColorResource(int)
     * @see #getRippleColor()
     */
    public void setRippleColor(@Nullable ColorStateList color) {
        mImageViewHelper.setRippleColor(color);
    }

    /**
     * Returns the motion spec for the show animation.
     */
    @Nullable
    public MotionSpec getShowMotionSpec() {
        return mShowStrategy.getMotionSpec();
    }

    /**
     * Updates the motion spec for the show animation.
     *
     * @attr ref R.styleable#ImageView_showMotionSpec
     */
    public void setShowMotionSpec(@Nullable MotionSpec spec) {
        mShowStrategy.setMotionSpec(spec);
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
    @Nullable
    public MotionSpec getHideMotionSpec() {
        return mHideStrategy.getMotionSpec();
    }

    /**
     * Updates the motion spec for the hide animation.
     *
     * @attr ref R.styleable#ImageView_hideMotionSpec
     */
    public void setHideMotionSpec(@Nullable MotionSpec spec) {
        mHideStrategy.setMotionSpec(spec);
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
     * Add a listener that will be invoked when this {@code ImageView} is shown. See {@link
     * Animator.AnimatorListener}.
     *
     * <p>Components that add a listener should take care to remove it when finished via {@link
     * #removeOnShowAnimationListener(Animator.AnimatorListener)}.
     *
     * @param listener listener to add
     */
    public void addOnShowAnimationListener(@NonNull Animator.AnimatorListener listener) {
        mShowStrategy.addAnimationListener(listener);
    }

    /**
     * Remove a listener that was previously added via
     * {@link #addOnShowAnimationListener(Animator.AnimatorListener)}.
     *
     * @param listener listener to remove
     */
    public void removeOnShowAnimationListener(@NonNull Animator.AnimatorListener listener) {
        mShowStrategy.removeAnimationListener(listener);
    }

    /**
     * Add a listener that will be invoked when this {@code ImageView} is hidden. See
     * {@link Animator.AnimatorListener}.
     *
     * <p>Components that add a listener should take care to remove it when finished via {@link
     * #removeOnHideAnimationListener(Animator.AnimatorListener)}.
     *
     * @param listener listener to add
     */
    public void addOnHideAnimationListener(@NonNull Animator.AnimatorListener listener) {
        mHideStrategy.addAnimationListener(listener);
    }

    /**
     * Remove a listener that was previously added via
     * {@link #addOnHideAnimationListener(Animator.AnimatorListener)}.
     *
     * @param listener listener to remove
     */
    public void removeOnHideAnimationListener(@NonNull Animator.AnimatorListener listener) {
        mHideStrategy.removeAnimationListener(listener);
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
     * @param callback the listener to notify when this view is shown
     */
    public void show(@Nullable OnChangedCallback callback) {
        performMotion(mShowStrategy, callback);
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
     * @param callback the listener to notify when this view is hidden
     */
    public void hide(@Nullable OnChangedCallback callback) {
        performMotion(mHideStrategy, callback);
    }

    void setPaddingInternal(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    void setBackgroundInternal(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
    }

    void setImageDrawableInternal(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    private boolean isOrWillBeShown() {
        if (getVisibility() != View.VISIBLE) {
            // If we're not currently visible, return true if we're animating to be shown
            return mAnimState == ANIM_STATE_SHOWING;
        } else {
            // Otherwise if we're visible, return true if we're not animating to be hidden
            return mAnimState != ANIM_STATE_HIDING;
        }
    }

    private boolean isOrWillBeHidden() {
        if (getVisibility() == View.VISIBLE) {
            // If we're currently visible, return true if we're animating to be hidden
            return mAnimState == ANIM_STATE_HIDING;
        } else {
            // Otherwise if we're not visible, return true if we're not animating to be shown
            return mAnimState != ANIM_STATE_SHOWING;
        }
    }

    private boolean shouldAnimateVisibilityChange() {
        return ViewCompat.isLaidOut(this) && !isInEditMode();
    }

    private void performMotion(@NonNull final MotionStrategy strategy, @Nullable final OnChangedCallback callback) {
        if (strategy.shouldCancel()) {
            return;
        }

        boolean shouldAnimate = shouldAnimateVisibilityChange();
        if (!shouldAnimate) {
            strategy.performNow();
            strategy.onChange(callback);
            return;
        }

        Animator animator = strategy.createAnimator();
        animator.addListener(
                new AnimatorListenerAdapter() {
                    private boolean cancelled;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        strategy.onAnimationStart(animation);
                        cancelled = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        cancelled = true;
                        strategy.onAnimationCancel();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        strategy.onAnimationEnd();
                        if (!cancelled) {
                            strategy.onChange(callback);
                        }
                    }
                });

        for (Animator.AnimatorListener l : strategy.getListeners()) {
            animator.addListener(l);
        }

        animator.start();
    }

    private class ShowStrategy extends BaseMotionStrategy {

        public ShowStrategy(AnimatorTracker animatorTracker) {
            super(ImageView.this, animatorTracker);
        }

        @Override
        public void performNow() {
            setVisibility(VISIBLE);
            setAlpha(1f);
            setScaleY(1f);
            setScaleX(1f);
        }

        @Override
        public void onChange(@Nullable final OnChangedCallback callback) {
            if (callback != null) {
                callback.onShown(ImageView.this);
            }
        }

        @Override
        public int getDefaultMotionSpecResource() {
            return R.animator.design_image_view_show_motion_spec;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            setVisibility(VISIBLE);
            mAnimState = ANIM_STATE_SHOWING;
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            mAnimState = ANIM_STATE_NONE;
        }

        @Override
        public boolean shouldCancel() {
            return isOrWillBeShown();
        }
    }

    private class HideStrategy extends BaseMotionStrategy {

        private boolean isCancelled;

        public HideStrategy(AnimatorTracker animatorTracker) {
            super(ImageView.this, animatorTracker);
        }

        @Override
        public void performNow() {
            setVisibility(INVISIBLE);
        }

        @Override
        public void onChange(@Nullable final OnChangedCallback callback) {
            if (callback != null) {
                callback.onHidden(ImageView.this);
            }
        }

        @Override
        public boolean shouldCancel() {
            return isOrWillBeHidden();
        }

        @Override
        public int getDefaultMotionSpecResource() {
            return R.animator.design_image_view_hide_motion_spec;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
            isCancelled = false;
            setVisibility(VISIBLE);
            mAnimState = ANIM_STATE_HIDING;
        }

        @Override
        public void onAnimationCancel() {
            super.onAnimationCancel();
            isCancelled = true;
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            mAnimState = ANIM_STATE_NONE;
            if (!isCancelled) {
                setVisibility(INVISIBLE);
            }
        }
    }

    /**
     * Behavior designed for use with {@link ImageView} instances. Its main function
     * is to move {@link ImageView} views so that any displayed {@link com.google.android.material.snackbar.Snackbar}s do
     * not cover them.
     * <p>
     */
    public static class Behavior<T extends ImageView> extends CoordinatorLayout.Behavior<T> {
        private static final boolean AUTO_HIDE_DEFAULT = true;

        private boolean mAutoHideEnabled;

        private Rect mTmpRect;
        private OnChangedCallback mInternalAutoHideListener;

        public Behavior() {
            super();
            mAutoHideEnabled = AUTO_HIDE_DEFAULT;
        }

        public Behavior(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView_Behavior_Layout);
            mAutoHideEnabled = a.getBoolean(R.styleable.ImageView_Behavior_Layout_behavior_autoHide, AUTO_HIDE_DEFAULT);
            a.recycle();
        }

        /**
         * Sets whether the associated {@code ImageView} automatically hides when there is
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
         * Returns whether the associated {@code ImageView} automatically hides when there is
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
        void setInternalAutoHideListener(OnChangedCallback listener) {
            mInternalAutoHideListener = listener;
        }

        private boolean shouldUpdateVisibility(View dependency, ImageView child) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (!mAutoHideEnabled) {
                return false;
            }

            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the FAB
            return lp.getAnchorId() == dependency.getId();
        }

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
                child.hide(mInternalAutoHideListener);
            } else {
                // Else, we'll animate our FAB back in
                child.show(mInternalAutoHideListener);
            }
            return true;
        }

        private boolean updateViewVisibilityForBottomSheet(View bottomSheet, ImageView child) {
            if (!shouldUpdateVisibility(bottomSheet, child)) {
                return false;
            }
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (bottomSheet.getTop() < child.getHeight() / 2 + lp.topMargin) {
                child.hide(mInternalAutoHideListener);
            } else {
                child.show(mInternalAutoHideListener);
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
            return true;
        }
    }
}