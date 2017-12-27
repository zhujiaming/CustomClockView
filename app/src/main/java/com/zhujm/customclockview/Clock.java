package com.zhujm.customclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhujiaming on 2017/12/26.
 * 模拟时钟
 */

public class Clock extends View {

    int clockBorderWidth;
    int defaultWidth;
    int defaultHeight;
    int defalutPadding;
    int degreeTextMargin;
    private static int DEGREE_WIDTH;
    private static int DEGREE_HEIGHT;
    private static int DEGREE_HEIGHT_MIDDLE;
    private static int DEGREE_HEIGHT_BIG;
    int degreeLineMargin;
    int handHourMargin;
    int handMinMargin;
    int handSecMargin;
    int centerCircleRadius;
    int handHourWidth;
    int handMinWidth;
    int handSecWidth;
    int handSecOutLen;
    int handMinOutLen;

    float hourAngle;
    float minuteAngle;
    float secondAngle;

    int bigTextSize;
    int middleTextSize;

    int tagTextSize;


    int bigTextColor = Color.BLACK;
    int middleTextColor = Color.BLACK;

    private ClockManager mClockManager;
    private final ClockManager.ClockListener clockListener = new ClockManager.ClockListener() {
        @Override
        public void onTimeChanged(int hour, int min, int sec) {
            hourAngle = hour * 30f;
            minuteAngle = min * 6f;
            secondAngle = sec * 6f;
            invalidate();
        }
    };
    private int clockBorderColor = Color.BLACK;
    private int degreeDefaultColor = Color.BLACK;
    private int handDefalutColor = Color.BLACK;
    private int centerPointColor = Color.BLACK;
    private int tagTextColor = Color.GRAY;

    private Paint mClockPaint;
    private Paint mDegreePaint;
    private Paint mDegreeTextPaint;
    private Paint mHandPaint;
    private Paint mCenterPaint;
    private Paint mTagPaint;

    private String tag = "-=====";
    private boolean needReDraw = true;


    public Clock(Context context) {
        this(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomClockView);
            String tag_ = typedArray.getString(R.styleable.CustomClockView_tag);
            if (tag_ != null) {
                this.tag = tag_;
            }
        } finally {
            typedArray.recycle();
        }
        init();

    }

    public void init() {

        clockBorderWidth = dp2px(10);
        defaultHeight = dp2px(200);
        defaultWidth = dp2px(200);
        defalutPadding = dp2px(10);
        DEGREE_HEIGHT = dp2px(3);
        DEGREE_HEIGHT_BIG = dp2px(12);
        DEGREE_HEIGHT_MIDDLE = dp2px(6);
        DEGREE_WIDTH = dp2px(1);
        degreeTextMargin = dp2px(4);
        degreeLineMargin = dp2px(3);
        handHourMargin = dp2px(60);
        handMinMargin = dp2px(30);
        handSecMargin = dp2px(15);
        centerCircleRadius = dp2px(3);
        handHourWidth = dp2px(4);
        handMinWidth = dp2px(2);
        handSecWidth = dp2px(1);
        handSecOutLen = dp2px(25);
        handMinOutLen = dp2px(15);


        bigTextSize = sp2px(18);
        middleTextSize = sp2px(14);

        tagTextSize = sp2px(10);

        initPaints();

        mClockManager = new ClockManager(this, clockListener);
    }

    private void initPaints() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mClockPaint = new Paint();
        mClockPaint.setStyle(Paint.Style.STROKE);
        mClockPaint.setStrokeWidth(clockBorderWidth);
        mClockPaint.setColor(clockBorderColor);
        mClockPaint.setAntiAlias(true);
        mClockPaint.setShadowLayer(10f, 5, 5, Color.GRAY);

        mTagPaint = new Paint();
        mTagPaint.setStyle(Paint.Style.FILL);
        mTagPaint.setColor(tagTextColor);
        mTagPaint.setTextSize(tagTextSize);
        mTagPaint.setTextAlign(Paint.Align.CENTER);
        mTagPaint.setTypeface(Typeface.SERIF);
        mTagPaint.setAlpha(150);
        mTagPaint.setAntiAlias(true);

        mDegreePaint = new Paint();
        mDegreePaint.setStyle(Paint.Style.STROKE);
        mDegreePaint.setStrokeWidth(DEGREE_WIDTH);
        mDegreePaint.setColor(degreeDefaultColor);
        mDegreePaint.setAntiAlias(true);

        mDegreeTextPaint = new Paint();
        mDegreeTextPaint.setTextSize(bigTextSize);
        mDegreeTextPaint.setColor(bigTextColor);
        mDegreeTextPaint.setTextAlign(Paint.Align.CENTER);
        mDegreeTextPaint.setTypeface(Typeface.SERIF);
        mDegreeTextPaint.setStyle(Paint.Style.FILL);

        mHandPaint = new Paint();
        mHandPaint.setStyle(Paint.Style.STROKE);
        mHandPaint.setStrokeWidth(handSecWidth);
        mHandPaint.setColor(handDefalutColor);
        mHandPaint.setAntiAlias(true);
        mHandPaint.setShadowLayer(10f, 5, 5, Color.GRAY);

        mCenterPaint = new Paint();
        mCenterPaint.setStyle(Paint.Style.STROKE);
        mCenterPaint.setStrokeWidth(centerCircleRadius * 2);
        mCenterPaint.setColor(centerPointColor);
        mCenterPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = getMeasuredSize(widthMeasureSpec, defaultWidth);
        int heightSize = getMeasuredSize(heightMeasureSpec, defaultHeight);
        int size = Math.max(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    private int getMeasuredSize(int measureSpec, int defalut) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.AT_MOST:
                result = defalut;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = defalut;
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = size < defalut ? defalut : size;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //相对于父容器的坐标，而不是屏幕
        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() / 2;

        int radius = getMeasuredWidth() / 2 - defalutPadding;

        canvas.drawCircle(px, py, radius, mClockPaint);

        if (needReDraw) {

            canvas.drawCircle(px, py, radius, mClockPaint);

            //绘制tag
            canvas.drawText(tag, px, py + radius / 2, mTagPaint);

//      save方法用于临时保存画布坐标系统的状态
//      restore方法可以用来恢复save之后设置的状态,
            int degreeTotal = 60;
            for (int i = 0; i < degreeTotal; i++) {
                canvas.save();
                int degreeCurrent = 360 / degreeTotal * i;
                canvas.rotate(degreeCurrent, px, py);
                RectF r;
                //绘制刻度
                if (degreeCurrent % 90 == 0)
                    r = new RectF(px - DEGREE_WIDTH / 2, py - radius + clockBorderWidth, px + DEGREE_WIDTH / 2, py - radius + DEGREE_HEIGHT_BIG + clockBorderWidth);
                else if (degreeCurrent % 30 == 0)
                    r = new RectF(px - DEGREE_WIDTH / 2, py - radius + degreeLineMargin + clockBorderWidth, px + DEGREE_WIDTH / 2, py - radius + DEGREE_HEIGHT_MIDDLE + degreeLineMargin + clockBorderWidth);
                else
                    r = new RectF(px - DEGREE_WIDTH / 2, py - radius + degreeLineMargin + clockBorderWidth, px + DEGREE_WIDTH / 2, py - radius + DEGREE_HEIGHT + degreeLineMargin + clockBorderWidth);
                canvas.drawRect(r, mDegreePaint);
                canvas.restore();

                //绘制1级数字(3,6,9,12)
                mDegreeTextPaint.setTextSize(bigTextSize);
                Paint pText = mDegreeTextPaint;
                if (degreeCurrent == 0) {
                    pText.setTextAlign(Paint.Align.CENTER);
                    Paint.FontMetrics fontMetrics = pText.getFontMetrics();
//                Log.i("zhujm", "ascent:" + fontMetrics.ascent + "  bottom:" + fontMetrics.bottom + "  descent:" + fontMetrics.descent+"   top:"+fontMetrics.top+"  leading:"+fontMetrics.leading);
                    canvas.drawText("12", px, r.bottom + degreeTextMargin + Math.abs(fontMetrics.ascent), pText);
                } else if (degreeCurrent == 90) {
                    pText.setTextAlign(Paint.Align.RIGHT);
                    Paint.FontMetrics fontMetrics = pText.getFontMetrics();
                    canvas.drawText("3", px + radius - r.height() - degreeTextMargin - clockBorderWidth, py + Math.abs(fontMetrics.ascent) / 2, pText);
                } else if (degreeCurrent == 180) {
                    pText.setTextAlign(Paint.Align.CENTER);
                    Paint.FontMetrics fontMetrics = pText.getFontMetrics();
                    canvas.drawText("6", px, py + radius - r.height() - degreeTextMargin - Math.abs(fontMetrics.descent) - clockBorderWidth, pText);
                } else if (degreeCurrent == 270) {
                    pText.setTextAlign(Paint.Align.LEFT);
                    Paint.FontMetrics fontMetrics = pText.getFontMetrics();
                    canvas.drawText("9", px - radius + r.height() + degreeTextMargin + clockBorderWidth, py + Math.abs(fontMetrics.ascent) / 2, pText);
                }

                //绘制2级数字(1,2,4,5,7,8,10,11)
                if (degreeCurrent % 30 == 0 && degreeCurrent != 0) {
                    mDegreeTextPaint.setTextSize(middleTextSize);
                    Paint pTextm = mDegreeTextPaint;
                    String text = (degreeCurrent * 12 / 360) + "";
                    int toTextLen = radius - DEGREE_HEIGHT_MIDDLE - degreeLineMargin - degreeTextMargin - clockBorderWidth;
                    int d = degreeCurrent % 90;
                    Rect textWH = getTextWH(text, pTextm);
                    pTextm.setTextAlign(Paint.Align.CENTER);
                    if (degreeCurrent > 0 && degreeCurrent < 90) {
                        float tx = (float) (px + toTextLen * sin(d));
                        float ty = (float) (py - toTextLen * cos(d));
                        canvas.drawText(text, tx - textWH.width() / 2, ty + textWH.height() / 2, pTextm);
                    } else if (degreeCurrent > 90 && degreeCurrent < 180) {
                        float tx = (float) (px + toTextLen * sin(90 - d));
                        float ty = (float) (py + toTextLen * cos(90 - d));
                        canvas.drawText(text, tx - textWH.width() / 2, ty, pTextm);
                    } else if (degreeCurrent > 180 && degreeCurrent < 270) {
                        float tx = (float) (px - toTextLen * sin(d));
                        float ty = (float) (py + toTextLen * cos(d));
                        canvas.drawText(text, tx + textWH.width() / 2, ty, pTextm);
                    } else if (degreeCurrent > 270 && degreeCurrent < 360) {
                        float tx = (float) (px - toTextLen * sin(90 - d));
                        float ty = (float) (py - toTextLen * cos(90 - d));
                        canvas.drawText(text, tx + textWH.width() / 2, ty + textWH.height() / 2, pTextm);
                    }
                }
            }
        }
//        needReDraw = false;

        Paint pHand = mHandPaint;

        //时针
        canvas.save();
        pHand.setStrokeWidth(handHourWidth);
        pHand.setColor(handDefalutColor);
        canvas.rotate(hourAngle, px, py);
        canvas.drawLine(px, py, px, py - radius + handHourMargin, pHand);
        canvas.restore();

        //分针
        canvas.save();
        pHand.setStrokeWidth(handMinWidth);
        pHand.setColor(handDefalutColor);
        canvas.rotate(minuteAngle, px, py);
        canvas.drawLine(px, py + handMinOutLen, px, py - radius + handMinMargin, pHand);
        canvas.restore();

        //秒针

        pHand.setStrokeWidth(handSecWidth);
        pHand.setColor(Color.RED);
        canvas.save();
        canvas.rotate(secondAngle, px, py);
        canvas.drawLine(px, py + handSecOutLen, px, py - radius + handSecMargin, pHand);
        canvas.restore();

        //中心点
        canvas.drawCircle(px, py, centerCircleRadius, mCenterPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        needReDraw = true;
        mClockManager.attachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        needReDraw = true;
        mClockManager.detachedFromWindow();
    }

    private double sin(int degree) {
        return Math.sin(Math.PI * degree / 180);
    }

    private double cos(int degree) {
        return Math.cos(Math.PI * degree / 180);

    }

    private int dp2px(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    private int sp2px(int spValue) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private Rect getTextWH(String text, Paint p) {
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }


}
