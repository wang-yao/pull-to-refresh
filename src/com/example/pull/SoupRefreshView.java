package com.example.pull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;

import com.example.pull.R;

/**
 * Created by Alexey on 28.01.2016.
 */
@SuppressLint("NewApi")
public class SoupRefreshView extends Drawable implements Animatable, Drawable.Callback {

    private final PullToRefreshView mParent;
    private final Matrix mMatrix;



    private float mScreenWidth;
    private float mScreenHeight;

    private float mPanTopOffset;

    private Bitmap mPan;

    private float mPercent = 0.0f;

    private boolean isRefreshing = false;

    private Context mContext = getContext();


    public SoupRefreshView(final PullToRefreshView layout) {
        mParent = layout;
        mMatrix = new Matrix();
        mContext = layout.getContext();
        setupAnimations();
        layout.post(new Runnable() {
            @Override
            public void run() {
                initiateDimens(layout.getWidth());
            }
        });

    }

    private void initiateDimens(int viewWidth) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return;
        mScreenWidth = viewWidth;
        mScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels;


        createBitmaps();

        mPanTopOffset = Utils.convertDpToFloatPixel(mContext, 10);



    }


    private void createBitmaps() {

        mPan = CreateBitmapFactory.getBitmapFromImage(R.drawable.pan, mContext);

    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, top);
    }

    @Override
    public boolean isRunning() {
        return isRefreshing;
    }

    @Override
    public void start() {
        isRefreshing = true;

        final AnimationSet animatorSet = new AnimationSet(false);

        pan.reset();
        animatorSet.addAnimation(pan);
        mParent.startAnimation(pan);
        
        pan.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mParent.startAnimation(animatorSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }


    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
    }


    public void offsetTopAndBottom(int offset) {
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {

        if (mScreenWidth <= 0) return;

        final int saveCount = canvas.save();

        canvas.translate(0, 0);
        canvas.clipRect(0, -mParent.getTotalDragDistance(), mScreenWidth, mParent.getTotalDragDistance());
        drawPan(canvas);

        canvas.restoreToCount(saveCount);
    }

    Context getContext() {
        return mParent != null ? mParent.getContext() : null;

    }

	@Override
    public void invalidateDrawable(@NonNull Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

	@Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    //No need for implementation,we don't need this method.
    @Override
    public void setAlpha(int alpha) {
    }

    //No need for implementation,we don't need this method.
    @Override
    public void setColorFilter(ColorFilter cf) {
    }


    private void drawPan(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        float dragPercent = Math.min(1f, Math.abs(mPercent));
        float offsetY;
        float offsetX = (mScreenWidth / 2) - (mPan.getWidth() / 2);
        offsetY = mPanTopOffset * dragPercent;
        matrix.postTranslate(offsetX, offsetY);

        Paint paint = new Paint();
        float alpha = ( mScale/ 2) * 500;
        paint.setAlpha((int) alpha);
        canvas.drawBitmap(mPan, matrix, paint);
    }


    private void setPercent(float percent) {
        mPercent = percent;
    }

    private float setVariable(float value) {
        invalidateSelf();
        return value;
    }

    /**
     * @param dp The offset of pivot to make bubbles move straight upward, while scaling.
     */

    private void resetOriginals() {
        setPercent(0);
    }
    private float mScale;
    private Animation pan;
    private void setupAnimations() {
        AnimationFactory animationFactory = new AnimationFactory();

        pan = animationFactory.getScale(new Animation() {

            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mScale = setVariable(interpolatedTime);
            }
        });
    }


}




