package com.mangostudio.anotherschoolproject.frontend.logic;

import java.io.Serializable;

public class Card implements Serializable {

    public final CardType type;
    public final CardColor color;

    public Card(CardType type, CardColor color) {
        this.type = type;
        this.color = color;
    }
}
