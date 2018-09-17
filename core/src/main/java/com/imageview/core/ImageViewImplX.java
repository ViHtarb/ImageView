package com.imageview.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;

import com.google.android.material.animation.MotionSpec;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.resources.MaterialResources;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Created by Viнt@rь on 09.09.2018
 */
@RequiresApi(ICE_CREAM_SANDWICH)
@TargetApi(ICE_CREAM_SANDWICH)
class ImageViewImplX {

    private static final ColorStateList TRANSPARENT_TINT = ColorStateList.valueOf(Color.TRANSPARENT);

    private boolean isCircle;
    private boolean isCompatPadding;

    private float mCornerRadius;
    private float mBorderWidth;
    private float mRotation;
    private float mElevation;
    private float mPressedTranslationZ;
    private float mHoveredFocusedTranslationZ;

    private final Context mContext;
    private final View mView;
    private final ViewDelegate mDelegate;

    private ColorStateList mBorderColor;

    private ColorStateList mBackgroundTint;
    private PorterDuff.Mode mBackgroundTintMode;

    private MotionSpec mShowMotionSpec;
    private MotionSpec mHideMotionSpec;

    protected ImageViewImplX(@NonNull View view, @NonNull ViewDelegate viewDelegate) {
        mContext = view.getContext();
        mView = view;
        mDelegate = viewDelegate;
    }

    @SuppressLint("RestrictedApi")
    protected void loadFromAttributes(TypedArray a) {
        isCircle = a.getBoolean(R.styleable.ImageView_circle, false);
        isCompatPadding = a.getBoolean(R.styleable.ImageView_useCompatPadding, false);

        mCornerRadius = a.getDimension(R.styleable.ImageView_cornerRadius, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mBorderColor = a.getColorStateList(R.styleable.ImageView_borderColor);

        mElevation = a.getDimension(R.styleable.ImageView_elevation, a.getDimension(R.styleable.ImageView_android_elevation, 0f));
        mPressedTranslationZ = a.getDimension(R.styleable.ImageView_pressedTranslationZ, 0f);
        mHoveredFocusedTranslationZ = a.getDimension(R.styleable.ImageView_hoveredFocusedTranslationZ, 0f);

        mBackgroundTint = MaterialResources.getColorStateList(mContext, a, R.styleable.ImageView_backgroundTint);//.getColorStateList(R.styleable.ImageView_backgroundTint);
        mBackgroundTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.ImageView_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);

        mShowMotionSpec = MotionSpec.createFromAttribute(mContext, a, R.styleable.ImageView_showMotionSpec);
        mHideMotionSpec = MotionSpec.createFromAttribute(mContext, a, R.styleable.ImageView_hideMotionSpec);

        mDelegate.setBackgroundDrawable();
    }

    protected final Context getContext() {
        return mContext;
    }

    protected final View getView() {
        return mView;
    }
}
