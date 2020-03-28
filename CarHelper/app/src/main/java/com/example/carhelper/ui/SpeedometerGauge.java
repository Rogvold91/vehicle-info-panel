package com.example.carhelper.ui;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.example.carhelper.R;
import com.example.carhelper.util.LabelConverter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import static com.example.carhelper.util.Constants.*;

public class SpeedometerGauge extends View {

    private double mMaxValue = DEFAULT_MAX_SPEED;
    private double mValue = 0;
    private double mMajorTickStep = DEFAULT_MAJOR_TICK_STEP;
    private int minorTicks = DEFAULT_MINOR_TICKS;
    private LabelConverter labelConverter;
    private String unitsText = " ";

    private List<ColoredRange> mRanges = new ArrayList<ColoredRange>();

    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTicksPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mUnitsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mColorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mLabelTextSize;
    private int mUnitsTextSize;

    public SpeedometerGauge(Context context) {
        super(context);
        init();
        float density = getResources().getDisplayMetrics().density;
        setmLabelTextSize(Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density));
        setUnitsTextSize(Math.round(DEFAULT_UNITS_TEXT_SIZE_DP * density));
    }

    public SpeedometerGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        float density = getResources().getDisplayMetrics().density;
        setmLabelTextSize(Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density));
        setUnitsTextSize(Math.round(DEFAULT_UNITS_TEXT_SIZE_DP * density));
    }

    public void setmMaxValue(double mMaxValue) {
        if (mMaxValue <= 0)
            throw new IllegalArgumentException("Non-positive value specified as max speed.");
        this.mMaxValue = mMaxValue;
        invalidate();
    }


    public void setUnitsText(String unitsText) {
        this.unitsText = unitsText;
    }

    private void beforeRedraw(double progress, long duration, long startDelay) {
        if (progress > mMaxValue)
            progress = mMaxValue;

        ValueAnimator va = ValueAnimator.ofObject((TypeEvaluator<Double>) (fraction, startValue, endValue) ->
                startValue + fraction * (endValue - startValue), Double.valueOf(mValue), Double.valueOf(progress));

        va.setDuration(duration);
        va.setStartDelay(startDelay);
        va.addUpdateListener(animation -> {
            Double value = (Double) animation.getAnimatedValue();
            if (value != null)
                invalidate();
        });
        va.start();
    }

    public void updateValue(double progress) {
        if (progress < 0)
            throw new IllegalArgumentException("Non-positive value specified as a speed.");
        if (progress > mMaxValue)
            mValue = mMaxValue;
        this.mValue = progress >= mMaxValue ? mMaxValue : progress;
        beforeRedraw(progress, ANIMATION_DURATION, ANIMATION_DELAY);
    }

    public void setmMajorTickStep(double mMajorTickStep) {
        if (mMajorTickStep <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a major tick step.");
        this.mMajorTickStep = mMajorTickStep;
    }

    public void setMinorTicks(int minorTicks) {
        this.minorTicks = minorTicks;
    }

    public void setLabelConverter(LabelConverter labelConverter) {
        this.labelConverter = labelConverter;
    }

    public void addColoredRange(double begin, double end, int color) {
        if (begin >= end)
            throw new IllegalArgumentException("Incorrect number range specified!");
        double coeff = 5.0/160; 
        if (begin < -coeff * mMaxValue)
            begin = -coeff * mMaxValue;
        if (end > mMaxValue * (coeff + 1))
            end = mMaxValue * (coeff + 1);
        mRanges.add(new ColoredRange(color, begin, end));
    }

    public void setmLabelTextSize(int mLabelTextSize) {
        this.mLabelTextSize = mLabelTextSize;
        if (mTxtPaint != null) {
            mTxtPaint.setTextSize(mLabelTextSize);
        }
    }

    public void setUnitsTextSize(int mUnitsTextSize) {
        this.mUnitsTextSize = mUnitsTextSize;
        if (mUnitsPaint != null) {
            mUnitsPaint.setTextSize(mUnitsTextSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Clear canvas
        canvas.drawColor(Color.TRANSPARENT);
        // Draw Metallic Arc and background
        drawBackground(canvas);
        // Draw Ticks and colored arc
        drawTicks(canvas);
        // Draw Needle
        drawNeedle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private void drawNeedle(Canvas canvas) {
        RectF oval = getOval(canvas, 1);
        float radius = oval.width()*0.35f + 10;
        RectF smallOval = getOval(canvas, 0.2f);

        float angle = 10 + (float) (mValue / mMaxValue * 160);
        double cos = Math.cos((180 - angle) / 180 * Math.PI);
        double sin = Math.sin(angle / 180 * Math.PI);
        canvas.drawLine(
                (float) (oval.centerX() + cos * smallOval.width()*0.5f),
                (float) (oval.centerY() - sin * smallOval.width()*0.5f),
                (float) (oval.centerX() + cos * radius),
                (float) (oval.centerY() - sin * radius),
                mNeedlePaint
        );
        canvas.drawArc(smallOval, 180, 180, true, mBackgroundPaint);
    }

    private void drawTicks(Canvas canvas) {
        float availableAngle = 160;
        float majorStep = (float) (mMajorTickStep / mMaxValue *availableAngle);
        float minorStep = majorStep / (1 + minorTicks);

        float majorTicksLength = 30;
        float minorTicksLength = majorTicksLength/2;

        RectF oval = getOval(canvas, 1);
        float radius = oval.width()*0.35f;

        float currentAngle = 10;
        double curProgress = 0;
        while (currentAngle <= 170) {

            double cos = Math.cos((180 - currentAngle) / 180 * Math.PI);
            double sin = Math.sin(currentAngle / 180 * Math.PI);
            canvas.drawLine(
                    (float) (oval.centerX() + cos *(radius-majorTicksLength/2)),
                    (float) (oval.centerY() - sin *(radius-majorTicksLength/2)),
                    (float) (oval.centerX() + cos *(radius+majorTicksLength/2)),
                    (float) (oval.centerY() - sin *(radius+majorTicksLength/2)),
                    mTicksPaint
            );

            for (int i = 1; i <= minorTicks; ++i) {
                float angle = currentAngle + i * minorStep;
                if (angle >= 170 + minorStep/2) {
                    break;
                }

                double cos1 = Math.cos((180 - angle) / 180 * Math.PI);
                double sin1 = Math.sin(angle / 180 * Math.PI);
                canvas.drawLine(
                        (float) (oval.centerX() + cos1 * radius),
                        (float) (oval.centerY() - sin1 * radius),
                        (float) (oval.centerX() + cos1 * (radius + minorTicksLength)),
                        (float) (oval.centerY() - sin1 * (radius + minorTicksLength)),
                        mTicksPaint
                );
            }

            if (labelConverter != null) {
                canvas.save();
                canvas.rotate(180 + currentAngle, oval.centerX(), oval.centerY());
                float txtX = oval.centerX() + radius + majorTicksLength/2 + 8;
                float txtY = oval.centerY();
                canvas.rotate(+90, txtX, txtY);
                canvas.drawText(labelConverter.getLabelFor(curProgress, mMaxValue), txtX, txtY, mTxtPaint);
                canvas.restore();
            }
            currentAngle += majorStep;
            curProgress += mMajorTickStep;
        }

        RectF smallOval = getOval(canvas, 0.7f);
        mColorLinePaint.setColor(DEFAULT_COLOR);
        canvas.drawArc(smallOval, 185, 170, false, mColorLinePaint);

        for (ColoredRange range: mRanges) {
            mColorLinePaint.setColor(range.getColor());
            canvas.drawArc(smallOval, (float) (190 + range.getBegin()/ mMaxValue *160), (float) ((range.getEnd() - range.getBegin())/ mMaxValue *160), false, mColorLinePaint);
        }
    }

    private RectF getOval(Canvas canvas, float factor) {
        RectF oval;
        final int canvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (canvasHeight*2 >= canvasWidth) {
            oval = new RectF(0, 0, canvasWidth*factor, canvasWidth*factor);
        } else {
            oval = new RectF(0, 0, canvasHeight*2*factor, canvasHeight*2*factor);
        }
        oval.offset((canvasWidth-oval.width())/2 + getPaddingLeft(), (canvasHeight*2-oval.height())/2 + getPaddingTop());

        return oval;
    }

    private void drawBackground(Canvas canvas) {
        RectF oval = getOval(canvas, 1);
        canvas.drawArc(oval, 180, 180, true, mBackgroundPaint);
        RectF innerOval = getOval(canvas, 0.9f);
        canvas.drawArc(innerOval, 180, 180, true, mBackgroundInnerPaint);
        canvas.drawText(unitsText, oval.centerX(), oval.centerY() / 1.5f, mUnitsPaint);
    }

    private void init_back_paint(Paint paint, Paint.Style style, int[] color) {
        paint.setStyle(style);
        paint.setColor(Color.argb(color[0], color[1], color[2], color[3]));
    }

    private void init_text_paint(Paint paint, int[] color, int size, Paint.Align align) {
        paint.setColor(Color.argb(color[0], color[1], color[2], color[3]));
        paint.setTextSize(size);
        paint.setTextAlign(align);
        paint.setLinearText(true);
    }

    private void init_comp_paint(Paint paint, Paint.Style style, float width, int[] color) {
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        paint.setColor(Color.argb(color[0], color[1], color[2], color[3]));
    }

    private void init() {
        if (!isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        int[] argb = getResources().getIntArray(R.array.back_color);
        init_back_paint(mBackgroundPaint, Paint.Style.FILL,argb);

        argb = getResources().getIntArray(R.array.back_inner_color);
        init_back_paint(mBackgroundInnerPaint, Paint.Style.FILL, argb);

        argb = getResources().getIntArray(R.array.text_color);
        init_text_paint(mTxtPaint, argb, mLabelTextSize, Paint.Align.CENTER);
        argb = getResources().getIntArray(R.array.unit_color);
        init_text_paint(mUnitsPaint, argb, mUnitsTextSize, Paint.Align.CENTER);

        argb = getResources().getIntArray(R.array.tick_color);
        init_comp_paint(mTicksPaint, Paint.Style.STROKE,3.0f, argb);

        argb = getResources().getIntArray(R.array.color_line);
        init_comp_paint(mColorLinePaint, Paint.Style.STROKE,5, argb);

        argb = getResources().getIntArray(R.array.color_needle);
        init_comp_paint(mNeedlePaint, Paint.Style.STROKE,5, argb);
    }

}