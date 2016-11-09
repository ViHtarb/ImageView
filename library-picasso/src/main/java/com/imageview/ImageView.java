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

package com.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.imageview.picasso.*;
import com.squareup.picasso.Picasso;

/**
 * Image view implementation with switchable modes
 * {@link ImageView.Mode#NORMAL} or {@link ImageView.Mode#CIRCLE}
 * with inherited picasso implementation
 */
public class ImageView extends com.imageview.core.integration.ImageView {

    private long mErrorResource;
    private Drawable mErrorDrawable;
    private Picasso mManager;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, com.imageview.picasso.R.attr.imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, com.imageview.picasso.R.styleable.ImageView, defStyleAttr, 0);

        Drawable errorDrawable = a.getDrawable(com.imageview.picasso.R.styleable.ImageView_error);
        setErrorDrawable(errorDrawable != null ? errorDrawable : getDrawable());

        a.recycle();

        if (!isInEditMode()) {
            mManager = Picasso.with(context);
        }
    }

    @Override
    public void setImageURL(String url) {
        mManager.load(url).error(mErrorDrawable).into(this);
    }

    public void setErrorDrawable(@DrawableRes int resId) {
        if (resId != mErrorResource) {
            mErrorResource = resId;
            setErrorDrawable(ContextCompat.getDrawable(getContext(), resId));
        }
    }

    public void setErrorDrawable(@Nullable Drawable drawable) {
        if (drawable != mErrorDrawable) {
            mErrorDrawable = drawable;
        }
    }
}