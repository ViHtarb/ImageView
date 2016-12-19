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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * Created by Viнt@rь on 18.12.2016
 */
abstract class ImageViewImpl {

    protected Drawable mShapeDrawable;
    protected BorderDrawable mBorderDrawable;
    protected Drawable mContentBackground;

    protected final ImageView mView;
    protected final ViewDelegate mViewDelegate;

    protected ImageViewImpl(ImageView view, ViewDelegate viewDelegate) {
        mView = view;
        mViewDelegate = viewDelegate;
    }

    protected abstract void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, int borderWidth, ColorStateList borderColor, boolean isCircle);

    protected abstract void setBackgroundTintList(ColorStateList tint);

    protected abstract void setBackgroundTintMode(PorterDuff.Mode tintMode);

    protected abstract void setImageDrawable(Drawable drawable);

    protected void setCircle(boolean isCircle) {
        mBorderDrawable.setCircle(isCircle);
    }

    protected void setBorderWidth(int width) {
        mBorderDrawable.setBorderWidth(width);
    }

    protected void setBorderColor(ColorStateList color) {
        mBorderDrawable.setBorderColor(color);
    }

    protected BorderDrawable createBorderDrawable(int width, ColorStateList color, boolean isCircle) {
        BorderDrawable borderDrawable = newBorderDrawable();
        borderDrawable.setBorderWidth(width);
        borderDrawable.setBorderColor(color);
        borderDrawable.setCircle(isCircle);
        return borderDrawable;
    }

    protected BorderDrawable newBorderDrawable() {
        return new BorderDrawable();
    }

    protected GradientDrawable createShapeDrawable() {
        GradientDrawable d = newGradientDrawableForShape();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(Color.WHITE);
        return d;
    }

    protected GradientDrawable newGradientDrawableForShape() {
        return new GradientDrawable();
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
