package com.imageview.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Viнt@rь on 18.11.2016
 */
public class ImageViewNew extends AppCompatImageView {

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;

    int mShadowRadius;

    private ShapeDrawable mBackgroundDrawable;

    public ImageViewNew(Context context) {
        this(context, null);
    }

    public ImageViewNew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0);

/*        final int mode = a.getInteger(R.styleable.ImageView_mode, ImageView.Mode.NORMAL.ordinal());
        setMode(ImageView.Mode.values()[mode]);

        mBorderColor = a.getColor(R.styleable.ImageView_borderColor, Color.BLACK);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.ImageView_borderWidth, 0);
        mBorderOverlay = a.getBoolean(R.styleable.ImageView_borderOverlay, false);*/

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

/*        mBackgroundDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(mBackgroundDrawable, ColorStateList.valueOf(Color.GREEN));*/
        //setBackgroundDrawable(createShapeDrawable());


/*        Drawable mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), mBackgroundDrawable, null);
        setBackgroundDrawable(mRippleDrawable);*/
        ViewCompat.setBackground(this, mBackgroundDrawable);
    }

    GradientDrawable createShapeDrawable() {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(Color.WHITE);
        return d;
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
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
            final int viewWidth = ImageViewNew.this.getWidth();
            final int viewHeight = ImageViewNew.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2, mShadowRadius, new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }
}
