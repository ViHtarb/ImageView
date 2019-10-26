package com.imageview.core;

import android.annotation.SuppressLint;
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
import android.util.AttributeSet;
import android.view.View;

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
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;

import static android.view.View.VISIBLE;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Created by Viнt@rь on 09.09.2018
 */
@RestrictTo(LIBRARY_GROUP)
class ImageViewHelper {
    private static final int[] EMPTY_STATE_SET = new int[0];
    private static final int[] ENABLED_STATE_SET = {android.R.attr.state_enabled};
    private static final int[] PRESSED_ENABLED_STATE_SET = {android.R.attr.state_pressed, android.R.attr.state_enabled};
    private static final int[] FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_focused, android.R.attr.state_enabled};
    private static final int[] HOVERED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_enabled};
    private static final int[] HOVERED_FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_hovered, android.R.attr.state_focused, android.R.attr.state_enabled};

    private static final ColorStateList TRANSPARENT_TINT = ColorStateList.valueOf(Color.TRANSPARENT);

    private final Context mContext;
    private final View mView;
    private final ImageViewDelegate mDelegate;

    private boolean isCircle;
    private boolean isCompatPadding;

    private float mCornerRadius;
    private float mStrokeWidth;
    private float mRotation;
    private float mElevation;
    private float mPressedTranslationZ;
    private float mHoveredFocusedTranslationZ;

    private ColorStateList mStrokeColor;

    private ColorStateList mBackgroundTint;
    private PorterDuff.Mode mBackgroundTintMode;

    private MotionSpec mShowMotionSpec;
    private MotionSpec mHideMotionSpec;

    //private ShapeAppearanceModel mShapeAppearanceModel;

    private MaterialShapeDrawable mBackgroundDrawable;
    private MaterialShapeDrawable mForegroundDrawable;
    //private Drawable mRippleDrawable;

    protected ImageViewHelper(@NonNull View view, @NonNull ImageViewDelegate delegate) {
        mContext = view.getContext();
        mView = view;
        mDelegate = delegate;
    }

    @SuppressLint("RestrictedApi")
    protected void loadFromAttributes(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(mContext, attrs, R.styleable.ImageView, defStyleAttr, defStyleRes);

        isCircle = a.getBoolean(R.styleable.ImageView_circle, false);
        //isCompatPadding = a.getBoolean(R.styleable.ImageView_useCompatPadding, false);

        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mStrokeColor = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_borderColor);

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
        //mBackgroundDrawable.setStroke(mStrokeWidth, mStrokeColor);
        mBackgroundDrawable.setTintList(mBackgroundTint);
        mBackgroundDrawable.setTintMode(mBackgroundTintMode);
        mBackgroundDrawable.setElevation(mElevation);

        int strokeWidth = (int) mStrokeWidth;
        mBackgroundDrawable.setPadding(strokeWidth, strokeWidth, strokeWidth, strokeWidth);

        mForegroundDrawable = new MaterialShapeDrawable(getShapeAppearanceModel());
        mForegroundDrawable.setFillColor(TRANSPARENT_TINT);
        mForegroundDrawable.setStroke(mStrokeWidth, mStrokeColor);

        //mRippleDrawable = new RippleDrawable(RippleUtils.sanitizeRippleDrawableColor(ColorStateList.valueOf(Color.WHITE)), mForegroundDrawable, new MaterialShapeDrawable(getShapeAppearanceModel()));

        mDelegate.setBackgroundDrawable(mBackgroundDrawable);

        //mView.setForeground(mForegroundDrawable);

        // setForeground analog for API 18 - 22
        mView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (v.getVisibility() == VISIBLE) {
                Rect badgeBounds = new Rect();
                v.getDrawingRect(badgeBounds);
                mForegroundDrawable.setBounds(badgeBounds);
            }
        });
        mView.getOverlay().add(mForegroundDrawable);

        //ViewCompat.setElevation(mView, mElevation);

        /*ImageView imageView = (ImageView) mView;
        if (imageView.getDrawable() != null) {
            setImageDrawable(imageView.getDrawable());
        }*/
    }

    protected final boolean isCircle() {
        return isCircle;
    }

    protected final void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) {
            this.isCircle = isCircle;

            CornerSize cornerSize = isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius);
            mBackgroundDrawable.setCornerSize(cornerSize);
            mForegroundDrawable.setCornerSize(cornerSize);

            ImageView imageView = (ImageView) mView;

            //if (isCircle) {
                //((RoundedBitmapDrawable) imageView.getDrawable()).setCircular(isCircle);
            //} else {
                //((RoundedBitmapDrawable) imageView.getDrawable()).setCornerRadius(mCornerRadius * 2.5f);
            //}
            /*if (imageView.getDrawable() != null) {
                setImageDrawable(imageView.getDrawable());
            }*/
        }
    }

    protected final float getCornerRadius() {
        return mCornerRadius;
    }

    protected final void setCornerRadius(float cornerRadius) {
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setCornerSize(cornerRadius);
            }
        }
    }

    protected final float getElevation() {
        return mElevation;
    }

    protected final void setElevation(float elevation) {
        if (mElevation != elevation) {
            mElevation = elevation;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setElevation(elevation);
            }
        }
    }

    protected final float getStrokeWidth() {
        return mStrokeWidth;
    }

    protected final void setStrokeWidth(float strokeWidth) {
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setStrokeWidth(mStrokeWidth);
            }
        }
    }

    protected final ColorStateList getStrokeColor() {
        return mStrokeColor;
    }

    protected final void setStrokeColor(@Nullable ColorStateList strokeColor) {
        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setStrokeColor(mStrokeColor);
            }
        }
    }

    protected final ColorStateList getBackgroundTintList() {
        return mBackgroundTint;
    }

    protected final void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (mBackgroundTint != tint) {
            mBackgroundTint = tint;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setTintList(mBackgroundTint);
            }
        }
    }

    protected final PorterDuff.Mode getBackgroundTintMode() {
        return mBackgroundTintMode;
    }

    protected final void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mBackgroundTintMode != tintMode) {
            mBackgroundTintMode = tintMode;
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.setTintMode(mBackgroundTintMode);
            }
        }
    }

    protected final MaterialShapeDrawable getBackgroundDrawable() {
        return mBackgroundDrawable;
    }

    protected final ShapeAppearanceModel getShapeAppearanceModel() {
        return mBackgroundDrawable.getShapeAppearanceModel();
    }

    protected final void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        mBackgroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
    }

    protected final void setImageDrawable(@Nullable Drawable drawable) {
        //LayerDrawable drawable1 = new LayerDrawable(new Drawable[] {drawable, mForegroundDrawable});
        //mDelegate.setImageDrawable(drawable1);

        mDelegate.setImageDrawable(drawable);
        //mDelegate.setImageDrawable(createRoundedDrawable(drawable));
    }

    protected Drawable createRoundedDrawable(Drawable drawable) {
        RoundedBitmapDrawable roundedBitmapDrawable;

        if (!(drawable instanceof RoundedBitmapDrawable)) {
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mView.getResources(), getBitmap(drawable));
            roundedBitmapDrawable.setAntiAlias(true);
        } else {
            roundedBitmapDrawable = (RoundedBitmapDrawable) drawable;
        }

        //if (isCircle()) {
            roundedBitmapDrawable.setCircular(isCircle);
        //} else {
        //    roundedBitmapDrawable.setCornerRadius(getCornerRadius() * 2.7f);
        //}

        return roundedBitmapDrawable;
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
}
