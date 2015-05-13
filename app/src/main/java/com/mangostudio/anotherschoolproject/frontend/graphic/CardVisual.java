package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.logic.Card;
import com.mangostudio.anotherschoolproject.frontend.logic.CardColor;
import com.mangostudio.anotherschoolproject.frontend.logic.CardType;

public class CardVisual {

    private static final float PADDING = 6;
    private static final Paint CARD_PAINT = new Paint();
    static {
        CARD_PAINT.setFlags(Paint.ANTI_ALIAS_FLAG | CARD_PAINT.getFlags());
    }

    private final ResourceLoader res;
    private final Card card;
    private final CardInnerVisual innerVisual;

    private Graphic emptyCard;
    private Graphic colorTop;
    private Graphic colorBottom;
    private Graphic charTop;
    private Graphic charBottom;

    public CardVisual(ResourceLoader res, Card card) {
        this.res = res;
        this.card = card;

        emptyCard = new SVGGraphic(res.emptyCard).setRelativeOrigin(0, 0);

        float width = emptyCard.getWidth();
        float height = emptyCard.getHeight();

        innerVisual = getFittingInnerVisual(res, card, width, height);

        Rect ssize = new Rect();
        CARD_PAINT.getTextBounds(card.type.identifier, 0, card.type.identifier.length(), ssize);
        float s = getIdentifierScaling(card.type);
        float cs = 3.5f; // character scale
        float swidth = ssize.width() * cs;
        float sheight = ssize.height() * cs;

        //spadesOrnated = new SVGGraphic(res.spadesAce).scale(3, 3).move(width / 2, height / 2);
        colorTop = new SVGGraphic(res.getColor(card.color))
                .setRelativeOrigin(0, 0)
                .scale(s, s)
                .move(PADDING, PADDING+sheight + 2);
        colorBottom = new SVGGraphic(res.getColor(card.color))
                .setRelativeOrigin(0, 0)
                .scale(-s, -s)
                .move(width - PADDING, height - (PADDING+sheight + 2));

        charTop = new TextGraphic(card.color.color, card.type.identifier)
                .scale(cs, cs)
                .move(PADDING+(colorTop.getWidth()-swidth)/2, PADDING+sheight);
        charBottom = new TextGraphic(card.color.color, card.type.identifier)
                .scale(-cs, -cs)
                .move(width-(PADDING+(colorTop.getWidth()-swidth)/2), height-(PADDING+sheight));
    }

    private static CardInnerVisual getFittingInnerVisual(ResourceLoader res, Card card, float w, float h) {
        float aceScale = card.color == CardColor.SPADES ? 3 : 1;
        switch (card.type) {
            case ACE: return new CardInnerVisualSpecial(new SVGGraphic(res.getAceInner(card.color)).scale(aceScale, aceScale).move(w/2, h/2));
            case JACK: return new CardInnerVisualSpecial(new SVGGraphic(res.getJackInner(card.color)).move(w/2, h/2));
            case QUEEN: return new CardInnerVisualSpecial(new SVGGraphic(res.getQueenInner(card.color)).move(w/2, h/2));
            case KING: return new CardInnerVisualSpecial(new SVGGraphic(res.getKingInner(card.color)).move(w/2, h/2));
            default: return new CardInnerVisualNumber(res, card, w, h);
        }
    }

    private static float getIdentifierScaling(CardType type) {
        switch (type) {
            case JACK: case QUEEN: case KING: return 0.7f;
            default: return 1;
        }
    }

    public void draw(Canvas canvas) {
        emptyCard.draw(canvas, CARD_PAINT);
        innerVisual.draw(canvas, CARD_PAINT);
        colorTop.draw(canvas, CARD_PAINT);
        colorBottom.draw(canvas, CARD_PAINT);
        charTop.draw(canvas, CARD_PAINT);
        charBottom.draw(canvas, CARD_PAINT);
    }

    public float getWidth() {
        return emptyCard.getWidth();
    }

    public float getHeight() {
        return emptyCard.getHeight();
    }
}
