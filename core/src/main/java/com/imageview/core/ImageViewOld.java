/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016. Viнt@rь
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.imageview.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Image view implementation with switchable modes
 * // TODO implement AppCompat support tinting
 * // TODO implement elevation for non android L
 * // TODO implement pressedTranslationZ
 * // TODO implement touchFeedback
 */
public abstract class ImageViewOld extends VisibilityAwareImageView {

    private static final String LOG_TAG = "ImageView";

    private boolean isCircle;

    private float mBorderWidth;
    private float mCornerRadius;

    private final Rect mShadowPadding = new Rect();

    private ColorStateList mBorderColor;

    private ColorStateList mBackgroundTint;
    private PorterDuff.Mode mBackgroundTintMode;

    private ImageViewImpl mImpl;

    public ImageViewOld(Context context) {
        this(context, null);
    }

    public ImageViewOld(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

        isCircle = a.getBoolean(R.styleable.ImageView_circle, false);

        mBorderWidth = a.getDimension(R.styleable.ImageView_borderWidth, 0);
        mBorderColor = a.getColorStateList(R.styleable.ImageView_borderColor);
        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);

        mBackgroundTint = ViewCompat.getBackgroundTintList(this);
        mBackgroundTintMode = ViewCompat.getBackgroundTintMode(this);

        /*mBackgroundTint = a.getColorStateList(R.styleable.ImageView_android_backgroundTint);
        mBackgroundTintMode = parseTintMode(a.getInt(R.styleable.ImageView_android_backgroundTintMode, -1), null);*/

        //getImpl().setBackgroundDrawable(mBackgroundTint, mBackgroundTintMode, isCircle, mBorderWidth, mBorderColor, mCornerRadius);

        a.recycle();
    }

    /**
     * Applies a tint to the background drawable. Does not modify the current tint
     * mode, which is {@link PorterDuff.Mode#SRC_IN} by default.
     *
     * @param tint the tint to apply, may be {@code null} to clear tint
     */
    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (mBackgroundTint != tint) {
            mBackgroundTint = tint;
            getImpl().setBackgroundTintList(tint);
        }
    }

    /**
     * Returns the tint applied to the background drawable, if specified.
     *
     * @return the tint applied to the background drawable
     * @see #setBackgroundTintList(ColorStateList)
     */
    @Nullable
    @Override
    public ColorStateList getBackgroundTintList() {
        return mBackgroundTint;
    }

    /**
     * Specifies the blending mode used to apply the tint specified by
     * {@link #setBackgroundTintList(ColorStateList)}} to the background
     * drawable. The default mode is {@link PorterDuff.Mode#SRC_IN}.
     *
     * @param tintMode the blending mode used to apply the tint, may be
     *                 {@code null} to clear tint
     */
    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mBackgroundTintMode != tintMode) {
            mBackgroundTintMode = tintMode;
            getImpl().setBackgroundTintMode(tintMode);
        }
    }

    /**
     * Returns the blending mode used to apply the tint to the background
     * drawable, if specified.
     *
     * @return the blending mode used to apply the tint to the background
     *         drawable
     * @see #setBackgroundTintMode(PorterDuff.Mode)
     */
    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return mBackgroundTintMode;
    }

/* // TODO override methods for tinting from AppCompat
    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        setBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return getBackgroundTintList();
    }

    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        setBackgroundTintMode(tintMode);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return getBackgroundTintMode();
    }
*/

    @Override
    public void setBackgroundDrawable(Drawable background) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resId) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        //getImpl().setImageDrawable(getDrawable());
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        //getImpl().setImageDrawable(drawable);
    }

    public void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) {
            this.isCircle = isCircle;
            //getImpl().setCircle(isCircle);

            setImageDrawable(getDrawable());
        }
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setBorderColor(@ColorInt int color) {
        setBorderColor(ColorStateList.valueOf(color));
    }

    public void setBorderColor(ColorStateList color) {
        if (mBorderColor != color) {
            mBorderColor = color;
            //getImpl().setBorderColor(color);
        }
    }

    public ColorStateList getBorderColor() {
        return mBorderColor;
    }

    public void setBorderWidth(@DimenRes int resId) {
        setBorderWidth(getResources().getDimension(resId));
    }

    public void setBorderWidth(float width) {
        if (mBorderWidth != width) {
            mBorderWidth = width;
            //getImpl().setBorderWidth(mBorderWidth);
        }
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setCornerRadius(@DimenRes int resId) {
        setCornerRadius(getResources().getDimension(resId));
    }

    public void setCornerRadius(float radius) {
        if (mCornerRadius != radius) {
            mCornerRadius = radius;
            //getImpl().setCornerRadius(radius);
        }
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    private ImageViewImpl getImpl() {
        if (mImpl == null) {
            mImpl = createImpl();
        }
        return mImpl;
    }

    private ImageViewImpl createImpl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //return new ImageViewLollipop(this, new ViewDelegateOldImpl());
        }
        //return new ImageViewImpl(this, new ViewDelegateOldImpl());
        return null;
    }

    private class ViewDelegateOldImpl implements ViewDelegate {

        @Override
        public float getRadius() {
            return /*getSizeDimension() / 2f*/0f;
        }

        @Override
        public void setShadowPadding(int left, int top, int right, int bottom) {
            mShadowPadding.set(left, top, right, bottom);
/*            setPadding(left + mImagePadding, top + mImagePadding,
                    right + mImagePadding, bottom + mImagePadding);*/
        }

        @Override
        public void setBackgroundDrawable(Drawable background) {
            ImageViewOld.super.setBackgroundDrawable(background);
        }

        @Override
        public boolean isCompatPadding() {
            return false;
        }

        @Override
        public void setImageDrawable(Drawable drawable) {

        }
    }

    private class ViewDelegateImpl implements ViewDelegateOld {

        @Override
        public void setBackgroundDrawable(Drawable background) {
            ImageViewOld.super.setBackgroundDrawable(background);
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            ImageViewOld.super.setImageDrawable(drawable);
        }
    }

/*    // TODO replace it
    static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            default:
                return defaultMode;
        }
    }*/
}