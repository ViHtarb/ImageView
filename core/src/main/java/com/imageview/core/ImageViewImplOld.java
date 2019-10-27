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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.animation.AnimatorSetCompat;
import com.google.android.material.animation.MotionSpec;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Viнt@rь on 18.12.2016
 */
@RequiresApi(ICE_CREAM_SANDWICH)
@TargetApi(ICE_CREAM_SANDWICH)
class ImageViewImplOld {
    protected static final int NO_ID = SDK_INT >= LOLLIPOP ? -1 : 0;

    protected static final long ELEVATION_ANIM_DELAY = 100;
    protected static final long ELEVATION_ANIM_DURATION = 100;

    protected static final int ANIM_STATE_NONE = 0;
    protected static final int ANIM_STATE_HIDING = 1;
    protected static final int ANIM_STATE_SHOWING = 2;

    protected static final int[] EMPTY_STATE_SET = new int[0];
    protected static final int[] ENABLED_STATE_SET = {android.R.attr.state_enabled};
    protected static final int[] PRESSED_ENABLED_STATE_SET = {android.R.attr.state_pressed, android.R.attr.state_enabled};
    protected static final int[] FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_focused, android.R.attr.state_enabled};
    protected static final int[] HOVERED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_enabled};
    protected static final int[] HOVERED_FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_focused, android.R.attr.state_enabled};

    protected static final TimeInterpolator ELEVATION_ANIM_INTERPOLATOR = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;

    private static final float HIDE_OPACITY = 0f;
    private static final float HIDE_SCALE = 0f;
    private static final float SHOW_OPACITY = 1f;
    private static final float SHOW_SCALE = 1f;

    private final Rect mTmpRect = new Rect();

    private int mAnimState = ANIM_STATE_NONE;

    private float mRotation;
    private float mElevation;
    private float mPressedTranslationZ;
    private float mHoveredFocusedTranslationZ;

    private StateListAnimator mStateListAnimator;

    private Animator mCurrentAnimator;

    private MotionSpec mDefaultShowMotionSpec;
    private MotionSpec mDefaultHideMotionSpec;

    private MotionSpec mShowMotionSpec;
    private MotionSpec mHideMotionSpec;

    protected final ColorStateList mTransparentTint = ColorStateList.valueOf(Color.TRANSPARENT);
    protected final ImageView mView;
    protected final ImageViewDelegate mViewDelegate;

    protected Drawable mShapeDrawable;
    protected BorderDrawable mBorderDrawable;
    protected Drawable mContentBackground;
    protected ShadowDrawableWrapper mShadowDrawable;

    protected ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;

    protected ImageViewImplOld(ImageView view, ImageViewDelegate viewDelegate) {
        mView = view;
        mViewDelegate = viewDelegate;

        mStateListAnimator = new StateListAnimator();

        // Elevate with translationZ when pressed, focused, or hovered
        mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createElevationAnimator(new ElevateToPressedTranslationZAnimation()));
        mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createElevationAnimator(new ElevateToHoveredFocusedTranslationZAnimation()));
        mStateListAnimator.addState(HOVERED_ENABLED_STATE_SET, createElevationAnimator(new ElevateToHoveredFocusedTranslationZAnimation()));
        mStateListAnimator.addState(HOVERED_FOCUSED_ENABLED_STATE_SET, createElevationAnimator(new ElevateToHoveredFocusedTranslationZAnimation()));
        // Reset back to elevation by default
        mStateListAnimator.addState(ENABLED_STATE_SET, createElevationAnimator(new ResetElevationAnimation()));
        // Set to 0 when disabled
        mStateListAnimator.addState(EMPTY_STATE_SET, createElevationAnimator(new DisabledElevationAnimation()));

        mRotation = mView.getRotation();
    }

    protected void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, boolean isCircle, float cornerRadius, float borderWidth, ColorStateList borderColor) {
        // Now we need to tint the original background with the tint, using
        // an InsetDrawable if we have a border width
        mShapeDrawable = DrawableCompat.wrap(createShapeDrawable(isCircle, cornerRadius));
        DrawableCompat.setTintList(mShapeDrawable, backgroundTint == null ? mTransparentTint : backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
        }

        mBorderDrawable = createBorderDrawable(isCircle, cornerRadius, borderWidth, borderColor);
        mContentBackground = new LayerDrawable(new Drawable[]{mShapeDrawable, mBorderDrawable});

        mShadowDrawable = new ShadowDrawableWrapper(mContentBackground, mElevation, mElevation + mPressedTranslationZ);
        mShadowDrawable.setAddPaddingForCorners(false);

        mViewDelegate.setBackgroundDrawable(mShadowDrawable);
    }

    protected void setBackgroundTintList(ColorStateList tint) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintList(mShapeDrawable, tint == null ? mTransparentTint : tint);
        }
    }

    protected void setBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintMode(mShapeDrawable, tintMode);
        }
    }

    protected void setImageDrawable(Drawable drawable) {
        if (mView.getCornerRadius() > 0 || mView.isCircle()) {
            boolean isTransition = isTransition(drawable);

            if (!isVector(drawable) && !isTransition) {
                drawable = createRoundedDrawable(drawable);
            } else if (isTransition) {
                final TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
                for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                    Drawable childDrawable = transitionDrawable.getDrawable(i);
                    if (!isVector(childDrawable)) {
                        int id = transitionDrawable.getId(i);
                        if (id == NO_ID) {
                            id = NO_ID + i + 1;
                            transitionDrawable.setId(i, id);
                        }
                        transitionDrawable.setDrawableByLayerId(id, createRoundedDrawable(childDrawable));
                    }
                }
            }
        }

        mViewDelegate.setImageDrawable(drawable);
    }

    protected void setCircle(boolean isCircle) {
        mBorderDrawable.setCircle(isCircle);

        GradientDrawable shapeDrawable = DrawableCompat.unwrap(mShapeDrawable);
        shapeDrawable.setShape(isCircle ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);
    }

    protected final void setShadowRadius(float radius) {
        if (mShadowDrawable != null) {
            mShadowDrawable.setCornerRadius(radius);
        }
    }

    protected float getElevation() {
        return mElevation;
    }

    protected final void setElevation(float elevation) {
        if (mElevation != elevation) {
            mElevation = elevation;
            onElevationsChanged(elevation, mPressedTranslationZ, mHoveredFocusedTranslationZ);
        }
    }

    protected float getPressedTranslationZ() {
        return mPressedTranslationZ;
    }

    protected final void setPressedTranslationZ(float translationZ) {
        if (mPressedTranslationZ != translationZ) {
            mPressedTranslationZ = translationZ;
            onElevationsChanged(mElevation, translationZ, mHoveredFocusedTranslationZ);
        }
    }

    protected float getHoveredFocusedTranslationZ() {
        return mHoveredFocusedTranslationZ;
    }

    protected final void setHoveredFocusedTranslationZ(float translationZ) {
        if (mHoveredFocusedTranslationZ != translationZ) {
            mHoveredFocusedTranslationZ = translationZ;
            onElevationsChanged(mElevation, mPressedTranslationZ, translationZ);
        }
    }

    protected void setCornerRadius(float radius) {
        mBorderDrawable.setCornerRadius(radius);

        GradientDrawable shapeDrawable = DrawableCompat.unwrap(mShapeDrawable);
        shapeDrawable.setCornerRadius(radius);
    }

    protected void setBorderWidth(float width) {
        mBorderDrawable.setWidth(width);
    }

    protected void setBorderColor(ColorStateList color) {
        mBorderDrawable.setColor(color);
    }

    @Nullable
    protected final MotionSpec getShowMotionSpec() {
        return mShowMotionSpec;
    }

    protected final void setShowMotionSpec(@Nullable MotionSpec spec) {
        mShowMotionSpec = spec;
    }

    @Nullable
    protected final MotionSpec getHideMotionSpec() {
        return mHideMotionSpec;
    }

    protected final void setHideMotionSpec(@Nullable MotionSpec spec) {
        mHideMotionSpec = spec;
    }

    protected void getPadding(Rect rect) {
        mShadowDrawable.getPadding(rect);
    }

    private MotionSpec getDefaultShowMotionSpec() {
        if (mDefaultShowMotionSpec == null) {
            mDefaultShowMotionSpec = MotionSpec.createFromResource(mView.getContext(), R.animator.design_image_view_show_motion_spec);
        }
        return mDefaultShowMotionSpec;
    }

    private MotionSpec getDefaultHideMotionSpec() {
        if (mDefaultHideMotionSpec == null) {
            mDefaultHideMotionSpec = MotionSpec.createFromResource(mView.getContext(), R.animator.design_image_view_hide_motion_spec);
        }
        return mDefaultHideMotionSpec;
    }

    protected void onAttachedToWindow() {
        if (requirePreDrawListener()) {
            ensurePreDrawListener();
            mView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        }
    }

    protected void onDetachedFromWindow() {
        if (mOnPreDrawListener != null) {
            mView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
            mOnPreDrawListener = null;
        }
    }

    protected void onElevationsChanged(float elevation, float pressedTranslationZ, float hoveredFocusedTranslationZ) {
        if (mShadowDrawable != null) {
            mShadowDrawable.setShadowSize(elevation, elevation + mPressedTranslationZ);
            updatePadding();
        }
    }

    protected void onDrawableStateChanged(int[] state) {
        mStateListAnimator.setState(state);
    }

    protected void onCompatShadowChanged() {
        // Ignore pre-v21
    }

    protected void onPaddingUpdated(Rect padding) {
    }

    protected void jumpDrawableToCurrentState() {
        mStateListAnimator.jumpToCurrentState();
    }

    protected void hide(@Nullable final InternalVisibilityChangedListener listener, final boolean fromUser) {
        if (isOrWillBeHidden()) {
            // We either are or will soon be hidden, skip the call
            return;
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        //mView.animate().cancel();

        if (shouldAnimateVisibilityChange()) {
            AnimatorSet set = createAnimator(mHideMotionSpec != null ? mHideMotionSpec : getDefaultHideMotionSpec(), HIDE_OPACITY, HIDE_SCALE);
            set.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled;

                @Override
                public void onAnimationStart(Animator animation) {
                    //mView.internalSetVisibility(View.VISIBLE, fromUser);

                    mAnimState = ANIM_STATE_HIDING;
                    mCurrentAnimator = animation;
                    mCancelled = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCancelled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimState = ANIM_STATE_NONE;
                    mCurrentAnimator = null;

                    if (!mCancelled) {
                        //mView.internalSetVisibility(fromUser ? View.GONE : View.INVISIBLE, fromUser);
                        if (listener != null) {
                            listener.onHidden();
                        }
                    }
                }
            });

            /*if (hideListeners != null) {
                for (Animator.AnimatorListener l : hideListeners) {
                    set.addListener(l);
                }
            }*/
            set.start();

            //mAnimState = ANIM_STATE_HIDING;

            /*mView.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(SHOW_HIDE_ANIM_DURATION)
                    .setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
                    .setListener(new AnimatorListenerAdapter() {
                        private boolean mCancelled;

                        @Override
                        public void onAnimationStart(Animator animation) {
                            mView.internalSetVisibility(View.VISIBLE, fromUser);
                            mCancelled = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mCancelled = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimState = ANIM_STATE_NONE;

                            if (!mCancelled) {
                                mView.internalSetVisibility(fromUser ? View.GONE : View.INVISIBLE, fromUser);
                                if (listener != null) {
                                    listener.onHidden();
                                }
                            }
                        }
                    });*/
        } else {
            // If the view isn't laid out, or we're in the editor, don't run the animation
            //mView.internalSetVisibility(fromUser ? View.GONE : View.INVISIBLE, fromUser);
            if (listener != null) {
                listener.onHidden();
            }
        }
    }

    protected void show(@Nullable final InternalVisibilityChangedListener listener, final boolean fromUser) {
        if (isOrWillBeShown()) {
            // We either are or will soon be visible, skip the call
            return;
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        //mView.animate().cancel();

        if (shouldAnimateVisibilityChange()) {
            //mAnimState = ANIM_STATE_SHOWING;

            if (mView.getVisibility() != View.VISIBLE) {
                // If the view isn't visible currently, we'll animate it from a single pixel
                mView.setAlpha(0f);
                mView.setScaleY(0f);
                mView.setScaleX(0f);

                //setImageMatrixScale(0f);
            }

            AnimatorSet set = createAnimator(mShowMotionSpec != null ? mShowMotionSpec : getDefaultShowMotionSpec(), SHOW_OPACITY, SHOW_SCALE);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //mView.internalSetVisibility(View.VISIBLE, fromUser);

                    mAnimState = ANIM_STATE_SHOWING;
                    mCurrentAnimator = animation;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimState = ANIM_STATE_NONE;
                    mCurrentAnimator = null;

                    if (listener != null) {
                        listener.onShown();
                    }
                }
            });
            /*if (showListeners != null) {
                for (Animator.AnimatorListener l : showListeners) {
                    set.addListener(l);
                }
            }*/
            set.start();

            /*mView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(SHOW_HIDE_ANIM_DURATION)
                    .setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mView.internalSetVisibility(View.VISIBLE, fromUser);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimState = ANIM_STATE_NONE;
                            if (listener != null) {
                                listener.onShown();
                            }
                        }
                    });*/
        } else {
            //mView.internalSetVisibility(View.VISIBLE, fromUser);
            mView.setAlpha(1f);
            mView.setScaleY(1f);
            mView.setScaleX(1f);

            //setImageMatrixScale(1f);

            if (listener != null) {
                listener.onShown();
            }
        }
    }

    protected final void updatePadding() {
        Rect rect = mTmpRect;
        getPadding(rect);
        onPaddingUpdated(rect);
        //mViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    protected boolean requirePreDrawListener() {
        return true;
    }

    protected GradientDrawable createShapeDrawable(boolean isCircle, float cornerRadius) {
        GradientDrawable d = newGradientDrawableForShape();
        d.setShape(isCircle ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);
        d.setColor(Color.WHITE);
        d.setCornerRadius(cornerRadius);
        return d;
    }

    protected GradientDrawable newGradientDrawableForShape() {
        return new GradientDrawable();
    }

    protected BorderDrawable createBorderDrawable(boolean isCircle, float cornerRadius, float width, ColorStateList color) {
        BorderDrawable borderDrawable = newBorderDrawable();
        borderDrawable.setCircle(isCircle);
        borderDrawable.setWidth(width);
        borderDrawable.setColor(color);
        borderDrawable.setCornerRadius(cornerRadius);
        return borderDrawable;
    }

    protected BorderDrawable newBorderDrawable() {
        return new BorderDrawable();
    }

    protected Drawable createRoundedDrawable(Drawable drawable) {
        RoundedBitmapDrawable roundedBitmapDrawable;

        if (!(drawable instanceof RoundedBitmapDrawable)) {
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mView.getResources(), getBitmap(drawable));
            roundedBitmapDrawable.setAntiAlias(true);
        } else {
            roundedBitmapDrawable = (RoundedBitmapDrawable) drawable;
        }

        if (mView.isCircle()) {
            roundedBitmapDrawable.setCornerRadius(0);
            roundedBitmapDrawable.setCircular(true);
        } else {
            roundedBitmapDrawable.setCircular(false);
            roundedBitmapDrawable.setCornerRadius(mView.getCornerRadius() / 3);
        }

        return roundedBitmapDrawable;
    }

    private ValueAnimator createElevationAnimator(@NonNull ShadowAnimatorImpl impl) {
        final ValueAnimator animator = new ValueAnimator();
        animator.setInterpolator(ELEVATION_ANIM_INTERPOLATOR);
        animator.setDuration(ELEVATION_ANIM_DURATION);
        animator.addListener(impl);
        animator.addUpdateListener(impl);
        animator.setFloatValues(0, 1);
        return animator;
    }

    @SuppressLint("RestrictedApi")
    private AnimatorSet createAnimator(@NonNull MotionSpec spec, float opacity, float scale) {
        List<Animator> animators = new ArrayList<>();

        Animator animator = ObjectAnimator.ofFloat(mView, View.ALPHA, opacity);
        spec.getTiming("opacity").apply(animator);
        animators.add(animator);

        animator = ObjectAnimator.ofFloat(mView, View.SCALE_X, scale);
        spec.getTiming("scale").apply(animator);
        animators.add(animator);

        animator = ObjectAnimator.ofFloat(mView, View.SCALE_Y, scale);
        spec.getTiming("scale").apply(animator);
        animators.add(animator);

        //calculateImageMatrixFromScale(iconScale, tmpMatrix);
        //animator = ObjectAnimator.ofObject(mView, new ImageMatrixProperty(), new MatrixEvaluator(), new Matrix(tmpMatrix));
        //spec.getTiming("iconScale").apply(animator);
        //animators.add(animator);

        AnimatorSet set = new AnimatorSet();
        AnimatorSetCompat.playTogether(set, animators);
        return set;
    }

    protected void onPreDraw() {
        final float rotation = mView.getRotation();
        if (mRotation != rotation) {
            mRotation = rotation;
            updateFromViewRotation();
        }
    }

    protected boolean isOrWillBeShown() {
        if (mView.getVisibility() != View.VISIBLE) {
            // If we not currently visible, return true if we're animating to be shown
            return mAnimState == ANIM_STATE_SHOWING;
        } else {
            // Otherwise if we're visible, return true if we're not animating to be hidden
            return mAnimState != ANIM_STATE_HIDING;
        }
    }

    protected boolean isOrWillBeHidden() {
        if (mView.getVisibility() == View.VISIBLE) {
            // If we currently visible, return true if we're animating to be hidden
            return mAnimState == ANIM_STATE_HIDING;
        } else {
            // Otherwise if we're not visible, return true if we're not animating to be shown
            return mAnimState != ANIM_STATE_SHOWING;
        }
    }

    protected boolean isVector(Drawable drawable) {
        return drawable instanceof VectorDrawableCompat;
    }

    protected boolean isTransition(Drawable drawable) {
        return drawable instanceof TransitionDrawable;
    }

    protected final Bitmap getBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ensurePreDrawListener() {
        if (mOnPreDrawListener == null) {
            mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ImageViewImplOld.this.onPreDraw();
                    return true;
                }
            };
        }
    }

    private boolean shouldAnimateVisibilityChange() {
        return ViewCompat.isLaidOut(mView) && !mView.isInEditMode();
    }

    private void updateFromViewRotation() {
        if (SDK_INT == Build.VERSION_CODES.KITKAT) {
            // KitKat seems to have an issue with views which are rotated with angles which are
            // not divisible by 90. Worked around by moving to software rendering in these cases.
            if ((mRotation % 90) != 0) {
                if (mView.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
                    mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            } else {
                if (mView.getLayerType() != View.LAYER_TYPE_NONE) {
                    mView.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            }
        }

        // Offset any View rotation
        if (mShadowDrawable != null) {
            mShadowDrawable.setRotation(-mRotation);
        }
        if (mBorderDrawable != null) {
            mBorderDrawable.setRotation(-mRotation);
        }
    }

    interface InternalVisibilityChangedListener {
        void onShown();

        void onHidden();
    }

    private abstract class ShadowAnimatorImpl extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        private boolean mValidValues;
        private float mShadowSizeStart;
        private float mShadowSizeEnd;

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            if (!mValidValues) {
                mShadowSizeStart = mShadowDrawable.getShadowSize();
                mShadowSizeEnd = getTargetShadowSize();
                mValidValues = true;
            }

            mShadowDrawable.setShadowSize(mShadowSizeStart + ((mShadowSizeEnd - mShadowSizeStart) * animator.getAnimatedFraction()));
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mShadowDrawable.setShadowSize(mShadowSizeEnd);
            mValidValues = false;
        }

        /**
         * @return the shadow size we want to animate to.
         */
        protected abstract float getTargetShadowSize();
    }

    private class ResetElevationAnimation extends ShadowAnimatorImpl {

        @Override
        protected float getTargetShadowSize() {
            return mElevation;
        }
    }

    private class ElevateToPressedTranslationZAnimation extends ShadowAnimatorImpl {

        @Override
        protected float getTargetShadowSize() {
            return mElevation + mPressedTranslationZ;
        }
    }

    private class ElevateToHoveredFocusedTranslationZAnimation extends ShadowAnimatorImpl {

        @Override
        protected float getTargetShadowSize() {
            return mElevation + mHoveredFocusedTranslationZ;
        }
    }

    private class DisabledElevationAnimation extends ShadowAnimatorImpl {

        @Override
        protected float getTargetShadowSize() {
            return 0f;
        }
    }
}
