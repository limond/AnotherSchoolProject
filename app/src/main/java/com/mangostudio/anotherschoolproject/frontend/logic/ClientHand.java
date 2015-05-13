package com.mangostudio.anotherschoolproject.frontend.logic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.mangostudio.anotherschoolproject.BluetoothDeviceRepresentation;
import com.mangostudio.anotherschoolproject.BluetoothPackage;
import com.mangostudio.anotherschoolproject.InterThreadCom;
import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.graphic.CardVisual;
import com.mangostudio.anotherschoolproject.frontend.graphic.DeckVisuals;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientHand implements View.OnTouchListener {

    private static final class CardDragInfo {
        final Card dragging;
        final int originalPosition;
        float x, y;

        public CardDragInfo(Card dragging, int originalPosition) {
            this.dragging = dragging;
            this.originalPosition = originalPosition;
        }
    }

    private static final float CARD_SPACING = 100f;

    private final DeckVisuals deckVisuals;

    private ArrayList<Card> cards = new ArrayList<>();
    private CardDragInfo dragInfo = null;

    private float minHeight;
    private int cardsInARow;

    private BluetoothDeviceRepresentation host;

    public ClientHand(ResourceLoader res) {
        deckVisuals = new DeckVisuals(res);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void draw(Canvas canvas) {
        minHeight = canvas.getHeight() * 0.9f;
        cardsInARow = (int)(canvas.getWidth() / CARD_SPACING);
        int col = 0;
        int row = 0;
        for (Card card : cards) {
            canvas.save();
            canvas.translate(col * CARD_SPACING, row * deckVisuals.getCardHeight());
            deckVisuals.drawCard(canvas, card);
            canvas.restore();
            col++;
            if (col == cardsInARow) {
                col = 0;
                row++;
            }
        }
        if (dragInfo != null) {
            CardVisual visual = deckVisuals.getCardVisual(dragInfo.dragging);
            canvas.save();
            canvas.translate(dragInfo.x - visual.getWidth() / 2, dragInfo.y - visual.getHeight() / 2);
            visual.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Card card = cardAt(x, y);
                if (card != null) {
                    dragInfo = new CardDragInfo(card, cards.indexOf(card));
                    dragInfo.x = x;
                    dragInfo.y = y;
                    cards.remove(card);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragInfo != null) {
                    dragInfo.x = x;
                    dragInfo.y = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragInfo != null) {
                    if (y >= minHeight) {
                        BluetoothPackage pkg = new BluetoothPackage(BluetoothPackage.HANDLER_DESTINATION_UI, BluetoothPackage.ACTION_GIVE_HOST_CARD);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("card", dragInfo.dragging);
                        pkg.setAdditionalData(data);
                        InterThreadCom.sendPackage(pkg, host.getAddress());
                    } else {
                        int index = clamp(cardIndexAt(x, y), 0, cards.size());
                        cards.add(index, dragInfo.dragging);
                    }
                    dragInfo = null;
                    return true;
                }
                break;
        }
        return false;
    }

    private int clamp(int value, int lower, int upper) {
        return Math.max(lower, Math.min(upper, value));
    }

    public int cardIndexAt(float x, float y) {
        int col = (int)(x / CARD_SPACING);
        int row = (int)(y / deckVisuals.getCardHeight());
        return row * cardsInARow + col;
    }

    public Card cardAt(float x, float y) {
        int index = cardIndexAt(x, y);
        if (0 <= index && index < cards.size()) {
            return cards.get(index);
        }
        return null;
    }

    public void setHost(BluetoothDeviceRepresentation host) {
        this.host = host;
    }
}
