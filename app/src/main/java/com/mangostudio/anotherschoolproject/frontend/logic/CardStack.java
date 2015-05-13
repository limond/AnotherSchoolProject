package com.mangostudio.anotherschoolproject.frontend.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CardStack {

    private final Stack<Card> cards;

    public CardStack() {
        this.cards = new Stack<>();
        // Alle "möglichkeiten" von Karten generieren:
        CardColor[] colorValues = CardColor.values();
        CardType[] typeValues = CardType.values();
        for (int i = 0; i < colorValues.length; i++) {
            for (int j = 0; j < typeValues.length; j++) {
                cards.push(new Card(typeValues[j],colorValues[i]));
            }
        }
    }

    public CardStack(Stack<Card> stack) {
        this.cards = stack;
    }

    // Return itself for method chaining:
    public CardStack shuffle() {
        Card[] arr = new Card[cards.size()];
        List<Card> cardsList = Arrays.asList(cards.toArray(arr));
        Collections.shuffle(cardsList);
        cards.removeAllElements();
        cards.addAll(cardsList);
        return this;
    }

    public CardStack takeFromTop(int n) {
        Stack<Card> newCardStack = new Stack<>();
        for (int i = 0; i < n; i++) {
            Card card = cards.pop();
            newCardStack.push(card);
        }
        return new CardStack(newCardStack);
    }

    public Stack<Card> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }
}
