package com.imageview.core;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Viнt@rь on 25.10.2019
 */
@RequiresApi(M)
@TargetApi(M)
final class ImageViewApi23Impl extends ImageViewApi21Impl {

    protected ImageViewApi23Impl(@NonNull ImageView view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(view, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDrawableStateChanged(int[] state) {
    }

    @Override
    protected void drawableHotspotChanged(float x, float y) {
    }

    @Override
    protected void jumpDrawableToCurrentState() {
    }

    @Override
    protected void setForeground(@NonNull Drawable foreground) {
        mView.setForeground(foreground);
    }
}
