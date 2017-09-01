package com.imageview.core;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Viнt@rь on 01.09.2017
 */
class VisibilityAwareImageView extends AppCompatImageView {

    private int mUserSetVisibility;

    public VisibilityAwareImageView(Context context) {
        this(context, null);
    }

    public VisibilityAwareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisibilityAwareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mUserSetVisibility = getVisibility();
    }

    @Override
    public void setVisibility(int visibility) {
        internalSetVisibility(visibility, true);
    }

    final void internalSetVisibility(int visibility, boolean fromUser) {
        super.setVisibility(visibility);
        if (fromUser) {
            mUserSetVisibility = visibility;
        }
    }

    final int getUserSetVisibility() {
        return mUserSetVisibility;
    }
}
