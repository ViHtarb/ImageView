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

import android.annotation.TargetApi;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Viнt@rь on 18.12.2016
 */
@RequiresApi(LOLLIPOP)
@TargetApi(LOLLIPOP)
class ImageViewLollipop extends ImageViewIcs { // TODO for android L and more need implement by outlines

    ImageViewLollipop(ImageView view, ViewDelegate viewDelegate) {
        super(view, viewDelegate);
    }

    /*    @Override
    void setImageDrawable(Drawable drawable) {
        mViewDelegate.setImageDrawable(drawable);
    }

    @Override
    CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }*/

    @Override
    GradientDrawable newGradientDrawableForShape() {
        return new AlwaysStatefulGradientDrawable();
    }

    /**
     * LayerDrawable on L+ caches its isStateful() state and doesn't refresh it,
     * meaning that if we apply a tint to one of its children, the parent doesn't become
     * stateful and the tint doesn't work for state changes. We workaround it by saying that we
     * are always stateful. If we don't have a stateful tint, the change is ignored anyway.
     **/
    static class AlwaysStatefulGradientDrawable extends GradientDrawable {
        @Override
        public boolean isStateful() {
            return true;
        }
    }
}
