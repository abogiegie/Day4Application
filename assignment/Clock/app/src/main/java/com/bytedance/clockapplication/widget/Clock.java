package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = "Homework";//Clock.class.getSimpleName();
    private final static String[] TIME_VALUES = {"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    private static final float DEFAULT_INNER_CENTER_STROKE_WIDTH = 40f;
    private static final float DEFAULT_OUTER_CENTER_STROKE_WIDTH = 20f;

    private static final float DEFAULT_TEXT_SIZE = 25f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = (int)(halfWidth * 0.8f);


        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(DEFAULT_TEXT_SIZE);
        textPaint.setColor(hoursValuesColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        int height = (int)(fontMetrics.bottom - fontMetrics.top);
        for(int i = 0, angle = 0; angle < FULL_ANGLE ; i++, angle = i * 30){
            int centerX = (int)(mCenterX + mRadius * Math.sin(Math.toRadians(angle)));
            int baseLineY = (int)(mCenterY + mRadius * -Math.cos(Math.toRadians(angle)) + height / 4);

            canvas.drawText(TIME_VALUES[i], centerX, baseLineY,textPaint);
        }

    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - minutesNeedleColor
        // - hoursNeedleColor
        int secondsNeedleRadius = (int)(mRadius * 0.9f);
        int minutesNeedleRadius = (int)(mRadius * 0.6f);
        int hoursNeedleRadius = (int)(mRadius * 0.5f);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        Paint secondsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        secondsPaint.setStrokeCap(Paint.Cap.ROUND);
        secondsPaint.setStrokeWidth(2);
        secondsPaint.setColor(secondsNeedleColor);

        Paint minutesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minutesPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        minutesPaint.setStrokeCap(Paint.Cap.ROUND);
        minutesPaint.setStrokeWidth(3);
        minutesPaint.setColor(minutesNeedleColor);

        Paint hoursPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hoursPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        hoursPaint.setStrokeCap(Paint.Cap.ROUND);
        hoursPaint.setStrokeWidth(5);
        hoursPaint.setColor(hoursNeedleColor);

        //hoursNeedle
        canvas.drawLine(mCenterX, mCenterY
                ,(float)(mCenterX + hoursNeedleRadius * Math.sin(Math.toRadians(hour * 30)))
                ,(float)(mCenterY + hoursNeedleRadius * -Math.cos(Math.toRadians(hour * 30)))
                , hoursPaint);

        //minutesNeedle
        canvas.drawLine(mCenterX, mCenterY
                , (float)(mCenterX + minutesNeedleRadius * Math.sin(Math.toRadians(minute * 6)))
                , (float)(mCenterY + minutesNeedleRadius * -Math.cos(Math.toRadians(minute * 6)))
                , minutesPaint);

        //secondsNeedle
        canvas.drawLine(mCenterX, mCenterY
                , (float)(mCenterX + secondsNeedleRadius * Math.sin(Math.toRadians(second * 6)))
                , (float)(mCenterY + secondsNeedleRadius * -Math.cos(Math.toRadians(second * 6)))
                , secondsPaint);
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint paintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInner.setStyle(Paint.Style.FILL_AND_STROKE);
        paintInner.setStrokeCap(Paint.Cap.ROUND);
        paintInner.setStrokeWidth(DEFAULT_INNER_CENTER_STROKE_WIDTH);
        paintInner.setColor(centerInnerColor);
        canvas.drawPoint(mCenterX, mCenterY, paintInner);

        Paint paintOuter = new Paint(paintInner);
        paintOuter.setStrokeWidth(DEFAULT_OUTER_CENTER_STROKE_WIDTH);
        paintOuter.setColor(centerOuterColor);
        canvas.drawPoint(mCenterX, mCenterY, paintOuter);
    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}