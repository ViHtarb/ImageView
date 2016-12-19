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
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;

/**
 * A drawable which draws an oval 'border'.
 */
class CircularBorderDrawable extends Drawable {

    /**
     * We actually draw the stroke wider than the border size given. This is to reduce any
     * potential transparent space caused by anti-aliasing and padding rounding.
     * This value defines the multiplier used to determine to draw stroke width.
     */
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;

    protected final Paint mPaint;
    protected final Rect mRect = new Rect();
    protected final RectF mRectF = new RectF();

    protected int mBorderWidth;

    private ColorStateList mBorderColor;
    private int mCurrentBorderTintColor;

    private boolean mInvalidateShader = true;

    public CircularBorderDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mInvalidateShader) {
            mPaint.setShader(createGradientShader());
            mInvalidateShader = false;
        }

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
        // Draw the oval
        canvas.drawOval(rectF, mPaint);
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
    protected void onBoundsChange(Rect bounds) {
        mInvalidateShader = true;
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
                mInvalidateShader = true;
                mCurrentBorderTintColor = newColor;
            }
        }
        if (mInvalidateShader) {
            invalidateSelf();
        }
        return mInvalidateShader;
    }

    /**
     * Set the border width
     */
    void setBorderWidth(int width) {
        if (mBorderWidth != width) {
            mBorderWidth = width;
            mPaint.setStrokeWidth(width * DRAW_STROKE_WIDTH_MULTIPLE);
            mInvalidateShader = true;
            invalidateSelf();
        }
    }

    /**
     * Set the border color
     */
    void setBorderColor(ColorStateList color) {
        if (color != null) {
            mCurrentBorderTintColor = color.getColorForState(getState(), mCurrentBorderTintColor);
        }
        mBorderColor = color;
        mInvalidateShader = true;
        invalidateSelf();
    }

    /**
     * Creates a vertical {@link LinearGradient}
     */
    private Shader createGradientShader() {
        final Rect rect = mRect;
        copyBounds(rect);

        final float borderRatio = mBorderWidth / rect.height();

        final int[] colors = new int[6];
        colors[0] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);
        colors[1] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);
        colors[2] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);
        colors[3] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);
        colors[4] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);
        colors[5] = ColorUtils.compositeColors(0, mCurrentBorderTintColor);

        final float[] positions = new float[6];
        positions[0] = 0f;
        positions[1] = borderRatio;
        positions[2] = 0.5f;
        positions[3] = 0.5f;
        positions[4] = 1f - borderRatio;
        positions[5] = 1f;

        return new LinearGradient(
                0, rect.top,
                0, rect.bottom,
                colors, positions,
                Shader.TileMode.CLAMP);
    }
}
