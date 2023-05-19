package com.realese.genfit.Frags;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCircleImageView extends CircleImageView {
    private Paint paint;

    public CustomCircleImageView(Context context) {
        super(context);
        init();
    }

    public CustomCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        PathEffect effects = new DashPathEffect(new float[] { 8, 8, 8, 8 }, 0);
        paint.setPathEffect(effects);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = Math.max(getWidth(), getHeight()) / 2f;
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius - 1, paint);
    }
}
