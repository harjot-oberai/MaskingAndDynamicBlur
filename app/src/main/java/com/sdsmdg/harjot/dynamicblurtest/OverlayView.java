package com.sdsmdg.harjot.dynamicblurtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 15-Dec-16.
 */

public class OverlayView extends View {

    Paint maskPaint, paint;

    Bitmap original, mask;
    Canvas maskCanvas, bitmapCanvas;

    boolean firstDraw = true;

    BitmapShader shader;

    List<Circle> circles = new ArrayList<>();

    Handler handler;
    Runnable r;

    public OverlayView(Context context) {
        super(context);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        maskPaint = new Paint();
        maskPaint.setColor(Color.WHITE);
        maskPaint.setAntiAlias(true);
        maskPaint.setStrokeWidth(150);
        maskPaint.setStyle(Paint.Style.FILL);

        handler = new Handler();

        r = new Runnable() {
            @Override
            public void run() {
                try {
                    invalidate();
                } finally {
                    handler.postDelayed(r, 1000 / 60);
                }
            }
        };

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (firstDraw) {

            original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(original);

            Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.img_back);
            Rect src = new Rect(0, 0, back.getWidth(), back.getHeight());
            Rect dest = new Rect(0, 0, width, height);

            bitmapCanvas.drawBitmap(back, src, dest, null);

            shader = new BitmapShader(original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);
            paint.setStyle(Paint.Style.FILL);
            setLayerType(LAYER_TYPE_HARDWARE, paint);

            r.run();

            firstDraw = false;
        }

        mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(mask);

        List<Circle> tmp = new ArrayList<>();

        for (int i = 0; i < circles.size(); i++) {
            Circle circle = circles.get(i);
            maskCanvas.drawCircle(circle.x, circle.y, circle.radius, maskPaint);
            circle.radius -= circle.reductionSpeed;
            if (circle.radius <= 0) {
                tmp.add(circle);
            }
        }

        for (int i = 0; i < tmp.size(); i++) {
            circles.remove(tmp.get(i));
        }

        mask = convertToAlphaMask(mask);
        canvas.drawBitmap(mask, 0, 0, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            circles.add(new Circle(event.getX(), event.getY()));
            invalidate();
        }
        return true;
    }

    private Bitmap convertToAlphaMask(Bitmap mask) {
        Bitmap alphaMask = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(alphaMask);
        canvas.drawBitmap(mask, 0.0f, 0.0f, null);
        return alphaMask;
    }

    private class Circle {
        float x, y;
        int radius = 130;
        int reductionSpeed = 5;

        Circle(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
