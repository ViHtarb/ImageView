package com.imageview.core.integration;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Viнt@rь on 09.11.2016
 */
public abstract class ImageView extends com.imageview.core.ImageView {

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, com.imageview.core.R.attr.imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setImageURL(String url);
}
