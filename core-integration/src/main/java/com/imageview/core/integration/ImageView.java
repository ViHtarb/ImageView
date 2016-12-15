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

    private Drawable mErrorDrawable;
    private Drawable mPlaceholderDrawable;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, com.imageview.core.R.attr.imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

        Drawable drawable = a.getDrawable(R.styleable.ImageView_placeholder);
        setPlaceholderDrawable(drawable != null ? drawable : getDrawable());

        drawable = a.getDrawable(R.styleable.ImageView_error);
        setErrorDrawable(drawable != null ? drawable : getDrawable());

        a.recycle();
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
