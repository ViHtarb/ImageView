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
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

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
        this(context, attrs, R.attr.materialImageViewStyle);
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

    /**
     * Sets an {@link Drawable} from resources to display while a resource is loading or ImageView drawable is not set
     *
     * @param resId The desired resource identifier of the drawable to display as a placeholder.
     */
    public void setPlaceholderDrawable(@DrawableRes int resId) {
        setPlaceholderDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    /**
     * Sets an {@link Drawable} to display while a resource is loading or ImageView drawable is not set
     *
     * @param drawable The drawable to display as a placeholder.
     */
    public void setPlaceholderDrawable(Drawable drawable) {
        if (mPlaceholderDrawable != drawable) {
            if (mPlaceholderDrawable == getDrawable()) {
                setImageDrawable(drawable);
            }

            mPlaceholderDrawable = drawable;
        }
    }

    /**
     * Sets a {@link Drawable} from resources to display if a load fails.
     *
     * @param resId The desired resource identifier of the drawable to display.
     */
    public void setErrorDrawable(@DrawableRes int resId) {
        setErrorDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    /**
     * Sets a {@link Drawable} to display if a load fails.
     *
     * @param drawable The drawable to display.
     */
    public void setErrorDrawable(Drawable drawable) {
        if (mErrorDrawable != drawable) {
            if (mErrorDrawable == getDrawable()) {
                setImageDrawable(drawable);
            }

            mErrorDrawable = drawable;
        }
    }

    /**
     * @return placeholder drawable.
     */
    @Nullable
    public Drawable getPlaceholderDrawable() {
        return mPlaceholderDrawable;
    }

    /**
     * @return error drawable.
     */
    @Nullable
    public Drawable getErrorDrawable() {
        return mErrorDrawable;
    }

    /**
     * Sets the url for load image.
     *
     * @param url A file path, or a uri or url.
     */
    public abstract void setImageURL(String url);
}
