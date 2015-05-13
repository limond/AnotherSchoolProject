package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TextGraphic extends Graphic {

    private final String text;
    private final int color;

    private float scaleX = 1, scaleY = 1;

    public TextGraphic(int color, String text) {
        super(0, 0, 0, 0);
        this.text = text;
        this.color = color;
    }

    @Override
    public Graphic scale(float sx, float sy) {
        this.scaleX *= sx;
        this.scaleY *= sy;
        return super.scale(sx, sy);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        int prev = paint.getColor();
        paint.setColor(color);
        canvas.translate(translation.x, translation.y);
        //canvas.scale(size.x / originalSize.x, size.y / originalSize.y);
        canvas.scale(scaleX, scaleY);
        canvas.drawText(text, 0, 0, paint);
        canvas.restore();
        paint.setColor(prev);
    }
}
