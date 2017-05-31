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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;

/**
 * Created by Viнt@rь on 18.12.2016
 */
class ImageViewGingerbread extends ImageViewImpl {

    static final int[] PRESSED_ENABLED_STATE_SET = {android.R.attr.state_pressed,
            android.R.attr.state_enabled};
    static final int[] FOCUSED_ENABLED_STATE_SET = {android.R.attr.state_focused,
            android.R.attr.state_enabled};

    //protected ShadowDrawableWrapper mShadowDrawable;

    protected ImageViewGingerbread(ImageView view, ViewDelegate viewDelegate) {
        super(view, viewDelegate);
    }

    @Override
    protected void setBackgroundDrawable(ColorStateList backgroundTint, PorterDuff.Mode backgroundTintMode, boolean isCircle, float borderWidth, ColorStateList borderColor, float cornerRadius) {
        // Now we need to tint the original background with the tint, using
        // an InsetDrawable if we have a border width
        mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(mShapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
        }

/*        mBorderDrawable = createBorderDrawable(isCircle, borderWidth, borderColor, cornerRadius);
        mContentBackground = new LayerDrawable(new Drawable[] {mBorderDrawable, mShapeDrawable});
        mViewDelegate.setBackgroundDrawable(mContentBackground);*/

/*        // Now we created a mask Drawable which will be used for touch feedback.
        GradientDrawable touchFeedbackShape = createShapeDrawable();

        // We'll now wrap that touch feedback mask drawable with a ColorStateList. We do not need
        // to inset for any border here as LayerDrawable will nest the padding for us
        mRippleDrawable = DrawableCompat.wrap(touchFeedbackShape);
        DrawableCompat.setTintList(mRippleDrawable, createColorStateList(Color.GREEN));*/

        mBorderDrawable = createBorderDrawable(isCircle, borderWidth, borderColor, cornerRadius);
        mContentBackground = new LayerDrawable(new Drawable[] {mBorderDrawable, mShapeDrawable/*, mRippleDrawable*/});

/*        mShadowDrawable = new ShadowDrawableWrapper(
                mView.getContext(),
                mContentBackground,
                24,
                24,
                38);
        mShadowDrawable.setAddPaddingForCorners(false);*/
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
        if (mView.getCornerRadius() > 0 || mView.isCircle()) {
            boolean isVector = isVector(drawable);
            boolean isTransition = isTransition(drawable);

            if (!isVector && !isTransition) {
                drawable = createRoundedDrawable(drawable);
            } else if (isTransition) {
                final TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
                for (int i = 0; i < transitionDrawable.getNumberOfLayers(); i++) {
                    Drawable childDrawable = transitionDrawable.getDrawable(i);
                    isVector = isVector(childDrawable);
                    if (!isVector) {
                        int id = transitionDrawable.getId(i);
                        if (id == View.NO_ID) {
                            id = i;
                            transitionDrawable.setId(i, id);
                        }
                        transitionDrawable.setDrawableByLayerId(id, createRoundedDrawable(childDrawable));
                    }
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

        if (mView.isCircle()) {
            roundedBitmapDrawable.setCornerRadius(0);
            roundedBitmapDrawable.setCircular(true);
        } else {
            roundedBitmapDrawable.setCircular(false);
            roundedBitmapDrawable.setCornerRadius(mView.getCornerRadius() / 3);
        }

        return roundedBitmapDrawable;
    }

    private static ColorStateList createColorStateList(int selectedColor) {
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int i = 0;

        states[i] = FOCUSED_ENABLED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        states[i] = PRESSED_ENABLED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = new int[0];
        colors[i] = Color.TRANSPARENT;

        return new ColorStateList(states, colors);
    }
}
