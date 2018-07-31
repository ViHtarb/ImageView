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
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    protected final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Rect mRect = new Rect();
    protected final RectF mRectF = new RectF();

    protected boolean isCircle;

    private int mCurrentColor;
    private ColorStateList mTint;

    protected float mRotation;
    protected float mCornerRadius;
    protected float mWidth;

    public BorderDrawable() {
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mPaint.getStrokeWidth() > 0) {
            mPaint.setColor(mCurrentColor);

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
            canvas.rotate(mRotation, rectF.centerX(), rectF.centerY());

            if (isCircle) {
                canvas.drawOval(rectF, mPaint);
            } else {
                if (mCornerRadius > 0) {
                    canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, mPaint);
                } else {
                    canvas.drawRect(rectF, mPaint);
                }
            }
            canvas.restore();
        }
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        final int borderWidth = Math.round(mWidth);
        padding.set(borderWidth, borderWidth, borderWidth, borderWidth);
        return true;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public boolean isStateful() {
        return (mTint != null && mTint.isStateful()) || super.isStateful();
    }

    @Override
    public int getOpacity() {
        return mWidth > 0 ? PixelFormat.TRANSLUCENT : PixelFormat.TRANSPARENT;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (mTint != null) {
            final int newColor = mTint.getColorForState(state, mCurrentColor);
            if (newColor != mCurrentColor) {
                mCurrentColor = newColor;
                invalidateSelf();
                return true;
            }
        }
        return false;
    }

    protected void setCircle(boolean isCircle) {
        if (this.isCircle != isCircle) { // TODO check is it need?
            this.isCircle = isCircle;
            invalidateSelf();
        }
    }

    protected final void setRotation(float rotation) {
        if (rotation != mRotation) {
            mRotation = rotation;
            invalidateSelf();
        }
    }

    protected void setCornerRadius(float radius) {
        if (mCornerRadius != radius) {
            mCornerRadius = radius;
            invalidateSelf();
        }
    }

    protected void setWidth(float width) {
        if (mWidth != width) {
            mWidth = width;
            mPaint.setStrokeWidth(width * DRAW_STROKE_WIDTH_MULTIPLE);
            invalidateSelf();
        }
    }

    protected void setColor(@Nullable ColorStateList tint) {
        if (mTint != tint) {
            if (tint != null) {
                mCurrentColor = tint.getColorForState(getState(), mCurrentColor);
            }
            mTint = tint;
            invalidateSelf();
        }
    }
}
