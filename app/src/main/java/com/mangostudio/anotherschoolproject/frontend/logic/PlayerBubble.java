package com.mangostudio.anotherschoolproject.frontend.logic;

import android.bluetooth.BluetoothDevice;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.FloatMath;

public class PlayerBubble {

    public static final float RADIUS = 60;

    private static final Paint CIRCLE_PAINT = new Paint();
    static {
        CIRCLE_PAINT.setStrokeWidth(10);
        CIRCLE_PAINT.setStyle(Paint.Style.STROKE);
        CIRCLE_PAINT.setColor(Color.GRAY);
        CIRCLE_PAINT.setFlags(Paint.ANTI_ALIAS_FLAG | CIRCLE_PAINT.getFlags());
    }
    private static final Paint TEXT_PAINT = new Paint();
    static {
        TEXT_PAINT.setColor(Color.LTGRAY);
    }

    private final BluetoothDevice playersDevice;
    private final String text;
    private final float textSize;
    private final float offX, offY;
    private final PointF position;

    public PlayerBubble(BluetoothDevice device) {
        this.playersDevice = device;
        this.position = new PointF();

        this.text = device.getName();
        Rect textMearsures = new Rect();
        TEXT_PAINT.getTextBounds(text, 0, text.length(), textMearsures);
        this.textSize = 110 / textMearsures.width();
        this.offX = -textMearsures.width()/2;
        this.offY = textMearsures.height()/2;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public PointF getPosition() {
        return position;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(position.x, position.y);

        canvas.save();
        canvas.scale(textSize, textSize);
        canvas.drawText(text, offX, offY, TEXT_PAINT);
        canvas.restore();

        canvas.drawCircle(0, 0, RADIUS, CIRCLE_PAINT);
        canvas.restore();
    }

    public boolean pointInsideBubble(float x, float y) {
        return FloatMath.hypot(x-position.x, y-position.y) <= RADIUS;
    }

    public BluetoothDevice getPlayersDevice() {
        return playersDevice;
    }

}
