package com.tacostudios.apps.bogey;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class ScrollWheel extends View {
    private final Paint arc_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arc_paint_alt = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float width;
    private float height;
    private float radius;

    private String[] names = {"Name1A", "Name1B", "Name2A", "Name2B"};

    private int segments = 16;

    public ScrollWheel(Context context, AttributeSet attr) {
        super(context, attr);

        // names = new String[NewGameData.NUM_PLAYERS];

        init();
    }

    private void init() {
        arc_paint.setStyle(Paint.Style.STROKE);
        arc_paint.setStrokeWidth( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, Resources.getSystem().getDisplayMetrics()) );
        arc_paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        arc_paint_alt.setStyle(Paint.Style.STROKE);
        arc_paint_alt.setStrokeWidth( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, Resources.getSystem().getDisplayMetrics()) );
        arc_paint_alt.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    @Override
    protected void onSizeChanged (final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        if (width > height) {
            radius = height * 3 / 8;
        } else {
            radius = width * 3 / 8;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rect = new RectF();
        rect.set(width/2 - radius, height/2 - radius, width/2 + radius, height/2 + radius);

        // outside stroke
        for (int arc = 0; arc < 4; arc++) {
            if ( (arc % 2) == 0) {
                canvas.drawArc(rect, 90 * arc, 90, false, arc_paint);
            } else {
                canvas.drawArc(rect, 90 * arc, 90, false, arc_paint_alt);
            }
        }

        // first name
        text_paint.setColor(ContextCompat.getColor( getContext(), android.R.color.tab_indicator_text));
        text_paint.setTextAlign(Paint.Align.CENTER);

        float text_size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, Resources.getSystem().getDisplayMetrics());

        text_paint.setTextSize(text_size);

        Path name = new Path();
        name.addArc(rect, 180, 90);
        canvas.drawTextOnPath(names[0], name, 0, -text_size/4, text_paint);

        name = new Path();
        name.addArc(rect, 270, 90);
        canvas.drawTextOnPath(names[1], name, 0, -text_size/4, text_paint);

        name = new Path();
        name.addArc(rect, 90, 90);
        canvas.drawTextOnPath(names[2], name, 0, -text_size/4, text_paint);

        name = new Path();
        name.addArc(rect, 0, 90);
        canvas.drawTextOnPath(names[3], name, 0, -text_size/4, text_paint);
    }

    public void setNames (String player_names[]) {
        names = player_names;
        invalidate();
    }

}
