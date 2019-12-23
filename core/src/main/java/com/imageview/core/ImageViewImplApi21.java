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
import androidx.core.view.ViewCompat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Viнt@rь on 25.10.2019
 */
@RequiresApi(LOLLIPOP)
@TargetApi(LOLLIPOP)
class ImageViewImplApi21 extends ImageViewImpl {

    @SuppressLint("RestrictedApi")
    protected ImageViewImplApi21(@NonNull ImageView view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(view, attrs, defStyleAttr, defStyleRes);

        view.setClipToOutline(true);
        view.setElevation(mElevation);
    }

    @Override
    protected void drawableHotspotChanged(float x, float y) {
        mRippleDrawable.setHotspot(x, y);
    }

    @Override
    protected final int fixPadding(int padding) {
        return padding;
    }

    @Override
    protected final void updateDrawable() {
    }

    @Override
    protected final void setCircle(boolean isCircle) {
        if (isCircle() != isCircle || !ViewCompat.isLaidOut(mView)) {
            CornerSize cornerSize = isCircle ? ShapeAppearanceModel.PILL : new AbsoluteCornerSize(mCornerRadius);
            mBackgroundDrawable.setCornerSize(cornerSize);
            mForegroundDrawable.setCornerSize(cornerSize);
            mMaskDrawable.setCornerSize(cornerSize);
        }
    }

    @Override
    protected final void setCornerRadius(float cornerRadius) {
        if (getCornerRadius() != cornerRadius) {
            mCornerRadius = cornerRadius;
            mBackgroundDrawable.setCornerSize(mCornerRadius);
            mForegroundDrawable.setCornerSize(mCornerRadius);
            mMaskDrawable.setCornerSize(mCornerRadius);
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
    @SuppressLint("RestrictedApi")
    protected final void setRippleColor(@Nullable ColorStateList rippleColor) {
        if (mRippleColor != rippleColor) {
            mRippleColor = rippleColor;

            ((RippleDrawable) mRippleDrawable).setColor(RippleUtils.sanitizeRippleDrawableColor(mRippleColor));
        }
    }

    @Override
    protected final void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        mBackgroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
        mForegroundDrawable.setShapeAppearanceModel(shapeAppearanceModel);
        mMaskDrawable.setShapeAppearanceModel(shapeAppearanceModel);
    }

    @Override
    protected final Drawable mergeForegroundDrawable() {
        return mRippleDrawable;
    }

    @Override
    protected final MaterialShapeDrawable createMaskDrawable() {
        return new MaterialShapeDrawable(getShapeAppearanceModel());
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected final Drawable createRippleDrawable() {
        return new RippleDrawable(RippleUtils.sanitizeRippleDrawableColor(mRippleColor), mForegroundDrawable, mMaskDrawable);
    }

    @Override
    protected final void setImageDrawable(Drawable drawable) {
        mView.setImageDrawableInternal(drawable);
    }
}
