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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;

/**
 * Created by Viнt@rь on 18.12.2016
 */
class ImageViewGingerbread extends ImageViewImpl {

    protected ImageViewGingerbread(ImageView view, ViewDelegate viewDelegate) {
        super(view, viewDelegate);
    }

    @Override
    protected void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, int borderWidth, ColorStateList borderColor, boolean isCircle) {
        // Now we need to tint the original background with the tint, using
        // an InsetDrawable if we have a border width
        mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(mShapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
        }

        final Drawable content;
        if (borderWidth > 0) {
            mBorderDrawable = createBorderDrawable(borderWidth, borderColor, isCircle);
            content = new LayerDrawable(new Drawable[] {mBorderDrawable, mShapeDrawable});
        } else {
            mBorderDrawable = null;
            content = mShapeDrawable;
        }

        mContentBackground = content;
        mViewDelegate.setBackgroundDrawable(mContentBackground);
    }

    @Override
    protected void setBackgroundTintList(ColorStateList tint) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintList(mShapeDrawable, tint);
        }
    }

    @Override
    protected void setBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintMode(mShapeDrawable, tintMode);
        }
    }

    @Override
    protected void setImageDrawable(Drawable drawable) {
        boolean isVector = drawable instanceof VectorDrawable;
        boolean isTransition = drawable instanceof TransitionDrawable;
        if (!isVector && !isTransition) {
            drawable = createRoundedDrawable(drawable);
        } else if (isTransition) {
            final TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
            for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                Drawable childDrawable = transitionDrawable.getDrawable(i);
                if (!(childDrawable instanceof VectorDrawable)) {
                    int id = transitionDrawable.getId(i);
                    if (id == View.NO_ID) {
                        id = i;
                        transitionDrawable.setId(i, id);
                    }
                    transitionDrawable.setDrawableByLayerId(id, createRoundedDrawable(childDrawable));
                }
            }
        }

        mViewDelegate.setImageDrawable(drawable);
    }

    protected Drawable createRoundedDrawable(Drawable drawable) {
        RoundedBitmapDrawable roundedBitmapDrawable;

        if (!(drawable instanceof RoundedBitmapDrawable)) {
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(null, getBitmap(drawable));
            roundedBitmapDrawable.setAntiAlias(true);
        } else {
            roundedBitmapDrawable = (RoundedBitmapDrawable) drawable;
        }
        roundedBitmapDrawable.setCircular(mView.isCircle());

        return roundedBitmapDrawable;
    }
}
