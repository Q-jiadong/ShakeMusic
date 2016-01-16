package com.game.shakemusic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/**
 * Created by Administrator on 2016-01-08.
 * QJD
 */
public class LoadingView extends View {

    public static final int HALF_MANUAL_MODE = 0;
    public static final int HALF_AUTO_MODE = 1;
    public static final int FULLY_AUTO_MODE = 2;
    private static final long ROTATE_TIME = 2000;
    private static final long ROTATE_SCALE = 720;
    private static final long LARGER_TIME = 1000;

    private static final int RECT_LEFT = 0;
    private static final int RECT_TOP = 0;
    private static final int INIT_RADIAN = 160;

    private static final long LARGER_AND_SMALLER_SCALE = 70;
    private static final int INFORM_INVALIDATE = 1;
    private final Interpolator PATHINTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 0.3f, 1.0f);

    private int mWidth = 0;
    private int mHeight = 0;
    private int mStrokeWidth = 0;
    private int mPaintColor = 0;
    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;
    private float mRotationX;
    private float mRotationY;
    private long mStartTime;
    private int mHalfInitAngle = 10;
    private int mInitAngle = 100;
    private float mRotateAngle = 0;
    private float mRotateRadian = 0;
    private static final int ROTATION_HALF = 180;

    private int mRotateMode = FULLY_AUTO_MODE;


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.LoadingViewStyle);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyle, 0);

        int length = getResources().getDimensionPixelOffset(R.dimen.loading_view_default_width);
        mWidth = ta.getDimensionPixelSize(R.styleable.LoadingView_loading_view_width, length);
        mHeight = ta.getDimensionPixelSize(R.styleable.LoadingView_loading_view_width, length);
        mStrokeWidth = context.getResources().getDimensionPixelSize(R.dimen.loading_view_stroke_width);
        mPaintColor = context.getResources().getColor(R.color.loading_view_paint_color);

        ta.recycle();

        int rectLeft = RECT_LEFT + mStrokeWidth;
        int rectRight = mWidth - mStrokeWidth;
        int rectTop = RECT_TOP + mStrokeWidth;
        int rectBottom = mHeight - mStrokeWidth;
        mRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
        mRotationX = (float) (rectRight - rectLeft) / 2.0f + rectLeft;
        mRotationY = (float) (rectBottom - rectTop) / 2.0f + rectTop;
        mPath = new Path();
        mPath.reset();
        mPath.arcTo(mRectF, mInitAngle + mRotateRadian, INIT_RADIAN - 2 * mRotateRadian);
        mPaint = new Paint();
        float direction[] = { mRotationX, mRotationX, 200 };
        EmbossMaskFilter emf = new EmbossMaskFilter(direction, 0.7f, 5f, 2f);
        mPaint.setMaskFilter(emf);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mPaintColor);
        mStartTime = System.currentTimeMillis();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (mRotateMode != HALF_MANUAL_MODE) {
            preparePathAndAngle();
        }

        canvas.rotate(mRotateAngle, mRotationX, mRotationY);
        canvas.drawPath(mPath, mPaint);
        canvas.rotate(ROTATION_HALF, mRotationX, mRotationY);
        canvas.drawPath(mPath, mPaint);
    }

    private void preparePathAndAngle() {
        long time = (System.currentTimeMillis() - mStartTime) % (ROTATE_TIME);
        float phase = 0;
        if (time <= ROTATE_TIME) {
            phase = constrain(0, 1, ((float) time / (float) ROTATE_TIME));
            mRotateAngle = PATHINTERPOLATOR.getInterpolation(phase) * ROTATE_SCALE;
            if (time <= LARGER_TIME) {
                phase = constrain(0, 1, ((float) time / (float) LARGER_TIME));
                mRotateRadian = getFirstRotateRadian(phase);
            }
            if (time > LARGER_TIME) {
                phase = constrain(0, 1, ((float) (time - LARGER_TIME) / ((float) ROTATE_TIME - (float) LARGER_TIME)));
                mRotateRadian = getSecondRotateRadian(phase);
            }
        }
        mPath.reset();
        mPath.arcTo(mRectF, mInitAngle + mRotateRadian, INIT_RADIAN - 2 * mRotateRadian);
        Runnable r = new Runnable() {
            public void run() {
                mHandler.sendEmptyMessage(INFORM_INVALIDATE);
            }
        };
        Thread NewThread = new Thread(r);
        NewThread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    public void setLoadingStatus(float percentage) {
        float rotateRadian = LARGER_AND_SMALLER_SCALE * (1 - percentage);
        mPath.reset();
        mPath.arcTo(mRectF, mInitAngle + rotateRadian, INIT_RADIAN - 2 * rotateRadian);
        invalidate();
    }

    private float getFirstRotateRadian(float phase) {
        switch (mRotateMode) {
            case FULLY_AUTO_MODE:
                return PATHINTERPOLATOR.getInterpolation(1 - phase) * LARGER_AND_SMALLER_SCALE;
            case HALF_AUTO_MODE:
            default:
                return (1 - PATHINTERPOLATOR.getInterpolation(1 - phase)) * LARGER_AND_SMALLER_SCALE;
        }
    }

    private float getSecondRotateRadian(float phase) {
        switch (mRotateMode) {
            case FULLY_AUTO_MODE:
                return PATHINTERPOLATOR.getInterpolation(phase) * LARGER_AND_SMALLER_SCALE; // 0-70
            case HALF_AUTO_MODE:
                default:
                return (1 - PATHINTERPOLATOR.getInterpolation(phase)) * LARGER_AND_SMALLER_SCALE; // 0-70
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INFORM_INVALIDATE:
                    invalidate();
                    break;
           }
        }
    };

    public static float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }

    public void setRotateMode(int mode) {
        if (mode == HALF_MANUAL_MODE) {
            mInitAngle = mHalfInitAngle;
            mRotateAngle = 0;
            mRotateRadian = LARGER_AND_SMALLER_SCALE;
        }
        mRotateMode = mode;
        mStartTime = System.currentTimeMillis();
        invalidate();
    }

    public int getRotateMode() {
        return mRotateMode;
    }
}