package com.app.mahindrafinancemfact.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.app.mahindrafinancemfact.R;

public class CircularTextView extends View {
    private Paint paint;
    private String text = "";

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int radius = Math.min(viewWidth, viewHeight) / 2;

        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, radius, paint);

        paint.setColor(getResources().getColor(R.color.black));
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        float xPos = viewWidth / 2;
        float yPos = (viewHeight / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(text, xPos, yPos, paint);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}

