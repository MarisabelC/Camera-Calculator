package com.marisabelchanggmail.firstapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Draw a light gray rectangle of size left:20, right: 700 top: 400
 */

public class FocusBoxView extends View {

    private Paint paint;
    private Rect rect;

    public FocusBoxView(Context context, int left, int right, int top, int bottom){
        super(context);
        paint=new Paint();
        rect=new Rect(left,top,right,bottom);
    }

    /**
     * getRect - get right, left, top and bottom points of the rectangle
     * @return the right, left, top and bottom points of the rectangle
     */
    public Rect getRect(){
        return rect;
    }

    /**
     * Draw the light gray rectangle.
     * @param canvas
     */
    @Override
    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(2);
        canvas.drawRect(rect, paint);
    }
}
