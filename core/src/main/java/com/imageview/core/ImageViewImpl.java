/*
 *  The MIT License (MIT)
 *  <p/>
 *  Copyright (c) 2019. Viнt@rь
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

    private final Rect mUserPadding = new Rect();

    protected final Context mContext;
    protected final ImageView mView;
    protected final MaterialShapeDrawable mBackgroundDrawable;

    protected boolean isImageOverlap;

    protected float mCornerRadius;
    protected float mStrokeWidth;
    protected float mElevation;

    protected ColorStateList mStrokeColor;
    protected ColorStateList mRippleColor;

    protected ColorStateList mBackgroundTint;
    protected PorterDuff.Mode mBackgroundTintMode;

    @SuppressLint("RestrictedApi")
    protected ImageViewImpl(@NonNull ImageView view, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        mContext = view.getContext();
        mView = view;
        mUserPadding.set(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());

        TypedArray a = ThemeEnforcement.obtainStyledAttributes(mContext, attrs, R.styleable.ImageView, defStyleAttr, defStyleRes);

        boolean isCircle = a.getBoolean(R.styleable.ImageView_circle, false);
        isImageOverlap = a.getBoolean(R.styleable.ImageView_imageOverlap, false);

        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);
        mStrokeWidth = a.getDimension(R.styleable.ImageView_strokeWidth, 0);
        mStrokeColor = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_strokeColor);
        mRippleColor = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_rippleColor);

        mElevation = a.getDimension(R.styleable.ImageView_elevation, ViewCompat.getElevation(mView));

        mBackgroundTint = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_backgroundTint);
        mBackgroundTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.ImageView_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);

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
        mView.post(() -> setImageDrawable(mView.getDrawable()));
    }

    protected void updatePadding(@Px int padding) {
        mView.setPaddingInternal(mUserPadding.left + padding, mUserPadding.top + padding, mUserPadding.right + padding, mUserPadding.bottom + padding);
    }

    protected final void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mUserPadding.set(left, top, right, bottom);
        updatePadding((int) mStrokeWidth);
    }

    protected final boolean isCircle() {
        return mBackgroundDrawable.getBottomRightCornerResolvedSize() == mView.getHeight() * 0.5;
    }

    protected void setCircle(boolean isCircle) {
        if (isCircle() != isCircle) {
            CornerSize cornerSize = isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius);
            mBackgroundDrawable.setCornerSize(cornerSize);

            // update the image view drawable with round rect drawable, needs only on pre-lollipop to provide something similar to outline provider
            setImageDrawable(mView.getDrawable());
        }
    }

    public boolean isImageOverlap() {
        return false;
    }

    public void setImageOverlap(boolean isImageOverlap) {
    }

    protected final float getCornerRadius() {
        return mBackgroundDrawable.getBottomRightCornerResolvedSize();
    }

    protected void setCornerRadius(float cornerRadius) {
        if (getCornerRadius() != cornerRadius) {
            mCornerRadius = cornerRadius;

            mBackgroundDrawable.setCornerSize(cornerRadius);

            // update the image view drawable with round rect drawable, needs only on pre-lollipop to provide something similar to outline provider
            setImageDrawable(mView.getDrawable());
        }
    }

    protected float getElevation() {
        return mElevation;
    }

    protected void setElevation(float elevation) {
        if (mElevation != elevation) {
            mElevation = elevation;

            mBackgroundDrawable.setElevation(elevation);
        }
    }

    protected final float getStrokeWidth() {
        return mStrokeWidth;
    }

    protected void setStrokeWidth(float strokeWidth) {
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

    protected void setStrokeColor(@Nullable ColorStateList strokeColor) {
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

    protected final void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (mBackgroundTint != tint) {
            mBackgroundTint = tint;

            mBackgroundDrawable.setTintList(mBackgroundTint);
        }
    }

    protected final PorterDuff.Mode getBackgroundTintMode() {
        return mBackgroundTintMode;
    }

    protected final void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mBackgroundTintMode != tintMode) {
            mBackgroundTintMode = tintMode;

            mBackgroundDrawable.setTintMode(mBackgroundTintMode);
        }
    }

    protected final MaterialShapeDrawable getBackgroundDrawable() {
        return mBackgroundDrawable;
    }

    protected final ShapeAppearanceModel getShapeAppearanceModel() {
        return mBackgroundDrawable.getShapeAppearanceModel();
    }

    protected void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        mBackgroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
    }

    protected void setImageDrawable(Drawable drawable) { // TODO mb need to check is vector drawable out of view bounds
        if (drawable != null && getCornerRadius() > 0) {
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
            float cornerRadius = getCornerRadius();
            if (mView.getHeight() > 0) {
                cornerRadius *= Math.max(1, roundedBitmapDrawable.getBounds().height() / mView.getHeight());
            }
            roundedBitmapDrawable.setCornerRadius(cornerRadius);
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
