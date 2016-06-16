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
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Image view implementation with switchable modes
 * {@link Mode#NORMAL} or {@link Mode#CIRCLE}
 * with inherited glide implementation
 */
public class ImageView extends AppCompatImageView {
    public enum Mode {
        NORMAL,
        CIRCLE
    }

    private boolean mBorderOverlay;

    private int mBorderColor;
    private int mBorderWidth;

    private long mErrorResource;

    private float mRadius;
    private float mBorderRadius;

    private float mElevation;

    private Mode mMode;

    private RectF mDrawableRect = new RectF();
    private RectF mBorderRect = new RectF();

    private Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private Paint mBorderPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;

    private Drawable mErrorDrawable;

    private ShapeDrawable mShapeDrawable;

    private RequestManager mManager;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

        final int mode = a.getInteger(R.styleable.ImageView_mode, Mode.NORMAL.ordinal());
        setMode(Mode.values()[mode]);

        Drawable errorDrawable = a.getDrawable(R.styleable.ImageView_error);
        setErrorDrawable(errorDrawable != null ? errorDrawable : getDrawable());

        mElevation = a.getDimension(R.styleable.ImageView_elevation, 0f);

        mBorderColor = a.getColor(R.styleable.ImageView_borderColor, Color.BLACK);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mBorderOverlay = a.getBoolean(R.styleable.ImageView_borderOverlay, false);

        a.recycle();

        if (mElevation > 0) {
            ViewCompat.setElevation(this, mElevation);
        }

        if (!isInEditMode()) {
            mManager = Glide.with(context);
        }
    }

/*    @Override
    public ScaleType getScaleType() {
        return mMode == Mode.CIRCLE ? ScaleType.CENTER_CROP : super.getScaleType();
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMode == Mode.NORMAL) {
            super.onDraw(canvas);
            return;
        }

        if (getDrawable() == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        float cx = getWidth() / 2f, cy = getHeight() / 2f;

        canvas.drawCircle(cx, cy, mRadius, mBitmapPaint);

        if (mBorderWidth != 0) {
            canvas.drawCircle(cx, cy, mBorderRadius, mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mMode == Mode.CIRCLE) {
            init();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);

        if (mMode == Mode.CIRCLE) {
            mBitmap = bitmap;
            init();
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);

        if (mMode == Mode.CIRCLE) {
            mBitmap = getBitmap(getDrawable());
            init();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (mMode == Mode.CIRCLE) {
            mBitmap = getBitmap(drawable);
            init();
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ImageView.class.getSimpleName();
    }

    public void setImageURL(String url) {
        mManager.load(url).error(mErrorDrawable).into(this);

        if (BuildConfig.DEBUG) {
            Log.d(getAccessibilityClassName().toString(), "imageURL = " + url);
        }
    }

    public void setErrorResource(@DrawableRes int resourceId) {
        if (resourceId != mErrorResource) {
            mErrorResource = resourceId;
            setErrorDrawable(ContextCompat.getDrawable(getContext(), resourceId));
        }
    }

    public void setErrorDrawable(@Nullable Drawable drawable) {
        if (drawable != mErrorDrawable) {
            mErrorDrawable = drawable;
        }
    }

    public void setBorderColor(@ColorInt int color) {
        if (color != mBorderColor) {
            mBorderColor = color;
            init();
        }
    }

    public void setBorderWidth(@DimenRes int resId) {
        int width = getResources().getDimensionPixelOffset(resId);
        if (width != mBorderWidth) {
            mBorderWidth = width;
            init();
        }
    }

    public void setBorderOverlay(boolean overlay) {
        if (overlay != mBorderOverlay) {
            mBorderOverlay = overlay;
            init();
        }
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        if (mode != mMode) {
            mMode = mode;

            if (mBitmap == null) {
                mBitmap = getBitmap(getDrawable());
            }
            invalidate();
            init();
        }
    }

    private void init() {
        if (mBitmap == null || mBitmapPaint == null || mMode != Mode.CIRCLE) {
            return;
        }

        if (mShapeDrawable == null && getBackground() == null) {
            mShapeDrawable = new ShapeDrawable(new OvalShape());
            mShapeDrawable.getPaint().setColor(Color.TRANSPARENT);
            setBackgroundDrawable(mShapeDrawable);
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2f, (mBorderRect.width() - mBorderWidth) / 2f);

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }
        mRadius = Math.min(mDrawableRect.height() / 2f, mDrawableRect.width() / 2f);

        updateShaderMatrix();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);
        mDrawableRect.set(0, 0, getWidth(), getHeight());

        if (mBitmap.getWidth() * mDrawableRect.height() > mDrawableRect.width() * mBitmap.getHeight()) {
            scale = mDrawableRect.height() / (float) mBitmap.getHeight();
            dx = (mDrawableRect.width() - mBitmap.getWidth() * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmap.getWidth();
            dy = (mDrawableRect.height() - mBitmap.getHeight() * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}