package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Graphic {

    protected final PointF translation;
    protected final PointF size;
    protected final PointF originalSize;

    public Graphic(PointF translation, PointF size) {
        this.translation = translation;
        this.size = size;
        this.originalSize = new PointF(size.x, size.y);
    }

    public Graphic(float posX, float posY, float width, float height) {
        this.translation = new PointF(posX, posY);
        this.size = new PointF(width, height);
        this.originalSize = new PointF(width, height);
    }

    public abstract void draw(Canvas canvas, Paint paint);

    // return itself for method chaining
    public Graphic move(float dx, float dy) {
        translation.offset(dx, dy);
        return this;
    }

    // return itself for method chaining
    public Graphic setTranslation(float tx, float ty) {
        translation.set(tx, ty);
        return this;
    }

    // return itself for method chaining
    public Graphic scale(float sx, float sy) {
        translation.x *= sx;
        translation.y *= sy;
        size.x *= sx;
        size.y *= sy;
        return this;
    }

    // return itself for method chaining
    public Graphic setRelativeOrigin(float rx, float ry) {
        return setTranslation(-rx*size.x, -ry*size.y);
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

}
