package com.mangostudio.anotherschoolproject.frontend;

import android.content.Context;
import android.content.res.Resources;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.mangostudio.anotherschoolproject.R;
import com.mangostudio.anotherschoolproject.frontend.logic.CardColor;

public class ResourceLoader {

    private static final Integer[] colorRaws = { R.raw.clubs, R.raw.diamonds, R.raw.hearts, R.raw.spades };
    private static final Integer[] aceRaws = { R.raw.ace_clubs, R.raw.ace_diamonds, R.raw.ace_hearts, R.raw.ace_spades };
    private static final Integer[] jackRaws = { R.raw.jack_clubs, R.raw.jack_diamonds, R.raw.jack_hearts, R.raw.jack_spades };
    private static final Integer[] queenRaws = { R.raw.queen_clubs, R.raw.queen_diamonds, R.raw.queen_hearts, R.raw.queen_spades };
    private static final Integer[] kingRaws = { R.raw.king_clubs, R.raw.king_diamonds, R.raw.king_hearts, R.raw.king_spades };

    private final SVG[] cardColors = new SVG[4];
    private final SVG[] cardAces = new SVG[4];
    private final SVG[] cardJacks = new SVG[4];
    private final SVG[] cardQueens = new SVG[4];
    private final SVG[] cardKings = new SVG[4];

    public SVG emptyCard;
    public SVG stackSpace;
    public SVG cardBackside;

    public ResourceLoader(Context context, Resources resources) {
        try {
            emptyCard = SVG.getFromResource(context.getResources(), R.raw.empty_card);
            stackSpace = SVG.getFromResource(context.getResources(), R.raw.stack_space);
            cardBackside = SVG.getFromResource(context.getResources(), R.raw.card_backside);
            loadSVGArray(context, cardColors, colorRaws);
            loadSVGArray(context, cardAces, aceRaws);
            loadSVGArray(context, cardJacks, jackRaws);
            loadSVGArray(context, cardQueens, queenRaws);
            loadSVGArray(context, cardKings, kingRaws);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
    }

    private void loadSVGArray(Context ctx, SVG[] arr, Integer[] ids) throws SVGParseException {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = SVG.getFromResource(ctx, ids[i]);
        }
    }

    public SVG getColor(CardColor color) {
        return cardColors[color.ordinal()];
    }

    public SVG getAceInner(CardColor color) {
        return cardAces[color.ordinal()];
    }

    public SVG getJackInner(CardColor color) {
        return cardJacks[color.ordinal()];
    }

    public SVG getQueenInner(CardColor color) {
        return cardQueens[color.ordinal()];
    }

    public SVG getKingInner(CardColor color) {
        return cardKings[color.ordinal()];
    }
}
