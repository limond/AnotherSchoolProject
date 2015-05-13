package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CardInnerVisualSpecial implements CardInnerVisual {

    private final Graphic innerGraphic;

    public CardInnerVisualSpecial(Graphic graphic) {
        this.innerGraphic = graphic;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        innerGraphic.draw(canvas, paint);
    }
}
