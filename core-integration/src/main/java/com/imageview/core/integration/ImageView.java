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

package com.imageview.core.integration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;

/**
 * Created by Viнt@rь on 09.11.2016
 */
public abstract class ImageView extends com.imageview.core.ImageView {

    private Drawable mPlaceholderDrawable;
    private Drawable mErrorDrawable;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

        Drawable placeholderDrawable = a.getDrawable(R.styleable.ImageView_placeholder);
        Drawable errorDrawable = a.getDrawable(R.styleable.ImageView_error);

        setPlaceholderDrawable(placeholderDrawable != null ? placeholderDrawable : getDrawable());
        setErrorDrawable(errorDrawable != null ? errorDrawable : getDrawable());

        a.recycle();
    }

    public void setDummyDrawable(@DrawableRes int resId) {
        setDummyDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public void setDummyDrawable(Drawable drawable) {
        setPlaceholderDrawable(drawable);
        setErrorDrawable(drawable);
    }

    public void setPlaceholderDrawable(@DrawableRes int resId) {
        setPlaceholderDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public void setPlaceholderDrawable(Drawable drawable) {
        if (mPlaceholderDrawable != drawable) {
            if (mPlaceholderDrawable == getDrawable()) {
                setImageDrawable(drawable);
            }

            mPlaceholderDrawable = drawable;
        }
    }

    public void setErrorDrawable(@DrawableRes int resId) {
        setErrorDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public void setErrorDrawable(Drawable drawable) {
        if (mErrorDrawable != drawable) {
            if (mErrorDrawable == getDrawable()) {
                setImageDrawable(drawable);
            }

            mErrorDrawable = drawable;
        }
    }

    @Nullable
    public Drawable getPlaceholderDrawable() {
        return mPlaceholderDrawable;
    }

    @Nullable
    public Drawable getErrorDrawable() {
        return mErrorDrawable;
    }

    public abstract void setImageURL(String url);
}
