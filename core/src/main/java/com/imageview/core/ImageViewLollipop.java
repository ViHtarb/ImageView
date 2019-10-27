package com.imageview.core;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

@RequiresApi(LOLLIPOP)
@TargetApi(LOLLIPOP)
class ImageViewLollipop extends ImageViewImplOld {

    protected ImageViewLollipop(ImageView view, ImageViewDelegate viewDelegate) {
        super(view, viewDelegate);
    }

    @Override
    protected void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, boolean isCircle, float cornerRadius, float borderWidth, ColorStateList borderColor) {
        // Now we need to tint the shape background with the tint
        mShapeDrawable = DrawableCompat.wrap(createShapeDrawable(isCircle, cornerRadius));
        DrawableCompat.setTintList(mShapeDrawable, backgroundTint == null ? mTransparentTint : backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
        }

        mBorderDrawable = createBorderDrawable(isCircle, cornerRadius, borderWidth, borderColor);
        mContentBackground = new LayerDrawable(new Drawable[]{mShapeDrawable, mBorderDrawable});
        mViewDelegate.setBackgroundDrawable(mContentBackground);
    }

    @Override
    protected void onElevationsChanged(final float elevation, final float pressedTranslationZ, final float hoveredFocusedTranslationZ) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            // Animations produce NPE in version 21. Bluntly set the values instead in
            // #onDrawableStateChanged (matching the logic in the animations below).
            mView.refreshDrawableState();
        } else {
            final StateListAnimator stateListAnimator = new StateListAnimator();

            // Animate elevation and translationZ to our values when pressed, focused, and hovered
            stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createElevationAnimator(elevation, pressedTranslationZ));
            stateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));
            stateListAnimator.addState(HOVERED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));
            stateListAnimator.addState(HOVERED_FOCUSED_ENABLED_STATE_SET, createElevationAnimator(elevation, hoveredFocusedTranslationZ));

            // Animate translationZ to 0 if not pressed, focused, or hovered
            AnimatorSet set = new AnimatorSet();
            List<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(mView, "elevation", elevation).setDuration(0));
            if (Build.VERSION.SDK_INT >= 22 && Build.VERSION.SDK_INT <= 24) {
                // This is a no-op animation which exists here only for introducing the duration
                // because setting the delay (on the next animation) via "setDelay" or "after"
                // can trigger a NPE between android versions 22 and 24 (due to a framework
                // bug). The issue has been fixed in version 25.
                animators.add(ObjectAnimator.ofFloat(mView, View.TRANSLATION_Z, mView.getTranslationZ()).setDuration(ELEVATION_ANIM_DELAY));
            }
            animators.add(ObjectAnimator.ofFloat(mView, View.TRANSLATION_Z, 0f).setDuration(ELEVATION_ANIM_DURATION));
            set.playSequentially(animators.toArray(new Animator[0]));
            set.setInterpolator(ELEVATION_ANIM_INTERPOLATOR);
            stateListAnimator.addState(ENABLED_STATE_SET, set);

            // Animate everything to 0 when disabled
            stateListAnimator.addState(EMPTY_STATE_SET, createElevationAnimator(0f, 0f));

            mView.setStateListAnimator(stateListAnimator);
        }

        /*if (mViewDelegate.isCompatPadding()) {
            updatePadding();
        }*/
    }

    @Override
    protected void getPadding(Rect rect) {
        /*if (mViewDelegate.isCompatPadding()) {
            final float radius = mViewDelegate.getRadius();
            final float maxShadowSize = getElevation() + getPressedTranslationZ();
            final int hPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateHorizontalPadding(maxShadowSize, radius, false));
            final int vPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateVerticalPadding(maxShadowSize, radius, false));
            rect.set(hPadding, vPadding, hPadding, vPadding);
        } else {
            rect.set(0, 0, 0, 0);
        }*/
    }

    @Override
    public float getElevation() {
        return mView.getElevation();
    }

    @Override
    protected void onCompatShadowChanged() {
        updatePadding();
    }

    @Override
    protected void onPaddingUpdated(Rect padding) {
        /*if (mViewDelegate.isCompatPadding()) {
            mViewDelegate.setBackgroundDrawable(new InsetDrawable(mContentBackground, padding.left, padding.top, padding.right, padding.bottom));
        } else {
            mViewDelegate.setBackgroundDrawable(mContentBackground);
        }*/
    }

    @Override
    protected void onDrawableStateChanged(int[] state) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            if (mView.isEnabled()) {
                mView.setElevation(getElevation());
                if (mView.isPressed()) {
                    mView.setTranslationZ(getPressedTranslationZ());
                } else if (mView.isFocused() || mView.isHovered()) {
                    mView.setTranslationZ(getHoveredFocusedTranslationZ());
                } else {
                    mView.setTranslationZ(0);
                }
            } else {
                mView.setElevation(0);
                mView.setTranslationZ(0);
            }
        }
    }

    @Override
    protected void jumpDrawableToCurrentState() {
        // no-op
    }

    @Override
    protected boolean requirePreDrawListener() {
        return false;
    }

    @Override
    protected GradientDrawable newGradientDrawableForShape() {
        return new AlwaysStatefulGradientDrawable();
    }

    @Override
    protected BorderDrawable newBorderDrawable() {
        return new BorderDrawableLollipop();
    }

    @Override
    protected boolean isVector(Drawable drawable) {
        return drawable instanceof VectorDrawable || super.isVector(drawable);
    }

    @NonNull
    private Animator createElevationAnimator(float elevation, float translationZ) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mView, "elevation", elevation).setDuration(0))
                .with(ObjectAnimator.ofFloat(mView, View.TRANSLATION_Z, translationZ).setDuration(ELEVATION_ANIM_DURATION));
        set.setInterpolator(ELEVATION_ANIM_INTERPOLATOR);
        return set;
    }

    /**
     * LayerDrawable on L+ caches its isStateful() state and doesn't refresh it,
     * meaning that if we apply a tint to one of its children, the parent doesn't become
     * stateful and the tint doesn't work for state changes. We workaround it by saying that we
     * are always stateful. If we don't have a stateful tint, the change is ignored anyway.
     */
    private static class AlwaysStatefulGradientDrawable extends GradientDrawable {
        @Override
        public boolean isStateful() {
            return true;
        }
    }
}
