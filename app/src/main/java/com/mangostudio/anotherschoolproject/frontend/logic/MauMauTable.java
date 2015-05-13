package com.mangostudio.anotherschoolproject.frontend.logic;

import android.bluetooth.BluetoothDevice;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.hardware.camera2.params.BlackLevelPattern;
import android.view.MotionEvent;
import android.view.View;

import com.mangostudio.anotherschoolproject.BluetoothPackage;
import com.mangostudio.anotherschoolproject.InterThreadCom;
import com.mangostudio.anotherschoolproject.frontend.ResourceLoader;
import com.mangostudio.anotherschoolproject.frontend.graphic.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MauMauTable implements View.OnTouchListener {

    private static final class CardDragInfo {
        final CardStackObject draggingFrom;
        final Card dragging;
        final boolean cardWasFlipped;
        float x, y;

        public CardDragInfo(CardStackObject draggingFrom, Card dragging, boolean wasFlipped) {
            this.draggingFrom = draggingFrom;
            this.dragging = dragging;
            this.cardWasFlipped = wasFlipped;
        }
    }

    private final DeckVisuals deckVisuals;

    private final ArrayList<CardStackObject> stacks = new ArrayList<>();
    private ArrayList<PlayerBubble> playerBubbles;

    private CardDragInfo dragInfo = null;

    public MauMauTable(ResourceLoader res) {
        deckVisuals = new DeckVisuals(res);

        stacks.add(new CardStackObject(res, deckVisuals, new CardStack().shuffle(), true, 100, 100));
        stacks.add(new CardStackObject(res, deckVisuals, new CardStack(new Stack<Card>()), false, 400, 100));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                CardStackObject stackObject = stackObjectAt(x, y);
                if (stackObject != null && !stackObject.getStack().getCards().isEmpty()) {
                    dragInfo = new CardDragInfo(stackObject, stackObject.getStack().getCards().pop(), stackObject.isFlipped());
                    dragInfo.x = x;
                    dragInfo.y = y;
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
                    CardStackObject stackObject1 = stackObjectAt(x, y);
                    if (stackObject1 != null) {
                        stackObject1.getStack().getCards().push(dragInfo.dragging);
                    } else {
                        PlayerBubble bubble = playerBubbleAt(x, y);
                        if (bubble != null) {
                            BluetoothPackage pkg = new BluetoothPackage(BluetoothPackage.HANDLER_DESTINATION_UI, BluetoothPackage.ACTION_GIVE_CLIENT_CARD);
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("card", dragInfo.dragging);
                            pkg.setAdditionalData(data);
                            InterThreadCom.sendPackage(pkg, bubble.getPlayersDevice().getAddress());
                        } else dragInfo.draggingFrom.getStack().getCards().push(dragInfo.dragging);
                    }
                    dragInfo = null;
                return true;
                }
                break;
        }
        return false;
    }

    public CardStackObject stackObjectAt(float x, float y) {
        for (CardStackObject stack : stacks) {
            if (stack.pointInsideStack(x, y)) return stack;
        }
        return null;
    }

    public PlayerBubble playerBubbleAt(float x, float y) {
        for (PlayerBubble bubble : playerBubbles) {
            if (bubble.pointInsideBubble(x, y)) return bubble;
        }
        return null;
    }

    public void draw(Canvas canvas) {
        float ymid = canvas.getHeight()/2;
        float xmid = canvas.getWidth()/2;
        for (int i = 0; i < stacks.size(); i++) {
            stacks.get(i).setPosition(xmid+((float)i-(float)(stacks.size())/2)*(stacks.get(i).getWidth()+40), ymid-stacks.get(i).getHeight()/2);
            stacks.get(i).draw(canvas);
        }
        if (playerBubbles != null) {
            int count = playerBubbles.size();
            final float pad = 2.1f*PlayerBubble.RADIUS;
            for (int i = 0; i < count; i++) {
                PointF pos = getPos(i / (float)count, canvas.getWidth()-pad, canvas.getHeight()-pad);
                playerBubbles.get(i).setPosition(pos.x + xmid, pos.y + ymid);
                playerBubbles.get(i).draw(canvas);
            }
        }
        if (dragInfo != null) {
            CardVisual visual = deckVisuals.getCardVisual(dragInfo.dragging);
            canvas.save();
            canvas.translate(dragInfo.x - visual.getWidth() / 2, dragInfo.y - visual.getHeight() / 2);
            if (dragInfo.cardWasFlipped) {
                deckVisuals.drawFlipped(canvas);
            } else {
                visual.draw(canvas);
            }
            canvas.restore();
        }
    }

    public void setConnectedDevices(ArrayList<BluetoothDevice> connectedDevices) {
        playerBubbles = new ArrayList<>();
        for (int i = 0; i < connectedDevices.size(); i++) {
            playerBubbles.add(new PlayerBubble(connectedDevices.get(i)));
        }
    }

    // Eine Oval-ähnliche parametrische Funktion, die am Rand eines Rechtecks verläuft:
    private PointF getPos(float t, float w, float h) {
        final double pi2 = 2 * Math.PI;
        return new PointF(w/2 * (float)Math.cos(pi2 * t), h/2 * (float)Math.sin(pi2 * t));
    }

    private float f(float x) {
        final float c = 0.12f;
        return 1 + square((float)Math.sin(4 * Math.PI * x)) * c;
    }

    private float square(float f) {
        return f * f;
    }

    public void addCard(Card card) {
        stacks.get(1).getStack().getCards().push(card);
    }
}
