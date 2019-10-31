/*
 *  The MIT License (MIT)
 *  <p/>
 *  Copyright (c) 2019. Viнt@rь
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
final class ImageViewImplApi23 extends ImageViewImplApi21 {

    protected ImageViewImplApi23(@NonNull ImageView view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
