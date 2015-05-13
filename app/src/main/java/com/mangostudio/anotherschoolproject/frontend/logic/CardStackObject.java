package com.mangostudio.anotherschoolproject.frontend.logic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.graphic.CardVisual;
import com.mangostudio.anotherschoolproject.frontend.graphic.DeckVisuals;
import com.mangostudio.anotherschoolproject.frontend.graphic.Graphic;
import com.mangostudio.anotherschoolproject.frontend.graphic.SVGGraphic;

public class CardStackObject {

    private static final Paint WHITE_TEXT = new Paint();
    static {
        WHITE_TEXT.setColor(Color.LTGRAY);
        WHITE_TEXT.setFlags(Paint.ANTI_ALIAS_FLAG | WHITE_TEXT.getFlags());
    }

    private static final float CM_WIDTH = 4;

    private final ResourceLoader res;
    private final DeckVisuals visuals;
    private final CardStack stack;

    private final Graphic stackLines;

    private boolean flipped;
    private final PointF position;

    public CardStackObject(ResourceLoader res, DeckVisuals visuals, CardStack stack, boolean flipped, float posX, float posY) {
        this.res = res;
        this.visuals = visuals;
        this.stack = stack;
        this.flipped = flipped;

        this.position = new PointF(posX, posY);
        this.stackLines = new SVGGraphic(res.stackSpace).setRelativeOrigin(0, 0);
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public PointF getPosition() {
        return this.position;
    }

    public CardStack getStack() {
        return stack;
    }

    private String getSizeString(int n) {
        return n + (n == 1 ? " Karte" : " Karten");
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(position.x, position.y);
        stackLines.draw(canvas, WHITE_TEXT);
        canvas.save();
        canvas.scale(5, 5);
        canvas.drawText(getSizeString(stack.getCards().size()), 0, -1, WHITE_TEXT);
        canvas.restore();
        if (!stack.getCards().isEmpty()) {
            Card topCard = stack.getCards().peek();
            CardVisual visual = visuals.getCardVisual(topCard);
            float tx = stackLines.getWidth() - visual.getWidth();
            float ty = stackLines.getHeight() - visual.getHeight();
            canvas.translate(tx / 2, ty / 2);
            if (flipped) {
                visuals.drawFlipped(canvas);
            } else {
                visual.draw(canvas);
            }
        }
        canvas.restore();
    }

    public boolean pointInsideStack(float x, float y) {
        return x >= position.x && y >= position.y && x <= position.x+getWidth() && y <= position.y+getHeight();
    }

    public float getWidth() {
        return stackLines.getWidth();
    }

    public float getHeight() {
        return stackLines.getHeight();
    }
}
