package com.garage.breco.smartgarage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.TimeUnit;


/**
 * Created by stormset on 2016. 11. 08.
 */
public class CircularTextView extends View {
    public String text = ""; //20 char
    RectF oval;
    String unit_sec = getResources().getString(R.string.seconds);
    String unit_minute = getResources().getString(R.string.minutes);
    String unit_hour = getResources().getString(R.string.hours);
    String unit_day = getResources().getString(R.string.days);
    private Path mArc;
    private Paint mPaintText;

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mArc = new Path();
        oval = new RectF(0, 0, 100, 100);
        mArc.addArc(oval, -160, 300);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(getResources().getDimensionPixelSize(R.dimen.circularTextSize));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 150;
        int desiredHeight = 150;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
        oval.set(0, 5, width - 5, height - 10);
        mArc = new Path();
        mArc.addArc(oval, -148, 300);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawTextOnPath(text, mArc, 0, 20, mPaintText);
        invalidate();
    }

    public void setTime(long seconds) {
        //15
        boolean lowerThan = false;
        int c = 0;
        int maxLength = 15;
        int days = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        StringBuilder text = new StringBuilder();
        if (days != 0) {
            if (hours != 0) {
                text.append(days).append(" ").append(unit_day).append("  ").append(hours).append(" ").append(unit_hour);
            } else if (minute != 0) {
                text.append(days).append(" ").append(unit_day).append("  ").append(minute).append(" ").append(unit_minute);
            } else {
                text.append(days).append(" ").append(unit_day);
            }
        } else if (minute != 0) {
            if (hours != 0) {
                text.append(hours).append(" ").append(unit_hour).append("  ").append(minute).append(" ").append(unit_minute);
            } else if (second != 0) {
                text.append(minute).append(" ").append(unit_minute).append("  ").append(second).append(" ").append(unit_sec);
            } else {
                text.append(minute).append(" ").append(unit_minute);
            }
        } else if (second != 0) {
            text.append(second).append(" ").append(unit_sec);
        }

        int length = text.length();
        if (length < maxLength) {
            if (length % 2 == 0) {
                lowerThan = true;
                c = maxLength - length - 1;
            } else c = maxLength - length;
            for (int i = 0; i < (c / 2); i++) {
                text.insert(0, " ");
                text.insert(text.length(), " ");
            }
            if (lowerThan) {
                text.insert(0, "  ");
            }
        }
        this.text = text.toString();
    }
}
