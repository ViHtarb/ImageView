package com.imageview.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Image view implementation with switchable modes
 * {@link Mode#NORMAL} or {@link Mode#CIRCLE}
 */
public abstract class ImageView extends AppCompatImageView {

    public enum Mode {
        NORMAL,
        CIRCLE
    }

    private boolean mBorderOverlay;

    private int mBorderColor;
    private int mBorderWidth;

    private float mRadius;
    private float mBorderRadius;

    private Mode mMode;

    private RectF mDrawableRect = new RectF();
    private RectF mBorderRect = new RectF();

    private Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private Paint mBorderPaint = new Paint();

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;


    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;

    int mShadowRadius;

    private ShapeDrawable mBackgroundDrawable;

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

        mBorderColor = a.getColor(R.styleable.ImageView_borderColor, Color.BLACK);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mBorderOverlay = a.getBoolean(R.styleable.ImageView_borderOverlay, false);

        a.recycle();

        // TODO only for circle mode

        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        if (elevationSupported()) {
            mBackgroundDrawable = new ShapeDrawable(new OvalShape());
        } else {
            mBackgroundDrawable = new ShapeDrawable(new OvalShadow(mShadowRadius));
            mBackgroundDrawable.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);

            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, mBackgroundDrawable.getPaint());

            final int padding = mShadowRadius; // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        mBackgroundDrawable.getPaint().setColor(Color.GREEN);
        setBackgroundDrawable(mBackgroundDrawable);
        //ViewCompat.setBackground(this, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_album, null));
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

   /* @Override
    protected void onDraw(Canvas canvas) {
        if (mMode == Mode.NORMAL || getDrawable() instanceof VectorDrawable) {
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
    }*/

/*    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mMode == Mode.CIRCLE) {
            init();
        }
    }*/
/*

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);

        if (mMode == Mode.CIRCLE) {
            mBitmap = bitmap;
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
*/

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

    public void setMode(@NonNull Mode mode) {
        if (mode != mMode) {
            mMode = mode;

            if (mBitmap == null) {
                mBitmap = getBitmap(getDrawable());
            }
            invalidate();
            init();
        }
    }

    private void initBorders() {

    }

    private void init() {
        if (mBitmap == null || mBitmapPaint == null || mMode != Mode.CIRCLE || getDrawable() instanceof VectorDrawable) {
            return;
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

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;

        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = ImageView.this.getWidth();
            final int viewHeight = ImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2, mShadowRadius, new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }
}
