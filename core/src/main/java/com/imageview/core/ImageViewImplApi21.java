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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;

import com.google.android.material.ripple.RippleUtils;
import com.google.android.material.shape.AbsoluteCornerSize;
import com.google.android.material.shape.CornerSize;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.View.VISIBLE;

/**
 * Created by Viнt@rь on 25.10.2019
 */
@RequiresApi(LOLLIPOP)
@TargetApi(LOLLIPOP)
class ImageViewImplApi21 extends ImageViewImpl {

    private final MaterialShapeDrawable mMaskDrawable;
    private final MaterialShapeDrawable mForegroundDrawable;
    private final RippleDrawable mRippleDrawable;

    @SuppressLint("RestrictedApi")
    protected ImageViewImplApi21(@NonNull ImageView view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(view, attrs, defStyleAttr, defStyleRes);

        view.setClipToOutline(true);
        view.setElevation(mElevation);

        mMaskDrawable = new MaterialShapeDrawable(getShapeAppearanceModel());
        mForegroundDrawable = new MaterialShapeDrawable(getShapeAppearanceModel());
        mForegroundDrawable.setFillColor(ColorStateList.valueOf(Color.TRANSPARENT));
        mForegroundDrawable.setStroke(mStrokeWidth, mStrokeColor);

        mRippleDrawable = new RippleDrawable(RippleUtils.sanitizeRippleDrawableColor(mRippleColor), mForegroundDrawable, mMaskDrawable);
        setForeground(mRippleDrawable);
    }

    @Override
    protected void onDrawableStateChanged(int[] state) {
        mRippleDrawable.setState(state);
    }

    @Override
    protected void drawableHotspotChanged(float x, float y) {
        mRippleDrawable.setHotspot(x, y);
    }

    @Override
    protected void jumpDrawableToCurrentState() {
        mRippleDrawable.jumpToCurrentState();
    }

    @Override
    protected final void updateStroke() {
    }

    @Override
    protected final void updateDrawable() {
    }

    @Override
    protected final void updatePadding(int padding) {
        if (isImageOverlap) {
            padding = 0;
        }
        super.updatePadding(padding);
    }

    @Override
    protected final void setCircle(boolean isCircle) {
        if (isCircle() != isCircle) {
            CornerSize cornerSize = isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius);
            mMaskDrawable.setCornerSize(cornerSize);
            mBackgroundDrawable.setCornerSize(cornerSize);
            mForegroundDrawable.setCornerSize(cornerSize);
        }
    }

    @Override
    public final boolean isImageOverlap() {
        return isImageOverlap;
    }

    @Override
    public final void setImageOverlap(boolean isImageOverlap) {
        if (this.isImageOverlap != isImageOverlap) {
            this.isImageOverlap = isImageOverlap;

            updatePadding((int) mStrokeWidth);
        }
    }

    @Override
    protected final void setCornerRadius(float cornerRadius) {
        if (getCornerRadius() != cornerRadius) {
            mCornerRadius = cornerRadius;

            mMaskDrawable.setCornerSize(mCornerRadius);
            mBackgroundDrawable.setCornerSize(mCornerRadius);
            mForegroundDrawable.setCornerSize(mCornerRadius);
        }
    }

    @Override
    protected final float getElevation() {
        return mView.getElevation();
    }

    @Override
    protected final void setElevation(float elevation) {
        mView.setElevation(elevation);
    }

    @Override
    protected final void setStrokeWidth(float strokeWidth) {
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth;

            mForegroundDrawable.setStrokeWidth(mStrokeWidth);
            updatePadding((int) mStrokeWidth);
        }
    }

    @Override
    protected final void setStrokeColor(@Nullable ColorStateList strokeColor) {
        if (mStrokeColor != strokeColor) {
            mStrokeColor = strokeColor;

            mForegroundDrawable.setStrokeColor(strokeColor);
        }
    }

    @Override
    @SuppressLint("RestrictedApi")
    protected void setRippleColor(@Nullable ColorStateList rippleColor) {
        if (mRippleColor != rippleColor) {
            mRippleColor = rippleColor;

            mRippleDrawable.setColor(RippleUtils.sanitizeRippleDrawableColor(mRippleColor));
        }
    }

    @Override
    protected final void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        super.setShapeAppearanceModel(shapeAppearanceModel);

        mMaskDrawable.setShapeAppearanceModel(shapeAppearanceModel);
        mForegroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
    }

    @Override
    protected final void setImageDrawable(Drawable drawable) {
        mView.setImageDrawableInternal(drawable);
    }

    protected void setForeground(@NonNull final Drawable foreground) {
        // setForeground analog for API 18 - 22
        mView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (v.getVisibility() == VISIBLE) {
                Rect bounds = new Rect();
                v.getDrawingRect(bounds);
                foreground.setBounds(bounds);
            }
        });
        mView.getOverlay().add(foreground);
    }
}
