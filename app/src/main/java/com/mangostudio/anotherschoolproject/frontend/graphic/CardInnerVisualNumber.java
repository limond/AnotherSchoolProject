package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.logic.Card;

public class CardInnerVisualNumber implements CardInnerVisual {

    private static final class SymbolPatch {
        final float[][] upper;
        final float[][] lower;

        public SymbolPatch(float[][] upper, float[][] lower) {
            this.upper = upper;
            this.lower = lower;
        }

        // In case it is Symmetrical
        public SymbolPatch(float[][] upper) {
            this(upper, calcSymmetric(upper));
        }

        static float[][] calcSymmetric(float[][] otherSide) {
            float[][] patch = new float[otherSide.length][2];
            for (int i = 0; i < otherSide.length; i++) {
                patch[i][0] = otherSide[i][0];
                patch[i][1] = -otherSide[i][1]; // mirroring, tadaaa
            }
            return patch;
        }
    }

    private static final SymbolPatch[] arrangements = {
            new SymbolPatch(new float[][] { {0, -1} }), // For the 2s
            new SymbolPatch(new float[][] { {0, -1}, {0, 0} }, new float[][] { {0, 1} }), // For the 3s, and so on
            new SymbolPatch(new float[][] { {-0.5f, -1}, {0.5f, -1} }),
            new SymbolPatch(new float[][] { {-0.5f, -1}, {0.5f, -1}, {0, 0} }, new float[][] { {-0.5f, 1}, {0.5f, 1} }),
            new SymbolPatch(new float[][] { {-0.5f, -1f}, {0.5f, -1f}, {-0.5f, 0}, {0.5f, 0} }, new float[][] { {-0.5f, 1f}, {0.5f, 1f} }),
            new SymbolPatch(new float[][] { {-0.5f, -1.2f}, {0.5f, -1.2f}, {0, -0.6f}, {-0.5f, 0}, {0.5f, 0} }, new float[][] { {-0.5f, 1.2f}, {0.5f, 1.2f} }),
            new SymbolPatch(new float[][] { {-0.5f, -1.2f}, {0.5f, -1.2f}, {0, -0.6f}, {-0.5f, 0}, {0.5f, 0} }, new float[][] { {-0.5f, 1.2f}, {0.5f, 1.2f}, {0, 0.6f}}),
            new SymbolPatch(new float[][] { {-0.5f, -1.4f}, {0.5f, -1.4f}, {0, -0.9f}, {-0.5f, -0.4f}, {0.5f, -0.4f} }, new float[][] { {-0.5f, 0.4f}, {0.5f, 0.4f}, {-0.5f, 1.4f}, {0.5f, 1.4f}}),
            new SymbolPatch(new float[][] { {-0.5f, -1.4f}, {0.5f, -1.4f}, {0, -0.9f}, {-0.5f, -0.4f}, {0.5f, -0.4f} })
    };

    private static final float SYMBOL_SCALE = 1.5f;

    private final SymbolPatch patch;
    private final Graphic cardColorSymbolUp;
    private final Graphic cardColorSymbolDown;
    private final float w, h;

    public CardInnerVisualNumber(ResourceLoader res, Card card, float w, float h) {
        if (card.type.ordinal() > 8) throw new IllegalArgumentException(); // Not a number card
        this.patch = arrangements[card.type.ordinal()];
        this.cardColorSymbolUp = new SVGGraphic(res.getColor(card.color)).scale(SYMBOL_SCALE, SYMBOL_SCALE);
        this.cardColorSymbolDown = new SVGGraphic(res.getColor(card.color)).scale(SYMBOL_SCALE, -SYMBOL_SCALE);
        this.w = w;
        this.h = h;
    }

    private void drawSeveralGraphics(float[][] positions, Graphic g, Canvas canvas, Paint paint) {
        for (int i = 0; i < positions.length; i++) {
            float x = positions[i][0]*70;
            float y = positions[i][1]*70;
            canvas.save();
            canvas.translate(x, y);
            g.draw(canvas, paint);
            canvas.restore();
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.translate(w/2, h/2);
        drawSeveralGraphics(patch.upper, cardColorSymbolUp, canvas, paint);
        drawSeveralGraphics(patch.lower, cardColorSymbolDown, canvas, paint);
        canvas.restore();
    }

}
