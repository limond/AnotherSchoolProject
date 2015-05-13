package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.logic.Card;
import com.mangostudio.anotherschoolproject.frontend.logic.CardColor;
import com.mangostudio.anotherschoolproject.frontend.logic.CardType;

public class DeckVisuals {

    private static final Paint CARD_PAINT = new Paint();
    static {
        CARD_PAINT.setFlags(Paint.ANTI_ALIAS_FLAG | CARD_PAINT.getFlags());
    }

    private final CardVisual[] visuals;
    private final Graphic cardBackside;

    public DeckVisuals(ResourceLoader res) {
        this.cardBackside = new SVGGraphic(res.cardBackside).setRelativeOrigin(0, 0);
        // Erstelle Visuals für alle Karten eines Decks:
        this.visuals = new CardVisual[CardColor.values().length * CardType.values().length];
        for (int col = 0; col < CardColor.values().length; col++) {
            for (int typ = 0; typ < CardType.values().length; typ++) {
                Card card = new Card(CardType.values()[typ], CardColor.values()[col]);
                visuals[typ * CardColor.values().length + col] = new CardVisual(res, card);
            }
        }
    }

    private int calcIndex(CardType type, CardColor color) {
        return type.ordinal() * CardColor.values().length + color.ordinal();
    }

    public void drawCard(Canvas canvas, Card card) {
        getCardVisual(card).draw(canvas);
    }

    public void drawFlipped(Canvas canvas) {
        cardBackside.draw(canvas, CARD_PAINT);
    }

    public CardVisual getCardVisual(Card card) {
        return visuals[calcIndex(card.type, card.color)];
    }

    public float getCardWidth() {
        return cardBackside.getWidth();
    }

    public float getCardHeight() {
        return cardBackside.getHeight();
    }
}
