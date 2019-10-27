package com.imageview.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;

import com.google.android.material.animation.MotionSpec;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.AbsoluteCornerSize;
import com.google.android.material.shape.CornerSize;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Created by Viнt@rь on 25.10.2019
 */
@RequiresApi(ICE_CREAM_SANDWICH)
@TargetApi(ICE_CREAM_SANDWICH)
class ImageViewImpl {
    private static final int NO_ID = 0;

    private static final int[] EMPTY_STATE_SET = new int[0];
    private static final int[] ENABLED_STATE_SET = {android.R.attr.state_enabled};
    private static final int[] PRESSED_ENABLED_STATE_SET = {android.R.attr.state_pressed, android.R.attr.state_enabled};
    private static final int[] FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_focused, android.R.attr.state_enabled};
    private static final int[] HOVERED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_enabled};
    private static final int[] HOVERED_FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_focused, android.R.attr.state_enabled};

    private final Rect mUserPadding = new Rect();

    protected final Context mContext;
    protected final ImageView mView;
    protected final MaterialShapeDrawable mBackgroundDrawable;

    protected boolean isCircle;
    protected boolean isImageOverlap;
    //protected boolean isCompatPadding;

    protected float mCornerRadius;
    protected float mStrokeWidth;
    //protected float mRotation;
    protected float mElevation;
    //protected float mPressedTranslationZ;
    //protected float mHoveredFocusedTranslationZ;

    protected ColorStateList mStrokeColor;
    protected ColorStateList mRippleColor;

    protected ColorStateList mBackgroundTint;
    protected PorterDuff.Mode mBackgroundTintMode;

    protected MotionSpec mShowMotionSpec;
    protected MotionSpec mHideMotionSpec;

    @SuppressLint("RestrictedApi")
    protected ImageViewImpl(@NonNull ImageView view, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        mContext = view.getContext();
        mView = view;
        mUserPadding.set(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());

        TypedArray a = ThemeEnforcement.obtainStyledAttributes(mContext, attrs, R.styleable.ImageView, defStyleAttr, defStyleRes);

        isCircle = a.getBoolean(R.styleable.ImageView_circle, false);
        isImageOverlap = a.getBoolean(R.styleable.ImageView_imageOverlap, false);
        //isCompatPadding = a.getBoolean(R.styleable.ImageView_useCompatPadding, false);

        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);
        mStrokeWidth = a.getDimension(R.styleable.ImageView_strokeWidth, 0);
        mStrokeColor = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_strokeColor);
        mRippleColor = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_rippleColor);

        mElevation = a.getDimension(R.styleable.ImageView_elevation, ViewCompat.getElevation(mView));
        //mPressedTranslationZ = a.getDimension(R.styleable.ImageView_pressedTranslationZ, 0f);
        //mHoveredFocusedTranslationZ = a.getDimension(R.styleable.ImageView_hoveredFocusedTranslationZ, 0f);

        mBackgroundTint = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_backgroundTint);
        mBackgroundTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.ImageView_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);

        mShowMotionSpec = MotionSpec.createFromAttribute(mContext, a, R.styleable.ImageView_showMotionSpec);
        mHideMotionSpec = MotionSpec.createFromAttribute(mContext, a, R.styleable.ImageView_hideMotionSpec);

        a.recycle();

        mBackgroundDrawable = new MaterialShapeDrawable(mContext, attrs, defStyleAttr, defStyleRes);
        mBackgroundDrawable.setShadowColor(Color.DKGRAY);
        mBackgroundDrawable.initializeElevationOverlay(mContext);
        mBackgroundDrawable.setCornerSize(isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius));
        mBackgroundDrawable.setTintList(mBackgroundTint);
        mBackgroundDrawable.setTintMode(mBackgroundTintMode);
        mBackgroundDrawable.setElevation(mElevation);

        mView.setBackgroundInternal(mBackgroundDrawable);

        updateStroke();
        updateDrawable();
        updatePadding((int) mStrokeWidth);
    }

    protected void onDrawableStateChanged(int[] state) {
    }

    protected void drawableHotspotChanged(float x, float y) {
    }

    protected void jumpDrawableToCurrentState() {
    }

    protected void updateStroke() {
        mBackgroundDrawable.setStroke(mStrokeWidth, mStrokeColor);
    }

    protected void updateDrawable() {
        if (mView.getDrawable() != null) {
            setImageDrawable(mView.getDrawable());
        }
    }

    protected void updatePadding(@Px int padding) {
        mView.setPaddingInternal(mUserPadding.left + padding, mUserPadding.top + padding, mUserPadding.right + padding, mUserPadding.bottom + padding);
    }

    protected final void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mUserPadding.set(left, top, right, bottom);
        updatePadding((int) mStrokeWidth);
    }

    protected final boolean isCircle() {
        return isCircle;
    }

    protected void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) {
            this.isCircle = isCircle;

            CornerSize cornerSize = isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius);
            mBackgroundDrawable.setCornerSize(cornerSize);

            // update the image view drawable with round rect drawable, needs only on pre-lollipop to provide something similar to outline provider
            if (mView.getDrawable() != null) {
                setImageDrawable(mView.getDrawable());
            }
        }
    }

    public boolean isImageOverlap() {
        return false;
    }

    public void setImageOverlap(boolean isImageOverlap) {
    }

    protected final float getCornerRadius() {
        return mCornerRadius;
    }

    protected void setCornerRadius(float cornerRadius) { // not checks
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;

            mBackgroundDrawable.setCornerSize(cornerRadius);

            // update the image view drawable with round rect drawable, needs only on pre-lollipop to provide something similar to outline provider
            if (mView.getDrawable() != null) {
                setImageDrawable(mView.getDrawable());
            }
        }
    }

    protected float getElevation() {
        return mElevation;
    }

    protected void setElevation(float elevation) { // checks
        if (mElevation != elevation) {
            mElevation = elevation;

            mBackgroundDrawable.setElevation(elevation);
        }
    }

    protected final float getStrokeWidth() {
        return mStrokeWidth;
    }

    protected void setStrokeWidth(float strokeWidth) { // not checks
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth;

            mBackgroundDrawable.setStrokeWidth(mStrokeWidth);
            updatePadding((int) mStrokeWidth);
        }
    }

    @Nullable
    protected final ColorStateList getStrokeColor() {
        return mStrokeColor;
    }

    protected void setStrokeColor(@Nullable ColorStateList strokeColor) { // checks
        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor;

            mBackgroundDrawable.setStrokeColor(mStrokeColor);
        }
    }

    @Nullable
    protected final ColorStateList getRippleColor() {
        return mRippleColor;
    }

    protected void setRippleColor(@Nullable ColorStateList rippleColor) {
        if (mRippleColor != rippleColor) {
            mRippleColor = rippleColor;
        }
    }

    protected final ColorStateList getBackgroundTintList() {
        return mBackgroundTint;
    }

    protected final void setBackgroundTintList(@Nullable ColorStateList tint) { // not checks
        if (mBackgroundTint != tint) {
            mBackgroundTint = tint;

            mBackgroundDrawable.setTintList(mBackgroundTint);
        }
    }

    protected final PorterDuff.Mode getBackgroundTintMode() {
        return mBackgroundTintMode;
    }

    protected final void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) { // checks
        if (mBackgroundTintMode != tintMode) {
            mBackgroundTintMode = tintMode;

            mBackgroundDrawable.setTintMode(mBackgroundTintMode);
        }
    }

    protected final MaterialShapeDrawable getBackgroundDrawable() {
        return mBackgroundDrawable;
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

    protected final ShapeAppearanceModel getShapeAppearanceModel() {
        return mBackgroundDrawable.getShapeAppearanceModel();
    }

    protected void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        mBackgroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
    }

    protected void setImageDrawable(Drawable drawable) { // TODO mb need to check is vector drawable out of view bounds
        if (getCornerRadius() > 0 || isCircle()) {
            boolean isTransition = isTransition(drawable);

            if (!isTransition) {
                drawable = createRoundedDrawable(drawable);
            } else {
                final TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
                for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                    Drawable childDrawable = transitionDrawable.getDrawable(i);
                    int id = transitionDrawable.getId(i);
                    if (id == NO_ID) {
                        id = i + 1;
                        transitionDrawable.setId(i, id);
                    }
                    transitionDrawable.setDrawableByLayerId(id, createRoundedDrawable(childDrawable));
                }
            }
        }

        mView.setImageDrawableInternal(drawable);
    }

    private Drawable createRoundedDrawable(Drawable drawable) {
        RoundedBitmapDrawable roundedBitmapDrawable;

        if (!(drawable instanceof RoundedBitmapDrawable)) {
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mView.getResources(), getBitmap(drawable));
            roundedBitmapDrawable.setAntiAlias(true);
        } else {
            roundedBitmapDrawable = (RoundedBitmapDrawable) drawable;
        }

        if (isCircle()) {
            roundedBitmapDrawable.setCircular(true);
        } else {
            roundedBitmapDrawable.setCornerRadius(getCornerRadius()/* * 2.7f*/);
        }

        return roundedBitmapDrawable;
    }

    private Bitmap getBitmap(Drawable drawable) {
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

    private boolean isVector(Drawable drawable) {
        return drawable instanceof VectorDrawableCompat;
    }

    private boolean isTransition(Drawable drawable) {
        return drawable instanceof TransitionDrawable;
    }
}
