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

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * A drawable which draws an 'border'.
 */
class BorderDrawable extends Drawable {

    /**
     * We actually draw the stroke wider than the border size given. This is to reduce any
     * potential transparent space caused by anti-aliasing and padding rounding.
     * This value defines the multiplier used to determine to draw stroke width.
     */
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;

    private boolean isCircle;

    private int mCurrentBorderTintColor;
    private ColorStateList mBorderColor;

    protected int mBorderWidth;

    protected final Paint mPaint;
    protected final Rect mRect = new Rect();
    protected final RectF mRectF = new RectF();

    public BorderDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(mCurrentBorderTintColor);

        final float halfBorderWidth = mPaint.getStrokeWidth() / 2f;
        final RectF rectF = mRectF;

        // We need to inset the oval bounds by half the border width. This is because stroke draws
        // the center of the border on the dimension. Whereas we want the stroke on the inside.
        copyBounds(mRect);
        rectF.set(mRect);
        rectF.left += halfBorderWidth;
        rectF.top += halfBorderWidth;
        rectF.right -= halfBorderWidth;
        rectF.bottom -= halfBorderWidth;

        canvas.save();

        if (isCircle) {
            canvas.drawOval(rectF, mPaint);
        } else {
            canvas.drawRect(rectF, mPaint);
        }
        canvas.restore();
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        final int borderWidth = Math.round(mBorderWidth);
        padding.set(borderWidth, borderWidth, borderWidth, borderWidth);
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mBorderWidth > 0 ? PixelFormat.TRANSLUCENT : PixelFormat.TRANSPARENT;
    }

    @Override
    public boolean isStateful() {
        return (mBorderColor != null && mBorderColor.isStateful()) || super.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (mBorderColor != null) {
            final int newColor = mBorderColor.getColorForState(state, mCurrentBorderTintColor);
            if (newColor != mCurrentBorderTintColor) {
                mCurrentBorderTintColor = newColor;
                invalidateSelf();
                return true;
            }
        }
        return false;
    }

    protected void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) {
            this.isCircle = isCircle;
            invalidateSelf();
        }
    }

    /**
     * Set the border width
     */
    protected void setBorderWidth(int width) {
        if (mBorderWidth != width) {
            mBorderWidth = width;
            mPaint.setStrokeWidth(width * DRAW_STROKE_WIDTH_MULTIPLE);
            invalidateSelf();
        }
    }

    /**
     * Set the border color
     */
    protected void setBorderColor(ColorStateList color) {
        if (mBorderColor != color) {
            if (color != null) {
                mCurrentBorderTintColor = color.getColorForState(getState(), mCurrentBorderTintColor);
            }
            mBorderColor = color;
            invalidateSelf();
        }
    }
}
