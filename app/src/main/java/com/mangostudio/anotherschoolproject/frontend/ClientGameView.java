package com.mangostudio.anotherschoolproject.frontend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.mangostudio.anotherschoolproject.BluetoothDeviceRepresentation;
import com.mangostudio.anotherschoolproject.frontend.logic.Card;
import com.mangostudio.anotherschoolproject.frontend.logic.ClientHand;

public class ClientGameView extends SurfaceView implements View.OnTouchListener {

    private static final Paint BG = new Paint();
    static {
        BG.setColor(Color.DKGRAY);
        BG.setAlpha(255);
        BG.setStrokeWidth(1);
    }

    private ResourceLoader res;
    private SurfaceHolder holder;

    private ClientHand hand;

    public ClientGameView(Context context) {
        super(context);
        init(context);
    }

    public ClientGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClientGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context ctx) {
        res = new ResourceLoader(ctx, ctx.getResources());
        hand = new ClientHand(res);

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                draw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        this.setOnTouchListener(this);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), BG);
        hand.draw(canvas);
    }

    public void repaint() {
        Canvas c = null;
        try {
            c = holder.lockCanvas();
            draw(c);
        } finally {
            if (c != null) holder.unlockCanvasAndPost(c);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean evUsed = hand.onTouch(v, event);
        if (evUsed) repaint();
        return evUsed;
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public void setHost(BluetoothDeviceRepresentation host) {
        hand.setHost(host);
    }
}
